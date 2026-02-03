package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.dao.DataIntegrityViolationException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ProfessorRepositoryTest {

    @Autowired
    private ProfessorRepository professorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Professor professorSalvo;
    private final String emailExistente = "prof.teste@repo.com";
    private final String emailInexistente = "prof.naoexiste@repo.com";
    private final String senhaExistente = "senha_segura_123";

    @BeforeEach
    void setUp() {
        Professor professor = new Professor();
        professor.setNome("Professor Teste");
        professor.setEmail(emailExistente);
        professor.setSenha(senhaExistente);

        professorSalvo = entityManager.persistAndFlush(professor);
    }

    @Test
    @DisplayName("Salvar: deve persistir o Professor com sucesso")
    void save_devePersistirProfessor() {
        Professor novoProfessor = new Professor();
        novoProfessor.setNome("Novo Professor");
        novoProfessor.setEmail("novo.prof@email.com");
        novoProfessor.setSenha("nova_senha_456");

        Professor resultado = professorRepository.save(novoProfessor);

        assertNotNull(resultado.getId());
        assertEquals("Novo Professor", resultado.getNome());
    }

    @Test
    @DisplayName("Salvar: deve lançar exceção ao tentar salvar com email duplicado (Integridade)")
    void save_comEmailDuplicado_deveLancarExcecao() {
        Professor duplicado = new Professor();
        duplicado.setNome("Professor Duplicado");
        duplicado.setEmail(emailExistente);
        duplicado.setSenha("duplicada");

        assertThrows(DataIntegrityViolationException.class, () -> {
            professorRepository.save(duplicado);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Buscar por Email: quando encontrado, deve retornar Optional com o Professor")
    void findByEmail_quandoEncontrado_deveRetornarOptionalComProfessor() {
        Optional<Professor> resultado = professorRepository.findByEmail(emailExistente);

        assertTrue(resultado.isPresent());
        assertEquals(professorSalvo.getNome(), resultado.get().getNome());
    }

    @Test
    @DisplayName("Buscar por Email: quando não encontrado, deve retornar Optional vazio")
    void findByEmail_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Professor> resultado = professorRepository.findByEmail(emailInexistente);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Buscar por Senha: quando encontrado, deve retornar Optional com o Professor")
    void findBySenha_quandoEncontrado_deveRetornarOptionalComProfessor() {
        Optional<Professor> resultado = professorRepository.findBySenha(senhaExistente);

        assertTrue(resultado.isPresent());
        assertEquals(professorSalvo.getEmail(), resultado.get().getEmail());
    }

    @Test
    @DisplayName("Buscar por Senha: quando não encontrado, deve retornar Optional vazio")
    void findBySenha_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Professor> resultado = professorRepository.findBySenha("senha_errada");

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Buscar por ID: quando encontrado, deve retornar o Professor")
    void findById_quandoEncontrado_deveRetornarProfessor() {
        Optional<Professor> resultado = professorRepository.findById(professorSalvo.getId());

        assertTrue(resultado.isPresent());
        assertEquals(emailExistente, resultado.get().getEmail());
    }

    @Test
    @DisplayName("Buscar por ID: quando não encontrado, deve retornar Optional vazio")
    void findById_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Professor> resultado = professorRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Buscar todos: deve retornar a lista de Professores")
    void findAll_deveRetornarListaDeProfessores() {
        Professor professor2 = new Professor();
        professor2.setNome("Professor Dois");
        professor2.setEmail("prof2@repo.com");
        professor2.setSenha("senha2");
        entityManager.persistAndFlush(professor2);

        List<Professor> resultado = professorRepository.findAll();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
    }

    @Test
    @DisplayName("Deletar: deve remover o Professor do banco de dados")
    void delete_deveRemoverProfessor() {
        assertTrue(professorRepository.findById(professorSalvo.getId()).isPresent());

        professorRepository.delete(professorSalvo);
        entityManager.flush();

        Optional<Professor> resultado = professorRepository.findById(professorSalvo.getId());
        assertFalse(resultado.isPresent());
    }
}