package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.service.ReuniaoService1;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
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
@WithMockUser(username = "admin@sam.com", roles = {"MENTOR", "ADMIN"})
public class ReuniaoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReuniaoService1 reuniaoService;

    private Grupo grupo;
    private Reuniao reuniao1;
    private Reuniao reuniao2;
    private Projeto projeto;

    private Date data;
    private LocalTime hora;

    @BeforeEach
    void setup(){
        LocalDate localDate = LocalDate.of(2025, 10, 23);
        this.data = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.hora = LocalTime.of(14, 0, 0);

        AreaDeAtuacao areaDeAtuacao = new AreaDeAtuacao(1L, "Tecnologia");
        Curso curso = new Curso(1L, "ADS", areaDeAtuacao);

        Aluno aluno1 = new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno2 = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        Aluno aluno3 = new Aluno(3L, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);

        Mentor mentor = new Mentor();
        mentor.setNome("Romana Novaes");
        mentor.setId(1L);
        mentor.setAreaDeAtuacao(areaDeAtuacao);

        grupo = new Grupo(1L, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, List.of(aluno1, aluno2, aluno3));

        projeto = new Projeto();
        projeto.setId(10L);
        projeto.setNomeDoProjeto("Projeto SAM");

        reuniao1 = new Reuniao();
        reuniao1.setId(1L);
        reuniao1.setAssunto("Validar requisitos do projeto");
        reuniao1.setData(data);
        reuniao1.setHora(hora);
        reuniao1.setFormatoReuniao(FormatoReuniao.ONLINE);
        reuniao1.setStatusReuniao(StatusReuniao.ACEITO);
        reuniao1.setProjeto(projeto);

        reuniao2 = new Reuniao();
        reuniao2.setId(2L);
        reuniao2.setAssunto("Assinar documentos");
        reuniao2.setData(data);
        reuniao2.setHora(hora);
        reuniao2.setFormatoReuniao(FormatoReuniao.PRESENCIAL);
        reuniao2.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniao2.setProjeto(projeto);
    }

    @Test
    @DisplayName("Deve retornar listas com todas as reuniões")
    void buscarReunioes_deveRetornarListaDeReunioes() throws Exception {
        when(reuniaoService.findAll()).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assunto").value("Validar requisitos do projeto"))
                .andExpect(jsonPath("$[1].assunto").value("Assinar documentos"));
    }

    @Test
    @DisplayName("Deve retornar reunião conforme o id")
    void buscarReuniao_deveRetornarReuniao() throws Exception{
        when(reuniaoService.findById(1L)).thenReturn(reuniao1);

        mockMvc.perform(get("/reunioes/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.assunto").value("Validar requisitos do projeto"));
    }

    @Test
    @DisplayName("Deve retornar erro quando Id da reunião inexistente")
    void buscarReuniao_quandoIdInexistente_deveRetornarErro() throws Exception {
        when(reuniaoService.findById(-1L)).thenThrow(new RuntimeException("Reunião não encontrada"));

        mockMvc.perform(get("/reunioes/findById/-1"))
                .andExpect(status().isBadRequest());
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões de acordo com o grupo")
    void buscarReunioes_deveRetornarReunioesDoGrupo() throws Exception {
        when(reuniaoService.findAllByGrupo(1L)).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findByGrupo/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[1].assunto").value("Assinar documentos"));
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões de acordo com o mentor")
    void buscarReunioes_deveRetornarReunioesDoMentor() throws Exception {
        when(reuniaoService.findAllByMentor(1L)).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findByMentor/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assunto").value("Validar requisitos do projeto"));
    }

    @Test
    @DisplayName("Deve retornar lista de reuniões de acordo com o projeto")
    void buscarReunioes_deveRetornarReunioesDoProjeto() throws Exception {
        when(reuniaoService.findAllByProjeto(10L)).thenReturn(List.of(reuniao1, reuniao2));

        mockMvc.perform(get("/reunioes/findByProjeto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].assunto").value("Validar requisitos do projeto"));
    }

    @Test
    @DisplayName("Deve salvar nova reunião e retornar status created")
    void saveReuniao_quandoInformacoesCorretas_deveRetornarSucesso() throws Exception {
        ReuniaoDTO reuniaoDTO = new ReuniaoDTO("nova reunião", data, hora, FormatoReuniao.ONLINE, 10L, "MENTOR");

        when(reuniaoService.save(any(ReuniaoDTO.class))).thenReturn("Solicitação de reunião enviada");

        mockMvc.perform(post("/reunioes/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reuniaoDTO)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Solicitação de reunião enviada"));
    }

    @Test
    @DisplayName("Deve salvar mudanças feitas na reunião e retornar status OK")
    void updateReuniao_quandoInformacoesCorretas_deveRetornarMensagemDeSucesso() throws Exception{
        Reuniao reuniaoUpdate = new Reuniao();
        reuniaoUpdate.setAssunto("Novo assunto");

        when(reuniaoService.update(eq(1L), any(Reuniao.class))).thenReturn("Reunião atualizada e reenviada para aprovação");

        mockMvc.perform(put("/reunioes/update/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(reuniaoUpdate)))
                .andExpect(status().isOk())
                .andExpect(content().string("Reunião atualizada e reenviada para aprovação"));
    }

    @Test
    @DisplayName("Deve alterar status e motivo da reuniao e retornar status 200")
    void confirmarReuniao_deveRetornarMensagemDeSucesso() throws Exception {
        String motivo = "HorarioOk";

        when(reuniaoService.aceitarReuniao(eq(1L), eq(StatusReuniao.ACEITO), anyString()))
                .thenReturn("Status reunião: aceito");

        // IMPORTANTE: Certifique-se que você corrigiu a "/" no Controller como falamos antes!
        mockMvc.perform(put("/reunioes/confirmarReuniao/1/status/aceito/motivo-cancelamento/" + motivo)
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Status reunião: aceito"));
    }

    @Test
    @DisplayName("Deve deletar Reuniao, retornar mensagem de sucesso e status OK")
    void deleteReuniao_deveRetornarMensagemDeSucesso() throws Exception{
        when(reuniaoService.delete(1L)).thenReturn("Reunião deletada com sucesso");

        mockMvc.perform(delete("/reunioes/delete/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Reunião deletada com sucesso"));
    }
}