package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto.ARQUIVADO;
import static com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto.ATIVO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ProjetoController.class)
@AutoConfigureMockMvc(addFilters = false)
@TestPropertySource(properties = {
        "JWT_SECRET=segredo-teste-mock-muito-seguro-123456"
})
@WithMockUser(username = "super_usuario", roles = {"ALUNO", "COORDENADOR", "PROFESSOR"})
public class ProjetoControllerTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockitoBean
    ProjetoService projetoService;

    @MockitoBean
    TokenService tokenService;

    private AreaDeAtuacao areaTI;
    private Mentor mentorCarlos;
    private Aluno alunoAdmin;
    private Grupo grupoA;
    private Projeto projeto1;
    private Projeto projeto2;
    private List<Projeto> listaProjetos;

    @BeforeEach
    void setup() {
        areaTI = new AreaDeAtuacao(1L, "Tecnologia");

        mentorCarlos = new Mentor();
        mentorCarlos.setId(1L);
        mentorCarlos.setNome("Carlos Silva");
        mentorCarlos.setStatusMentor(StatusMentor.ATIVO);
        mentorCarlos.setAreaDeAtuacao(areaTI);

        alunoAdmin = new Aluno();
        alunoAdmin.setId(1L);
        alunoAdmin.setNome("Joana Silveira");

        grupoA = new Grupo();
        grupoA.setId(1L);
        grupoA.setNome("Grupo A");
        grupoA.setAlunoAdmin(alunoAdmin);

        projeto1 = new Projeto();
        projeto1.setId(1L);
        projeto1.setNomeDoProjeto("Sistema Escolar");
        projeto1.setDescricao("Gerenciamento de alunos");
        projeto1.setAreaDeAtuacao(areaTI);
        projeto1.setDataInicioProjeto(LocalDate.of(2024, 1, 1));
        projeto1.setDataFinalProjeto(LocalDate.of(2024, 6, 30));
        projeto1.setPeriodo("1° Periodo");
        projeto1.setMentor(mentorCarlos);
        projeto1.setStatusProjeto(ATIVO);
        projeto1.setGrupo(grupoA);

        projeto2 = new Projeto();
        projeto2.setId(2L);
        projeto2.setNomeDoProjeto("App Financeiro");
        projeto2.setDescricao("Controle financeiro pessoal");
        projeto2.setAreaDeAtuacao(areaTI);
        projeto2.setDataInicioProjeto(LocalDate.of(2024, 2, 1));
        projeto2.setDataFinalProjeto(LocalDate.of(2024, 8, 31));
        projeto2.setPeriodo("Integral");
        projeto2.setMentor(mentorCarlos);
        projeto2.setStatusProjeto(ARQUIVADO);
        projeto2.setGrupo(grupoA);

        listaProjetos = Arrays.asList(projeto1, projeto2);
    }

    @Test
    @DisplayName("Deve retornar todos os projetos")
    void listAll_deveRetornarTodosOsProjetos() throws Exception {
        when(projetoService.listAll()).thenReturn(listaProjetos);

        mockMvc.perform(get("/projetos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(listaProjetos.size()))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$[1].nomeDoProjeto").value("App Financeiro"));
    }

    @Test
    @DisplayName("Deve retornar projeto por ID")
    void findById_quandoIdValido_deveRetornarProjeto() throws Exception {
        when(projetoService.findById(1L)).thenReturn(projeto1);

        mockMvc.perform(get("/projetos/findById/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$.descricao").value("Gerenciamento de alunos"))
                .andExpect(jsonPath("$.statusProjeto").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar projetos filtrando por nome")
    void buscarPorNome_deveRetornarProjetos() throws Exception {
        when(projetoService.buscarPorNome("Sistema Escolar")).thenReturn(Arrays.asList(projeto1));

        mockMvc.perform(get("/projetos/buscar-por-nome").param("nome", "Sistema Escolar"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"));
    }

    @Test
    @DisplayName("Deve retornar projetos filtrando por área de atuação")
    void buscarPorAtuacao_quandoNomeValido_deveRetornarProjetos() throws Exception {
        when(projetoService.buscarPorAreaAtuacao(any(AreaDeAtuacao.class)))
                .thenReturn(listaProjetos);

        mockMvc.perform(get("/projetos/buscar-por-atuacao")
                        .param("areaNome", "Tecnologia"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$[1].nomeDoProjeto").value("App Financeiro"));
    }


    @Test
    @DisplayName("Deve salvar um projeto")
    void save_deveSalvarProjeto() throws Exception {
        when(projetoService.save(any(Projeto.class))).thenReturn(projeto1);

        String projetoJson = objectMapper.writeValueAsString(projeto1);

        mockMvc.perform(post("/projetos/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoProjeto").value("Sistema Escolar"))
                .andExpect(jsonPath("$.statusProjeto").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve atualizar um projeto")
    void update_deveAtualizarProjeto() throws Exception {
        Projeto projetoAtualizado = new Projeto();
        projetoAtualizado.setId(1L);
        projetoAtualizado.setNomeDoProjeto("Sistema Escolar Atualizado");
        projetoAtualizado.setDescricao("Gerenciamento atualizado");
        projetoAtualizado.setAreaDeAtuacao(areaTI);
        projetoAtualizado.setPeriodo("1° Periodo");
        projetoAtualizado.setMentor(mentorCarlos);
        projetoAtualizado.setStatusProjeto(ATIVO);
        projetoAtualizado.setGrupo(grupoA);

        when(projetoService.update(eq(1L), any(Projeto.class))).thenReturn(projetoAtualizado);

        String projetoJson = objectMapper.writeValueAsString(projetoAtualizado);

        mockMvc.perform(put("/projetos/update/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(projetoJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nomeDoProjeto").value("Sistema Escolar Atualizado"))
                .andExpect(jsonPath("$.descricao").value("Gerenciamento atualizado"))
                .andExpect(jsonPath("$.statusProjeto").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve deletar um projeto")
    void delete_deveRemoverProjeto() throws Exception {
        Mockito.doNothing().when(projetoService).delete(1L);

        mockMvc.perform(delete("/projetos/delete/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(content().string("Projeto excluído com sucesso"));
    }
}
