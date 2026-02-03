package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class GrupoServiceTest {

    @InjectMocks
    private GrupoService grupoService;

    @Mock
    private GrupoRepository grupoRepository;
    @Mock
    private AlunoRepository alunoRepository;
    @Mock
    private ProfessorRepository professorRepository;

    private Aluno aluno1;
    private Aluno aluno2;
    private Aluno aluno3;
    private Aluno aluno4;
    private Aluno aluno5;
    private Aluno aluno6;
    private Aluno aluno7;
    private Aluno aluno10;
    private Aluno aluno11, aluno8;
    private Grupo grupo1;
    private Grupo grupo2;
    private Grupo grupo3;
    private Grupo grupo4;
    private GrupoDTO grupoSalvarSucesso;
    private Professor professor;

    @BeforeEach
    void setup() {
        AreaDeAtuacao area1 = new AreaDeAtuacao(1L, "Tecnologia");
        AreaDeAtuacao area2 = new AreaDeAtuacao(2L, "Saúde");

        Curso curso1 = new Curso(1L, "Engenharia de Software", area1);
        Curso curso2 = new Curso(2L, "Veterinária", area2);

        aluno1 = new Aluno(1L, "Ana Silva", 1001, "senha123", "ana.silva@email.com", curso1, StatusAlunoGrupo.ATIVO);
        aluno2 = new Aluno(2L, "Bruno Costa", 1002, "senha123", "bruno.costa@email.com", curso1, StatusAlunoGrupo.ATIVO);
        aluno3 = new Aluno(3L, "Carla Mendes", 1003, "senha123", "carla.mendes@email.com", curso1, StatusAlunoGrupo.AGUARDANDO);
        aluno4 = new Aluno(4L, "Diego Oliveira", 1004, "senha123", "diego.oliveira@email.com", curso1, StatusAlunoGrupo.ATIVO);
        aluno11 = new Aluno(11L, "Artemis", 1011, "senha123", "artemis.gmail.com", curso1, StatusAlunoGrupo.ATIVO);

        aluno5 = new Aluno(5L, "Elisa Fernandes", 1005, "senha123", "elisa.fernandes@email.com", curso2, StatusAlunoGrupo.ATIVO);
        aluno6 = new Aluno(6L, "Fábio Santos", 1006, "senha123", "fabio.santos@email.com", curso2, StatusAlunoGrupo.ATIVO);
        aluno7 = new Aluno(7L, "Gabriela Lima", 1007, "senha123", "gabriela.lima@email.com", curso2, StatusAlunoGrupo.AGUARDANDO);
        aluno8 = new Aluno(8L, "Regina Lima", 1008, "senha123", "regina.lima@email.com", curso2, StatusAlunoGrupo.AGUARDANDO);
        Aluno aluno9 = new Aluno(9L, "Guilherme Lima", 1009, "senha123", "gruilherme@gmail.com", curso2, StatusAlunoGrupo.ATIVO);
        aluno10 = new Aluno(10L, "Lauriane Lisiane", 1010, "senha123", "lauriane@gmail.com", curso2, StatusAlunoGrupo.ATIVO);

        grupo1 = new Grupo(1L, "Grupo Eng. Soft.", StatusGrupo.ATIVO, aluno1, new ArrayList<>(List.of(aluno1, aluno2, aluno3)));
        grupo3 = new Grupo(3L, "Grupo Eng. Soft Arquivado", StatusGrupo.ARQUIVADO, aluno1, List.of(aluno1, aluno2, aluno3, aluno4));
        grupo2 = new Grupo(2L, "Grupo Veterinária", StatusGrupo.ARQUIVADO, aluno5, List.of(aluno5, aluno6, aluno7));
        grupo4 = new Grupo(4L, "Grupo 4", StatusGrupo.ATIVO, aluno6, List.of(aluno5, aluno6, aluno7, aluno8, aluno9, aluno10));

        aluno1.setGrupos(new ArrayList<>(List.of(grupo1, grupo3)));
        aluno2.setGrupos(new ArrayList<>(List.of(grupo1, grupo3)));
        aluno3.setGrupos(new ArrayList<>(List.of(grupo1, grupo3)));
        aluno4.setGrupos(new ArrayList<>(List.of(grupo3)));

        aluno5.setGrupos(new ArrayList<>(List.of(grupo2)));
        aluno6.setGrupos(new ArrayList<>(List.of(grupo2)));
        aluno7.setGrupos(new ArrayList<>(List.of(grupo2)));
        aluno8.setGrupos(new ArrayList<>(List.of(grupo1)));
        aluno10.setGrupos(new ArrayList<>(List.of(grupo4)));
        aluno11.setGrupos(new ArrayList<>());

        grupoSalvarSucesso = new GrupoDTO(10L, "Grupo salvo com sucesso", 5L, List.of(5L, 6L, 7L), List.of(1L), "4 PERIODO");

        List<Grupo> todosGrupos = List.of(grupo1, grupo2, grupo3);
        List<Grupo> gruposAluno1 = List.of(grupo1);

        professor = new Professor(1L, "Alexandra", "alexandra@uniamerica.br", "senha123", List.of(curso1, curso2));
    }

    @Test
    @DisplayName("Deve retornar todos os grupos")
    void buscar_deveRetornarTodosOsGrupos() {
        when(grupoRepository.findAll()).thenReturn(List.of(grupo1, grupo2, grupo3, grupo4));
        List<Grupo> response = grupoService.findAll();
        assertEquals(4, response.size());
    }

    @Test
    @DisplayName("Deve retornar o grupo pelo ID")
    void buscarPorId_quandoExiste_deveRetornarGrupo() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        Grupo response = grupoService.findById(1L);
        assertEquals(1L, response.getId());
        assertEquals(3, response.getAlunos().size());
    }

    @Test
    @DisplayName("Deve lançar exceção quando o ID não existir")
    void buscarPorId_quandoNaoExiste_deveCairEmException() {
        assertThrows(RuntimeException.class, () -> grupoService.findById(-1L));
    }

    @Test
    @DisplayName("Deve retornar grupo ativo de um aluno")
    void buscarPorAlunoId_quandoExiste_deveRetornarGrupoAtivoDoAluno() {
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, 1L)).thenReturn(List.of(grupo1));
        Grupo response = grupoService.findByAluno(aluno1);
        assertEquals(StatusGrupo.ATIVO, response.getStatusGrupo());
        assertEquals(true, response.getAlunos().contains(aluno1));
    }


    // --- cenários de erros para salvar grupo
    @Test
    @DisplayName("Deve retornar erro ao buscar id de aluno que não existe ao tentar salvar grupo")
    void salvarGrupo_quandoIdDeAlunoNaoExiste_deveRetornarException() {
        assertThrows(Exception.class, () -> {
            GrupoDTO grupoSave = new GrupoDTO(4L, "Grupo Teste Save", -41L, List.of(5L, 6L, 7L), List.of(1L), "4 PERIODO");
            grupoService.save(grupoSave);
        });
    }

    @Test
    @DisplayName("Deve retornor erro ao tentar salvar um novo grupo quando o admin informado já participa de outro grupo ativo")
    void salvarGrupo_quandoAdminJaPossuiGrupoAtivo_deveRetornarException() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno1));
        assertThrows(IllegalStateException.class, () -> {
            GrupoDTO grupoErroStatus = new GrupoDTO(5L, "Grupo erro status", 1L, List.of(1L, 2L, 3L, 4L), List.of(1L), "4 PERIODO");
            grupoService.save(grupoErroStatus);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar novo grupo quando algum aluno informado não existe")
    void salvarGrupo_quandoAlunoInformadoNaoExiste_deveRetornarExceprion() {
        assertThrows(IllegalArgumentException.class, () -> {
            GrupoDTO grupoErroAlunos = new GrupoDTO(6L, "Grupo teste erro Alunos", 5L, List.of(5L, 6L, -7L), List.of(1L), "4 PERIODO");
            grupoService.save(grupoErroAlunos);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo com qtd de alunos menor do que a permitida")
    void salvarGrupo_quandoQtdDeAlunoEhMenorDoQuePermitida_deveRetornarErro() {
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(alunoRepository.findAllById(List.of(5L, 6L))).thenReturn(List.of(aluno5, aluno6));
        when(professorRepository.findAllById(List.of(1L))).thenReturn(List.of(professor));
        assertThrows(IllegalStateException.class, () -> {
            GrupoDTO grupoErroQtdAlunos = new GrupoDTO(7L, "Grupo teste erro qtd alunos", 5L, List.of(5L, 6L), List.of(1L), "4 PERIODO");
            grupoService.save(grupoErroQtdAlunos);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo quando o id do admin não é passado cna lista de alunos")
    void salvarGrupo_quandoIdDoAdminNaoForInformadoNaLista_deveRetornarErro() {
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(alunoRepository.findAllById(List.of(6L, 7L, 8L))).thenReturn(List.of(aluno6, aluno7, aluno8));
        when(professorRepository.findAllById(List.of(1L))).thenReturn(List.of(professor));
        assertThrows(IllegalStateException.class, () -> {
            GrupoDTO grupoErroListaALunos = new GrupoDTO(8L, "Grupo teste erro lista alunos", 5L, List.of(6L, 7L, 8L), List.of(1L), "4 PERIODO");
            grupoService.save(grupoErroListaALunos);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo quando algum aluno informado já participa de outro grupo ativo")
    void salvarGrupo_quandoAlgumAlunoTemGrupoAtivo_deveRetorarErro() {
        when(alunoRepository.findById(6L)).thenReturn(Optional.of(aluno6));
        when(alunoRepository.findAllById(List.of(6L, 7L, 8L))).thenReturn(List.of(aluno6, aluno7, aluno8));
        when(professorRepository.findAllById(List.of(1L))).thenReturn(List.of(professor));
        assertThrows(IllegalStateException.class, () -> {
            GrupoDTO grupoErroAlunoComGrupoAtivo = new GrupoDTO(9L, "Grupo teste erro aluno com grupo ativo",
                    6L, List.of(6L, 7L, 8L), List.of(1L), "4 PERIODO");
            grupoService.save(grupoErroAlunoComGrupoAtivo);
        });
    }

    // --- cenários de sucesso para salvar grupo

    @Test
    @DisplayName("Deve salvar novo grupo quando não há erros")
    void salvarGrupo_quandoInformacoesCorretas_deveSalvar() {
        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> {
            Grupo grupo = invocation.getArgument(0);
            grupo.setId(10L);
            return grupo;
        });
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(alunoRepository.findAllById(List.of(5L, 6L, 7L))).thenReturn(List.of(aluno5, aluno6, aluno7));
        when(professorRepository.findAllById(List.of(1L))).thenReturn(List.of(professor));
        GrupoDTO response = grupoService.save(grupoSalvarSucesso);
        assertEquals(10L, response.id());
    }

    // -- testes de update
    @Test
    @DisplayName("Deve retornar erro ao tentar buscar grupo por Id que não existe")
    void updateGrupo_quandoIdErrado_deveRetornarErro() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.updateGrupoInfo(-1L, 1L, new GrupoUpdateDTO("Grupo Novo Nome"));
        });
    }

    @Test
    @DisplayName("Deve salvar novo nome do grupo")
    void updateGrupo_quandoinformacoesCorretas_atualizaNome() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        String retorno = this.grupoService.updateGrupoInfo(1L, 1L, new GrupoUpdateDTO("Novo nome do grupo"));
        assertEquals("Informações do grupo atualizadas com sucesso.", retorno);
    }

    // -- testes de put - adicionar novo aluno no grupo

    @Test
    @DisplayName("Deve retornar erro ao tentar buscar grupo com Id que não existe")
    void putAluno_quandoIdErrado_deveRetornarErro() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.adicionarAlunoAoGrupo(1L, -1L, 8L);
        });
    }

    @Test
    @DisplayName("Deve retornar erro quando o Id do aluno passado for diferente do id do admin do grupo")
    void putAluno_quandoAlunoNaoAdmin_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.adicionarAlunoAoGrupo(2L, 1L, 8L);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao adicionar aluno se o grupo já tiver mais alunos do que o permitido")
    void putAluno_quandoQtdMaiorDoQuePermitida_deveRetornarErro() {
        when(grupoRepository.findById(4L)).thenReturn(Optional.of(grupo4));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.adicionarAlunoAoGrupo(6L, 4L, 1L);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao adicionar aluno com Id incorreto")
    void putAluno_quandoIdDoAlunoNovoErrado_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.adicionarAlunoAoGrupo(1L, 1L, -7L);
        });
    }

    @Test
    @DisplayName("Deve retornar erro se novo aluno informado já partici[a de outro grupo ativo")
    void putAluno_quandoAlunoAtivoEmOutroGrupo_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.adicionarAlunoAoGrupo(1L, 1L, 10L);
        });
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso ao salvar grupo")
    void putAluno_quandoInformacoesCorretas_deveSalvar() {
        when(alunoRepository.save(aluno11)).thenAnswer(invocation -> {
            aluno11.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
            return aluno11;
        });
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(alunoRepository.findById(11L)).thenReturn(Optional.of(aluno11));

        String response = this.grupoService.adicionarAlunoAoGrupo(1L, 1L, 11L);
        assertEquals("Aluno adicionado com sucesso ao grupo", response);
    }

    // -- testa remover aluno diretamente

    @Test
    @DisplayName("Testa se o Id do grupo existe se não retorna erro")
    void excluiAluno_quandoGrupoIdInexistente_deveRetornarErro() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.removerAlunoDiretamente(-1L, 2L, 1L);
        });
    }

    @Test
    @DisplayName("Testa se o id do aluno que será removido é igual ao id do admin do grupo")
    void excluiAluno_quandoIdDoAlunoExcForIgualDoAdmin_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.removerAlunoDiretamente(1L, 1L, 1L);
        });
    }

    @Test
    @DisplayName("Testa se o id do aluno que será removido existe")
    void excluiAluno_quandoIdAlunoExcInexistente_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.removerAlunoDiretamente(1L, -1L, 1L);
        });
    }

    @Test
    @DisplayName("Testa se o aluno que será excluido faz parte do grupo")
    void excluiAluno_quandoAlunoNaoParticipaDoGrupo_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.removerAlunoDiretamente(1L, 5L, 1L);
        });
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso quando o aluno for excluido")
    void excluiAluno_quandoInformacoesCorretas_deveRetornarSucesso() {
        when(alunoRepository.findById(2L)).thenReturn(Optional.of(aluno2));
        when(grupoRepository.save(grupo1)).thenAnswer(invocation -> {
            aluno2.setStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);
            grupo1.getAlunos().remove(aluno2);
            return grupo1;
        });
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        String retorno = this.grupoService.removerAlunoDiretamente(1L, 2L, 1L);
        assertEquals("Aluno Bruno Costa foi removido do grupo", retorno);
    }

    // -- Testa buscar alunos por status no grupo

    @Test
    @DisplayName("Deve retornar lista com alunos que estão com status AGUARDANDO")
    void buscarAlunosPorStatus_deveRetornarAlunosComStatusAguardando() {
        when(grupoRepository.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO)).thenReturn(List.of(grupo1, grupo2, grupo4));
        List<Grupo> retorno = this.grupoService.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);
        assertEquals(3, retorno.size());
    }

    // -- testes analizar exclusão de alunos no grupo

    @Test
    @DisplayName("Deve retornar erro quando o id do grupo for inexistente")
    void buscarGrupos_quandoIdInexistente_deveRetornarErro() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.analizarExclusaoAluno("senha123", -1L, 3L, true);
        });
    }

    @Test
    @DisplayName("Deve retornar erro quando o professor não for encontrado")
    void buscarGrupos_quandoProfessorNaoEncontrado_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.analizarExclusaoAluno("senha1", 1L, 3L, true);
        });
    }

    @Test
    @DisplayName("Deve retornar erro quando aluno não for encontrado")
    void buscarGrupos_quandoAlunoNaoEncontrado_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.analizarExclusaoAluno("senha1", 1L, -1L, true);
        });
    }

    @Test
    @DisplayName("Deve retornar erro caso não tenha sido solicitado exclusão do aluno informado")
    void buscarGrupos_quandoStatusAlunoAtivo_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno1));
        when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));

        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.analizarExclusaoAluno("senha123", 1L, 1L, true);
        });
    }

    @Test
    @DisplayName("Quando a solicitação de exclusão for aceita, o aluno deve ser removido do grupo")
    void buscarGrupos_quandoSolicitacaoAceita_deveRemoverAlunoDoGrupo() {
        when(alunoRepository.findById(3L)).thenReturn(Optional.of(aluno3));
        when(grupoRepository.save(grupo1)).thenAnswer(invocation -> {
            grupo1.getAlunos().remove(aluno3);
            aluno3.getGrupos().remove(grupo1);
            aluno3.setStatusAlunoGrupo(null);

            return grupo1;
        });
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(alunoRepository.findById(3L)).thenReturn(Optional.of(aluno3));
        when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));

        String retorno = this.grupoService.analizarExclusaoAluno("senha123", 1L, 3L, true);
        assertEquals("Aluno Carla Mendes foi removido do grupo", retorno);
    }

    @Test
    @DisplayName("Quando a solicitação de exclusão for negada, o aluno deve permanecer no grupo")
    void buscarGrupos_quandoSolicitacaoNegada_deveManterALuno() {
        when(alunoRepository.findById(3L)).thenReturn(Optional.of(aluno3));
        when(grupoRepository.save(grupo1)).thenAnswer(invocation -> {
            aluno3.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
            return grupo1;
        });
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(alunoRepository.findById(3L)).thenReturn(Optional.of(aluno3));
        when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));

        String retorno = this.grupoService.analizarExclusaoAluno("senha123", 1L, 3L, false);
        assertEquals("Solicitação de exclusão recusada. O aluno permanece no grupo", retorno);
    }

    // -- teste deletar grupo

    @Test
    @DisplayName("Deve retornar erro ao não encontrar o grupo")
    void deletarGrupo_quandoIdGrupoIncorreto_deveRetornarErro() {
        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.deletarGrupo(-1L, 1L);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao não encontrar Id do professor")
    void deletarGrupo_quandoIdProfessorIncorreto_deveRetornarErro() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.deletarGrupo(1L, -1L);
        });
    }

    @Test
    @DisplayName("Deve deletar grupo quando informações corretas")
    void deletarGrupo_quandoInfoCorretas_deveDeletarGrupo() {
        when(professorRepository.findById(1L)).thenReturn(Optional.of(professor));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        doAnswer(invocation -> {
            for (Aluno aluno : grupo1.getAlunos()) {
                aluno.getGrupos().remove(grupo1);
                aluno.setStatusAlunoGrupo(null);
            }
            return null;
        }).when(grupoRepository).delete(grupo1);

        String retorno = this.grupoService.deletarGrupo(1L, 1L);
        assertEquals("Grupo deletado com sucesso", retorno);
    }

    // -- testa buscar grupos por aluno

    @Test
    @DisplayName("Deve retornar erro caso o grupo do aluno estiver arquivado")
    void buscarGrupoAtivo_quandoAlunoNaoPossuiGrupoAtivo_deveRetornarNull() {
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, aluno4.getId())).thenReturn(List.of());

        Grupo response = grupoService.findByAluno(aluno4);
        assertNull(response);
    }

    @Test
    @DisplayName("Deve retornar o grupo ativo do aluno")
    void buscarGrupoAtivo_quandoAlunoPossuiGrupoAtivo_deveRetornarGrupo() {
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, aluno1.getId())).thenReturn(List.of(grupo1));

        Grupo retorno = this.grupoService.findByAluno(aluno1);
        assertEquals(grupo1, retorno);
    }

    // -- testes arquivar grupos

    @Test
    @DisplayName("Deve retornar erro ao passar um id de grupo inexistente")
    void arquivarGrupo_quandoIdInexistente_deveRetornarErro() {
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, () -> {
            this.grupoService.arquivarGrupo(-1L);
        });

        assertEquals("Grupo não encontrado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso quando o grupo for arquivado")
    void arquivarGrupo_quandoInfoCorretas_deveRetornarMsgDeSucesso() {
        when(grupoRepository.save(grupo1)).thenAnswer(invocation -> {
            grupo1.setStatusGrupo(StatusGrupo.ARQUIVADO);
            return grupo1;
        });
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        String retorno = this.grupoService.arquivarGrupo(1L);
        assertEquals("Grupo arquivado com sucesso!", retorno);
    }

    // -- testes para buscar grupos arquivados

    @Test
    @DisplayName("Deve retornar erro quando não encontrar nenhum grupo arquivado")
    void buscarGrupos_quandoAlunoNaoPossuiGruposArquivados_DeveRetornarErro() {
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ARQUIVADO, aluno10.getId())).thenReturn(List.of());

        RuntimeException runtimeException = assertThrows(RuntimeException.class, () -> {
            this.grupoService.findByGruposArquivados(10L);
        });

        assertEquals("Aluno não possui nenhum grupo arquivado", runtimeException.getMessage());
    }

    @Test
    @DisplayName("Deve retornar uma lista com os grupos do arquivados do aluno ao encontrar grupo arquivado")
    void buscarGrupos_quandoExisteGrupoArquivado_deveRetornarGrupos() {
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ARQUIVADO, 10L)).thenReturn(List.of(grupo3));
        List<Grupo> retorno = this.grupoService.findByGruposArquivados(10L);
        assertEquals(1, retorno.size());
    }


    @Test
    @DisplayName("Deve retornar erro ao tentar salvar grupo quando algum aluno já tem grupo ativo (cenário misto)")
    void salvarGrupo_quandoAlunoTemGrupoAtivo_deveRetornarErroCenarioMisto() {
        GrupoDTO grupoErroAlunoAtivo = new GrupoDTO(12L, "Grupo Misturado", 5L, List.of(1L, 5L, 6L), List.of(1L), "4 PERIODO");

        when(alunoRepository.findAllById(List.of(1L, 5L, 6L)))
                .thenReturn(List.of(aluno1, aluno5, aluno6));
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(professorRepository.findAllById(List.of(1L))).thenReturn(List.of(professor));

        assertThrows(IllegalStateException.class, () -> grupoService.save(grupoErroAlunoAtivo));
    }


    @Test
    @DisplayName("Deve retornar erro ao adicionar aluno se grupo já possui 6 alunos")
    void adicionarAluno_quandoGrupoNoLimite_deveRetornarErro() {
        when(grupoRepository.findById(4L)).thenReturn(Optional.of(grupo4));
        assertThrows(IllegalStateException.class, () -> {
            grupoService.adicionarAlunoAoGrupo(6L, 4L, 5L);
        });
    }

    @Test
    @DisplayName("Deve retornar erro quando aluno não estiver aguardando exclusão")
    void analizarExclusaoAluno_quandoStatusNaoAguardando_deveRetornarErro() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno1));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));
        assertThrows(IllegalStateException.class, () -> {
            grupoService.analizarExclusaoAluno("senha123", 1L, 1L, true);
        });
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar grupos arquivados quando lista vazia")
    void findByGruposArquivados_quandoListaVazia_deveRetornarErro() {
        when(grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ARQUIVADO, 99L))
                .thenReturn(List.of());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            grupoService.findByGruposArquivados(99L);
        });

        assertEquals("Aluno não possui nenhum grupo arquivado", exception.getMessage());
    }

    @Test
    @DisplayName("Deve adicionar aluno mesmo que grupo esteja parcialmente cheio")
    void adicionarAluno_quandoGrupoTemMenosDe6Alunos_deveAdicionarAluno() {
        Grupo grupoParcial = new Grupo(5L, "Grupo Parcial", StatusGrupo.ATIVO, aluno5, new ArrayList<>(List.of(aluno5, aluno6, aluno7)));
        aluno5.setGrupos(List.of(grupoParcial));
        aluno6.setGrupos(List.of(grupoParcial));
        aluno7.setGrupos(List.of(grupoParcial));

        when(grupoRepository.findById(5L)).thenReturn(Optional.of(grupoParcial));
        when(alunoRepository.findById(8L)).thenReturn(Optional.of(new Aluno(8L,"Regina",1008,"senha","email", aluno5.getCurso(), null)));

        String msg = grupoService.adicionarAlunoAoGrupo(5L, 5L, 8L);
        assertEquals("Aluno adicionado com sucesso ao grupo", msg);
        assertEquals(4, grupoParcial.getAlunos().size());
    }

    @Test
    @DisplayName("Não deve atualizar o nome do grupo se o nome for nulo")
    void updateGrupo_quandoNomeNulo_naoAtualizaNome() {
        // Mock para verificar se o save foi chamado, mas o nome não deve ser alterado
        when(grupoRepository.save(grupo1)).thenReturn(grupo1);
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        String nomeOriginal = grupo1.getNome();
        String retorno = this.grupoService.updateGrupoInfo(1L, 1L, new GrupoUpdateDTO(null));

        assertEquals("Informações do grupo atualizadas com sucesso.", retorno);
        assertEquals(nomeOriginal, grupo1.getNome()); // Nome deve permanecer o original
        verify(grupoRepository, times(1)).save(grupo1);
    }

    @Test
    @DisplayName("Não deve atualizar o nome do grupo se o nome for vazio")
    void updateGrupo_quandoNomeVazio_naoAtualizaNome() {
        when(grupoRepository.save(grupo1)).thenReturn(grupo1);
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        String nomeOriginal = grupo1.getNome();
        String retorno = this.grupoService.updateGrupoInfo(1L, 1L, new GrupoUpdateDTO(""));

        assertEquals("Informações do grupo atualizadas com sucesso.", retorno);
        assertEquals(nomeOriginal, grupo1.getNome()); // Nome deve permanecer o original
        verify(grupoRepository, times(1)).save(grupo1);
    }

    @Test
    @DisplayName("Não deve atualizar o nome do grupo se o nome for em branco")
    void updateGrupo_quandoNomeEmBranco_naoAtualizaNome() {
        when(grupoRepository.save(grupo1)).thenReturn(grupo1);
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));

        String nomeOriginal = grupo1.getNome();
        String retorno = this.grupoService.updateGrupoInfo(1L, 1L, new GrupoUpdateDTO("   "));

        assertEquals("Informações do grupo atualizadas com sucesso.", retorno);
        assertEquals(nomeOriginal, grupo1.getNome()); // Nome deve permanecer o original
        verify(grupoRepository, times(1)).save(grupo1);
    }

    // -- testes de put - adicionar novo aluno no grupo para cobrir if (g.getStatusGrupo() == StatusGrupo.ATIVO) {

    @Test
    @DisplayName("Deve retornar erro se novo aluno informado já participa de outro grupo ATIVO")
    void putAluno_quandoAlunoAtivoEmOutroGrupo_deveRetornarErro_CobreIf() {
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.adicionarAlunoAoGrupo(1L, 1L, 10L);
        });
    }

    // -- testes analizar exclusão de alunos no grupo para cobrir if (!grupo.getAlunos().contains(aluno) || aluno.getStatusAlunoGrupo() != StatusAlunoGrupo.AGUARDANDO) {

    @Test
    @DisplayName("Deve retornar erro ao analisar exclusão se o aluno NÃO pertence ao grupo")
    void analisarExclusaoAluno_quandoAlunoNaoPertenceAoGrupo_deveRetornarErro() {
        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));
        when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
        assertThrows(IllegalStateException.class, () -> {
            this.grupoService.analizarExclusaoAluno("senha123", 1L, 5L, true);
        });
    }

    @Test
    @DisplayName("Deve retornar erro ao analisar exclusão se o aluno pertence, mas não está AGUARDANDO")
    void analisarExclusaoAluno_quandoAlunoNaoEstaAguardando_deveRetornarErro() {
        assertThrows(IllegalStateException.class, () -> {
            when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno1));
            when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));
            when(grupoRepository.findById(1L)).thenReturn(Optional.of(grupo1));
            this.grupoService.analizarExclusaoAluno("senha123", 1L, 1L, true);
        });
    }

    @Test
    @DisplayName("Deve retornar erro quando algum professor informado não existe")
    void salvarGrupo_quandoProfessorNaoExiste_deveRetornarException() {

        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(alunoRepository.findAllById(List.of(5L, 6L, 7L))).thenReturn(List.of(aluno5, aluno6, aluno7));

        when(professorRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(professor));

        GrupoDTO grupoComProfessoresInvalidos = new GrupoDTO(20L, "Grupo com professor inválido",
                5L, List.of(5L, 6L, 7L), List.of(1L, 2L), "4 PERIODO");

        assertThrows(IllegalArgumentException.class, () -> {
            grupoService.save(grupoComProfessoresInvalidos);
        });
    }

    @Test
    @DisplayName("Deve salvar grupo com professores válidos corretamente")
    void salvarGrupo_quandoProfessoresValidos_deveSalvar() {
        when(grupoRepository.save(any(Grupo.class))).thenAnswer(invocation -> {
            Grupo grupo = invocation.getArgument(0);
            grupo.setId(30L);
            return grupo;
        });

        when(alunoRepository.findById(5L)).thenReturn(Optional.of(aluno5));
        when(alunoRepository.findAllById(List.of(5L, 6L, 7L))).thenReturn(List.of(aluno5, aluno6, aluno7));
        Professor professor2 = new Professor(2L, "Bruna", "bruna@uniamerica.br", "senha123", List.of());
        when(professorRepository.findAllById(List.of(1L, 2L))).thenReturn(List.of(professor, professor2));

        GrupoDTO grupoComProfessores = new GrupoDTO(30L, "Grupo com professores",
                5L, List.of(5L, 6L, 7L), List.of(1L, 2L), "4 PERIODO");

        GrupoDTO response = grupoService.save(grupoComProfessores);

        assertEquals(30L, response.id());
        assertEquals(2, response.professoresIds().size());
        assertTrue(response.professoresIds().contains(1L));
        assertTrue(response.professoresIds().contains(2L));
    }


}

