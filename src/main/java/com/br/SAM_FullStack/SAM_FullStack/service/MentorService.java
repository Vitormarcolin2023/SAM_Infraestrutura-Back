package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.MentorDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import jakarta.ws.rs.core.Response;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.admin.client.resource.UserResource;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class MentorService {
    private final MentorRepository mentorRepository;


    @Autowired
    private EmailService emailService;

    @Value("${app.security.keycloak.realm}")
    private String realmName;

    @Value("${app.security.server.url}")
    private String serverUrl;

    @Value("${app.security.client.id}")
    private String clientId;

    @Value("${app.security.client.secret}")
    private String clientSecret;

    public MentorService(MentorRepository mentorRepository){
        this.mentorRepository = mentorRepository;
    }

    //listar
    public List<Mentor> listAll(){
        return mentorRepository.findAll();
    }

    public Mentor findByKeycloakId(String keycloakId) {
        return mentorRepository.findByKeycloakId(keycloakId).orElseThrow(() ->
                new RuntimeException("Mentor não encontrado"));
    }

    //buscar por id
    public Mentor findById(long id){
        return mentorRepository.findById(id)
                .orElseThrow(()-> new RuntimeException("Mentor não encontrado"));
    }

    @Transactional
    public Mentor save(MentorDTO dto) {

        String keycloakId = criarUsuarioNoKeycloak(dto);

        Mentor mentor = new Mentor();
        mentor.setNome(dto.getNome());
        mentor.setEmail(dto.getEmail());
        mentor.setCpf(dto.getCpf());
        mentor.setTipoDeVinculo(dto.getTipoDeVinculo());
        mentor.setFormacaoDoMentor(dto.getFormacaoDoMentor());
        mentor.setTempoDeExperiencia(dto.getTempoDeExperiencia());
        mentor.setAreaDeAtuacao(dto.getAreaDeAtuacao());
        mentor.setResumo(dto.getResumo());
        mentor.setStatusMentor(StatusMentor.PENDENTE);
        mentor.setEndereco(dto.getEndereco());

        mentor.setKeycloakId(keycloakId);
        mentor.setStatusMentor(StatusMentor.PENDENTE);

        Mentor mentorSalvo = mentorRepository.save(mentor);

        try {
            String destinatario = mentorSalvo.getEmail();
            String assunto = "Bem-vindo(a) ao SAM - Cadastro em Análise";
            Map<String, Object> variaveis = Map.of("nomeMentor", mentorSalvo.getNome());
            String template = "emails/boasVindasMentor";
            emailService.enviarEmailComTemplate(destinatario, assunto, template, variaveis);
        } catch (Exception e) {
            throw new RuntimeException("Algo deu errado " + e.getMessage());
        }

        return mentorSalvo;
    }

    @Transactional
    public Mentor update(Long id, MentorDTO dto) {
        Mentor mentorExistente = mentorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Mentor não encontrado"));

        boolean emailMudou = !mentorExistente.getEmail().equalsIgnoreCase(dto.getEmail());
        atualizarUsuarioNoKeycloak(mentorExistente.getKeycloakId(), dto, emailMudou);

        // Atualizar campos locais
        mentorExistente.setNome(dto.getNome());
        mentorExistente.setEmail(dto.getEmail());
        mentorExistente.setCpf(dto.getCpf());
        mentorExistente.setTipoDeVinculo(dto.getTipoDeVinculo());
        mentorExistente.setFormacaoDoMentor(dto.getFormacaoDoMentor());
        mentorExistente.setTempoDeExperiencia(dto.getTempoDeExperiencia());
        mentorExistente.setAreaDeAtuacao(dto.getAreaDeAtuacao());
        mentorExistente.setResumo(dto.getResumo());

        return mentorRepository.save(mentorExistente);
    }

    private String criarUsuarioNoKeycloak(MentorDTO dto) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(dto.getEmail());
        user.setEmail(dto.getEmail());
        user.setFirstName(dto.getNome());
        user.setEmailVerified(true);
        user.setRequiredActions(new ArrayList<>());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(dto.getSenha());
        user.setCredentials(List.of(cred));

        Response response = keycloak.realm(realmName).users().create(user);

        if (response.getStatus() == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            atribuirClientRole(keycloak, realmName, userId, "MENTOR");

            return userId;
        } else {
            throw new RuntimeException("Erro ao criar mentor no Keycloak: " + response.getStatus());
        }
    }

    private void atribuirClientRole(Keycloak keycloak, String realm, String userId, String roleName) {
        String clientName = clientId;

        String clientUuid = keycloak.realm(realm).clients()
                .findByClientId(clientName).get(0).getId();

        RoleRepresentation clientRole = keycloak.realm(realm).clients().get(clientUuid)
                .roles().get(roleName).toRepresentation();

        keycloak.realm(realm).users().get(userId).roles()
                .clientLevel(clientUuid).add(List.of(clientRole));

    }

    private void atualizarUsuarioNoKeycloak(String keycloakId, MentorDTO dto, boolean emailMudou) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        UserResource userResource = keycloak.realm(realmName).users().get(keycloakId);
        UserRepresentation user = userResource.toRepresentation();

        user.setFirstName(dto.getNome());
        if (emailMudou) {
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getEmail());
        }

        userResource.update(user);
    }

    public void delete(Long id){
        Mentor mentor = findById(id);
        mentorRepository.delete(mentor);
    }

    public String updateStatus(long id, String statusString){
        Mentor mentor = mentorRepository.findById(id).orElseThrow(() -> new RuntimeException("Mentor não encontrado."));

        StatusMentor novoStatus = StatusMentor.valueOf(statusString.toUpperCase());

        mentor.setStatusMentor(novoStatus);
        mentorRepository.save(mentor);

        return "Status do mentor atualizado com sucesso!";
    }

    public Mentor findByEmail(String email) {
        // Usa o repositório para buscar o mentor por e-mail e retorna o objeto ou null
        Optional<Mentor> mentor = mentorRepository.findByEmail(email);
        return mentor.orElse(null);
    }

    public List<Mentor> findByArea(Long idArea) {
        return mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(idArea, StatusMentor.ATIVO);
    }
}
