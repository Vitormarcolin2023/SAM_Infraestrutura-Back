package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomUserDetailsServiceTest {

    @InjectMocks
    private CustomUserDetailsService userDetailsService;

    @Mock
    private AlunoRepository alunoRepo;

    @Mock
    private MentorRepository mentorRepo;

    @Mock
    private ProfessorRepository professorRepo;

    @Mock
    private CoordenadorRepository coordenadorRepo;

    private final String email = "teste@email.com";

    private Aluno aluno;
    private Mentor mentor;
    private Professor professor;
    private Coordenador coordenador;

    @BeforeEach
    void setup() {
        MockitoAnnotations.openMocks(this);

        aluno = new Aluno();
        aluno.setEmail(email);
        aluno.setSenha("senha123");

        mentor = new Mentor();
        mentor.setEmail(email);
        mentor.setSenha("senha123");

        professor = new Professor();
        professor.setEmail(email);
        professor.setSenha("senha123");

        coordenador = new Coordenador();
        coordenador.setEmail(email);
        coordenador.setSenha("senha123");
    }

    @Test
    @DisplayName("Deve carregar usuário do tipo Aluno")
    void loadUserByUsername_quandoAlunoExiste_deveRetornarAluno() {
        when(alunoRepo.findByEmail(email)).thenReturn(Optional.of(aluno));

        UserDetails user = userDetailsService.loadUserByUsername(email);

        assertNotNull(user);
        assertTrue(user instanceof Aluno);
        assertEquals(email, user.getUsername());
    }

    @Test
    @DisplayName("Deve carregar usuário do tipo Mentor")
    void loadUserByUsername_quandoMentorExiste_deveRetornarMentor() {
        when(alunoRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(mentorRepo.findByEmail(email)).thenReturn(Optional.of(mentor));

        UserDetails user = userDetailsService.loadUserByUsername(email);

        assertNotNull(user);
        assertTrue(user instanceof Mentor);
        assertEquals(email, user.getUsername());
    }

    @Test
    @DisplayName("Deve carregar usuário do tipo Professor")
    void loadUserByUsername_quandoProfessorExiste_deveRetornarProfessor() {
        when(alunoRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(mentorRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(professorRepo.findByEmail(email)).thenReturn(Optional.of(professor));

        UserDetails user = userDetailsService.loadUserByUsername(email);

        assertNotNull(user);
        assertTrue(user instanceof Professor);
        assertEquals(email, user.getUsername());
    }

    @Test
    @DisplayName("Deve carregar usuário do tipo Coordenador")
    void loadUserByUsername_quandoCoordenadorExiste_deveRetornarCoordenador() {
        when(alunoRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(mentorRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(professorRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(coordenadorRepo.findByEmail(email)).thenReturn(Optional.of(coordenador));

        UserDetails user = userDetailsService.loadUserByUsername(email);

        assertNotNull(user);
        assertTrue(user instanceof Coordenador);
        assertEquals(email, user.getUsername());
    }

    @Test
    @DisplayName("Deve lançar exceção quando usuário não for encontrado")
    void loadUserByUsername_quandoUsuarioNaoExiste_deveLancarExcecao() {
        when(alunoRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(mentorRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(professorRepo.findByEmail(email)).thenReturn(Optional.empty());
        when(coordenadorRepo.findByEmail(email)).thenReturn(Optional.empty());

        UsernameNotFoundException exception = assertThrows(UsernameNotFoundException.class, () -> {
            userDetailsService.loadUserByUsername(email);
        });

        assertEquals("Usuário não encontrado", exception.getMessage());
    }
}
