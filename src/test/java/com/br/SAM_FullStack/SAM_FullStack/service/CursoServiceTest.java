package com.br.SAM_FullStack.SAM_FullStack.service;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;


import java.util.*;
import java.util.concurrent.Callable;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CursoServiceTest {

    @InjectMocks
    CursoService cursoService;

    @Mock
    CursoRepository cursoRepository;

    private List<Curso> cursosMock;
    private AreaDeAtuacao area;

    @BeforeEach
    void setup() {
        area = new AreaDeAtuacao(10L, "Tecnologia");

        cursosMock = List.of(
                new Curso(1L, "Banco de dados", area),
                new Curso(2L, "Java", area),
                new Curso(4L, "Orientação a objetos", area)
        );
    }


    @DisplayName("Deve retornar curdo quando existe")
    @Test
    void findById_quandoCursoExiste_deveRetornarCurso() {
        Curso curso = new Curso(1L, "Analise e desenvolveimento de sistema", area);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(curso));

        Curso retorno = cursoService.findById(1L);

        assertEquals(1L, retorno.getId());
        assertEquals("Analise e desenvolveimento de sistema", retorno.getNome());
    }

    @DisplayName("Quando nao existe o curso, lanca exceçao")
    @Test
    void findById_quandoCursoNaoExiste_deveLancarException() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            cursoService.findById(1L);
        });
        assertEquals("Curso não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Teste de retorno da lista de cursos")
    void findAll_deveRetornarListaDeCursos() {

        when(cursoRepository.findAll()).thenReturn(cursosMock);

        List<Curso> resultado = cursoService.findAll();

        assertEquals(3, resultado.size());
        assertEquals("Banco de dados", resultado.get(0).getNome());
        assertEquals("Tecnologia", resultado.get(0).getAreaDeAtuacao().getNome());
    }

    @Test
    @DisplayName("Deve salvar curso com sucesso")
    void save_deveSalvarCursoComSucesso() {
        Curso curso = new Curso(null, "HTML", area);
        Curso cursoSalvo = new Curso(3L, "HTML", area);

        when(cursoRepository.save(curso)).thenReturn(cursoSalvo);

        Curso resultado = cursoService.save(curso);
        assertEquals(3L, resultado.getId());
        assertEquals("HTML", resultado.getNome());
        verify(cursoRepository, times(1)).save(curso);

    }

    @Test
    @DisplayName("Autulizar curso com sucesso")
    void update_deveAtualizarCursoComSucesso() {
        Curso cursoExistente = cursosMock.get(0);
        Curso cursoUpdate = new Curso(null, "HTML e CSS", area);
        Curso cursoAtualizado = new Curso(1L, "HTML e CSS", area);

        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoExistente));
        when(cursoRepository.save(any(Curso.class))).thenReturn(cursoAtualizado);

        Curso resultado = cursoService.update(1L, cursoUpdate);

        assertEquals(1L, resultado.getId());
        assertEquals("HTML e CSS", resultado.getNome());
        assertEquals("Tecnologia", resultado.getAreaDeAtuacao().getNome());

        verify(cursoRepository, times(1)).findById(1L);
        verify(cursoRepository, times(1)).save(cursoExistente);
    }

    @Test
    @DisplayName("Deletar curso com sucesso")
    void delete_deveRemoverCursoComSucesso() {
        Curso cursoExistente = cursosMock.get(0);
        when(cursoRepository.findById(1L)).thenReturn(Optional.of(cursoExistente));
        doNothing().when(cursoRepository).delete(cursoExistente);

        cursoService.delete(1L);

        verify(cursoRepository, times(1)).findById(1L);
        verify(cursoRepository, times(1)).delete(cursoExistente);

    }
    @Test
    @DisplayName("Quanado não deleta lancar excecao")
    void delete_quandoCursoNaoExiste_deveLancarException() {
        when(cursoRepository.findById(1L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> cursoService.delete(1L));

        assertEquals("Curso não encontrado", exception.getMessage());
        verify(cursoRepository, never()).delete(any());
    }


    @Test
    @DisplayName("Salvar listas de cursos")
    void savaAll_deveSalvarTodosOsCursosComSucesso() {
        when(cursoRepository.save(any(Curso.class))).thenAnswer(invocation -> {
            Curso curso = invocation.getArgument(0);
            if (curso.getId() == null) {
                curso.setId((long) (Math.random() * 1000));
            }
            return curso;
        });
        List<Curso> cursosParaSalvar = new ArrayList<>(cursosMock);

        List<Curso> resultado = cursoService.saveAll(cursosParaSalvar);

        assertEquals(cursosParaSalvar.size(), resultado.size());
        for (int i = 0; i < resultado.size(); i++) {
            assertEquals(cursosParaSalvar.get(i).getNome(), resultado.get(i).getNome());
            assertEquals(cursosParaSalvar.get(i).getAreaDeAtuacao().getNome(), resultado.get(i).getAreaDeAtuacao().getNome());

        }
        verify(cursoRepository, times(cursosParaSalvar.size())).save(any(Curso.class));

    }

    @Test
    @DisplayName("bBuscar por curso e retorna lista com os cursos")
    void buscarPorCurso_quandoCursoExisteExiste_deveRetornarListaDeCursos() {
        String termoBusca = "Java";
        List<Curso> cursosEncontrados = cursosMock.stream()
                .filter(c -> c.getNome().toLowerCase().contains(termoBusca.toLowerCase()))
                .toList();

        when(cursoRepository.findByNomeContainingIgnoreCase(termoBusca))
                .thenReturn(cursosEncontrados);

        List<Curso> resultado = cursoService.buscarPorCurso(termoBusca);

        assertEquals(1, resultado.size());
        assertEquals("Java", resultado.get(0).getNome());
        assertEquals("Tecnologia", resultado.get(0).getAreaDeAtuacao().getNome());

        verify(cursoRepository, times(1)).findByNomeContainingIgnoreCase(termoBusca);

    }

    @Test
    @DisplayName("Quando não encontra o curso  lanca lista vazia")
    void buscarPorCurso_quandoNenhumCusoEncontrado_deveRetornarListaVazia() {
        String termoBusca = "Python";

        when(cursoRepository.findByNomeContainingIgnoreCase(termoBusca))
                .thenReturn(Collections.emptyList());

        List<Curso> resultado = cursoService.buscarPorCurso(termoBusca);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(cursoRepository, times(1)).findByNomeContainingIgnoreCase(termoBusca);
    }

    @Test
    @DisplayName("Pesquisa de curso por area de atuacao e retornar lista com o cursos  da area")
    void buscarPorNomeDaArea_quandoCursosExistem_deveRetornarListaDeCursos() {

        String termoBusca = "Tecnologia";

        List<Curso> cursosEncontrados = cursosMock.stream()
                .filter(c -> c.getAreaDeAtuacao().getNome().equalsIgnoreCase(termoBusca))
                .toList();

        when(cursoRepository.findByAreaDeAtuacaoNomeContainingIgnoreCase(termoBusca))
                .thenReturn(cursosEncontrados);

        List<Curso> resultado = cursoService.buscarPorNomeDaArea(termoBusca);

        assertEquals(cursosEncontrados.size(), resultado.size());

        assertTrue(resultado.stream()
                .allMatch(c -> c.getAreaDeAtuacao().getNome().equalsIgnoreCase(termoBusca)));

        verify(cursoRepository, times(1))
                .findByAreaDeAtuacaoNomeContainingIgnoreCase(termoBusca);
    }
    @Test
    @DisplayName("Quando nao encintrado o curso na area retorna lista vazia")
    void buscarPorNomeDaArea_quandoNenhumCursoEncontrado_deveRetornarListaVazia() {
        String termoBusca = "Saúde";

        when(cursoRepository.findByAreaDeAtuacaoNomeContainingIgnoreCase(termoBusca))
                .thenReturn(Collections.emptyList());

        List<Curso> resultado = cursoService.buscarPorNomeDaArea(termoBusca);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(cursoRepository, times(1)).findByAreaDeAtuacaoNomeContainingIgnoreCase(termoBusca);
    }

    @Test
    @DisplayName("Deve retornar cursos vinculados ao coordenador")
    void findByCoordenadorId_deveRetornarCursosDoCoordenador() {
        Long coordenadorId = 1L;

        List<Curso> cursosDoCoordenador = cursosMock.subList(0, 2);
        when(cursoRepository.findByCoordenadorId(coordenadorId)).thenReturn(cursosDoCoordenador);

        List<Curso> resultado = cursoService.findByCoordenadorId(coordenadorId);

        assertEquals(2, resultado.size());
        assertEquals("Banco de dados", resultado.get(0).getNome());
        assertEquals("Java", resultado.get(1).getNome());

        verify(cursoRepository, times(1)).findByCoordenadorId(coordenadorId);
    }
    @Test
    @DisplayName("Quando coordenador não possui cursos retorna lista vazia")
    void findByCoordenadorId_quandoNaoExistemCursos_deveRetornarListaVazia() {
        Long coordenadorId = 7L;

        when(cursoRepository.findByCoordenadorId(coordenadorId)).thenReturn(Collections.emptyList());

        List<Curso> resultado = cursoService.findByCoordenadorId(coordenadorId);

        assertNotNull(resultado);
        assertTrue(resultado.isEmpty());

        verify(cursoRepository, times(1)).findByCoordenadorId(coordenadorId);
    }

}