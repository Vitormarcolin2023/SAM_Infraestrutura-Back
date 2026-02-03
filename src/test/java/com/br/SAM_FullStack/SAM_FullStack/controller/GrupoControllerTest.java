package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.AdicionarAlunoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.AnalizarExclusaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProfessorRepository;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import com.br.SAM_FullStack.SAM_FullStack.service.GrupoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

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

@WithMockUser(username = "super_usuario", authorities = {"ROLE_ALUNO", "ROLE_COORDENADOR", "ROLE_PROFESSOR", "ROLE_MENTOR", "ROLE_ADMIN"})
public class GrupoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private GrupoService grupoService;

    @MockitoBean
    private AlunoService alunoService;

    @MockitoBean
    private ProfessorRepository professorRepository;

    private Aluno aluno1;
    private Aluno aluno2;
    private Aluno aluno3;
    private Aluno aluno4;
    private Aluno aluno5;
    private Aluno aluno6;
    private Grupo grupo1;
    private Grupo grupo2;
    private Professor professor;

    @BeforeEach
    void setup(){
        AreaDeAtuacao areaDeAtuacao = new AreaDeAtuacao(1L, "Tecnologia");
        Curso curso = new Curso(1L, "Engenharia de Software", areaDeAtuacao);

        aluno1 =  new Aluno(1L, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno2 = new Aluno(2L, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno3  = new Aluno(3L, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);
        aluno4 =new Aluno(4L, "Josue Arlindo", 1004, "senha123", "josue@gmail.com", curso, null);
        aluno5 = new Aluno(5L, "Jamile Kauena", 1005, "senha123", "jamile@gmail.com", curso, null);
        aluno6 = new Aluno(6L, "Ursula Fraz", 1006, "senha123", "ursula@gmail.com", curso, null);

        professor = new Professor(1L, "Kassia Kiss", "kassia@uniamerica.br", "senha123", List.of(curso));

        grupo1 = new Grupo(1L, "Grupo Ativo", StatusGrupo.ATIVO, aluno1, new ArrayList<>(List.of(aluno1, aluno2, aluno3)));
        grupo2 = new Grupo(2L, "Grupo Arquivado", StatusGrupo.ARQUIVADO, aluno1, new ArrayList<>(List.of(aluno1, aluno2, aluno3)));

        aluno1.setGrupos(new ArrayList<>(List.of(grupo1, grupo2)));
        aluno2.setGrupos(new ArrayList<>(List.of(grupo1, grupo2)));
        aluno3.setGrupos(new ArrayList<>(List.of(grupo1, grupo2)));
    }

    @Test
    @DisplayName("Deve retornar todos os grupos cadastrados no sistema e status OK")
    void buscarGrupos_deveRetornarTodosOsGrupos() throws Exception {
        when(this.grupoService.findAll()).thenReturn(List.of(grupo1, grupo2));

        mockMvc.perform(get("/grupos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Grupo Ativo"))
                .andExpect(jsonPath("$[0].alunos[0].nome").value("Joana Silveira"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 ao buscar aluno inexistente")
    void buscarGrupoPorId_quandoIdInexistente_deveRetornarErro() throws Exception{
        when(grupoService.findById(-1L)).thenThrow(new RuntimeException("Grupo não encontrado"));

        mockMvc.perform(get("/grupos/findById/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Grupo não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar o grupo com Id passado e mensagem 200")
    void buscarGrupoPorId_quandoIdCorreto_deveRetornarGrupo() throws Exception {
        when(grupoService.findById(1L)).thenReturn(grupo1);

        mockMvc.perform(get("/grupos/findById/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Grupo Ativo"));
    }

    @Test
    @DisplayName("Deve retornar erro 400 quando grupo não encontrado com id de aluno inexistente")
    void buscarGrupoPorAluno_quandoIdInexistente_deveRetornarErro() throws Exception {
        when(alunoService.findById(-1L)).thenThrow(new RuntimeException("Aluno não encontrado"));

        mockMvc.perform(get("/grupos/findByAluno/-1"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("Aluno não encontrado"));
    }

    @Test
    @DisplayName("Deve retornar grupo ativo do aluno e status 200")
    void buscarGrupoPorAluno_quandoExiste_deveRetornarGrupoAtivo() throws Exception{
        when(alunoService.findById(1L)).thenReturn(aluno1);
        when(grupoService.findByAluno(aluno1)).thenReturn(grupo1);

        mockMvc.perform(get("/grupos/findByAluno/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Grupo Ativo"))
                .andExpect(jsonPath("$.statusGrupo").value("ATIVO"));
    }

    @Test
    @DisplayName("Deve retornar GrupoDTO quando salvo com sucesso e retornar created")
    void salvarGrupo_quandoInformacoesCorretas_deveRetornarGrupoDTO() throws Exception{
        GrupoDTO novoGrupo = new GrupoDTO(3L, "Novo Grupo", 4L, List.of(4L, 5L, 6L), List.of(1L), "4 PERIODO");
        when(grupoService.save(any(GrupoDTO.class))).thenReturn(novoGrupo);

        mockMvc.perform(post("/grupos/save")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoGrupo)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.nome").value("Novo Grupo"));
    }

    @Test
    @DisplayName("Deve retornar um mensagem de sucesso quando as alterações forem salvas e status OK")
    void updateGrupo_quandoParametrosCorretos_deveRetornarSucesso() throws Exception{
        GrupoUpdateDTO update = new GrupoUpdateDTO("Novo nome");
        // O anyLong() agora vai funcionar por causa do import static no topo
        when(grupoService.updateGrupoInfo(anyLong(), anyLong(), any(GrupoUpdateDTO.class))).thenReturn("Alterações salvas com sucesso!");

        mockMvc.perform(put("/grupos/update/1/admin/1")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(content().string("Alterações salvas com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso ao adicionar novo aluno e status created")
    void adicionarAluno_quandoParametrosCorretos_deveRetornarSucesso() throws Exception {
        AdicionarAlunoDTO novoAluno = new AdicionarAlunoDTO(1L, 1L, 4L);
        when(grupoService.adicionarAlunoAoGrupo(novoAluno.getIdAdmin(), novoAluno.getIdGrupo(), novoAluno.getIdAluno())).thenReturn("Aluno adicionado com sucesso!");

        mockMvc.perform(post("/grupos/adicionar-aluno")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(novoAluno)))
                .andExpect(status().isCreated())
                .andExpect(content().string("Aluno adicionado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso ao remover aluno com status OK")
    void removerAluno_quandoInformacoesCorretas_deveRetornarSucesso() throws Exception {
        when(grupoService.removerAlunoDiretamente(1L, 3L, 1L)).thenReturn("Aluno removido com sucesso!");

        mockMvc.perform(delete("/grupos/1/remover-aluno/3/admin/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Aluno removido com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar lista dos grupos onde tem alunos aguardando exlcusao com status OK")
    void buscarGrupos_comParametrosCorretos_deveRetornarListaDeGrupos() throws Exception {
        when(grupoService.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO)).thenReturn(List.of(grupo1));

        mockMvc.perform(get("/grupos/findSolicitacoes"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Grupo Ativo"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso ao aceitar solicitação de exclusão de um aluno do grupo e status OK")
    void putExcluirAluno_quandoParametrosCorretos_deveRetornarSucesso() throws Exception{
        AnalizarExclusaoDTO exc = new AnalizarExclusaoDTO("senha123", 1L, 3L, true);

        when(professorRepository.findBySenha("senha123")).thenReturn(Optional.of(professor));
        when(grupoService.analizarExclusaoAluno("senha123", 1L, 3L, true)).thenReturn("Aluno excluído com sucesso!");

        mockMvc.perform(put("/grupos/analizarSolicitacao")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(exc)))
                .andExpect(status().isOk())
                .andExpect(content().string("Aluno excluído com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar mensagem de sucesso ao deleter grupo")
    void deleteGrupo_quandoParametrosCorretos_deveRetornarSucesso() throws Exception {
        when(grupoService.deletarGrupo(2L, 1L)).thenReturn("Grupo deletado com sucesso!");

        mockMvc.perform(delete("/grupos/delete/2/professor/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Grupo deletado com sucesso!"));
    }

    @Test
    @DisplayName("Deve Retornar Grupo quando o aluno por aluno logado e status OK")
    @WithAnonymousUser
    void buscarGrupo_quandoPossuiGrupoAtivo_deveRetornarGrupo() throws Exception {
        when(grupoService.findByAluno(aluno1)).thenReturn(grupo1);

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ALUNO")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                aluno1,
                "senha123",
                authorities
        );

        mockMvc.perform(get("/grupos/por-aluno-logado")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.nome").value("Grupo Ativo"));
    }

    @Test
    @DisplayName("Deve retornar erro de notFound quando o grupo for null")
    @WithAnonymousUser
    void buscarGrupo_quandoNaoPossuiGrupoAtivo_deveRetornarErro() throws Exception{
        when(grupoService.findByAluno(aluno4)).thenReturn(null);

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ALUNO")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                aluno4,
                "senha123",
                authorities
        );

        mockMvc.perform(get("/grupos/por-aluno-logado")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @DisplayName("Deve retornar erro quando o aluno passado for null")
    @WithAnonymousUser
    void buscarGrupo_quandoAlunoNull_deveRetornarErro() throws Exception{

        List<SimpleGrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_ALUNO")
        );
        Authentication authentication = new UsernamePasswordAuthenticationToken(
                null,
                "",
                authorities
        );

        mockMvc.perform(get("/grupos/por-aluno-logado")
                        .with(SecurityMockMvcRequestPostProcessors.authentication(authentication))
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isUnauthorized());
    }

    @Test
    @DisplayName("Deve retornar uma mensagem de sucesso ao arquivar grupo e status OK")
    void arquivarGrupo_deveRetornarMsgDeSucesso() throws Exception {
        when(grupoService.arquivarGrupo(1L)).thenReturn("Grupo arquivado com sucesso!");

        mockMvc.perform(put("/grupos/arquivar/1")
                        .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").value("Grupo arquivado com sucesso!"));
    }

    @Test
    @DisplayName("Deve retornar uma lista com todos os grupos arquivados do aluno e status 200")
    void buscarGruposArquivados_deveRetornarGruposArquivados() throws Exception {
        when(grupoService.findByGruposArquivados(1L)).thenReturn(List.of(grupo2));

        mockMvc.perform(get("/grupos/findByGruposArquivados/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].nome").value("Grupo Arquivado"));
    }
}