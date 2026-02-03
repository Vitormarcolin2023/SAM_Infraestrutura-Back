package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
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
@DisplayName("Testes de Integração de Dados do AreaDeAtuacaoRepository")
class AreaDeAtuacaoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AreaDeAtuacaoRepository areaDeAtuacaoRepository;

    private AreaDeAtuacao area;

    @BeforeEach
    void setUp() {
        area = new AreaDeAtuacao();
        area.setNome("Tecnologia");
        entityManager.persist(area);
    }

    @Test
    @DisplayName("Deve encontrar área pelo nome exato")
    void findByNome_quandoNomeExistente_deveRetornarOptionalComArea() {
        Optional<AreaDeAtuacao> resultado = areaDeAtuacaoRepository.findByNome("Tecnologia");
        assertTrue(resultado.isPresent());
        assertEquals("Tecnologia", resultado.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar nome inexistente")
    void findByNome_quandoNomeNaoExistente_deveRetornarOptionalVazio() {
        Optional<AreaDeAtuacao> resultado = areaDeAtuacaoRepository.findByNome("Saúde");
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve encontrar áreas começando com prefixo (IgnoreCase)")
    void findByNomeStartingWithIgnoreCase_deveRetornarListaDeAreas() {
        List<AreaDeAtuacao> resultado = areaDeAtuacaoRepository.findByNomeStartingWithIgnoreCase("tec");
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deve retornar lista vazia se prefixo não encontrado (IgnoreCase)")
    void findByNomeStartingWithIgnoreCase_quandoNaoEncontrado_deveRetornarListaVazia() {
        List<AreaDeAtuacao> resultado = areaDeAtuacaoRepository.findByNomeStartingWithIgnoreCase("sau");
        assertTrue(resultado.isEmpty());
    }
}