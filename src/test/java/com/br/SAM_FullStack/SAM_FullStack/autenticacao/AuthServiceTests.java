package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.lang.reflect.Field;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {

    private AuthService authService;
    private CustomUserDetailsService userDetailsServiceMock;
    private PasswordEncoder passwordEncoderMock;
    private TokenService tokenServiceReal;

    private final String email = "user@email.com";
    private final String senha = "123456";

    @BeforeEach
    void setup() throws NoSuchFieldException, IllegalAccessException {
        authService = new AuthService();

        // mocks
        userDetailsServiceMock = mock(CustomUserDetailsService.class);
        passwordEncoderMock = mock(PasswordEncoder.class);

        // TokenService real com chave fake
        tokenServiceReal = new TokenService();
        Field secretField = TokenService.class.getDeclaredField("secret");
        secretField.setAccessible(true);
        secretField.set(tokenServiceReal, "chaveFake123456");

        // injeta campos
        Field userField = AuthService.class.getDeclaredField("userDetailsService");
        userField.setAccessible(true);
        userField.set(authService, userDetailsServiceMock);

        Field tokenField = AuthService.class.getDeclaredField("tokenService");
        tokenField.setAccessible(true);
        tokenField.set(authService, tokenServiceReal);

        Field passField = AuthService.class.getDeclaredField("passwordEncoder");
        passField.setAccessible(true);
        passField.set(authService, passwordEncoderMock);
    }

    @Test
    @DisplayName("Login Aluno: deve gerar token real e role ALUNO")
    void login_quandoUsuarioAluno_deveRetornarTokenERoleAluno() {
        Aluno aluno = new Aluno();
        aluno.setEmail(email);
        aluno.setSenha(senha);
        aluno.setNome("Joana");
        aluno.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_ALUNO")));

        when(userDetailsServiceMock.loadUserByUsername(email)).thenReturn(aluno);
        when(passwordEncoderMock.matches(senha, aluno.getPassword())).thenReturn(true);

        RespostaLoginDTO resposta = authService.login(new LoginDTO(email, senha));

        assertEquals("ALUNO", resposta.getRole());
        assertEquals(email, resposta.getEmail());
        assertNotNull(resposta.getToken());
        assertFalse(resposta.getToken().isEmpty());
        assertNull(resposta.getStatus());
    }

    @Test
    @DisplayName("Login Mentor: deve gerar token real e retornar status Mentor")
    void login_quandoUsuarioMentor_deveRetornarStatusMentor() {
        Mentor mentor = new Mentor();
        mentor.setEmail(email);
        mentor.setSenha(senha);
        mentor.setNome("Romana Novaes");
        mentor.setStatusMentor(StatusMentor.ATIVO);
        mentor.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_MENTOR")));

        when(userDetailsServiceMock.loadUserByUsername(email)).thenReturn(mentor);
        when(passwordEncoderMock.matches(senha, mentor.getPassword())).thenReturn(true);

        RespostaLoginDTO resposta = authService.login(new LoginDTO(email, senha));

        assertEquals("MENTOR", resposta.getRole());
        assertEquals("ATIVO", resposta.getStatus());
        assertNotNull(resposta.getToken());
        assertFalse(resposta.getToken().isEmpty());
    }

    @Test
    @DisplayName("Login Professor: deve gerar token real e role PROFESSOR")
    void login_quandoUsuarioProfessor_deveRetornarTokenERoleProfessor() {
        Professor professor = new Professor();
        professor.setEmail(email);
        professor.setSenha(senha);
        professor.setNome("Daniel Professor");
        professor.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_PROFESSOR")));

        when(userDetailsServiceMock.loadUserByUsername(email)).thenReturn(professor);
        when(passwordEncoderMock.matches(senha, professor.getPassword())).thenReturn(true);

        RespostaLoginDTO resposta = authService.login(new LoginDTO(email, senha));

        assertEquals("PROFESSOR", resposta.getRole());
        assertEquals(email, resposta.getEmail());
        assertNotNull(resposta.getToken());
        assertFalse(resposta.getToken().isEmpty());
        assertNull(resposta.getStatus());
    }

    @Test
    @DisplayName("Login Coordenador: deve gerar token real e role COORDENADOR")
    void login_quandoUsuarioCoordenador_deveRetornarTokenERoleCoordenador() {
        Coordenador coordenador = new Coordenador();
        coordenador.setEmail(email);
        coordenador.setSenha(senha);
        coordenador.setNome("Coordenadora Joana");
        coordenador.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_COORDENADOR")));

        when(userDetailsServiceMock.loadUserByUsername(email)).thenReturn(coordenador);
        when(passwordEncoderMock.matches(senha, coordenador.getPassword())).thenReturn(true);

        RespostaLoginDTO resposta = authService.login(new LoginDTO(email, senha));

        assertEquals("COORDENADOR", resposta.getRole());
        assertEquals(email, resposta.getEmail());
        assertNotNull(resposta.getToken());
        assertFalse(resposta.getToken().isEmpty());
        assertNull(resposta.getStatus());
    }

    @Test
    @DisplayName("Deve lançar exceção quando senha incorreta")
    void login_quandoSenhaIncorreta_deveLancarExcecao() {
        Aluno aluno = new Aluno();
        aluno.setEmail(email);
        aluno.setSenha(senha);
        aluno.setNome("Joana");
        aluno.setAuthorities(List.of(new SimpleGrantedAuthority("ROLE_ALUNO")));

        when(userDetailsServiceMock.loadUserByUsername(email)).thenReturn(aluno);
        when(passwordEncoderMock.matches(senha, aluno.getPassword())).thenReturn(false);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(new LoginDTO(email, senha)));

        assertEquals("Email ou senha inválidos", exception.getMessage());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for Aluno, Mentor, Professor ou Coordenador")
    void login_quandoUsuarioDesconhecido_deveLancarExcecao() {
        UserDetails user = new org.springframework.security.core.userdetails.User(
                email,
                senha,
                List.of(new SimpleGrantedAuthority("ROLE_DESCONHECIDO"))
        );

        when(userDetailsServiceMock.loadUserByUsername(email)).thenReturn(user);
        when(passwordEncoderMock.matches(senha, user.getPassword())).thenReturn(true);

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> authService.login(new LoginDTO(email, senha)));

        assertEquals("Usuário não detectado", exception.getMessage());
    }
}
