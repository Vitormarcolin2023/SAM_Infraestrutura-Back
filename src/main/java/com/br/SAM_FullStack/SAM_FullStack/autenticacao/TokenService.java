package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import org.springframework.beans.factory.annotation.Value; // Adicione esta importação
import org.springframework.stereotype.Service;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;

@Service
public class TokenService {

    /**
     * ATENÇÃO: A chave secreta (secret) é injetada aqui.
     * Ela será lida da propriedade 'app.security.jwt.secret' definida no application.properties,
     * que por sua vez, está lendo a variável de ambiente ${JWT_SECRET} do seu arquivo .env
     */
    @Value("${app.security.jwt.secret}")
    private String secret;

    public String generateToken(String email, String role, String nome){
        try{
            Algorithm algorithm = Algorithm.HMAC256(secret);
            String token = JWT.create()
                    .withIssuer("auth-api")
                    .withSubject(nome)
                    .withClaim("Role", role)
                    .withClaim("email", email)
                    .withExpiresAt(generateExpirationDate())
                    .sign(algorithm);
            return token;
        } catch (JWTCreationException exception){
            throw new RuntimeException("Erro na geração do Token de acesso ", exception);
        }
    }

    public boolean validateToken(String token){
        try {
            Algorithm algorithm = Algorithm.HMAC256(secret);
            JWT.require(algorithm)
                    .withIssuer("auth-api")
                    .build()
                    .verify(token); // se não lançar exceção, token é válido
            return true;
        } catch (JWTVerificationException exception){
            return false;
        }
    }


    // gera tempo de expiração do Token de 2 horas
    private Instant generateExpirationDate(){
        // O fuso horário de Brasília é -03:00
        return LocalDateTime.now().plusHours(2).toInstant(ZoneOffset.of("-03:00"));
    }

    public String extractEmail(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getClaim("email")  // pega o claim "email"
                .asString();
    }

    public String extractRole(String token) {
        Algorithm algorithm = Algorithm.HMAC256(secret);
        return JWT.require(algorithm)
                .withIssuer("auth-api")
                .build()
                .verify(token)
                .getClaim("Role")
                .asString();
    }

}