package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.CustomUserDetailsService;
import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.service.AreaDeAtuacaoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@DisplayName("Testes de Integração Web do AreaDeAtuacaoController")
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
class AreaDeAtuacaoControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    AreaDeAtuacaoService areaDeAtuacaoService;
    @MockitoBean
    TokenService tokenService;
    @MockitoBean
    CustomUserDetailsService customUserDetailsService;

    AreaDeAtuacao area;

    @BeforeEach
    void setUp() {
        area = new AreaDeAtuacao();
        area.setId(1L);
        area.setNome("Tecnologia");
    }

    @Test
    @DisplayName("Deve retornar lista de áreas e status OK (Autenticado)")
    void findAll_quandoAutenticado_deveRetornarListaEStatusOK() throws Exception {
        when(areaDeAtuacaoService.findAll()).thenReturn(Arrays.asList(area));
        mockMvc.perform(get("/areas/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve retornar lista de áreas e status OK (Não Autenticado)")
    @WithAnonymousUser // Garante teste sem login
    void findAll_quandoNaoAutenticado_deveRetornarOK() throws Exception {
        // Como /areas/findAll é permitAll() na SecurityConfig, deve retornar 200 mesmo sem usuário
        when(areaDeAtuacaoService.findAll()).thenReturn(Arrays.asList(area));
        mockMvc.perform(get("/areas/findAll"))
                .andExpect(status().isOk());
    }

    @Test
    @DisplayName("Deve salvar área e retornar status CREATED")
    void save_quandoAutenticadoEValido_deveRetornarCreatedEArea() throws Exception {
        when(areaDeAtuacaoService.save(any(AreaDeAtuacao.class))).thenReturn(area);
        mockMvc.perform(post("/areas/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve atualizar área e retornar status OK")
    void update_quandoAutenticadoEValido_deveRetornarOKeArea() throws Exception {
        when(areaDeAtuacaoService.update(eq(1L), any(AreaDeAtuacao.class))).thenReturn(area);
        mockMvc.perform(put("/areas/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(area)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));
    }

    @Test
    @DisplayName("Deve deletar área e retornar status NO_CONTENT")
    void delete_quandoAutenticadoEIdExistente_deveRetornarNoContent() throws Exception {
        doNothing().when(areaDeAtuacaoService).delete(1L);
        mockMvc.perform(delete("/areas/delete/1")
                        .with(csrf()))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve buscar por início do nome e retornar lista e status OK")
    void getAreaPorInicioDoNome_quandoAutenticadoEEncontrado_deveRetornarListaEStatusOK() throws Exception {
        when(areaDeAtuacaoService.buscarPorInicioDoNome("Tec")).thenReturn(Arrays.asList(area));
        mockMvc.perform(get("/areas/buscar-por-inicio").param("prefixo", "Tec"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve buscar por início do nome e retornar NO_CONTENT")
    void getAreaPorInicioDoNome_quandoAutenticadoENaoEncontrado_deveRetornarNoContent() throws Exception {
        when(areaDeAtuacaoService.buscarPorInicioDoNome("X")).thenReturn(Collections.emptyList());
        mockMvc.perform(get("/areas/buscar-por-inicio").param("prefixo", "X"))
                .andExpect(status().isNoContent());
    }

    @Test
    @DisplayName("Deve salvar lista de áreas e retornar status CREATED")
    void saveAll_quandoAutenticadoEValido_deveRetornarCreatedELista() throws Exception {
        AreaDeAtuacao area2 = new AreaDeAtuacao();
        area2.setId(2L);
        area2.setNome("Saúde");

        List<AreaDeAtuacao> areasParaSalvar = Arrays.asList(area, area2);
        List<AreaDeAtuacao> areasSalvas = Arrays.asList(area, area2);

        when(areaDeAtuacaoService.saveAll(any(List.class))).thenReturn(areasSalvas);

        mockMvc.perform(post("/areas/batch")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(areasParaSalvar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[1].nome").value("Saúde"));
    }

    @Test
    @DisplayName("Deve retornar área do aluno logado e status OK")
    void getAreaDeAtuacaoDoAlunoLogado_quandoAutenticadoEEncontrado_deveRetornarAreaEStatusOK() throws Exception {
        when(areaDeAtuacaoService.findByAlunoLogado()).thenReturn(area);
        mockMvc.perform(get("/areas/por-aluno-logado"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Tecnologia"));
    }

    @Test
    @DisplayName("Deve retornar NOT_FOUND quando área do aluno logado não encontrada")
    void getAreaDeAtuacaoDoAlunoLogado_quandoAutenticadoENaoEncontrado_deveRetornarNotFound() throws Exception {
        when(areaDeAtuacaoService.findByAlunoLogado()).thenReturn(null);
        mockMvc.perform(get("/areas/por-aluno-logado"))
                .andExpect(status().isNotFound());
    }
}