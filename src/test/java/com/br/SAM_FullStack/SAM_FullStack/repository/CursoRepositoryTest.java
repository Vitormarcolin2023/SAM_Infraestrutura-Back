package com.br.SAM_FullStack.SAM_FullStack.repository;


import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class CursoRepositoryTest {
    @Autowired
    private CursoRepository cursoRepository;

    @Autowired
    private AreaDeAtuacaoRepository areaDeAtuacaoRepository;
    private AreaDeAtuacao area;

    private Curso novoCurso(String nome, AreaDeAtuacao area) {
        return new Curso(null, nome, area);
    }

    @BeforeEach
    void setup () {
        area = areaDeAtuacaoRepository.save(new AreaDeAtuacao(null, "Tecnologia"));

        cursoRepository.saveAll(List.of(
                novoCurso("Java Básico", area),
                novoCurso("Java Avançado", area),
                novoCurso("Python", area)
        ));
    }

    @Test
    @DisplayName("Quando cursos contendo 'Java' existirem, deve retornar lista de cursos")

    void findByNomeContainingIgnoreCase_quandoCursoExiste_deveRetornarListaDeCursos() {
      List<Curso> retorno = cursoRepository.findByNomeContainingIgnoreCase("Java");

        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertFalse(retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals(2, retorno.size(), "Deve retornar exatamente 2 cursos");

        assertTrue(retorno.stream().allMatch(c -> c.getNome().toLowerCase().contains("java")),
                "Todos os cursos retornados devem conter 'Java' no nome");

    }

    @Test
    @DisplayName("Quando nenhum curso encontrado, deve retornar lista vazia")

    void findByNomeContainingIgnoreCase_quandoNenhumCursoEncontrado_deveRetornarListaVazia() {
        List<Curso> retorno = cursoRepository.findByNomeContainingIgnoreCase("HTML");

        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertTrue(retorno.isEmpty(), "A lista deve estar vazia");
    }
    @Test
    @DisplayName("Quando cursos da área 'Tecnologia' existirem, deve retornar lista de cursos")
    void findByAreaDeAtuacaoNomeContainingIgnoreCase_quandoAreaExiste_deveRetornarCursos() {
        List<Curso> retorno = cursoRepository.findByAreaDeAtuacaoNomeContainingIgnoreCase("tecnologia");
        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertFalse(retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals(3, retorno.size(), "Deve retornar exatamente 3 cursos da área Tecnologia");
        assertTrue(retorno.stream().allMatch(c -> c.getAreaDeAtuacao().getNome().equalsIgnoreCase("Tecnologia")),
                "Todos os cursos retornados devem pertencer à área Tecnologia");
    }

    @Test
    @DisplayName("Quando nenhuma área correspondente for encontrada, deve retornar lista vazia")
    void findByAreaDeAtuacaoNomeContainingIgnoreCase_quandoAreaNaoExistir_deveRetornarListaVazia() {
        List<Curso> retorno = cursoRepository.findByAreaDeAtuacaoNomeContainingIgnoreCase("Saúde");

        assertNotNull(retorno, "O retorno não deve ser nulo");
        assertTrue(retorno.isEmpty(), "A lista deve estar vazia");
    }

}
