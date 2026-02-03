package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProjetoServiceTest {

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    @InjectMocks
    private ProjetoService projetoService;

    private AreaDeAtuacao areaTI;
    private Endereco enderecoCarlos;
    private Mentor mentorCarlos;
    private Aluno alunoAdmin;
    private Grupo grupoA;
    private Projeto projeto1;
    private Projeto projeto2;
    private List<Projeto> listaProjetos;

    @BeforeEach
    void setup() {

        // Área de Atuação
        areaTI = new AreaDeAtuacao(1L, "Tecnologia");

        // Endereço
        enderecoCarlos = new Endereco();
        enderecoCarlos.setId(1L);
        enderecoCarlos.setBairro("Centro");
        enderecoCarlos.setCep("00000-000");
        enderecoCarlos.setCidade("São Paulo");
        enderecoCarlos.setEstado("SP");
        enderecoCarlos.setNumero("123");
        enderecoCarlos.setRua("Rua X");

        // Mentor
        mentorCarlos = new Mentor();
        mentorCarlos.setId(1L);
        mentorCarlos.setNome("Carlos Silva");
        mentorCarlos.setCpf("12345678901");
        mentorCarlos.setEmail("carlos@gmail.com");
        mentorCarlos.setSenha("senha123");
        mentorCarlos.setTelefone("11999999999");
        mentorCarlos.setTempoDeExperiencia("5 anos");
        mentorCarlos.setStatusMentor(StatusMentor.ATIVO);
        mentorCarlos.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentorCarlos.setAreaDeAtuacao(areaTI);
        mentorCarlos.setEndereco(enderecoCarlos);

        // Aluno Admin
        alunoAdmin = new Aluno();
        alunoAdmin.setId(1L);
        alunoAdmin.setNome("Joana Silveira");
        alunoAdmin.setEmail("joana@gmail.com");
        alunoAdmin.setSenha("senha123");

        // Grupo
        grupoA = new Grupo();
        grupoA.setId(1L);
        grupoA.setNome("Grupo A");
        grupoA.setAlunoAdmin(alunoAdmin);
        grupoA.setStatusGrupo(StatusGrupo.ATIVO);

        // Projetos
        projeto1 = new Projeto();
        projeto1.setId(1L);
        projeto1.setNomeDoProjeto("Sistema Escolar");
        projeto1.setDescricao("Gerenciamento de alunos");
        projeto1.setAreaDeAtuacao(areaTI);
        projeto1.setDataInicioProjeto(LocalDate.of(2024, 1, 1));
        projeto1.setDataFinalProjeto(LocalDate.of(2024, 6, 30));
        projeto1.setPeriodo("1° Periodo");
        projeto1.setMentor(mentorCarlos);
        projeto1.setStatusProjeto(StatusProjeto.ATIVO);
        projeto1.setGrupo(grupoA);

        projeto2 = new Projeto();
        projeto2.setId(2L);
        projeto2.setNomeDoProjeto("App Financeiro");
        projeto2.setDescricao("Controle financeiro pessoal");
        projeto2.setAreaDeAtuacao(areaTI);
        projeto2.setDataInicioProjeto(LocalDate.of(2024, 2, 1));
        projeto2.setDataFinalProjeto(LocalDate.of(2024, 8, 31));
        projeto2.setPeriodo("Integral");
        projeto2.setMentor(mentorCarlos);
        projeto2.setStatusProjeto(StatusProjeto.ATIVO);
        projeto2.setGrupo(grupoA);

        listaProjetos = Arrays.asList(projeto1, projeto2);
    }


    @Test
    @DisplayName("Retornar todos os projetos")
    void listAll_deveRetornarTodosProjetos() {
        when(projetoRepository.findAll()).thenReturn(listaProjetos);

        List<Projeto> result = projetoService.listAll();

        assertEquals(2, result.size());
        assertEquals("Sistema Escolar", result.get(0).getNomeDoProjeto());
    }

    @Test
    @DisplayName("Quandi ID valido retorno o projeto")
    void findById_quandoIdValido_deveRetornarProjeto() {
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto1));

        Projeto result = projetoService.findById(1L);

        assertEquals("Sistema Escolar", result.getNomeDoProjeto());
    }

    @Test
    @DisplayName("Quando ID invalido lanca exceção")
    void findById_quandoIdInvalido_deveLancarExcecao() {
        when(projetoRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projetoService.findById(99L));

        assertEquals("Projeto não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Buscar por nome, quando existente retorna o projeto")
    void buscarPorNome_quandoNomeExistente_deveRetornarProjetos() {
        when(projetoRepository.findByNomeDoProjetoIgnoreCaseContaining("Sistema"))
                .thenReturn(Collections.singletonList(projeto1));

        List<Projeto> result = projetoService.buscarPorNome("Sistema");

        assertEquals(1, result.size());
        assertEquals("Sistema Escolar", result.get(0).getNomeDoProjeto());
    }

    @Test
    @DisplayName("Quando não exite projeto retorna vazio")
    void buscarPorNome_quandoNomeNaoExistente_deveRetornarListaVazia() {
        when(projetoRepository.findByNomeDoProjetoIgnoreCaseContaining("Inexistente"))
                .thenReturn(Collections.emptyList());

        List<Projeto> result = projetoService.buscarPorNome("Inexistente");

        assertTrue(result.isEmpty());
    }

    @Test
    @DisplayName("Buscar por area de atuacao")
    void buscarPorAreaAtuacao_deveRetornarProjetosDaArea() {
        when(projetoRepository.findByAreaDeAtuacao(areaTI)).thenReturn(listaProjetos);

        List<Projeto> result = projetoService.buscarPorAreaAtuacao(areaTI);

        assertEquals(2, result.size());
    }
    @Test
    @DisplayName("save_quandoProjetoValido_deveSalvarComSucesso")
    void save_quandoProjetoValido_deveSalvarComSucesso() {
        // Adiciona alunos ao grupo
       Aluno aluno1 = new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", new Curso(1L, "ADS", new AreaDeAtuacao(1L, "Tecnologia")), StatusAlunoGrupo.ATIVO);
        Aluno aluno2 = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", new Curso(1L, "ADS", new AreaDeAtuacao(1L, "Tecnologia")), StatusAlunoGrupo.ATIVO);
        grupoA.setAlunos(List.of(aluno1, aluno2));

        when(grupoRepository.findByIdWithAlunos(grupoA.getId()))
                .thenReturn(Optional.of(grupoA));
        when(projetoRepository.save(projeto1)).thenReturn(projeto1);

        Projeto result = projetoService.save(projeto1);

        assertEquals("Sistema Escolar", result.getNomeDoProjeto());
        verify(projetoRepository, times(1)).save(projeto1);
    }

    @Test
    @DisplayName("Quando area dos alunos diferente, lanca exeção")
    void save_quandoAreaDiferenteDosAlunos_deveLancarExcecao() {
        AreaDeAtuacao areaSaude = new AreaDeAtuacao(2L, "Saúde");
        Aluno alunoComOutraArea = new Aluno();
        Curso curso = new Curso();
        curso.setAreaDeAtuacao(areaSaude);
        alunoComOutraArea.setCurso(curso);

        grupoA.setAlunos(List.of(alunoComOutraArea));
        projeto1.setAreaDeAtuacao(areaTI);

        when(grupoRepository.findByIdWithAlunos(grupoA.getId()))
                .thenReturn(Optional.of(grupoA));

        assertThrows(IllegalArgumentException.class, () -> projetoService.save(projeto1));
    }

    @Test
    @DisplayName("Quando valido, vai atualizar projeto")
    void update_quandoIdValido_deveAtualizarProjeto(){
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto1));
        when(projetoRepository.save(any(Projeto.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Projeto projetoAtualizado = new Projeto();
        projetoAtualizado.setNomeDoProjeto("Sistema Escolar Atualizado");
        projetoAtualizado.setDescricao("Nova descrição");
        projetoAtualizado.setPeriodo("2° Periodo");
        projetoAtualizado.setAreaDeAtuacao(areaTI);
        projetoAtualizado.setDataInicioProjeto(LocalDate.of(2024, 1, 1));
        projetoAtualizado.setDataFinalProjeto(LocalDate.of(2024, 12, 31));

        Projeto result = projetoService.update(1L, projetoAtualizado);

        assertEquals("Sistema Escolar Atualizado", result.getNomeDoProjeto());
        assertEquals("Nova descrição", result.getDescricao());
        assertEquals("2° Periodo", result.getPeriodo());
        verify(projetoRepository, times(1)).save(any(Projeto.class));

    }
    @Test
    @DisplayName("Quando id invalido, deve lancar excecao")
    void update_quandoIdInvalido_deveLancarExcecao() {
        when(projetoRepository.findById(8L)).thenReturn(Optional.empty());

        Projeto projetoAtualizado = new Projeto();
        projetoAtualizado.setNomeDoProjeto("Projeto Inválido");

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projetoService.update(8L, projetoAtualizado));

        assertEquals("Projeto não encontrado", exception.getMessage());
        verify(projetoRepository, never()).save(any());

    }
    @Test
    @DisplayName("Deletar projeto ao encontrar")
    void delete_quandoIdValido_deveExcluirProjeto() {
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto1));

        projetoService.delete(1L);

        verify(projetoRepository, times(1)).delete(projeto1);
    }
    @Test
    @DisplayName("delete_quandoIdInvalido_deveLancarExcecao")
    void delete_quandoIdInvalido_deveLancarExcecao() {

        when(projetoRepository.findById(9L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projetoService.delete(9L));

        assertEquals("Projeto não encontrado", exception.getMessage());
        verify(projetoRepository, never()).delete(any());
    }
    @Test
    @DisplayName("Desvincular o mentor do projeto ao ser deletado")
    void desvincularMentor_quandoExistemProjetos_deveRemoverMentorDeTodos() {
        Long mentorId = 1L;
        projeto1.setMentor(mentorCarlos);
        projeto2.setMentor(mentorCarlos);
        when(projetoRepository.findByMentorId(mentorId)).thenReturn(Arrays.asList(projeto1, projeto2));

        projetoService.desvincularMentor(mentorId);

        assertNull(projeto1.getMentor());
        assertNull(projeto2.getMentor());
        verify(projetoRepository, times(2)).save(any(Projeto.class));
    }
    @Test
    @DisplayName("Buscar os projeto vinculado no professor e retornar a lista")
    void buscarProjetosPorProfessor_quandoProfessorPossuiProjetos_deveRetornarLista() {
        Long professorId = 5L;
        when(projetoRepository.findAllByProfessoresId(professorId)).thenReturn(listaProjetos);

        List<Projeto> result = projetoService.buscarProjetosPorProfessor(professorId);

        assertEquals(2, result.size());
        assertEquals("Sistema Escolar", result.get(0).getNomeDoProjeto());
        verify(projetoRepository, times(1)).findAllByProfessoresId(professorId);
    }

    @Test
    @DisplayName("Buscar projetos vinculados no professor se naõ encontrado lanca exeção")
    void buscarProjetosPorProfessor_quandoProfessorSemProjetos_deveRetornarListaVazia() {
        Long professorId = 10L;
        when(projetoRepository.findAllByProfessoresId(professorId)).thenReturn(Collections.emptyList());

        List<Projeto> result = projetoService.buscarProjetosPorProfessor(professorId);

        assertTrue(result.isEmpty());
        verify(projetoRepository, times(1)).findAllByProfessoresId(professorId);
    }
    @Test
    @DisplayName("Buscar projetos por mentor quando existem projetos deve retornar lista")
    void findByMentor_quandoExistemProjetos_deveRetornarLista() {
        Long mentorId = 1L;
        when(projetoRepository.findByMentorId(mentorId)).thenReturn(listaProjetos);

        List<Projeto> result = projetoService.findByMentor(mentorId);

        assertEquals(2, result.size());

        assertEquals("Sistema Escolar", result.get(0).getNomeDoProjeto());
        verify(projetoRepository, times(1)).findByMentorId(mentorId);
    }

    @Test
    @DisplayName("Buscar projetos por mentor quando não existem projetos deve lançar exceção")
    void findByMentor_quandoNaoExistemProjetos_deveLancarExcecao() {
        Long mentorId = 2L;
        when(projetoRepository.findByMentorId(mentorId)).thenReturn(Collections.emptyList());

        RuntimeException exception = assertThrows(RuntimeException.class,
                () -> projetoService.findByMentor(mentorId));

        assertEquals("Nenhum projeto encontrado para este mentor.", exception.getMessage());
        verify(projetoRepository, times(1)).findByMentorId(mentorId);
    }

    }