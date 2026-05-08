package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
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
            JsonNode payloadJson = mapper.readTree(payload); // transforma em json

            List<String> roles = new ArrayList<>();
            payloadJson.get("sam_app").get("roles").forEach(role -> roles.add(role.asText())); // pega as roles atribuídas

            String email = loginDTO.getEmail();

            if (roles.contains("ALUNO")) {
                // Validar se já existe cadastro baseado no e-mail, se não, cria um novo aluno
            }

            return new RespostaLoginDTO(token, expiration_time);

        } catch (Exception e) {
            throw new RuntimeException("Erro ao processar autenticação", e);
        }
    }
}