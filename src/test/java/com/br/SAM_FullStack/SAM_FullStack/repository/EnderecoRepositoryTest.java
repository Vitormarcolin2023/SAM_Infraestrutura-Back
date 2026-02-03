package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
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
public class EnderecoRepositoryTest {

    @Autowired
    private EnderecoRepository enderecoRepository;

    @Autowired
    private TestEntityManager entityManager;

    private Endereco enderecoBase;

    @BeforeEach
    void setUp() {
        // Objeto Endereco para ser usado nos testes
        enderecoBase = new Endereco(
                null, //id nulo para simular um novo registro
                "Rua das Acácias",
                "123",
                "Centro",
                "Belo Horizonte",
                "MG",
                "30110-000",
                null // Mentor
        );
        entityManager.persist(enderecoBase);
        entityManager.flush();
    }

    // TESTES DE CRUD BÁSICO
    @Test
    @DisplayName("Deve salvar um novo Endereco e gerar ID")
    void save_DeveSalvarNovoEndereco() {
        // Cenário- Novo endereço com ID nulo
        Endereco novoEndereco = new Endereco(
                null, "Av. Paulista", "2000", "Bela Vista",
                "São Paulo", "SP", "01310-200", null
        );

        Endereco enderecoSalvo = enderecoRepository.save(novoEndereco);

        // Verificação- o id deve ter sido gerado
        assertNotNull(enderecoSalvo.getId());
        assertTrue(enderecoSalvo.getId() > 0);
        assertEquals("Av. Paulista", enderecoSalvo.getRua());
    }

    @Test
    @DisplayName(" Deve buscar um Endereco por ID com sucesso")
    void findById_DeveRetornarEnderecoExistente() {
        //Buscar pelo ID gerado no setUp
        Optional<Endereco> resultado = enderecoRepository.findById(enderecoBase.getId());

        // Verificação
        assertTrue(resultado.isPresent(), "O Endereço deveria ser encontrado");
        assertEquals("Belo Horizonte", resultado.get().getCidade());
    }

    @Test
    @DisplayName("Deve retornar Optional vazio para ID inexistente")
    void findById_DeveRetornarOptionalVazio() {
        //Buscar por um ID que certamente não existe
        Optional<Endereco> resultado = enderecoRepository.findById(999L);

        // Verificação
        assertFalse(resultado.isPresent(), "O Optional deveria estar vazio");
    }

    @Test
    @DisplayName("Deve listar todos os Enderecos")
    void findAll_DeveRetornarListaNaoVazia() {
        //Buscar todos os endereços
        List<Endereco> lista = enderecoRepository.findAll();

        // Verificação- Deve haver pelo menos 1 (o que foi salvo no setUp)
        assertFalse(lista.isEmpty());
        assertTrue(lista.size() >= 1);
    }

    @Test
    @DisplayName("Deletar um Endereco por ID")
    void deleteById_DeveRemoverEndereco() {
        Long idDeletar = enderecoBase.getId();

        //Deletar o registro
        enderecoRepository.deleteById(idDeletar);

        // Verificação- A busca subsequente deve falhar
        Optional<Endereco> resultado = enderecoRepository.findById(idDeletar);
        assertFalse(resultado.isPresent(), "O Endereço deve ter sido deletado");
    }
}
