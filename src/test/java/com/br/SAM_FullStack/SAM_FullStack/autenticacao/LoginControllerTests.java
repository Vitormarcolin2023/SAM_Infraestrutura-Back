package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthControllerTest {

    private AuthController authController;
    private AuthService authServiceMock;

    private final String email = "teste@email.com";
    private final String senha = "senha123";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        authServiceMock = mock(AuthService.class);
        authController = new AuthController();

        // injeta o AuthService mock no controller via reflexão
        Field authServiceField = AuthController.class.getDeclaredField("authService");
        authServiceField.setAccessible(true);
        authServiceField.set(authController, authServiceMock);
    }


}
