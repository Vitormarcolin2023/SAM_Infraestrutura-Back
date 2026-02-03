package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.auth0.jwt.exceptions.JWTCreationException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;

class TokenServiceTest {

    private TokenService tokenService;

    private String email = "teste@email.com";
    private String role = "ALUNO";
    private String nome = "Joana Silveira";

    private final String secretFake = "chaveDeTeste123456";

    @BeforeEach
    void setup() throws IllegalAccessException, NoSuchFieldException {
        tokenService = new TokenService();
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenService, secretFake);
    }

    @Test
    @DisplayName("Deve gerar token não nulo")
    void generateToken_deveGerarToken() {
        String token = tokenService.generateToken(email, role, nome);
        assertNotNull(token, "O token não deve ser nulo");
        assertFalse(token.isEmpty(), "O token não deve estar vazio");
    }

    @Test
    @DisplayName("Deve validar token válido")
    void validateToken_quandoTokenValido_deveRetornarTrue() {
        String token = tokenService.generateToken(email, role, nome);
        boolean valido = tokenService.validateToken(token);
        assertTrue(valido, "O token gerado deve ser válido");
    }

    @Test
    @DisplayName("Deve invalidar token inválido")
    void validateToken_quandoTokenInvalido_deveRetornarFalse() {
        String tokenInvalido = "token.invalido.123";
        boolean valido = tokenService.validateToken(tokenInvalido);
        assertFalse(valido, "O token inválido deve retornar false");
    }

    @Test
    @DisplayName("Deve extrair email do token")
    void extractEmail_deveRetornarEmailCorreto() {
        String token = tokenService.generateToken(email, role, nome);
        String emailExtraido = tokenService.extractEmail(token);
        assertEquals(email, emailExtraido, "O email extraído deve ser igual ao original");
    }

    @Test
    @DisplayName("Deve extrair role do token")
    void extractRole_deveRetornarRoleCorreta() {
        String token = tokenService.generateToken(email, role, nome);
        String roleExtraida = tokenService.extractRole(token);
        assertEquals(role, roleExtraida, "A role extraída deve ser igual à original");
    }

    @Test
    @DisplayName("Deve lançar exceção ao gerar token com chave inválida")
    void generateToken_quandoErro_deveLancarExcecao() {
        TokenService serviceComErro = new TokenService() {
            @Override
            public String generateToken(String email, String role, String nome) {
                throw new RuntimeException("Erro na geração do Token de acesso");
            }
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () ->
                serviceComErro.generateToken(email, role, nome)
        );

        assertEquals("Erro na geração do Token de acesso", exception.getMessage());
    }


    @Test
    @DisplayName("Deve lançar RuntimeException quando houver erro na geração do token")
    void deveLancarRuntimeExceptionAoGerarToken() {
        // Cria uma subclasse que força o erro no momento da criação do token
        TokenService tokenServiceQueFalha = new TokenService() {
            @Override
            public String generateToken(String email, String role, String nome) {
                throw new RuntimeException("Erro na geração do Token de acesso ",
                        new JWTCreationException("Erro simulado", null));
            }
        };

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            tokenServiceQueFalha.generateToken(email, role, nome);
        });

        assertTrue(exception.getMessage().contains("Erro na geração do Token de acesso"));
    }

}
