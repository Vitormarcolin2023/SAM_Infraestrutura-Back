package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.AlunoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import jakarta.ws.rs.core.Response;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Service
@RequiredArgsConstructor
@Slf4j
public class AlunoService {

    private final AlunoRepository alunoRepository;
    @Autowired
    private EmailService emailService;

    @Autowired
    private CursoRepository cursoRepository;

    @Value("${app.security.keycloak.realm}")
    private String realmName;

    @Value("${app.security.server.url}")
    private String serverUrl;

    @Value("${app.security.client.id}")
    private String clientId;

    @Value("${app.security.client.secret}")
    private String clientSecret;


    public Aluno findById(Long id) {
        return alunoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Aluno não encontrado com ID: " + id));
    }

    public Aluno findByKeycloakId(String keycloakId) {
        return alunoRepository.findByKeycloakId(keycloakId).orElseThrow(() ->
                new RuntimeException("Aluno não encontrado."));
    }

    public Aluno findByRa(Integer ra){
        return alunoRepository.findByRa(ra).orElseThrow(() ->
                new RuntimeException("Aluno não encontrado com RA: " + ra));
    }

    public List<Aluno> findAll(){
        return alunoRepository.findAll();
    }

    @Transactional
    public Aluno save(AlunoDTO alunoDto) {
        alunoRepository.findByRa(alunoDto.getRa()).ifPresent(a -> {
            throw new RuntimeException("Aluno com RA " + alunoDto.getRa() + " já está cadastrado");
        });

        String keycloakId = criarUsuarioNoKeycloak(alunoDto);

        Aluno aluno = new Aluno();
        aluno.setNome(alunoDto.getNome());
        aluno.setEmail(alunoDto.getEmail());
        aluno.setRa(alunoDto.getRa());
        aluno.setKeycloakId(keycloakId);

        if (alunoDto.getCursoId() != null) {
            Curso curso = cursoRepository.findById(alunoDto.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado."));
            aluno.setCurso(curso);
        }

        Aluno alunoSalvo = alunoRepository.save(aluno);

        emailService.enviarEmailTexto(
                alunoSalvo.getEmail(),
                "Aluno Cadastrado com Sucesso",
                "Olá " + alunoSalvo.getNome() + ", seu cadastro foi realizado com sucesso! RA: " + alunoSalvo.getRa()
        );

        return alunoSalvo;
    }


    @Transactional
    public Aluno update(Long id, AlunoDTO dto) {
        Aluno alunoExistente = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado"));

        if (!alunoExistente.getEmail().equalsIgnoreCase(dto.getEmail())) {
            alunoRepository.findByEmail(dto.getEmail()).ifPresent(a -> {
                throw new RuntimeException("O e-mail '" + dto.getEmail() + "' já está em uso.");
            });
        }

        boolean emailMudou = !alunoExistente.getEmail().equalsIgnoreCase(dto.getEmail());
        atualizarUsuarioNoKeycloak(alunoExistente.getKeycloakId(), dto, emailMudou);

        alunoExistente.setNome(dto.getNome());
        alunoExistente.setEmail(dto.getEmail());
        alunoExistente.setRa(dto.getRa());

        if (dto.getCursoId() != null) {
            Curso curso = cursoRepository.findById(dto.getCursoId())
                    .orElseThrow(() -> new RuntimeException("Curso não encontrado"));
            alunoExistente.setCurso(curso);
        }

        return alunoRepository.save(alunoExistente);
    }

    private String criarUsuarioNoKeycloak(AlunoDTO aluno) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        UserRepresentation user = new UserRepresentation();
        user.setEnabled(true);
        user.setUsername(aluno.getEmail());
        user.setEmail(aluno.getEmail());
        user.setFirstName(aluno.getNome());
        user.setEmailVerified(true);
        user.setRequiredActions(new ArrayList<>());

        CredentialRepresentation cred = new CredentialRepresentation();
        cred.setTemporary(false);
        cred.setType(CredentialRepresentation.PASSWORD);
        cred.setValue(aluno.getSenha());
        user.setCredentials(List.of(cred));

        Response response = keycloak.realm(realmName).users().create(user);

        if (response.getStatus() == 201) {
            String path = response.getLocation().getPath();
            String userId = path.substring(path.lastIndexOf("/") + 1);

            atribuirClientRole(keycloak, realmName, userId, "ALUNO");

            return userId;
        } else {
            throw new RuntimeException("Erro ao criar usuário no Keycloak. Status: " + response.getStatus());
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

    private void atualizarUsuarioNoKeycloak(String keycloakId, AlunoDTO dto, boolean emailMudou) {
        Keycloak keycloak = KeycloakBuilder.builder()
                .serverUrl(serverUrl)
                .realm(realmName)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .grantType(OAuth2Constants.CLIENT_CREDENTIALS)
                .build();

        try {
            UserResource userResource = keycloak.realm(realmName).users().get(keycloakId);
            UserRepresentation user = userResource.toRepresentation();

            user.setFirstName(dto.getNome());
            if (emailMudou) {
                user.setEmail(dto.getEmail());
                user.setUsername(dto.getEmail());
            }

            userResource.update(user);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao atualizar dados no servidor de autenticação.");
        }
    }


    @Transactional
    public String delete(long id) {

        Aluno aluno = alunoRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Aluno não encontrado com o ID: " + id));

        if (aluno.getKeycloakId() != null) {
            deletarUsuarioNoKeycloak(aluno.getKeycloakId());
        }

        this.alunoRepository.delete(aluno);

        return "Aluno e conta de acesso deletados com sucesso!";
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


    public List<Aluno> buscarPorNome(String nome) {

        return alunoRepository.findByNomeContainingIgnoreCase(nome);
    }

    public List<Aluno> buscarTodosOrdenadoPorNome() {
        return alunoRepository.findAllByOrderByNomeAsc();
    }

    public Aluno findByEmail(String email) {
        return alunoRepository.findByEmail(email).orElseThrow(() ->
                new RuntimeException("Aluno não encontrado com o E-mail: " + email));
    }

    public List<Aluno> findByCurso(Long idGrupo){
        List<Aluno> alunos = alunoRepository.findByCursoId(idGrupo);

        if(alunos.isEmpty()){
            throw new RuntimeException("Nenhum aluno encontrado nesse curso");
        }

        return alunos;
    }
}

