package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class ReuniaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private ReuniaoRepository reuniaoRepository;

    private Grupo grupo;
    private Mentor mentor;
    private Projeto projeto;

    private LocalDate dataHoje;
    private LocalTime horaAgora;
    private Date dataLegacy;

    @BeforeEach
    void setup() {

        dataHoje = LocalDate.now();
        horaAgora = LocalTime.of(14, 0);
        dataLegacy = Date.from(dataHoje.atStartOfDay(ZoneId.systemDefault()).toInstant());


        AreaDeAtuacao areaDeAtuacao = new AreaDeAtuacao(null, "Tecnologia");
        entityManager.persistAndFlush(areaDeAtuacao);


        Curso curso = new Curso(null, "ADS", areaDeAtuacao);
        entityManager.persistAndFlush(curso);


        Aluno aluno1 = new Aluno(null, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno2 = new Aluno(null, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno3 = new Aluno(null, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);
        entityManager.persistAndFlush(aluno1);
        entityManager.persistAndFlush(aluno2);
        entityManager.persistAndFlush(aluno3);


        mentor = new Mentor();
        mentor.setNome("Romana Novaes");
        mentor.setEmail("romana@teste.com");
        mentor.setSenha("senha123");
        mentor.setTelefone("11999999999");
        mentor.setCpf("54815344442");
        mentor.setFormacaoDoMentor("Mestrado");
        mentor.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentor.setAreaDeAtuacao(areaDeAtuacao);
        mentor.setStatusMentor(StatusMentor.ATIVO);

        entityManager.persistAndFlush(mentor);


        grupo = new Grupo(null, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3));
        entityManager.persistAndFlush(grupo);


        projeto = new Projeto();
        projeto.setNomeDoProjeto("Projeto Teste");
        projeto.setDescricao("Descrição do projeto teste");
        projeto.setDataInicioProjeto(dataHoje);
        projeto.setDataFinalProjeto(dataHoje.plusMonths(6));
        projeto.setPeriodo("Noturno");
        projeto.setStatusProjeto(StatusProjeto.ATIVO);
        projeto.setAreaDeAtuacao(areaDeAtuacao);
        projeto.setGrupo(grupo);
        projeto.setMentor(mentor);

        entityManager.persistAndFlush(projeto);

        Reuniao reuniao1 = new Reuniao();
        reuniao1.setAssunto("Validar requisitos do projeto");
        reuniao1.setData(dataLegacy);
        reuniao1.setHora(horaAgora);
        reuniao1.setFormatoReuniao(FormatoReuniao.ONLINE);
        reuniao1.setStatusReuniao(StatusReuniao.ACEITO);
        reuniao1.setSolicitadoPor("MENTOR");
        reuniao1.setProjeto(projeto);

        Reuniao reuniao2 = new Reuniao();
        reuniao2.setAssunto("Assinar documentos");
        reuniao2.setData(dataLegacy);
        reuniao2.setHora(horaAgora);
        reuniao2.setFormatoReuniao(FormatoReuniao.PRESENCIAL);
        reuniao2.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniao2.setSolicitadoPor("GRUPO");
        reuniao2.setProjeto(projeto);

        entityManager.persistAndFlush(reuniao1);
        entityManager.persistAndFlush(reuniao2);
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões por Grupo")
    void buscarReunioesGrupo_deveRetornarListaDeReunioes(){
        List<Reuniao> retorno = reuniaoRepository.findAllGrupo(grupo.getId());

        assertFalse(retorno.isEmpty(), "A lista de reuniões do grupo não deve estar vazia");
        assertTrue(retorno.stream().anyMatch(r -> r.getAssunto().equals("Validar requisitos do projeto")));
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não existir reuniões para o grupo informado")
    void buscarReunioesGrupo_quandoNaoExistirReunioes_deveRetornarListaVazia() {
        Grupo grupoSemReuniao = new Grupo(null, "Grupo Sem Reunião", StatusGrupo.ATIVO, null, List.of());
        entityManager.persistAndFlush(grupoSemReuniao);

        List<Reuniao> retorno = reuniaoRepository.findAllGrupo(grupoSemReuniao.getId());
        assertTrue(retorno.isEmpty(), "A lista deveria estar vazia");
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões por Mentor")
    void buscarReunioesMentor_deveRetornarListaDeReunioes(){
        List<Reuniao> retorno = reuniaoRepository.findAllMentor(mentor.getId());

        assertFalse(retorno.isEmpty(), "A lista de reuniões do mentor não deve estar vazia");
        assertTrue(retorno.stream().anyMatch(r -> r.getAssunto().equals("Validar requisitos do projeto")));
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões pelo Id do projeto associado")
    void buscarReunioesProjetoId_deveRetornarListaDeReunioesAssociadasAoProjeto() {
        List<Reuniao> retorno = reuniaoRepository.findAllByProjetoId(projeto.getId());

        assertFalse(retorno.isEmpty(), "A lista de reuniões associadas ao projeto nãoo deve estar vazia");
        assertEquals(2, retorno.size());
    }
}