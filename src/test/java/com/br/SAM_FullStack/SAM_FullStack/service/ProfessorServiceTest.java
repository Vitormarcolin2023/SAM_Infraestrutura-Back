package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProfessorServiceTest {

    @InjectMocks
    private ProfessorService professorService;

    @Mock
    private ProfessorRepository professorRepository;
    @Mock
    private MentorService mentorService;
    @Mock
    private ProjetoService projetoService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Professor professorMock;
    private final Long professorId = 1L;
    private final String professorEmail = "professor@sam.com";
    private final String senhaBruta = "senha123";
    private final String senhaCriptografada = "senha_criptografada_mock";
    private List<Curso> cursosExemplo;

    @BeforeEach
    void setup() {
        cursosExemplo = Arrays.asList(new Curso(1L, "Java", null));
        professorMock = new Professor(professorId, "Dr. Einstein", professorEmail, senhaBruta, cursosExemplo);
    }

    @Test
    @DisplayName("Salvar: deve criptografar a senha e salvar o professor, retornando mensagem de sucesso")
    void save_deveCriptografarESalvarProfessor() {
        when(passwordEncoder.encode(eq(senhaBruta))).thenReturn(senhaCriptografada);
        when(professorRepository.save(any(Professor.class))).thenReturn(professorMock);

        String resultado = professorService.save(professorMock);

        assertEquals("Professor salvo com sucesso!", resultado);
        assertEquals(senhaCriptografada, professorMock.getSenha());
        verify(professorRepository, times(1)).save(professorMock);
    }

    @Test
    @DisplayName("Atualizar: deve atribuir o ID, criptografar e salvar, retornando mensagem de sucesso")
    void update_deveAtribuirIDECriptografarESalvar() {
        when(passwordEncoder.encode(eq(senhaBruta))).thenReturn(senhaCriptografada);
        when(professorRepository.save(any(Professor.class))).thenReturn(professorMock);

        String resultado = professorService.update(professorMock, professorId);

        assertEquals("Professor atualizado com sucesso!", resultado);
        assertEquals(professorId, professorMock.getId());
        assertEquals(senhaCriptografada, professorMock.getSenha());
        verify(professorRepository, times(1)).save(professorMock);
    }

    @Test
    @DisplayName("Deletar: deve chamar deleteById no repositório, retornando sucesso")
    void delete_deveChamarDeleteByIdERetornarSucesso() {
        doNothing().when(professorRepository).deleteById(professorId);

        String resultado = professorService.delete(professorId);

        assertEquals("Professor deletado com sucesso!", resultado);
        verify(professorRepository, times(1)).deleteById(professorId);
    }

    @Test
    @DisplayName("Buscar Todos: deve retornar lista de professores preenchida")
    void findAll_deveRetornarListaDeProfessores() {
        List<Professor> listaMock = Arrays.asList(professorMock, new Professor());
        when(professorRepository.findAll()).thenReturn(listaMock);

        List<Professor> resultado = professorService.findAll();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(professorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar Todos: deve retornar lista vazia quando nenhum professor for encontrado")
    void findAll_quandoVazio_deveRetornarListaVazia() {
        when(professorRepository.findAll()).thenReturn(Collections.emptyList());

        List<Professor> resultado = professorService.findAll();

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());
        verify(professorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar Todos Mentores: deve chamar MentorService e retornar lista de Mentores")
    void findAllMentores_deveChamarMentorServiceERetornarLista() {
        List<Mentor> mentoresMock = Arrays.asList(new Mentor(), new Mentor());
        when(mentorService.listAll()).thenReturn(mentoresMock);

        List<Mentor> resultado = professorService.findAllMentores();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mentorService, times(1)).listAll();
    }

    @Test
    @DisplayName("Buscar Todos Projetos: deve chamar ProjetoService e retornar lista de Projetos")
    void findAllProjetos_deveChamarProjetoServiceERetornarLista() {
        List<Projeto> projetosMock = Arrays.asList(new Projeto());
        when(projetoService.listAll()).thenReturn(projetosMock);

        List<Projeto> resultado = professorService.findAllProjetos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(projetoService, times(1)).listAll();
    }

    @Test
    @DisplayName("Buscar por Email: deve retornar o professor quando encontrado")
    void findByEmail_quandoEncontrado_deveRetornarProfessor() {
        when(professorRepository.findByEmail(eq(professorEmail))).thenReturn(Optional.of(professorMock));

        Professor resultado = professorService.findByEmail(professorEmail);

        assertNotNull(resultado);
        assertEquals(professorMock.getEmail(), resultado.getEmail());
        verify(professorRepository, times(1)).findByEmail(eq(professorEmail));
    }

    @Test
    @DisplayName("Buscar por Email: deve lançar NoSuchElementException quando não encontrado")
    void findByEmail_quandoNaoEncontrado_deveLancarNoSuchElementException() {
        String emailInvalido = "naoexiste@email.com";
        when(professorRepository.findByEmail(eq(emailInvalido))).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            professorService.findByEmail(emailInvalido);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(professorRepository, times(1)).findByEmail(eq(emailInvalido));
    }

    @Test
    @DisplayName("Buscar por ID: deve retornar o professor quando encontrado")
    void findById_quandoEncontrado_deveRetornarProfessor() {
        when(professorRepository.findById(eq(professorId))).thenReturn(Optional.of(professorMock));

        Professor resultado = professorService.findById(professorId);

        assertNotNull(resultado);
        assertEquals(professorMock.getId(), resultado.getId());
        verify(professorRepository, times(1)).findById(eq(professorId));
    }

    @Test
    @DisplayName("Buscar por ID: deve lançar NoSuchElementException quando não encontrado")
    void findById_quandoNaoEncontrado_deveLancarNoSuchElementException() {
        Long idInvalido = 99L;
        when(professorRepository.findById(eq(idInvalido))).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            professorService.findById(idInvalido);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
        verify(professorRepository, times(1)).findById(eq(idInvalido));
    }
}