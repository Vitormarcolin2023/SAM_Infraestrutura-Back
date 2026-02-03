package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
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
class CoordenadorRepositoryTest {

    @Autowired
    private CoordenadorRepository coordenadorRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Coordenador coordenadorSalvo;
    private final String emailExistente = "teste@repositorio.com";
    private final String emailInexistente = "naoexiste@repositorio.com";

    @BeforeEach
    void setUp() {
        Coordenador coordenador = new Coordenador();
        coordenador.setNome("Coordenador Teste");
        coordenador.setEmail(emailExistente);
        coordenador.setSenha("senha123");

        coordenadorSalvo = entityManager.persistAndFlush(coordenador);
    }

    @Test
    @DisplayName("Salvar: deve persistir o Coordenador com sucesso")
    void save_devePersistirCoordenador() {
        Coordenador novoCoordenador = new Coordenador();
        novoCoordenador.setNome("Novo Coordenador");
        novoCoordenador.setEmail("novo@email.com");
        novoCoordenador.setSenha("outrasenha");

        Coordenador resultado = coordenadorRepository.save(novoCoordenador);

        assertNotNull(resultado.getId());
        assertEquals("Novo Coordenador", resultado.getNome());
    }

    @Test
    @DisplayName("Salvar: deve lançar exceção ao tentar salvar com email duplicado (Integridade)")
    void save_comEmailDuplicado_deveLancarExcecao() {
        Coordenador duplicado = new Coordenador();
        duplicado.setNome("Coordenador Duplicado");
        duplicado.setEmail(emailExistente);
        duplicado.setSenha("duplicada");

        assertThrows(DataIntegrityViolationException.class, () -> {
            coordenadorRepository.save(duplicado);
            entityManager.flush();
        });
    }

    @Test
    @DisplayName("Buscar por Email: quando encontrado, deve retornar o Coordenador")
    void findByEmail_quandoEncontrado_deveRetornarOptionalComCoordenador() {
        Optional<Coordenador> resultado = coordenadorRepository.findByEmail(emailExistente);

        assertTrue(resultado.isPresent());
        assertEquals(coordenadorSalvo.getNome(), resultado.get().getNome());
    }

    @Test
    @DisplayName("Buscar por Email: quando não encontrado, deve retornar Optional vazio")
    void findByEmail_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Coordenador> resultado = coordenadorRepository.findByEmail(emailInexistente);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Buscar por ID: quando encontrado, deve retornar o Coordenador")
    void findById_quandoEncontrado_deveRetornarCoordenador() {
        Optional<Coordenador> resultado = coordenadorRepository.findById(coordenadorSalvo.getId());

        assertTrue(resultado.isPresent());
        assertEquals(emailExistente, resultado.get().getEmail());
    }

    @Test
    @DisplayName("Buscar por ID: quando não encontrado, deve retornar Optional vazio")
    void findById_quandoNaoEncontrado_deveRetornarOptionalVazio() {
        Optional<Coordenador> resultado = coordenadorRepository.findById(999L);

        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Buscar todos: deve retornar a lista de Coordenadores")
    void findAll_deveRetornarListaDeCoordenadores() {
        List<Coordenador> resultado = coordenadorRepository.findAll();

        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
    }

    @Test
    @DisplayName("Deletar: deve remover o Coordenador do banco de dados")
    void delete_deveRemoverCoordenador() {
        assertTrue(coordenadorRepository.findById(coordenadorSalvo.getId()).isPresent());

        coordenadorRepository.delete(coordenadorSalvo);
        entityManager.flush();

        Optional<Coordenador> resultado = coordenadorRepository.findById(coordenadorSalvo.getId());
        assertFalse(resultado.isPresent());
    }

    @Test
    @DisplayName("Deve retornar o coordenador pelo email passado")
    void findByEmail_deveRetornarCoordenador() {
        Optional<Coordenador> result = coordenadorRepository.findByEmail(emailExistente);

        assertEquals("Coordenador Teste", result.get().getNome());
    }

}