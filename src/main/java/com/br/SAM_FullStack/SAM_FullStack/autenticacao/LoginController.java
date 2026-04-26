package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.TokenDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/token")
@CrossOrigin("*")
public class LoginController {

    @Autowired
    private AuthService authService;

    @Value("${app.security.token.url}")
    private String tokenUrl;

    @Value("${app.security.client.secret}")
    private String clientSecret;

    @PostMapping("/")
    public ResponseEntity<String> token(@RequestBody TokenDTO tokenDTO) {

        HttpHeaders headers = new HttpHeaders();
        RestTemplate rt = new RestTemplate();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("username", tokenDTO.getUsername());
        formData.add("password", tokenDTO.getPassword());
        formData.add("client_id", tokenDTO.getClientId());
        formData.add("client_secret", clientSecret);
        formData.add("grant_type", tokenDTO.getGrantType());

        HttpEntity<MultiValueMap<String, String>> entity = new HttpEntity<>(formData, headers);

        var result = rt.postForEntity(tokenUrl, entity, String.class);

        return result;
    }
}