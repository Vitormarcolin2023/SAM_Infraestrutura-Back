package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.ProfessorService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.security.test.context.support.WithMockUser;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(ProfessorController.class)
@TestPropertySource(properties = {
        "MAIL_HOST=localhost",
        "MAIL_PORT=1025",
        "MAIL_USERNAME=usuario-teste",
        "MAIL_PASSWORD=senha-teste",
        "JWT_SECRET=segredo-teste-mock-muito-seguro-123456",
        "DB_URL=jdbc:h2:mem:testdb;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=FALSE",
        "DB_USERNAME=sa",
        "DB_PASSWORD=",
        "spring.datasource.driver-class-name=org.h2.Driver",
        "spring.jpa.database-platform=org.hibernate.dialect.H2Dialect"
})

@WithMockUser(username = "super_usuario", roles = {"ALUNO", "COORDENADOR", "PROFESSOR"})
class ProfessorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ProfessorService professorService;

    @MockitoBean
    private TokenService tokenService;

    private Professor professorValido;
    private final Long professorId = 1L;
    private final String email = "ana.souza@email.com";
    private List<Curso> cursosExemplo;

    @BeforeEach
    void setUp() {
        cursosExemplo = Arrays.asList(
                new Curso(10L, "ES", null),
                new Curso(20L, "AS", null)
        );
        professorValido = new Professor(professorId, "Ana Souza", email, "senha123", cursosExemplo);
    }

    @Test
    @DisplayName("Salvar: Deve retornar HTTP 200 OK com mensagem de sucesso.")
    void save_deveRetornarStatusOkEMensagemDeSucesso() throws Exception {
        String mensagemSucesso = "Professor salvo com sucesso!";
        when(professorService.save(any(Professor.class))).thenReturn(mensagemSucesso);

        mockMvc.perform(post("/api/professor/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorValido)))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(professorService, times(1)).save(any(Professor.class));
    }

    @Test
    @DisplayName("Atualizar: Deve retornar HTTP 200 OK com mensagem de sucesso ao atualizar professor existente.")
    void update_quandoProfessorExiste_deveRetornarStatusOkEMensagemDeSucesso() throws Exception {
        String mensagemSucesso = "Professor atualizado com sucesso!";
        when(professorService.update(any(Professor.class), eq(professorId))).thenReturn(mensagemSucesso);

        mockMvc.perform(put("/api/professor/update/{id}", professorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(professorValido)))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(professorService, times(1)).update(any(Professor.class), eq(professorId));
    }

    @Test
    @DisplayName("Deletar: Deve retornar HTTP 200 OK com mensagem de sucesso ao deletar.")
    void delete_deveRetornarStatusOkEMensagemDeSucesso() throws Exception {
        String mensagemSucesso = "Professor deletado com sucesso!";
        when(professorService.delete(eq(professorId))).thenReturn(mensagemSucesso);

        mockMvc.perform(delete("/api/professor/delete/{id}", professorId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(mensagemSucesso));

        verify(professorService, times(1)).delete(eq(professorId));
    }

    @Test
    @DisplayName("Buscar Todos: Deve retornar HTTP 200 OK com a lista de todos os professores.")
    void findAll_deveRetornarListaDeProfessores() throws Exception {
        List<Professor> lista = Arrays.asList(professorValido, new Professor());
        when(professorService.findAll()).thenReturn(lista);

        mockMvc.perform(get("/api/professor/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].email").value(email));

        verify(professorService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar Todos: Deve retornar HTTP 200 OK com lista vazia se não houver professores.")
    void findAll_quandoVazio_deveRetornarListaVazia() throws Exception {
        List<Professor> listaVazia = Collections.emptyList();
        when(professorService.findAll()).thenReturn(listaVazia);

        mockMvc.perform(get("/api/professor/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0))
                .andExpect(jsonPath("$").isArray());

        verify(professorService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar Mentores: Deve retornar HTTP 200 OK com a lista de mentores.")
    void findAllMentores_deveRetornarStatusOkComLista() throws Exception {
        List<Mentor> mentoresMock = List.of(new Mentor());
        when(professorService.findAllMentores()).thenReturn(mentoresMock);

        mockMvc.perform(get("/api/professor/mentores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(professorService, times(1)).findAllMentores();
    }

    @Test
    @DisplayName("Buscar Projetos: Deve retornar HTTP 200 OK com a lista de projetos.")
    void findAllProjetos_deveRetornarStatusOkComLista() throws Exception {
        List<Projeto> projetosMock = List.of(new Projeto());
        when(professorService.findAllProjetos()).thenReturn(projetosMock);

        mockMvc.perform(get("/api/professor/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1));

        verify(professorService, times(1)).findAllProjetos();
    }

    @Test
    @DisplayName("Buscar por Email: Deve retornar HTTP 200 OK e o objeto Professor quando o email é encontrado.")
    void getProfessorPorEmail_quandoEmailExiste_deveRetornarProfessor() throws Exception {
        when(professorService.findByEmail(eq(email))).thenReturn(professorValido);

        mockMvc.perform(get("/api/professor/buscar-por-email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(professorService, times(1)).findByEmail(eq(email));
    }

    @Test
    @DisplayName("Buscar por Email: Deve retornar HTTP 404 NOT FOUND quando o Service retorna null.")
    void getProfessorPorEmail_quandoEmailNaoExiste_deveRetornarStatusNotFound() throws Exception {
        String emailInvalido = "naoexiste@email.com";
        when(professorService.findByEmail(eq(emailInvalido))).thenReturn(null);

        mockMvc.perform(get("/api/professor/buscar-por-email").param("email", emailInvalido))
                .andExpect(status().isNotFound());

        verify(professorService, times(1)).findByEmail(eq(emailInvalido));
    }

    @Test
    @DisplayName("Buscar por ID: Deve retornar HTTP 200 OK e o objeto Professor quando o ID é encontrado.")
    void getProfessorPorId_quandoIdExiste_deveRetornarStatusOkEProfessor() throws Exception {
        when(professorService.findById(eq(professorId))).thenReturn(professorValido);

        mockMvc.perform(get("/api/professor/getById/{id}", professorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(professorId));

        verify(professorService, times(1)).findById(eq(professorId));
    }

    @Test
    @DisplayName("Buscar por ID: Deve retornar HTTP 404 NOT FOUND quando o ID não é encontrado.")
    void getProfessorPorId_quandoIdNaoExiste_deveRetornarStatusNotFound() throws Exception {
        Long idInvalido = 99L;
        when(professorService.findById(eq(idInvalido))).thenReturn(null);

        mockMvc.perform(get("/api/professor/getById/{id}", idInvalido))
                .andExpect(status().isNotFound());

        verify(professorService, times(1)).findById(eq(idInvalido));
    }
}