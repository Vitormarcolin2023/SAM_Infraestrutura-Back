package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

@Service
public class AuthService {

    @Value("${app.security.token.url}")
    private String tokenUrl;

    @Value("${app.security.client.secret}")
    private String clientSecret;

    @Value("${app.security.client.id}")
    private String clientId;

    @Autowired
    AlunoRepository alunoRepository;

    @Autowired
    ProfessorRepository professorRepository;

    @Autowired
    CoordenadorRepository coordenadorRepository;

    @Autowired
    MentorRepository mentorRepository;

    public RespostaLoginDTO get_token_login(LoginDTO loginDTO) {

        try {
            HttpHeaders headers = new HttpHeaders();
            RestTemplate rt = new RestTemplate();
            headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

            MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
            formData.add("username", loginDTO.getEmail());
            formData.add("password", loginDTO.getSenha());
            formData.add("client_id", clientId);
            formData.add("client_secret", clientSecret);
            formData.add("grant_type", "password");

            HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

            var response = rt.postForEntity(tokenUrl, entity, String.class).getBody();

            ObjectMapper mapper = new ObjectMapper();
            JsonNode json = mapper.readTree(response);

            String token = json.get("access_token").asText();
            int expiration_time = json.get("expires_in").asInt();

            String[] token_split = token.split("\\."); // quebra o token, para separarmos as 3 partes
            String payload = new String(Base64.getUrlDecoder().decode(token_split[1])); // decodifica a segunda parte do token (onde fica as informações importantes)
            JsonNode payloadJson = mapper.readTree(payload); // trnsforma em json

            List<String> roles = new ArrayList<>();
            payloadJson.get("resource_access").get("sam_app").get("roles").forEach(role -> roles.add(role.asText())); // pega as roles atribuídas

            String email = loginDTO.getEmail();
            String keycloakId = payloadJson.get("sub").asText();
            String nome = payloadJson.get("name").asText();

           sincronizarDados(roles, email, keycloakId, nome);

            return new RespostaLoginDTO(token, expiration_time);

        } catch (HttpClientErrorException.Unauthorized e) {
            throw new RuntimeException("Usuário ou senha inválidos");
        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar autenticação", e);
        }
    }

    private void sincronizarDados (List<String> roles, String email, String keycloakId, String nome) {
        if (roles.contains("ALUNO")) {
            alunoRepository.findByEmail(email).orElseGet(() -> {
                Aluno novoAluno = new Aluno();
                novoAluno.setEmail(email);
                novoAluno.setKeycloakId(keycloakId);
                novoAluno.setNome(nome);
                return alunoRepository.save(novoAluno);
            });
        } else if (roles.contains("PROFESSOR")) {
            professorRepository.findByEmail(email).orElseGet(() -> {
                Professor novoProfessor = new Professor();
                novoProfessor.setEmail(email);
                novoProfessor.setKeycloakId(keycloakId);
                novoProfessor.setNome(nome);
                return professorRepository.save(novoProfessor);
            });
        } else if (roles.contains("COORDENADOR")) {
            coordenadorRepository.findByEmail(email).orElseGet(() -> {
                Coordenador novoCoordenador = new Coordenador();
                novoCoordenador.setEmail(email);
                novoCoordenador.setKeycloakId(keycloakId);
                novoCoordenador.setNome(nome);
                return coordenadorRepository.save(novoCoordenador);
            });
        } else if (roles.contains("MENTOR")) {
            mentorRepository.findByEmail(email).orElseGet(() -> {
                Mentor novoMentor = new Mentor();
                novoMentor.setEmail(email);
                novoMentor.setKeycloakId(keycloakId);
                novoMentor.setNome(nome);
                return mentorRepository.save(novoMentor);
            });
        }
    }
}