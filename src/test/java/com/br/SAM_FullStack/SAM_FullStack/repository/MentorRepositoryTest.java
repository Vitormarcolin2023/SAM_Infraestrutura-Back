package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.model.TipoDeVinculo;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
public class MentorRepositoryTest {

    @Autowired
    private MentorRepository mentorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Mentor mentorAtivo;
    private Mentor mentorPendente;
    private AreaDeAtuacao areaTI;
    private AreaDeAtuacao areaSaude;

    @BeforeEach
    void setUp() {
        // 1. Configura e persiste as Áreas de Atuação
        areaTI = new AreaDeAtuacao(null, "Tecnologia da Informação");
        areaSaude = new AreaDeAtuacao(null, "Saúde");

        entityManager.persist(areaTI);
        entityManager.persist(areaSaude);

        // 2. Cria Mentor Ativo
        mentorAtivo = new Mentor();
        mentorAtivo.setNome("Alice Teste");
        mentorAtivo.setCpf("22057014797");
        mentorAtivo.setEmail("alice@ativo.com");
        mentorAtivo.setSenha("hashAtivo");
        mentorAtivo.setTelefone("888888888");
        mentorAtivo.setFormacaoDoMentor("Engenharia de Software");
        mentorAtivo.setTempoDeExperiencia("3 anos");
        mentorAtivo.setStatusMentor(StatusMentor.ATIVO);
        mentorAtivo.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentorAtivo.setAreaDeAtuacao(areaTI);
        mentorAtivo.setResumo("Resumo Ativo");
        mentorAtivo.setEndereco(null);

        // 3. Cria Mentor Pendente
        mentorPendente = new Mentor();
        mentorPendente.setNome("Bob Pendente");
        mentorPendente.setCpf("75874887156");
        mentorPendente.setEmail("bob@pendente.com");
        mentorPendente.setSenha("hashPendente");
        mentorPendente.setTelefone("777777777");
        mentorPendente.setFormacaoDoMentor("Ciência da Computação");
        mentorPendente.setTempoDeExperiencia("1 ano");
        mentorPendente.setStatusMentor(StatusMentor.PENDENTE);
        mentorPendente.setTipoDeVinculo(TipoDeVinculo.PJ);
        mentorPendente.setAreaDeAtuacao(areaTI);
        mentorPendente.setResumo("Resumo Pendente");
        mentorPendente.setEndereco(null);

        // 4. Salva no banco de dados em memória
        entityManager.persist(mentorAtivo);
        entityManager.persist(mentorPendente);

        entityManager.flush();
    }

    // --- TESTES findByEmail ---

    @Test
    @DisplayName("Busca por Email deve encontrar Mentor existente")
    void findByEmail_DeveRetornarMentorExistente() {
        Optional<Mentor> resultado = mentorRepository.findByEmail(mentorAtivo.getEmail());

        assertTrue(resultado.isPresent(), "O Optional deve conter o mentor");
        assertEquals(mentorAtivo.getNome(), resultado.get().getNome());
    }

    @Test
    @DisplayName("Busca por Email inexistente deve retornar Optional vazio")
    void findByEmail_DeveRetornarOptionalVazio() {
        Optional<Mentor> resultado = mentorRepository.findByEmail("email@naoexiste.com");

        assertFalse(resultado.isPresent());
    }

    // --- TESTES findByEmailAndSenha ---

    @Test
    @DisplayName("Busca por Email e Senha correta deve retornar Mentor")
    void findByEmailAndSenha_DeveRetornarMentor() {
        Optional<Mentor> resultado = mentorRepository.findByEmailAndSenha(
                mentorAtivo.getEmail(), mentorAtivo.getSenha()
        );

        assertTrue(resultado.isPresent());
        assertEquals(mentorAtivo.getEmail(), resultado.get().getEmail());
    }

    @Test
    @DisplayName("Busca por Senha incorreta deve retornar Vazio")
    void findByEmailAndSenha_ComSenhaIncorreta_DeveFalhar() {
        Optional<Mentor> resultado = mentorRepository.findByEmailAndSenha(
                mentorAtivo.getEmail(), "senhaErrada"
        );

        assertFalse(resultado.isPresent());
    }

    // --- TESTES findByAreaDeAtuacaoIdAndStatusMentor ---

    @Test
    @DisplayName("Busca por Área e Status deve filtrar corretamente")
    void findByAreaDeAtuacao_DeveRetornarApenasAtivos() {
        // Busca na área de TI apenas os ATIVOS (deve ignorar o mentorPendente que também é de TI)
        List<Mentor> resultado = mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(
                areaTI.getId(), StatusMentor.ATIVO
        );

        assertNotNull(resultado);
        assertEquals(1, resultado.size(), "Deve retornar apenas 1 mentor (Alice)");
        assertEquals(mentorAtivo.getNome(), resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Busca por Área sem mentores deve retornar lista vazia")
    void findByAreaDeAtuacao_SemMentores_DeveRetornarVazio() {
        List<Mentor> resultado = mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(
                areaSaude.getId(), StatusMentor.ATIVO
        );

        assertTrue(resultado.isEmpty());
    }
}