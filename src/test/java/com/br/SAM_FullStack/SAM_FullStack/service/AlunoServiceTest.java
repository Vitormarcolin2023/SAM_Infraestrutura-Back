package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes Unitários do AlunoService")
class AlunoServiceTest {

    @Mock
    AlunoRepository alunoRepository;

    AlunoService alunoService;

    @Mock
    EmailService emailService;
    @Mock
    PasswordEncoder passwordEncoder;
    @Mock
    TokenService tokenService;

    Aluno aluno;
    Curso mockCurso;

    @BeforeEach
    void setUp() {
        alunoService = new AlunoService(alunoRepository);

        ReflectionTestUtils.setField(alunoService, "emailService", emailService);
        ReflectionTestUtils.setField(alunoService, "passwordEncoder", passwordEncoder);
        ReflectionTestUtils.setField(alunoService, "tokenService", tokenService);

        mockCurso = new Curso();
        mockCurso.setId(1L);
        mockCurso.setNome("Eng Teste");

        aluno = new Aluno();
        aluno.setId(1L);
        aluno.setNome("Aluno Teste");
        aluno.setEmail("teste@email.com");
        aluno.setRa(12345);
        aluno.setSenha("senha123"); // Senha não encriptada para teste
        aluno.setCurso(mockCurso);
    }


    @Test
    @DisplayName("Deve retornar aluno ao buscar por ID existente")
    void findById_quandoIdExistente_deveRetornarAluno() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        Aluno alunoEncontrado = alunoService.findById(1L);
        assertNotNull(alunoEncontrado);
        assertEquals(1L, alunoEncontrado.getId());
        verify(alunoRepository).findById(1L);
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por ID inexistente")
    void findById_quandoIdNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findById(anyLong())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.findById(99L));
        assertEquals("Aluno não encontrado com ID: 99", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar aluno ao buscar por RA existente")
    void findByRa_quandoRaExistente_deveRetornarAluno() {
        when(alunoRepository.findByRa(12345)).thenReturn(Optional.of(aluno));
        Aluno alunoEncontrado = alunoService.findByRa(12345);
        assertNotNull(alunoEncontrado);
        assertEquals(12345, alunoEncontrado.getRa());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por RA inexistente")
    void findByRa_quandoRaNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findByRa(anyInt())).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.findByRa(99999));
        assertEquals("Aluno não encontrado com RA: 99999", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista de todos os alunos")
    void findAll_deveRetornarListaDeAlunos() {
        List<Aluno> alunos = Arrays.asList(aluno, new Aluno());
        when(alunoRepository.findAll()).thenReturn(alunos);
        List<Aluno> resultado = alunoService.findAll();
        assertNotNull(resultado);
        assertEquals(2, resultado.size());
    }


    @Test
    @DisplayName("Deve salvar aluno com RA não existente e enviar email")
    void save_quandoRaNaoExistente_deveSalvarEEnviarEmail() {
        when(alunoRepository.findByRa(aluno.getRa())).thenReturn(Optional.empty());
        when(passwordEncoder.encode(aluno.getSenha())).thenReturn("senhaEncriptada");
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> {
            Aluno a = invocation.getArgument(0);
            a.setId(1L);
            return a;
        });
        when(emailService.enviarEmailTexto(anyString(), anyString(), anyString()))
                .thenReturn("Email Enviado - Mock");

        Aluno alunoSalvo = alunoService.save(aluno);

        assertNotNull(alunoSalvo);
        assertEquals("senhaEncriptada", alunoSalvo.getSenha());
        verify(alunoRepository).findByRa(aluno.getRa());
        verify(passwordEncoder).encode("senha123"); // Verifica se o encoder foi chamado
        verify(alunoRepository).save(aluno);
        verify(emailService).enviarEmailTexto( // Verifica se o email foi chamado
                eq(aluno.getEmail()),
                eq("Aluno Cadastrado com Sucesso"),
                contains("Olá Aluno Teste")
        );
    }


    @Test
    @DisplayName("Deve lançar exceção ao salvar aluno com RA existente")
    void save_quandoRaExistente_deveLancarRuntimeException() {
        Aluno alunoExistente = new Aluno();
        when(alunoRepository.findByRa(aluno.getRa())).thenReturn(Optional.of(alunoExistente));
        when(emailService.enviarEmailTexto(anyString(), anyString(), anyString()))
                .thenReturn("Email Enviado - Mock Falha");
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.save(aluno));

        assertEquals("Aluno com RA 12345 já está cadastrado!", exception.getMessage());

        verify(emailService).enviarEmailTexto(
                eq(aluno.getEmail()),
                eq("Falha no Cadastro: RA já existente"),
                contains("Olá Aluno Teste")
        );
        verify(alunoRepository, never()).save(any(Aluno.class)); // Não deve salvar
        verify(passwordEncoder, never()).encode(anyString()); // Não deve encriptar
    }

    @Test
    @DisplayName("Deve atualizar aluno com sucesso sem alterar email e sem nova senha")
    void update_quandoSucessoSemAlterarEmailESenha_deveRetornarAlunoAtualizado() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setNome("Novo Nome");
        alunoUpdate.setEmail("teste@email.com");
        alunoUpdate.setRa(54321);
        alunoUpdate.setSenha(null);

        String originalEncodedPassword = "encodedOriginalPassword";
        aluno.setSenha(originalEncodedPassword);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aluno alunoAtualizado = alunoService.update(1L, alunoUpdate);

        assertNotNull(alunoAtualizado);
        assertEquals("Novo Nome", alunoAtualizado.getNome());
        assertEquals(54321, alunoAtualizado.getRa());
        assertEquals("teste@email.com", alunoAtualizado.getEmail());
        assertEquals(originalEncodedPassword, alunoAtualizado.getSenha()); // Manteve a senha original
        verify(alunoRepository).findById(1L);
        verify(alunoRepository, never()).findByEmail(anyString()); // Não deve checar email
        verify(passwordEncoder, never()).encode(anyString()); // Não deve encriptar
        verify(alunoRepository).save(aluno);
    }

    @Test
    @DisplayName("Deve atualizar aluno com nova senha")
    void update_quandoSenhaNova_deveEncriptarESalvar() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setNome("Aluno Teste");
        alunoUpdate.setEmail("teste@email.com");
        alunoUpdate.setRa(12345);
        alunoUpdate.setSenha("novaSenha123"); // Nova senha

        aluno.setSenha("senhaAntigaEncriptada");

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        // Mock do PasswordEncoder (ESSENCIAL)
        when(passwordEncoder.encode("novaSenha123")).thenReturn("novaSenhaEncriptada");
        when(alunoRepository.save(any(Aluno.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Aluno alunoAtualizado = alunoService.update(1L, alunoUpdate);

        assertNotNull(alunoAtualizado);
        assertEquals("novaSenhaEncriptada", alunoAtualizado.getSenha()); // Verifica se a senha foi atualizada
        verify(alunoRepository).findById(1L);
        verify(passwordEncoder).encode("novaSenha123"); // Verifica se o encoder foi chamado
        verify(alunoRepository).save(aluno);
    }


    @Test
    @DisplayName("Deve atualizar aluno com email alterado e disponível")
    void update_quandoEmailAlteradoEDisponivel_deveRetornarAlunoAtualizado() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setNome("Novo Nome");
        alunoUpdate.setEmail("novoemail@email.com"); // Email alterado
        alunoUpdate.setSenha(null);

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("novoemail@email.com")).thenReturn(Optional.empty()); // Email está disponível
        when(alunoRepository.save(any(Aluno.class))).thenReturn(aluno);

        Aluno alunoAtualizado = alunoService.update(1L, alunoUpdate);

        assertNotNull(alunoAtualizado);
        assertEquals("novoemail@email.com", alunoAtualizado.getEmail());
        verify(alunoRepository).findByEmail("novoemail@email.com"); // Deve checar o email
        verify(alunoRepository).save(aluno);
        verify(passwordEncoder, never()).encode(anyString());
    }

    @Test
    @DisplayName("Deve lançar exceção ao atualizar aluno com e-mail que já existe")
    void update_quandoEmailAlteradoEJaExiste_deveLancarRuntimeException() {
        Aluno alunoUpdate = new Aluno();
        alunoUpdate.setEmail("emailjaexistente@email.com"); // Email alterado
        alunoUpdate.setSenha(null);

        Aluno outroAluno = new Aluno();
        outroAluno.setId(2L); // É outro aluno

        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        when(alunoRepository.findByEmail("emailjaexistente@email.com")).thenReturn(Optional.of(outroAluno)); // Email já existe

        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.update(1L, alunoUpdate));

        assertEquals("O e-mail 'emailjaexistente@email.com' já está cadastrado.", exception.getMessage());
        verify(alunoRepository, never()).save(any(Aluno.class)); // Não deve salvar
    }

    @Test
    @DisplayName("Deve deletar aluno com sucesso")
    void delete_quandoIdExistente_deveChamarDelete() {
        when(alunoRepository.findById(1L)).thenReturn(Optional.of(aluno));
        doNothing().when(alunoRepository).delete(aluno); // Mock para método void

        alunoService.delete(1L);

        verify(alunoRepository).delete(aluno); // Verifica se o delete foi chamado
    }

    @Test
    @DisplayName("Deve lançar exceção ao deletar aluno inexistente")
    void delete_quandoIdNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> alunoService.delete(99L));

        verify(alunoRepository, never()).delete(any(Aluno.class)); // Não deve chamar o delete
    }


    @Test
    @DisplayName("Deve salvar lista de alunos")
    void saveAll_deveSalvarTodosAlunos() {
        // --- Aluno 1 ---
        Aluno aluno1 = new Aluno();
        aluno1.setRa(111);
        aluno1.setEmail("a1@email.com");
        aluno1.setSenha("senha1");
        aluno1.setCurso(mockCurso);

        // --- Aluno 2 ---
        Aluno aluno2 = new Aluno();
        aluno2.setRa(222);
        aluno2.setEmail("a2@email.com");
        aluno2.setSenha("senha2");
        aluno2.setCurso(mockCurso);

        List<Aluno> alunosParaSalvar = Arrays.asList(aluno1, aluno2);

        // --- Mocks Aluno 1 ---
        when(alunoRepository.findByRa(111)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha1")).thenReturn("enc1");
        when(alunoRepository.save(aluno1)).thenAnswer(inv -> { inv.getArgument(0, Aluno.class).setId(10L); return aluno1; });

        // --- Mocks Aluno 2 ---
        when(alunoRepository.findByRa(222)).thenReturn(Optional.empty());
        when(passwordEncoder.encode("senha2")).thenReturn("enc2");
        when(alunoRepository.save(aluno2)).thenAnswer(inv -> { inv.getArgument(0, Aluno.class).setId(11L); return aluno2; });

        // Mock do EmailService (genérico)
        when(emailService.enviarEmailTexto(anyString(), eq("Aluno Cadastrado com Sucesso"), anyString()))
                .thenReturn("Email Enviado - Mock");

        // --- Execução ---
        List<Aluno> alunosSalvos = alunoService.saveAll(alunosParaSalvar);

        // --- Verificação ---
        assertNotNull(alunosSalvos);
        assertEquals(2, alunosSalvos.size());

        // Verifica chamadas para Aluno 1
        verify(alunoRepository).findByRa(111);
        verify(passwordEncoder).encode("senha1");
        verify(alunoRepository).save(aluno1);
        verify(emailService).enviarEmailTexto(eq("a1@email.com"), eq("Aluno Cadastrado com Sucesso"), anyString());

        // Verifica chamadas para Aluno 2
        verify(alunoRepository).findByRa(222);
        verify(passwordEncoder).encode("senha2");
        verify(alunoRepository).save(aluno2);
        verify(emailService).enviarEmailTexto(eq("a2@email.com"), eq("Aluno Cadastrado com Sucesso"), anyString());

        // Verifica totais
        verify(passwordEncoder, times(2)).encode(anyString());
        verify(alunoRepository, times(2)).save(any(Aluno.class));
        verify(emailService, times(2)).enviarEmailTexto(anyString(), eq("Aluno Cadastrado com Sucesso"), anyString());
    }


    @Test
    @DisplayName("Deve retornar alunos ao buscar por nome")
    void buscarPorNome_deveRetornarListaDeAlunos() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findByNomeContainingIgnoreCase("Teste")).thenReturn(alunos);
        List<Aluno> resultado = alunoService.buscarPorNome("Teste");
        assertFalse(resultado.isEmpty());
        assertEquals("Aluno Teste", resultado.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar alunos ordenados por nome")
    void buscarTodosOrdenadoPorNome_deveRetornarListaOrdenada() {
        List<Aluno> alunos = Arrays.asList(aluno);
        when(alunoRepository.findAllByOrderByNomeAsc()).thenReturn(alunos);
        List<Aluno> resultado = alunoService.buscarTodosOrdenadoPorNome();
        assertFalse(resultado.isEmpty());
    }

    @Test
    @DisplayName("Deve retornar aluno ao buscar por e-mail existente")
    void findByEmail_quandoEmailExistente_deveRetornarAluno() {
        when(alunoRepository.findByEmail("teste@email.com")).thenReturn(Optional.of(aluno));
        Aluno alunoEncontrado = alunoService.findByEmail("teste@email.com");
        assertNotNull(alunoEncontrado);
        assertEquals("teste@email.com", alunoEncontrado.getEmail());
    }

    @Test
    @DisplayName("Deve lançar exceção ao buscar por e-mail inexistente")
    void findByEmail_quandoEmailNaoExistente_deveLancarRuntimeException() {
        when(alunoRepository.findByEmail("naoexiste@email.com")).thenReturn(Optional.empty());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> {
            alunoService.findByEmail("naoexiste@email.com");
        });
        assertEquals("Aluno não encontrado com o E-mail: naoexiste@email.com", exception.getMessage());
    }

    @Test
    @DisplayName("Deve retornar lista de alunos ao buscar por curso")
    void findByCurso_quandoExistemAlunos_deveRetornarLista() {
        Long cursoId = 1L;
        when(alunoRepository.findByCursoId(cursoId)).thenReturn(Arrays.asList(aluno));
        List<Aluno> resultado = alunoService.findByCurso(cursoId);
        assertFalse(resultado.isEmpty());
        assertEquals(1, resultado.size());
        assertEquals(aluno.getId(), resultado.get(0).getId());
    }

    @Test
    @DisplayName("Deve lançar exceção quando nenhum aluno encontrado no curso")
    void findByCurso_quandoNenhumAlunoNoCurso_deveLancarRuntimeException() {
        Long cursoId = 99L;
        when(alunoRepository.findByCursoId(cursoId)).thenReturn(Collections.emptyList());
        RuntimeException exception = assertThrows(RuntimeException.class, () -> alunoService.findByCurso(cursoId));
        assertEquals("Nenhum aluno encontrado nesse curso", exception.getMessage());
    }
}