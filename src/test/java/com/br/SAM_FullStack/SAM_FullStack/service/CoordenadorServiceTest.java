package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.CoordenadorRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CoordenadorServiceTest {

    @InjectMocks
    private CoordenadorService coordenadorService;

    @Mock
    private CoordenadorRepository coordenadorRepository;
    @Mock
    private CursoRepository cursoRepository;
    @Mock
    private MentorService mentorService;
    @Mock
    private ProjetoService projetoService;
    @Mock
    private PasswordEncoder passwordEncoder;

    private Coordenador coordenadorMock;
    private CoordenadorDTO coordenadorDTOMock;
    private CoordenadorUpdateDTO coordenadorUpdateDTOMock;
    private Curso cursoMock;

    @BeforeEach
    void setup() {
        coordenadorMock = new Coordenador();
        coordenadorMock.setId(1L);
        coordenadorMock.setNome("Coordenador Teste");
        coordenadorMock.setEmail("teste@email.com");
        coordenadorMock.setCursos(new ArrayList<>());

        cursoMock = new Curso();
        cursoMock.setId(10L);

        coordenadorDTOMock = new CoordenadorDTO();
        coordenadorDTOMock.setNome("Novo Coordenador");
        coordenadorDTOMock.setEmail("novo@email.com");
        coordenadorDTOMock.setSenha("senha123");
        coordenadorDTOMock.setCursosIds(Arrays.asList(10L));

        coordenadorUpdateDTOMock = new CoordenadorUpdateDTO();
        coordenadorUpdateDTOMock.setNome("Update Coordenador");
        coordenadorUpdateDTOMock.setEmail("update@email.com");
        coordenadorUpdateDTOMock.setSenha("novaSenha123");
        coordenadorUpdateDTOMock.setCursosIds(Arrays.asList(20L));
    }

    @Test
    @DisplayName("Salvar: coordenador com curso vinculado")
    void save_comCursosValidos_deveSalvarCoordenadorComCursosVinculados() {
        when(passwordEncoder.encode(anyString())).thenReturn("senha_criptografada");

        List<Curso> cursos = Arrays.asList(cursoMock);
        when(cursoRepository.findAllById(coordenadorDTOMock.getCursosIds())).thenReturn(cursos);

        when(coordenadorRepository.save(any(Coordenador.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Coordenador resultado = coordenadorService.save(coordenadorDTOMock);

        assertNotNull(resultado);
        assertEquals(coordenadorDTOMock.getNome(), resultado.getNome());
        assertEquals(1, resultado.getCursos().size());

        assertEquals(resultado, cursos.get(0).getCoordenador());

        verify(coordenadorRepository, times(1)).save(any(Coordenador.class));
    }

    @Test
    @DisplayName("Atualizar: coordenador com novos cursos, mensagem de salvo com sucesso")
    void update_CoordenadorEncontradoComNovosCursos_deveAtualizarComSucesso() {
        coordenadorMock.setCursos(new ArrayList<>(Arrays.asList(cursoMock)));

        Curso novoCursoMock = new Curso();
        novoCursoMock.setId(20L);

        when(coordenadorRepository.findById(1L)).thenReturn(Optional.of(coordenadorMock));
        when(passwordEncoder.encode(anyString())).thenReturn("nova_senha_criptografada");
        when(cursoRepository.findById(20L)).thenReturn(Optional.of(novoCursoMock));
        when(coordenadorRepository.save(any(Coordenador.class))).thenReturn(coordenadorMock);

        String mensagem = coordenadorService.update(coordenadorUpdateDTOMock, 1L);

        assertEquals("Coordenador atualizado com sucesso!", mensagem);
        assertEquals("Update Coordenador", coordenadorMock.getNome());
        assertEquals(1, coordenadorMock.getCursos().size());

        assertNull(cursoMock.getCoordenador());
        assertEquals(coordenadorMock, novoCursoMock.getCoordenador());

        verify(coordenadorRepository, times(1)).save(coordenadorMock);
    }

    @Test
    @DisplayName("Atualizar: exceção de coordenador não encontrado")
    void update_CoordenadorNaoEncontrado_deveLancarRuntimeException() {
        when(coordenadorRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            coordenadorService.update(coordenadorUpdateDTOMock, 1L);
        });

        assertTrue(exception.getMessage().contains("Coordenador não encontrado"));
        verify(coordenadorRepository, never()).save(any(Coordenador.class));
    }

    @Test
    @DisplayName("Atualizar: lançar exceção para curso não encontrado")
    void update_CursoNaoEncontrado_deveLancarRuntimeException() {
        when(coordenadorRepository.findById(1L)).thenReturn(Optional.of(coordenadorMock));
        coordenadorUpdateDTOMock.setCursosIds(Arrays.asList(99L));
        when(cursoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            coordenadorService.update(coordenadorUpdateDTOMock, 1L);
        });

        assertTrue(exception.getMessage().contains("Curso não encontrado"));
        verify(coordenadorRepository, never()).save(any(Coordenador.class));
    }

    @Test
    @DisplayName("Deletar: encontra coordenador, desvincula curso e deleta")
    void delete_CoordenadorEncontrado_deveDesvincularCursosESerDeletado() {
        coordenadorMock.setCursos(new ArrayList<>(Arrays.asList(cursoMock)));
        when(coordenadorRepository.findById(1L)).thenReturn(Optional.of(coordenadorMock));

        coordenadorService.delete(1L);

        assertNull(cursoMock.getCoordenador());

        verify(cursoRepository, times(1)).saveAll(coordenadorMock.getCursos());

        verify(coordenadorRepository, times(1)).delete(coordenadorMock);
    }

    @Test
    @DisplayName("Deletar: lançar exceção de coordenador não encontrado")
    void delete_CoordenadorNaoEncontrado_deveLancarRuntimeException() {
        when(coordenadorRepository.findById(1L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> coordenadorService.delete(1L));

        verify(coordenadorRepository, never()).delete(any(Coordenador.class));
    }

    @Test
    @DisplayName("Ativar mentor")
    void ativarMentor_chamadaBemSucedida_deveRetornarSucesso() throws Exception {
        when(mentorService.updateStatus(1L, "ATIVO")).thenReturn("Status atualizado");

        String resultado = coordenadorService.ativarMentor(1L);

        assertEquals("Mentor ativado com sucesso!", resultado);
        verify(mentorService, times(1)).updateStatus(1L, "ATIVO");
    }

    @Test
    @DisplayName("Ativar mentor: chamada com erro")
    void ativarMentor_chamadaComErro_deveRetornarMensagemDeErro() throws Exception {
        doThrow(new RuntimeException()).when(mentorService).updateStatus(1L, "ATIVO");

        String resultado = coordenadorService.ativarMentor(1L);

        assertEquals("Erro ao tentar ativar o mentor.", resultado);
        verify(mentorService, times(1)).updateStatus(1L, "ATIVO");
    }

    @Test
    @DisplayName("Inativar mentor: mensagem de sucesso")
    void inativarMentor_chamadaBemSucedida_deveRetornarSucesso() throws Exception {
        when(mentorService.updateStatus(1L, "INATIVO")).thenReturn("Status atualizado");

        String resultado = coordenadorService.inativarMentor(1L);

        assertEquals("Mentor inativado com sucesso.", resultado);
        verify(mentorService, times(1)).updateStatus(1L, "INATIVO");
    }

    @Test
    @DisplayName("Inativar mentor: retornar mensagem de erro")
    void inativarMentor_chamadaComErro_deveRetornarMensagemDeErro() throws Exception {
        doThrow(new RuntimeException()).when(mentorService).updateStatus(1L, "INATIVO");

        String resultado = coordenadorService.inativarMentor(1L);

        assertEquals("Erro ao tentar inativar o mentor.", resultado);
        verify(mentorService, times(1)).updateStatus(1L, "INATIVO");
    }

    @Test
    @DisplayName("Encontrar mentores: retorna lista de mentores")
    void findAllMentores_deveRetornarListaDeMentores() {
        List<Mentor> mentores = Arrays.asList(new Mentor(), new Mentor());
        when(mentorService.listAll()).thenReturn(mentores);

        List<Mentor> resultado = coordenadorService.findAllMentores();

        assertNotNull(resultado);
        assertEquals(2, resultado.size());
        verify(mentorService, times(1)).listAll();
    }

    @Test
    @DisplayName("Encontrar projetos: retorna lista de projetos")
    void findAllProjetos_deveRetornarListaDeProjetos() {
        List<Projeto> projetos = Arrays.asList(new Projeto());
        when(projetoService.listAll()).thenReturn(projetos);

        List<Projeto> resultado = coordenadorService.findAllProjetos();

        assertNotNull(resultado);
        assertEquals(1, resultado.size());
        verify(projetoService, times(1)).listAll();
    }

    @Test
    @DisplayName("Buscar por email: retorna coordenador")
    void buscarPorEmail_quandoEncontrado_deveRetornarCoordenador() {
        when(coordenadorRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(coordenadorMock));

        Coordenador resultado = coordenadorService.buscarPorEmail("teste@email.com");

        assertNotNull(resultado);
        assertEquals(coordenadorMock.getNome(), resultado.getNome());
    }

    @Test
    @DisplayName("Buscar por email: retorna null se não encontrar")
    void buscarPorEmail_quandoNaoEncontrado_deveRetornarNull() {
        when(coordenadorRepository.findByEmail("email_nao_existe@email.com")).thenReturn(Optional.empty());

        Coordenador resultado = coordenadorService.buscarPorEmail("email_nao_existe@email.com");

        assertNull(resultado);
    }

    @Test
    @DisplayName("Encontrar por Id: retorna coordenador")
    void findById_CoordenadorEncontrado_deveRetornarCoordenador() {
        when(coordenadorRepository.findById(1L)).thenReturn(Optional.of(coordenadorMock));

        Coordenador resultado = coordenadorService.findById(1L);

        assertNotNull(resultado);
        assertEquals(coordenadorMock.getId(), resultado.getId());
    }

    @Test
    @DisplayName("Encontrar por Id: lança exception")
    void findById_CoordenadorNaoEncontrado_deveLancarNoSuchElementException() {
        when(coordenadorRepository.findById(2L)).thenReturn(Optional.empty());

        NoSuchElementException exception = assertThrows(NoSuchElementException.class, () -> {
            coordenadorService.findById(2L);
        });

        assertTrue(exception.getMessage().contains("não encontrado"));
    }
}