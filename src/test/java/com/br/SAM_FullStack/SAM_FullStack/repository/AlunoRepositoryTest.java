package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
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
@DisplayName("Testes de Integração de Dados do AlunoRepository")
public class AlunoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private AlunoRepository alunoRepository;

    private Curso curso;
    private Aluno aluno;

    @BeforeEach
    void setUp() {

        AreaDeAtuacao area = new AreaDeAtuacao();
        area.setNome("TI");
        entityManager.persistAndFlush(area);

        curso = new Curso();
        curso.setNome("ADS");
        curso.setAreaDeAtuacao(area);
        entityManager.persistAndFlush(curso);

        aluno = new Aluno();
        aluno.setNome("Bruno Silva");
        aluno.setRa(12345);
        aluno.setEmail("bruno@email.com");
        aluno.setSenha("senhaEncriptada123");
        aluno.setCurso(curso);
        entityManager.persistAndFlush(aluno);
    }

    @Test
    @DisplayName("Deve encontrar aluno pelo RA")
    void findByRa_quandoRaExistente_deveRetornarOptionalComAluno() {
        Optional<Aluno> resultado = alunoRepository.findByRa(12345);

        assertTrue(resultado.isPresent(), "Esperado encontrar aluno com RA 12345");
        assertEquals(12345, resultado.get().getRa(), "RA do aluno encontrado deve ser 12345");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio ao buscar RA inexistente")
    void findByRa_quandoRaNaoExistente_deveRetornarOptionalVazio() {
        Optional<Aluno> resultado = alunoRepository.findByRa(99999);

        assertFalse(resultado.isPresent(), "Não esperado encontrar aluno com RA 99999");
    }

    @Test
    @DisplayName("Deve encontrar aluno pelo Email")
    void findByEmail_quandoEmailExistente_deveRetornarOptionalComAluno() {
        Optional<Aluno> resultado = alunoRepository.findByEmail("bruno@email.com");

        assertTrue(resultado.isPresent(), "Esperado encontrar aluno com email bruno@email.com");
        assertEquals("bruno@email.com", resultado.get().getEmail(), "Email do aluno encontrado deve ser bruno@email.com");
    }

    @Test
    @DisplayName("Deve encontrar alunos contendo nome (IgnoreCase)")
    void findByNomeContainingIgnoreCase_deveRetornarListaDeAlunos() {
        List<Aluno> resultado = alunoRepository.findByNomeContainingIgnoreCase("bruno");

        assertFalse(resultado.isEmpty(), "Esperado encontrar alunos contendo 'bruno'");
        assertEquals(1, resultado.size(), "Esperado encontrar 1 aluno");
        assertEquals("Bruno Silva", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar lista vazia se nome não contém (IgnoreCase)")
    void findByNomeContainingIgnoreCase_quandoNaoEncontrado_deveRetornarListaVazia() {
        List<Aluno> resultado = alunoRepository.findByNomeContainingIgnoreCase("Zelia");

        assertTrue(resultado.isEmpty(), "Não esperado encontrar alunos contendo 'Zelia'");
    }

    @Test
    @DisplayName("Deve retornar todos os alunos ordenados por nome ASC")
    void findAllByOrderByNomeAsc_deveRetornarListaOrdenada() {
        // Cria um segundo aluno para testar a ordenação
        Aluno aluno2 = new Aluno();
        aluno2.setNome("Ana Clara");
        aluno2.setRa(54321);
        aluno2.setEmail("ana@email.com");
        aluno2.setSenha("senhaEncriptada456");
        aluno2.setCurso(curso);
        entityManager.persistAndFlush(aluno2);

        List<Aluno> resultado = alunoRepository.findAllByOrderByNomeAsc();

        assertEquals(2, resultado.size(), "Esperado encontrar 2 alunos no total");
        assertEquals("Ana Clara", resultado.get(0).getNome(), "Primeiro aluno na ordem deve ser 'Ana Clara'");
        assertEquals("Bruno Silva", resultado.get(1).getNome(), "Segundo aluno na ordem deve ser 'Bruno Silva'");
    }

    @Test
    @DisplayName("Deve encontrar alunos pelo ID do curso")
    void findByCursoId_quandoIdCursoExistente_deveRetornarListaDeAlunos() {
        List<Aluno> resultado = alunoRepository.findByCursoId(curso.getId());

        assertFalse(resultado.isEmpty(), "Esperado encontrar alunos para o curso ID: " + curso.getId());
        assertEquals(1, resultado.size(), "Esperado encontrar 1 aluno neste curso");
        assertEquals(aluno.getId(), resultado.get(0).getId(), "O aluno encontrado deve ser o persistido no setUp");
        assertEquals(curso.getId(), resultado.get(0).getCurso().getId(), "O curso do aluno encontrado deve ser o correto");
    }

    @Test
    @DisplayName("Deve retornar lista vazia ao buscar por ID de curso sem alunos ou inexistente")
    void findByCursoId_quandoIdCursoInexistenteOuSemAlunos_deveRetornarListaVazia() {
        List<Aluno> resultado = alunoRepository.findByCursoId(999L);

        assertTrue(resultado.isEmpty(), "Não esperado encontrar alunos para o curso ID: 999");
    }
}