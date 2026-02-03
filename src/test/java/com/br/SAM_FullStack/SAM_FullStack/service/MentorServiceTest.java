package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.model.TipoDeVinculo;
import com.br.SAM_FullStack.SAM_FullStack.repository.MentorRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class MentorServiceTest {

    @Mock
    private MentorRepository mentorRepository;

    @Mock
    private EmailService emailService;

    @Mock
    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private MentorService mentorService;

    private Mentor mentor;
    private AreaDeAtuacao area;

    @BeforeEach
    void setUp() {
        area = new AreaDeAtuacao(1L, "Arquitetura");

        mentor = new Mentor();
        mentor.setId(1L);
        mentor.setNome("João Teste");
        mentor.setCpf("12345678900");
        mentor.setEmail("joao@teste.com");
        mentor.setSenha("senha123");
        mentor.setTelefone("999999999");
        mentor.setTempoDeExperiencia("5 anos");
        mentor.setStatusMentor(StatusMentor.ATIVO);
        mentor.setTipoDeVinculo(TipoDeVinculo.CLT);
        mentor.setAreaDeAtuacao(area);
        mentor.setResumo("Resumo Teste");
        mentor.setFormacaoDoMentor("Bacharelado");

        // Injeção de dependências dos mocks
        ReflectionTestUtils.setField(mentorService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(mentorService, "emailService", emailService);
    }

    // --- TESTES DE LISTAGEM E BUSCA ---

    @Test
    @DisplayName("Listagem de todos os Mentores")
    void listAll_DeveRetornarListaDeMentores() {
        Mentor mentor2 = new Mentor();
        List<Mentor> listaEsperada = Arrays.asList(mentor, mentor2);
        when(mentorRepository.findAll()).thenReturn(listaEsperada);

        List<Mentor> listaAtual = mentorService.listAll();

        assertFalse(listaAtual.isEmpty());
        assertEquals(2, listaAtual.size());
        verify(mentorRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("Busca de Mentor por ID deve retornar sucesso")
    void findById_DeveRetornarMentorQuandoEncontrado() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        Mentor mentorEncontrado = mentorService.findById(mentor.getId());

        assertNotNull(mentorEncontrado);
        assertEquals(mentor.getNome(), mentorEncontrado.getNome());
        verify(mentorRepository, times(1)).findById(mentor.getId());
    }

    @Test
    @DisplayName("Busca de Mentor por ID inexistente deve lançar exceção")
    void findById_MentorNaoEncontrado_DeveLancarExcecao() {
        when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            mentorService.findById(99L);
        });

        assertEquals("Mentor não encontrado", exception.getMessage());
    }

    // --- TESTES DE CADASTRO (SAVE) ---

    @Test
    @DisplayName("Salvar Mentor deve setar status PENDENTE, encriptar senha e tentar enviar email")
    void save_DeveAplicarRegrasDeNegocio_ComSuccesso() {
        // Arrange
        Mentor novoMentor = new Mentor();
        novoMentor.setNome("Maria");
        novoMentor.setEmail("maria@teste.com");
        novoMentor.setSenha("senhaNova123");

        String senhaEncriptada = "$2a$10$HASHENCRIPTADO";

        // Mocks
        when(passwordEncoder.encode("senhaNova123")).thenReturn(senhaEncriptada);
        // O save deve retornar o objeto passado (ou uma cópia modificada)
        when(mentorRepository.save(any(Mentor.class))).thenAnswer(i -> i.getArguments()[0]);

        // Act
        Mentor mentorSalvo = mentorService.save(novoMentor);

        // Assert
        assertEquals(StatusMentor.PENDENTE, mentorSalvo.getStatusMentor(), "Status inicial deve ser PENDENTE");
        assertEquals(senhaEncriptada, mentorSalvo.getSenha(), "A senha deve ser encriptada");

        // Verify
        verify(passwordEncoder).encode("senhaNova123");
        verify(mentorRepository).save(novoMentor);

        // Verifica se tentou enviar o e-mail (mesmo dentro do try-catch)
        verify(emailService).enviarEmailComTemplate(
                eq("maria@teste.com"),
                contains("Bem-vindo"),
                eq("emails/boasVindasMentor"),
                anyMap()
        );
    }

    // --- TESTES DE UPDATE ---

    @Test
    @DisplayName("Update Mentor deve atualizar apenas campos não nulos")
    void update_DeveAtualizarApenasCamposPreenchidos() {
        // Arrange
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));
        when(mentorRepository.save(any(Mentor.class))).thenReturn(mentor);

        // Objeto de update com apenas alguns campos preenchidos
        Mentor mentorUpdate = new Mentor();
        mentorUpdate.setNome("Nome Atualizado");
        mentorUpdate.setResumo(null); // Campo nulo não deve apagar o resumo existente

        // Act
        Mentor mentorResultado = mentorService.update(mentor.getId(), mentorUpdate);

        // Assert
        assertEquals("Nome Atualizado", mentorResultado.getNome());
        assertEquals("Resumo Teste", mentorResultado.getResumo(), "O resumo original não deveria ser alterado por um nulo");

        verify(mentorRepository).save(mentor);
    }

    @Test
    @DisplayName("Update Status deve aceitar string, converter e salvar")
    void updateStatus_DeveMudarStatusDoMentor() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        // Testando com letras minúsculas para validar o toUpperCase()
        String resultado = mentorService.updateStatus(mentor.getId(), "inativo");

        assertEquals("Status do mentor atualizado com sucesso!", resultado);
        assertEquals(StatusMentor.INATIVO, mentor.getStatusMentor());
        verify(mentorRepository).save(mentor);
    }

    @Test
    @DisplayName("Update Status deve lançar exceção se mentor não existe")
    void updateStatus_DeveLancarExcecao_SeIdInvalido() {
        when(mentorRepository.findById(99L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () ->
                mentorService.updateStatus(99L, "ATIVO")
        );

        assertEquals("Mentor não encontrado.", ex.getMessage());
        verify(mentorRepository, never()).save(any());
    }

    // --- TESTES DE DELETE ---

    @Test
    @DisplayName("Delete Mentor com sucesso")
    void delete_DeveDeletarMentorQuandoEncontrado() {
        when(mentorRepository.findById(mentor.getId())).thenReturn(Optional.of(mentor));

        mentorService.delete(mentor.getId());

        verify(mentorRepository).delete(mentor);
    }

    // --- TESTES DE CONSULTAS ESPECÍFICAS ---

    @Test
    @DisplayName("Busca por Email deve retornar Mentor")
    void findByEmail_DeveRetornarMentor() {
        when(mentorRepository.findByEmail(mentor.getEmail())).thenReturn(Optional.of(mentor));

        Mentor resultado = mentorService.findByEmail(mentor.getEmail());

        assertNotNull(resultado);
        assertEquals(mentor.getEmail(), resultado.getEmail());
    }

    @Test
    @DisplayName("Busca por Email inexistente deve retornar null")
    void findByEmail_DeveRetornarNull() {
        when(mentorRepository.findByEmail("inexistente@email.com")).thenReturn(Optional.empty());

        Mentor resultado = mentorService.findByEmail("inexistente@email.com");

        assertNull(resultado);
    }

    @Test
    @DisplayName("Busca por Área deve retornar lista de Mentores ATIVOS")
    void findByArea_DeveRetornarListaFiltrada() {
        List<Mentor> listaAtivos = Collections.singletonList(mentor);
        when(mentorRepository.findByAreaDeAtuacaoIdAndStatusMentor(area.getId(), StatusMentor.ATIVO))
                .thenReturn(listaAtivos);

        List<Mentor> resultado = mentorService.findByArea(area.getId());

        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        verify(mentorRepository).findByAreaDeAtuacaoIdAndStatusMentor(area.getId(), StatusMentor.ATIVO);
    }
}