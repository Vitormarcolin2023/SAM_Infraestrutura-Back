package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.repository.AreaDeAtuacaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do AreaDeAtuacaoService")
class AreaDeAtuacaoServiceTest {

    @Mock
    AreaDeAtuacaoRepository areaDeAtuacaoRepository;

    @InjectMocks
    AreaDeAtuacaoService areaDeAtuacaoService;

    AreaDeAtuacao area;
    Aluno aluno;
    Curso curso;

    @BeforeEach
    void setUp() {
        area = new AreaDeAtuacao();
        area.setId(1L);
        area.setNome("Tecnologia");

        curso = new Curso();
        curso.setId(1L);
        curso.setAreaDeAtuacao(area);

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setCurso(curso);
    }

    @Test
    @DisplayName("Deve retornar área de atuação ao buscar por ID existente")
    void findById_quandoIdExistente_deveRetornarArea() {
        when(areaDeAtuacaoRepository.findById(1L)).thenReturn(Optional.of(area));
        AreaDeAtuacao areaEncontrada = areaDeAtuacaoService.findById(1L);
        assertNotNull(areaEncontrada);
        assertEquals(1L, areaEncontrada.getId());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por ID inexistente")
    void findById_quandoIdNaoExistente_deveLancarRuntimeException() {
        when(areaDeAtuacaoRepository.findById(99L)).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> areaDeAtuacaoService.findById(99L));
        assertEquals("Area de Atuação não encontrada com ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista de todas as áreas")
    void findAll_deveRetornarListaDeAreas() {
        when(areaDeAtuacaoRepository.findAll()).thenReturn(Arrays.asList(area));
        List<AreaDeAtuacao> resultado = areaDeAtuacaoService.findAll();
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve salvar e retornar área de atuação")
    void save_deveRetornarAreaSalva() {
        when(areaDeAtuacaoRepository.save(any(AreaDeAtuacao.class))).thenReturn(area);
        AreaDeAtuacao areaSalva = areaDeAtuacaoService.save(area);
        assertNotNull(areaSalva);
        verify(areaDeAtuacaoRepository).save(area);
    }

    @Test
    @DisplayName("Deve salvar e retornar uma lista de áreas")
    void saveAll_deveSalvarListaDeAreas() {
        AreaDeAtuacao area1 = new AreaDeAtuacao();
        area1.setNome("Saúde");

        AreaDeAtuacao area2 = new AreaDeAtuacao();
        area2.setNome("Educação");

        List<AreaDeAtuacao> listaParaSalvar = Arrays.asList(area1, area2);

        when(areaDeAtuacaoRepository.save(any(AreaDeAtuacao.class)))
                .thenAnswer(invocation -> invocation.getArgument(0)); // Retorna o mesmo objeto que recebeu

        List<AreaDeAtuacao> resultado = areaDeAtuacaoService.saveAll(listaParaSalvar);

        assertNotNull(resultado);
        assertEquals(2, resultado.size());

        verify(areaDeAtuacaoRepository, times(2)).save(any(AreaDeAtuacao.class));
    }


    @Test
    @DisplayName("Deve atualizar e retornar área de atuação")
    void update_quandoIdExistente_deveRetornarAreaAtualizada() {
        AreaDeAtuacao areaUpdate = new AreaDeAtuacao();
        areaUpdate.setNome("Saúde");
        when(areaDeAtuacaoRepository.findById(1L)).thenReturn(Optional.of(area));
        when(areaDeAtuacaoRepository.save(any(AreaDeAtuacao.class))).thenReturn(area);

        AreaDeAtuacao areaAtualizada = areaDeAtuacaoService.update(1L, areaUpdate);

        assertNotNull(areaAtualizada);
        assertEquals("Saúde", areaAtualizada.getNome());
        verify(areaDeAtuacaoRepository).save(area);
    }

    @Test
    @DisplayName("Deve deletar área com sucesso")
    void delete_quandoIdExistente_deveChamarDelete() {
        when(areaDeAtuacaoRepository.findById(1L)).thenReturn(Optional.of(area));
        doNothing().when(areaDeAtuacaoRepository).delete(area);
        areaDeAtuacaoService.delete(1L);
        verify(areaDeAtuacaoRepository).delete(area);
    }

    @Test
    @DisplayName("Deve retornar áreas ao buscar por início do nome")
    void buscarPorInicioDoNome_deveRetornarListaDeAreas() {
        when(areaDeAtuacaoRepository.findByNomeStartingWithIgnoreCase("Tec")).thenReturn(Arrays.asList(area));
        List<AreaDeAtuacao> resultado = areaDeAtuacaoService.buscarPorInicioDoNome("Tec");
        assertFalse(resultado.isEmpty());
        assertEquals("Tecnologia", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar área do aluno logado com curso")
    void findByAlunoLogado_quandoAlunoLogadoComCurso_deveRetornarArea() {
        try (MockedStatic<SecurityContextHolder> mockedContext = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(aluno);

            AreaDeAtuacao areaEncontrada = areaDeAtuacaoService.findByAlunoLogado();

            assertNotNull(areaEncontrada);
            assertEquals("Tecnologia", areaEncontrada.getNome());
        }
    }

    @Test
    @DisplayName("Deve retornar null quando aluno logado não tem curso")
    void findByAlunoLogado_quandoAlunoSemCurso_deveRetornarNull() {
        aluno.setCurso(null);
        try (MockedStatic<SecurityContextHolder> mockedContext = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(true);
            when(authentication.getPrincipal()).thenReturn(aluno);

            AreaDeAtuacao areaEncontrada = areaDeAtuacaoService.findByAlunoLogado();
            assertNull(areaEncontrada);
        }
    }

    @Test
    @DisplayName("Deve retornar null quando não está autenticado")
    void findByAlunoLogado_quandoNaoAutenticado_deveRetornarNull() {
        try (MockedStatic<SecurityContextHolder> mockedContext = Mockito.mockStatic(SecurityContextHolder.class)) {
            SecurityContext securityContext = mock(SecurityContext.class);
            Authentication authentication = mock(Authentication.class);

            mockedContext.when(SecurityContextHolder::getContext).thenReturn(securityContext);
            when(securityContext.getAuthentication()).thenReturn(authentication);
            when(authentication.isAuthenticated()).thenReturn(false);

            AreaDeAtuacao areaEncontrada = areaDeAtuacaoService.findByAlunoLogado();
            assertNull(areaEncontrada);
        }
    }
}