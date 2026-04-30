package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import jakarta.transaction.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class CoordenadorService {

    @Value("${app.security.keycloak.realm}")
    private String realmName;

    @Value("${app.security.server.url}")
    private String serverUrl;

    @Value("${app.security.client.id}")
    private String clientId;

    @Value("${app.security.client.secret}")
    private String clientSecret;

    private final CoordenadorRepository coordenadorRepository;
    private final CursoRepository cursoRepository;
    private final MentorService mentorService;
    private final ProjetoService projetoService;

    @Transactional
    public Coordenador save(CoordenadorDTO coordenadorDTO) {
        if (coordenadorRepository.findByEmail(coordenadorDTO.getEmail()).isPresent()) {
            throw new RuntimeException("E-mail já cadastrado no sistema.");
        }
        String keycloakId = criarUsuarioNoKeycloak(coordenadorDTO);


        Coordenador coordenador = new Coordenador();
        coordenador.setNome(coordenadorDTO.getNome());
        coordenador.setEmail(coordenadorDTO.getEmail());
        coordenador.setKeycloakId(keycloakId);

        List<Curso> cursos = cursoRepository.findAllById(coordenadorDTO.getCursosIds());
        for (Curso curso : cursos) {
            curso.setCoordenador(coordenador);
        }
        coordenador.setCursos(cursos);


        return coordenadorRepository.save(coordenador);
    }

    private String criarUsuarioNoKeycloak(CoordenadorDTO dto) {

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
        user.setRequiredActions(Collections.emptyList());

        CredentialRepresentation credential = new CredentialRepresentation();
        credential.setType(CredentialRepresentation.PASSWORD);
        credential.setValue(dto.getSenha());
        credential.setTemporary(false);
        user.setCredentials(List.of(credential));

        Response response = keycloak.realm(realmName).users().create(user);

        if (response.getStatus() == 201) {
            String path = response.getLocation().getPath();
            String keycloakId = path.substring(path.lastIndexOf("/") + 1);

            try {
                atribuirClientRole(keycloak, realmName, keycloakId, "COORDENADOR");
            } catch (Exception e) {
                throw new RuntimeException(e.getMessage());
            }

            return keycloakId;
        } else {
            throw new RuntimeException("Falha ao criar credenciais de acesso.");
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

    public Coordenador findByKeycloakId(String keycloakId) {
        return coordenadorRepository.findByKeycloakId(keycloakId).orElseThrow(() ->
                new RuntimeException("Coordenador não encontrado."));
    }


    @Transactional
    public String update(CoordenadorUpdateDTO coordenadorDTO, long id) {

        Coordenador coordenadorExistente = coordenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com o ID: " + id));

        atualizarUsuarioNoKeycloak(coordenadorExistente.getEmail(), coordenadorDTO);

        coordenadorExistente.setNome(coordenadorDTO.getNome());
        coordenadorExistente.setEmail(coordenadorDTO.getEmail());

        if (coordenadorExistente.getCursos() != null) {
            coordenadorExistente.getCursos().forEach(curso -> curso.setCoordenador(null));
        }
        coordenadorExistente.getCursos().clear();

        if (coordenadorDTO.getCursosIds() != null && !coordenadorDTO.getCursosIds().isEmpty()) {
            for (Long cursoId : coordenadorDTO.getCursosIds()) {
                Curso cursoDoBanco = cursoRepository.findById(cursoId)
                        .orElseThrow(() -> new RuntimeException("Curso não encontrado com o ID: " + cursoId));
                cursoDoBanco.setCoordenador(coordenadorExistente);
                coordenadorExistente.getCursos().add(cursoDoBanco);
            }
        }

        coordenadorRepository.save(coordenadorExistente);
        return "Coordenador atualizado com sucesso!";
    }

    private void atualizarUsuarioNoKeycloak(String emailAntigo, CoordenadorUpdateDTO dto) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        List<UserRepresentation> users = keycloak.realm(realmName).users().searchByEmail(emailAntigo, true);

        if (users.isEmpty()) {
            throw new RuntimeException("Usuário não encontrado no servidor de autenticação.");
        }

        UserRepresentation user = users.get(0);
        user.setEmail(dto.getEmail());
        user.setUsername(dto.getEmail());
        user.setFirstName(dto.getNome());

        keycloak.realm(realmName).users().get(user.getId()).update(user);
    }

    @Transactional
    public void delete(Long id) {
        Coordenador coordenadorExistente = coordenadorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coordenador não encontrado com o ID: " + id));

        if (coordenadorExistente.getCursos() != null) {
            coordenadorExistente.getCursos().forEach(curso -> curso.setCoordenador(null));
        }

        coordenadorExistente.getCursos().clear();

        cursoRepository.saveAll(coordenadorExistente.getCursos());

        coordenadorRepository.delete(coordenadorExistente);
    }

    public String ativarMentor(long mentorId){
        try {
            String mensagem = this.mentorService.updateStatus(mentorId, "ATIVO");
            return "Mentor ativado com sucesso!";
        } catch (Exception e) {
            return "Erro ao tentar ativar o mentor.";
        }
    }

    public String inativarMentor(long mentorId){
        try {
            String mensagem = this.mentorService.updateStatus(mentorId, "INATIVO");
            return "Mentor inativado com sucesso.";
        } catch (Exception e) {
            return "Erro ao tentar inativar o mentor.";
        }
    }

    public List<Mentor> findAllMentores(){
        return this.mentorService.listAll();
    }

    public List<Projeto> findAllProjetos(){
        return this.projetoService.listAll();
    }

    public Coordenador buscarPorEmail(String email) {
        Optional<Coordenador> coordenador = coordenadorRepository.findByEmail(email);
        return coordenador.orElse(null);
    }

    public Coordenador findById(Long id) {
        return coordenadorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Coordenador com ID " + id + " não encontrado."));
    }
}