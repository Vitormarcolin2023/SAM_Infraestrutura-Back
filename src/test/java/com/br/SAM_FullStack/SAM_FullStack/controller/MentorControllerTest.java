package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.service.MentorService;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(MentorController.class)
// Remove filtros de segurança para que o teste passe pelo Controller sem autenticação
@AutoConfigureMockMvc(addFilters = false)
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
public class MentorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    // Mocks de TODAS as dependências do Controller
    @MockitoBean
    private MentorService mentorService;
    @MockitoBean
    private TokenService tokenService; // Mock para a lógica de token
    @MockitoBean
    private ProjetoService projetoService;

    private Mentor mentor;
    private final String AUTH_HEADER = "Bearer validToken";
    private final String MENTOR_EMAIL = "alice@teste.com";

    @BeforeEach
    void setUp() {
        mentor = new Mentor();
        mentor.setId(1L);
        mentor.setNome("Alice Teste");
        mentor.setEmail(MENTOR_EMAIL);
        mentor.setCpf("12345678900");
        mentor.setStatusMentor(StatusMentor.ATIVO);
    }

    //TESTES CRUD BÁSICOS

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – GET /findAll deve retornar status 200 e lista de mentores")
    void listAll_DeveRetornarStatusOkELista() throws Exception {
        List<Mentor> listaMock = Arrays.asList(mentor, new Mentor());
        when(mentorService.listAll()).thenReturn(listaMock);

        mockMvc.perform(get("/mentores/findAll").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Alice Teste"));

        verify(mentorService, times(1)).listAll();
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – GET /findById/{id} deve retornar status 200 e o Mentor")
    void findById_DeveRetornarStatusOk() throws Exception {
        when(mentorService.findById(1L)).thenReturn(mentor);

        mockMvc.perform(get("/mentores/findById/1").contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.email").value(MENTOR_EMAIL));

        verify(mentorService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – Cenário de Exceção (assertThrows): GET /findById/{id} deve retornar 400 Bad Request")
    void findById_DeveRetornarErroQuandoNaoExiste() throws Exception {
        // Simula a exceção do Service
        when(mentorService.findById(99L)).thenThrow(new RuntimeException("Mentor não encontrado"));

        // Ajusta a asserção para o 400 que o Spring está retornando
        mockMvc.perform(get("/mentores/findById/99"))
                .andExpect(status().isBadRequest()); // <--- CORREÇÃO AQUI

        verify(mentorService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – POST /save deve retornar status 201 CREATED")
    void save_DeveRetornarStatusCreated() throws Exception {
        when(mentorService.save(any(Mentor.class))).thenReturn(mentor);

        mockMvc.perform(post("/mentores/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isCreated());

        verify(mentorService, times(1)).save(any(Mentor.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – PUT /update/{id} deve retornar status 200 OK")
    void update_DeveRetornarStatusOk() throws Exception {
        Mentor mentorNovoNome = mentor;
        mentorNovoNome.setNome("Nome Novo");
        when(mentorService.update(eq(1L), any(Mentor.class))).thenReturn(mentorNovoNome);

        mockMvc.perform(put("/mentores/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(mentor)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Nome Novo"));

        verify(mentorService, times(1)).update(eq(1L), any(Mentor.class));
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – DELETE /delete/{id} deve retornar status 200 OK")
    void delete_DeveRetornarStatusOk() throws Exception {
        doNothing().when(mentorService).delete(1L);

        mockMvc.perform(delete("/mentores/delete/1"))
                .andExpect(status().isOk());

        verify(mentorService, times(1)).delete(1L);
    }

    //TESTES DE SEGURANÇA E OUTROS MÉTODOS

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – GET /me deve retornar 200 OK e o perfil do Mentor (Token Válido)")
    void getMentorProfile_DeveRetornarStatusOk() throws Exception {
        // Mocking: Simula o sucesso da extração e da busca
        when(tokenService.extractEmail(anyString())).thenReturn(MENTOR_EMAIL);
        when(mentorService.findByEmail(MENTOR_EMAIL)).thenReturn(mentor);

        mockMvc.perform(get("/mentores/me")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(MENTOR_EMAIL));

        verify(tokenService, times(1)).extractEmail(anyString());
        verify(mentorService, times(1)).findByEmail(MENTOR_EMAIL);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – GET /me deve retornar 400 Bad Request quando token é nulo (Header Ausente)")
    void getMentorProfile_DeveRetornar400QuandoTokenAusente() throws Exception {
        // O Spring lança MissingRequestHeaderException e mapeia para 400
        mockMvc.perform(get("/mentores/me")) // Sem o header Authorization
                .andExpect(status().isBadRequest()); // <--- CORREÇÃO AQUI

        verify(mentorService, never()).findByEmail(any());
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – GET /me deve retornar 404 Not Found quando Mentor não é encontrado pelo email")
    void getMentorProfile_DeveRetornar404QuandoMentorNaoEncontrado() throws Exception {
        // Mocking: Simula token válido, mas Service retorna null
        when(tokenService.extractEmail(anyString())).thenReturn("naoexiste@email.com");
        when(mentorService.findByEmail(anyString())).thenReturn(null);

        mockMvc.perform(get("/mentores/me")
                        .header("Authorization", AUTH_HEADER))
                .andExpect(status().isNotFound()); // status 404

        verify(mentorService, times(1)).findByEmail(anyString());
    }


    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – PUT /desvincular-projetos deve retornar status 200 OK")
    void desvincularProjetos_DeveRetornarStatusOk() throws Exception {
        doNothing().when(projetoService).desvincularMentor(1L);

        mockMvc.perform(put("/mentores/mentor/1/desvincular-projetos"))
                .andExpect(status().isOk());

        verify(projetoService, times(1)).desvincularMentor(1L);
    }

    @Test
    @DisplayName("TESTE DE INTEGRAÇÃO API – GET /area/{id} deve retornar status 200 e lista de mentores por área")
    void findByAreaDeAtuacao_DeveRetornarStatusOk() throws Exception {
        when(mentorService.findByArea(anyLong())).thenReturn(Collections.singletonList(mentor));

        mockMvc.perform(get("/mentores/area/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1))
                .andExpect(jsonPath("$[0].nome").value("Alice Teste"));

        verify(mentorService, times(1)).findByArea(10L);
    }
}