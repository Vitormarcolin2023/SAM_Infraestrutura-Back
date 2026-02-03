package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
import com.br.SAM_FullStack.SAM_FullStack.repository.EnderecoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.RestTemplate;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do EnderecoService")
public class EnderecoServiceTest {

    @Mock
    private EnderecoRepository enderecoRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private EnderecoService enderecoService;

    private Endereco enderecoApiRetorno;

    @BeforeEach
    void setUp() {
        // Mock do retorno da API ViaCEP para um cenário ideal.
        enderecoApiRetorno = new Endereco();
        enderecoApiRetorno.setCep("12345678");
        enderecoApiRetorno.setRua("Rua da API");
        enderecoApiRetorno.setBairro("Bairro da API");
        enderecoApiRetorno.setCidade("Cidade da API");
        enderecoApiRetorno.setEstado("Estado da API");

        ReflectionTestUtils.setField(enderecoService, "restTemplate", restTemplate);
    }

    // --- Testes de Busca ---
    @Test
    @DisplayName("Deve buscar endereço por ID com sucesso")
    void findById_quandoExistente_deveRetornarEndereco() {

        Endereco enderecoParaTeste = new Endereco(1L, "Rua Teste", "100", "Bairro X", "Cidade Y", "Estado Z", "12345678", null);

        when(enderecoRepository.findById(1L)).thenReturn(Optional.of(enderecoParaTeste));

        Endereco encontrado = enderecoService.findById(1L);

        assertNotNull(encontrado);
        assertEquals(1L, encontrado.getId());
        verify(enderecoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar endereço por ID inexistente")
    void findById_quandoInexistente_deveLancarExcecao() {
        when(enderecoRepository.findById(anyLong())).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> enderecoService.findById(99L));

        assertEquals("Endereço não encontrado", exception.getMessage());
        verify(enderecoRepository).findById(99L);
    }

    // --- Testes de Salvar ---
    @Test
    @DisplayName("Deve salvar endereço preenchendo campos nulos pela API")
    void save_quandoCamposNulos_devePreencherPelaApiESalvar() {

        // Endereço de entrada com CEP, mas campos vazios (numero como String)
        Endereco novoEndereco = new Endereco(null, null, "200", null, null, null, "12345678", null);

        // Simular a chamada ao ViaCEP
        String urlEsperada = "https://viacep.com.br/ws/12345678/json/";
        when(restTemplate.getForObject(eq(urlEsperada), eq(Endereco.class))).thenReturn(enderecoApiRetorno);

        // Simular o salvamento no repositório
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(i -> i.getArgument(0));

        // EXECUÇÃO
        Endereco enderecoSalvo = enderecoService.save(novoEndereco);

        // VERIFICAÇÕES
        assertNotNull(enderecoSalvo);
        assertEquals("Rua da API", enderecoSalvo.getRua());
        assertEquals("Bairro da API", enderecoSalvo.getBairro());

        assertEquals("200", enderecoSalvo.getNumero());

        verify(restTemplate).getForObject(eq(urlEsperada), eq(Endereco.class));
        verify(enderecoRepository).save(enderecoSalvo);
    }

    @Test
    @DisplayName("Deve salvar endereço sem chamar a API se o CEP for nulo")
    void save_quandoCepNulo_naoDeveChamarApi() {

        Endereco novoEndereco = new Endereco(null, "Rua A", "1", "Bairro B", "Cidade C", "Estado D", null, null);

        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(i -> i.getArgument(0));

        enderecoService.save(novoEndereco);

        // Deve salvar, mas NÃO deve chamar o restTemplate
        verify(restTemplate, never()).getForObject(anyString(), eq(Endereco.class));
        verify(enderecoRepository).save(novoEndereco);
    }

    // --- Teste de Listar Todos ---
    @Test
    @DisplayName("Deve retornar todos os endereços")
    void listAll_deveRetornarListaDeEnderecos() {
        // PREPARAÇÃO
        Endereco endereco1 = new Endereco(1L, "Rua A", "10", "Bairro X", "Cidade Y", "Estado Z", "12345678", null);
        Endereco endereco2 = new Endereco(2L, "Rua B", "20", "Bairro W", "Cidade V", "Estado U", "87654321", null);
        List<Endereco> listaMocks = Arrays.asList(endereco1, endereco2);

        when(enderecoRepository.findAll()).thenReturn(listaMocks);

        // EXECUÇÃO
        List<Endereco> resultado = enderecoService.listAll();

        // VERIFICAÇÕES
        assertNotNull(resultado);
        assertFalse(resultado.isEmpty());
        assertEquals(2, resultado.size());
        verify(enderecoRepository).findAll();
    }

    // --- Testes de Atualização (Update) ---
    @Test
    @DisplayName("Deve atualizar endereço com sucesso e preencher dados novos pela API")
    void update_deveAtualizarEPreencherDadosPelaApi() {
        // PREPARAÇÃO
        Long idExistente = 1L;

        Endereco enderecoExistente = new Endereco(1L, "Rua Antiga", "100", "Bairro Antigo", "Cidade Velha", "Estado Velho", "12345678", null);

        // Dados de entrada para atualização (apenas CEP e Novo Número)
        Endereco enderecoUpdateInput = new Endereco();
        enderecoUpdateInput.setCep("98765432");
        enderecoUpdateInput.setNumero("999");

        // Mock do retorno da API ViaCEP para o novo CEP
        Endereco novoEnderecoApiRetorno = new Endereco();
        novoEnderecoApiRetorno.setRua("Rua Nova da API");

        // Simula a busca do endereço existente
        when(enderecoRepository.findById(idExistente)).thenReturn(Optional.of(enderecoExistente));

        //Simula a chamada à API com o novo CEP
        String urlEsperada = "https://viacep.com.br/ws/98765432/json/";
        when(restTemplate.getForObject(eq(urlEsperada), eq(Endereco.class))).thenReturn(novoEnderecoApiRetorno);

        //Simula o salvamento
        when(enderecoRepository.save(any(Endereco.class))).thenAnswer(i -> i.getArgument(0));

        Endereco enderecoAtualizado = enderecoService.update(idExistente, enderecoUpdateInput);

        assertNotNull(enderecoAtualizado);
        assertEquals(idExistente, enderecoAtualizado.getId());

        // Verifica se o campo de entrada foi atualizado
        assertEquals("999", enderecoAtualizado.getNumero());

        // Verifica se o campo foi preenchido pela API
        assertEquals("Rua Nova da API", enderecoAtualizado.getRua());

        verify(enderecoRepository).findById(idExistente);
        verify(restTemplate).getForObject(eq(urlEsperada), eq(Endereco.class));
        verify(enderecoRepository).save(enderecoAtualizado);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar atualizar endereço inexistente")
    void update_quandoInexistente_deveLancarExcecao() {
        Long idInexistente = 99L;
        Endereco enderecoUpdate = new Endereco();

        when(enderecoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // EXECUÇÃO E VERIFICAÇÃO DE EXCEÇÃO
        assertThrows(RuntimeException.class, () -> enderecoService.update(idInexistente, enderecoUpdate));

        verify(enderecoRepository).findById(idInexistente);
        verify(enderecoRepository, never()).save(any(Endereco.class));
        verify(restTemplate, never()).getForObject(anyString(), eq(Endereco.class));
    }

    // --- Testes de Deletar ---
    @Test
    @DisplayName("Deve deletar endereço por ID com sucesso")
    void delete_quandoExistente_deveChamarDelete() {
        Long idExistente = 1L;
        Endereco enderecoExistente = new Endereco(1L, "Rua a deletar", "10", "Bairro X", "Cidade Y", "Estado Z", "12345678", null);

        // PREPARAÇÃO
        when(enderecoRepository.findById(idExistente)).thenReturn(Optional.of(enderecoExistente));
        doNothing().when(enderecoRepository).delete(any(Endereco.class));

        // EXECUÇÃO
        enderecoService.delete(idExistente);

        // VERIFICAÇÕES
        verify(enderecoRepository).findById(idExistente);
        verify(enderecoRepository).delete(enderecoExistente);
    }

    @Test
    @DisplayName("Deve lançar exceção ao tentar deletar endereço inexistente")
    void delete_quandoInexistente_deveLancarExcecao() {
        Long idInexistente = 99L;

        // PREPARAÇÃO
        when(enderecoRepository.findById(idInexistente)).thenReturn(Optional.empty());

        // EXECUÇÃO E VERIFICAÇÃO DE EXCEÇÃO
        assertThrows(RuntimeException.class, () -> enderecoService.delete(idInexistente));

        verify(enderecoRepository).findById(idInexistente);
        verify(enderecoRepository, never()).delete(any(Endereco.class));
    }
}