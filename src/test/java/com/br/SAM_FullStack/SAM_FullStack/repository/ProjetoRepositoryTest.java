package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
public class ProjetoRepositoryTest {
    @Autowired
    private ProjetoRepository projetoRepository;

    @Autowired
    private AreaDeAtuacaoRepository areaRepository;

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private ProfessorRepository professorRepository;

    private AreaDeAtuacao areaTI;
    private Mentor mentorCarlos;
    private Projeto projeto1;
    private Projeto projeto2;
    private Professor professorJoao;

    @BeforeEach
    void setup() {

        // Área de Atuação
        areaTI = new AreaDeAtuacao();
        areaTI.setNome("Tecnologia");
        areaTI = areaRepository.save(areaTI);

        // Mentor (CORRIGIDO)
        mentorCarlos = new Mentor();
        mentorCarlos.setNome("Carlos Silva");
        mentorCarlos.setCpf("54815344442");
        mentorCarlos.setEmail("carlos@gmail.com");
        mentorCarlos.setSenha("senha123");
        mentorCarlos.setTelefone("11999999999");
        mentorCarlos.setTempoDeExperiencia("5 anos");
        mentorCarlos.setFormacaoDoMentor("Ciência da Computação");
        mentorCarlos.setStatusMentor(StatusMentor.ATIVO);
        mentorCarlos.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentorCarlos.setAreaDeAtuacao(areaTI);
        mentorCarlos = mentorRepository.save(mentorCarlos);

        // Professor
        professorJoao = new Professor();
        professorJoao.setNome("João da Silva");
        professorJoao.setEmail("joao@prof.com"); // Email pode ser obrigatório, preenchendo por segurança
        professorJoao.setSenha("senha123");
        professorJoao = professorRepository.save(professorJoao);

        // Projetos
        projeto1 = new Projeto();
        projeto1.setNomeDoProjeto("Sistema Escolar");
        projeto1.setDescricao("Gerenciamento de alunos");
        projeto1.setAreaDeAtuacao(areaTI);
        projeto1.setDataInicioProjeto(LocalDate.of(2024, 1, 1));
        projeto1.setDataFinalProjeto(LocalDate.of(2024, 6, 30));
        projeto1.setPeriodo("1° Periodo");
        projeto1.setMentor(mentorCarlos);
        projeto1.setStatusProjeto(StatusProjeto.ATIVO);
        projeto1.setProfessores(new ArrayList<>());
        projeto1.getProfessores().add(professorJoao);
        projetoRepository.save(projeto1);

        projeto2 = new Projeto();
        projeto2.setNomeDoProjeto("App Financeiro");
        projeto2.setDescricao("Controle financeiro pessoal");
        projeto2.setAreaDeAtuacao(areaTI);
        projeto2.setDataInicioProjeto(LocalDate.of(2024, 2, 1));
        projeto2.setDataFinalProjeto(LocalDate.of(2024, 8, 31));
        projeto2.setPeriodo("3° Periodo");
        projeto2.setMentor(mentorCarlos);
        projeto2.setStatusProjeto(StatusProjeto.ARQUIVADO);
        projeto2.setProfessores(new ArrayList<>());
        projetoRepository.save(projeto2);
    }

    @Test
    @DisplayName("Buscar por nome contendo texto (IgnoreCase)")
    void findByNomeDoProjetoIgnoreCaseContaining_deveRetornarProjetos() {
        List<Projeto> result = projetoRepository.findByNomeDoProjetoIgnoreCaseContaining("sistema");
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNomeDoProjeto()).isEqualTo("Sistema Escolar");
    }

    @Test
    @DisplayName("Buscar projetos por mentor")
    void findByMentorId_deveRetornarProjetosDoMentor() {
        List<Projeto> result = projetoRepository.findByMentorId(mentorCarlos.getId());
        assertThat(result).hasSize(2); // Deve retornar os 2 projetos criados no setup
    }

    @Test
    @DisplayName("Buscar projetos por área de atuação")
    void findByAreaDeAtuacao_deveRetornarProjetosDaArea() {
        List<Projeto> result = projetoRepository.findByAreaDeAtuacao(areaTI);
        assertThat(result).hasSize(2);
    }

    @Test
    @DisplayName("Buscar projetos por professor")
    void findAllByProfessoresId_deveRetornarProjetosDoProfessor() {
        List<Projeto> result = projetoRepository.findAllByProfessoresId(professorJoao.getId());
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNomeDoProjeto()).isEqualTo("Sistema Escolar");
    }
}