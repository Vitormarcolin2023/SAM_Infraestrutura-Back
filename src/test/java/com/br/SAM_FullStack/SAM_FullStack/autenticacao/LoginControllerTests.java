package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class LoginControllerTest {

    private LoginController loginController;
    private AuthService authServiceMock;

    private final String email = "teste@email.com";
    private final String senha = "senha123";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        authServiceMock = mock(AuthService.class);
        loginController = new LoginController();

        // injeta o AuthService mock no controller via reflexão
        Field authServiceField = LoginController.class.getDeclaredField("authService");
        authServiceField.setAccessible(true);
        authServiceField.set(loginController, authServiceMock);
    }

    @Test
    @DisplayName("Login correto deve retornar 200 e token")
    void login_quandoCorreto_deveRetornar200() {
        LoginDTO loginDTO = new LoginDTO(email, senha);
        RespostaLoginDTO respostaMock = new RespostaLoginDTO("token123", "ALUNO", email, null);

        when(authServiceMock.login(loginDTO)).thenReturn(respostaMock);

        var response = loginController.login(loginDTO);

        assertNotNull(response);
        assertEquals(200, response.getStatusCodeValue());
        assertEquals("token123", response.getBody().getToken());
        assertEquals("ALUNO", response.getBody().getRole());
    }

    @Test
    @DisplayName("Login incorreto deve lançar exceção")
    void login_quandoFalha_deveLancarExcecao() {
        LoginDTO loginDTO = new LoginDTO(email, "senhaErrada");

        when(authServiceMock.login(loginDTO))
                .thenThrow(new RuntimeException("Email ou senha inválidos"));

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> loginController.login(loginDTO));

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }
}
