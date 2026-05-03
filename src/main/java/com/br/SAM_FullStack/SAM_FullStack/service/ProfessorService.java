package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.ProfessorDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;
import lombok.extern.slf4j.Slf4j;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.keycloak.representations.idm.CredentialRepresentation;
import org.keycloak.representations.idm.RoleRepresentation;
import org.keycloak.representations.idm.UserRepresentation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

@Slf4j
@Service
public class ProfessorService {
    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private MentorService mentorService;


    @Autowired
    private ProjetoService projetoService;

    @Value("${app.security.keycloak.realm}")
    private String realmName;

    @Value("${app.security.server.url}")
    private String serverUrl;

    @Value("${app.security.client.id}")
    private String clientId;

    @Value("${app.security.client.secret}")
    private String clientSecret;


    public Professor findByKeycloakId(String keycloakId) {
        return professorRepository.findByKeycloakId(keycloakId).orElseThrow(() ->
                new RuntimeException("Professor não encontrado"));
    }

    @Transactional
    public String save(ProfessorDTO dto) {
        String keycloakId = criarUsuarioNoKeycloak(dto);

        Professor professor = new Professor();
        professor.setNome(dto.getNome());
        professor.setEmail(dto.getEmail());
        professor.setKeycloakId(keycloakId);

        if (dto.getCursosIds() != null && !dto.getCursosIds().isEmpty()) {
            for (Long cursoId : dto.getCursosIds()) {
                Curso cursoDoBanco = cursoRepository.findById(cursoId)
                        .orElseThrow(() -> new RuntimeException("Curso não encontrado com o ID: " + cursoId));
                cursoDoBanco.getProfessores().add(professor);
                professor.getCursos().add(cursoDoBanco);
            }
        }

        professorRepository.save(professor);
        return "Professor salvo com sucesso!";
    }

    private String criarUsuarioNoKeycloak(ProfessorDTO dto) {
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

        CredentialRepresentation cert = new CredentialRepresentation();
        cert.setTemporary(false);
        cert.setType(CredentialRepresentation.PASSWORD);
        cert.setValue(dto.getSenha());
        user.setCredentials(List.of(cert));

        Response response = keycloak.realm(realmName).users().create(user);

        if (response.getStatus() == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            atribuirClientRole(keycloak, realmName, userId, "PROFESSOR");

            return userId;
        } else if (response.getStatus() == 409) {
            throw new RuntimeException("E-mail já cadastrado no sistema de autenticação.");
        } else {
            throw new RuntimeException("Falha ao criar professor no Keycloak. Status: " + response.getStatus());
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

    private void atualizarUsuarioNoKeycloak(String emailAtual, ProfessorDTO dto, boolean emailMudou) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        List<UserRepresentation> users = keycloak.realm(realmName).users().searchByEmail(emailAtual, true);

        if (users.isEmpty()) throw new RuntimeException("Usuário não encontrado no Keycloak");

        UserRepresentation user = users.get(0);
        user.setFirstName(dto.getNome());

        if (emailMudou) {
            user.setEmail(dto.getEmail());
            user.setUsername(dto.getEmail());
        }

        keycloak.realm(realmName).users().get(user.getId()).update(user);
    }

    @Transactional
    public String update(ProfessorDTO dto, Long id) {
        Professor professorExistente = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado"));

        boolean emailMudou = !professorExistente.getEmail().equalsIgnoreCase(dto.getEmail());

        atualizarUsuarioNoKeycloak(professorExistente.getEmail(), dto, emailMudou);

        professorExistente.setNome(dto.getNome());
        professorExistente.setEmail(dto.getEmail());

        if (dto.getCursosIds() != null) {
            List<Curso> novosCursos = cursoRepository.findAllById(dto.getCursosIds());
            professorExistente.setCursos(novosCursos);
        }

        professorRepository.save(professorExistente);
        return "Professor atualizado com sucesso!";
    }


    @Transactional
    public String delete(long id) {

        Professor professor = professorRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Professor não encontrado com o ID: " + id));

        if (professor.getKeycloakId() != null) {
            deletarUsuarioNoKeycloak(professor.getKeycloakId());
        }

        this.professorRepository.delete(professor);

        return "Professor e conta de acesso deletados com sucesso!";
    }

    private void deletarUsuarioNoKeycloak(String keycloakId) {
        try {
            Keycloak keycloak = KeycloakBuilder.builder()
                    .serverUrl(serverUrl)
                    .realm(realmName)
                    .clientId(clientId)
                    .clientSecret(clientSecret)
                    .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                    .build();

            keycloak.realm(realmName).users().get(keycloakId).remove();
        } catch (jakarta.ws.rs.NotFoundException e) {
            log.warn("Usuário já não existia no keycloak, prosseguindo para deletar no banco.");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao deletar usuário.");
        }
    }

    public List<Professor> findAll(){
        List<Professor> lista = this.professorRepository.findAll();
        return lista;
    }

    public List<Mentor> findAllMentores(){
        return this.mentorService.listAll();
    }

    public List<Projeto> findAllProjetos(){
        return this.projetoService.listAll();
    }

    public Professor findByEmail(String email) {
        return professorRepository.findByEmail(email)
                .orElseThrow(() -> new NoSuchElementException("Professor com email " + email + " não encontrado."));
    }

    public Professor findById(Long id) {
        return professorRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("Professor com ID " + id + " não encontrado."));
    }

}