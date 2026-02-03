package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.CoordenadorService;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;

@WebMvcTest(CoordenadorController.class)
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
class CoordenadorControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CoordenadorService coordenadorService;

    @MockitoBean
    private TokenService tokenService;

    private CoordenadorDTO coordenadorDTO;
    private CoordenadorUpdateDTO coordenadorUpdateDTO;
    private Coordenador coordenador;
    private final Long coordenadorId = 1L;
    private final String email = "test@example.com";

    @BeforeEach
    void setUp() {
        coordenadorDTO = new CoordenadorDTO();
        coordenadorDTO.setNome("Nome Teste");
        coordenadorDTO.setEmail(email);
        coordenadorDTO.setSenha("senha123");
        coordenadorDTO.setCursosIds(Arrays.asList(10L, 20L));

        coordenadorUpdateDTO = new CoordenadorUpdateDTO();
        coordenadorUpdateDTO.setNome("Nome Atualizado");
        coordenadorUpdateDTO.setEmail("novo_email@example.com");
        coordenadorUpdateDTO.setSenha("novasenha123");
        coordenadorUpdateDTO.setCursosIds(Arrays.asList(30L));

        coordenador = new Coordenador();
        coordenador.setId(coordenadorId);
        coordenador.setNome("Nome Teste");
        coordenador.setEmail(email);
        coordenador.setSenha("senha123");
        coordenador.setCursos(new ArrayList<>());
    }

    @Test
    @DisplayName("Salvar: deve retornar HTTP 201 CREATED e o objeto Coordenador salvo")
    void save_deveRetornarCreatedCoordenador() throws Exception {
        when(coordenadorService.save(any(CoordenadorDTO.class))).thenReturn(coordenador);

        mockMvc.perform(post("/api/coordenador/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coordenadorDTO)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.email").value(coordenador.getEmail()));

        verify(coordenadorService, times(1)).save(any(CoordenadorDTO.class));
    }

    @Test
    @DisplayName("Atualizar: deve retornar HTTP 200 OK com mensagem de sucesso")
    void update_deveRetornarOkComMensagem() throws Exception {
        String successMessage = "Coordenador atualizado com sucesso.";
        when(coordenadorService.update(any(CoordenadorUpdateDTO.class), eq(coordenadorId))).thenReturn(successMessage);

        mockMvc.perform(put("/api/coordenador/update/{id}", coordenadorId)
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(coordenadorUpdateDTO)))
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));

        verify(coordenadorService, times(1)).update(any(CoordenadorUpdateDTO.class), eq(coordenadorId));
    }

    @Test
    @DisplayName("Deletar: deve retornar HTTP 200 OK com mensagem de sucesso")
    void delete_deveRetornarOkComMensagem() throws Exception {
        // O Service real não retorna String, mas o Controller retorna 200 OK com mensagem
        doNothing().when(coordenadorService).delete(coordenadorId);

        mockMvc.perform(delete("/api/coordenador/delete/{id}", coordenadorId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Coordenador excluído com sucesso"));

        verify(coordenadorService, times(1)).delete(coordenadorId);
    }

    @Test
    @DisplayName("Ativar Mentor: deve retornar HTTP 200 OK com mensagem de ativação")
    void ativarMentor_deveRetornarOkComMensagem() throws Exception {
        String message = "Mentor ativado.";
        when(coordenadorService.ativarMentor(coordenadorId)).thenReturn(message);

        mockMvc.perform(put("/api/coordenador/ativarMentor/{id}", coordenadorId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(message));

        verify(coordenadorService, times(1)).ativarMentor(coordenadorId);
    }

    @Test
    @DisplayName("Inativar Mentor: deve retornar HTTP 200 OK com mensagem de inativação")
    void inativarMentor_deveRetornarOkComMensagem() throws Exception {
        String message = "Mentor inativado.";
        when(coordenadorService.inativarMentor(coordenadorId)).thenReturn(message);

        mockMvc.perform(put("/api/coordenador/inativarMentor/{id}", coordenadorId)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string(message));

        verify(coordenadorService, times(1)).inativarMentor(coordenadorId);
    }

    @Test
    @DisplayName("Buscar todos os Mentores: deve retornar HTTP 200 OK com a lista de Mentores")
    void findAllMentores_deveRetornarOkComLista() throws Exception {
        List<Mentor> mentores = Arrays.asList(new Mentor(), new Mentor());
        when(coordenadorService.findAllMentores()).thenReturn(mentores);

        mockMvc.perform(get("/api/coordenador/mentores"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(coordenadorService, times(1)).findAllMentores();
    }

    @Test
    @DisplayName("Buscar todos os Projetos: deve retornar HTTP 200 OK com a lista de Projetos")
    void findAllProjetos_deveRetornarOkComLista() throws Exception {
        List<Projeto> projetos = Arrays.asList(new Projeto(), new Projeto());
        when(coordenadorService.findAllProjetos()).thenReturn(projetos);

        mockMvc.perform(get("/api/coordenador/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2));

        verify(coordenadorService, times(1)).findAllProjetos();
    }

    @Test
    @DisplayName("Buscar todos os Projetos: quando vazia, deve retornar HTTP 200 OK com lista vazia")
    void findAllProjetos_quandoVazia_deveRetornarOkComListaVazia() throws Exception {
        List<Projeto> projetos = Collections.emptyList();
        when(coordenadorService.findAllProjetos()).thenReturn(projetos);

        mockMvc.perform(get("/api/coordenador/projetos"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(coordenadorService, times(1)).findAllProjetos();
    }

    @Test
    @DisplayName("Buscar por Email: quando encontrado, deve retornar HTTP 200 OK e o objeto Coordenador")
    void buscarPorEmail_quandoEncontrado_deveRetornarOkCoordenador() throws Exception {
        when(coordenadorService.buscarPorEmail(email)).thenReturn(coordenador);

        mockMvc.perform(get("/api/coordenador/buscar-por-email").param("email", email))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.email").value(email));

        verify(coordenadorService, times(1)).buscarPorEmail(email);
    }

    @Test
    @DisplayName("Buscar por Email: quando não encontrado, deve retornar HTTP 404 NOT_FOUND")
    void buscarPorEmail_quandoNaoEncontrado_deveRetornarNotFound() throws Exception {
        String emailInvalido = "naoexiste@example.com";
        when(coordenadorService.buscarPorEmail(emailInvalido)).thenReturn(null);

        mockMvc.perform(get("/api/coordenador/buscar-por-email").param("email", emailInvalido))
                .andExpect(status().isNotFound());

        verify(coordenadorService, times(1)).buscarPorEmail(emailInvalido);
    }

    @Test
    @DisplayName("Buscar por ID: quando encontrado, deve retornar HTTP 200 OK e o objeto Coordenador")
    void getCoordenadorPorId_quandoEncontrado_deveRetornarOkCoordenador() throws Exception {
        when(coordenadorService.findById(coordenadorId)).thenReturn(coordenador);

        mockMvc.perform(get("/api/coordenador/getById/{id}", coordenadorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(coordenadorId));

        verify(coordenadorService, times(1)).findById(coordenadorId);
    }

    @Test
    @DisplayName("Buscar por ID: quando não encontrado (null), deve retornar HTTP 404 NOT_FOUND")
    void getCoordenadorPorId_quandoNaoEncontrado_deveRetornarNotFound() throws Exception {
        Long idInvalido = 99L;
        when(coordenadorService.findById(idInvalido)).thenReturn(null);

        mockMvc.perform(get("/api/coordenador/getById/{id}", idInvalido))
                .andExpect(status().isNotFound());

        verify(coordenadorService, times(1)).findById(idInvalido);
    }
}