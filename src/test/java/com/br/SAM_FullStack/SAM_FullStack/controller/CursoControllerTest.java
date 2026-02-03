package com.br.SAM_FullStack.SAM_FullStack.controller;


import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.service.CursoService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.ImportAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.mockito.Mockito.when;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc (addFilters = false)
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
public class CursoControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private CursoService cursoService;

    @MockitoBean
    TokenService tokenService;

    private List<Curso> cursos;
    private ObjectMapper objectMapper = new ObjectMapper();


    @BeforeEach
    void setup() {
        cursos = Arrays.asList(
                new Curso(1L, "Java Básico", new AreaDeAtuacao(1L, "Tecnologia")),
                new Curso(2L, "Python", new AreaDeAtuacao(1L, "Tecnologia"))
        );
    }

    @Test
    @DisplayName("Buscar todos os Cursos: deve retornat TTTP 200 OK com a lista de cursos ")
    void findAllCursos_deveRetornarOkComLista() throws Exception {
        when(cursoService.findAll()).thenReturn(cursos);

        mockMvc.perform(get("/cursos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].nome").value("Java Básico"))
                .andExpect(jsonPath("$[1].nome").value("Python"));

        verify(cursoService, times(1)).findAll();
    }

    @Test
    @DisplayName("Buscar todos os cursos, deve retornar HTTP 200 OK e lista vazia quando não houver cursos")
    void findAllCursos_quandoNaoExistemCursos_deveRetornarListaVazia() throws Exception {
        when(cursoService.findAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cursos/findAll"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(0));

        verify(cursoService, times(1)).findAll();
    }

    @Test
    @DisplayName("Salvar curso, deve retornar HTTP 201 CREATED com o curso salvo")
    void saveCurso_deveRetornarCreatedComCurso() throws Exception {
        Curso novoCurso = new Curso(null, "Java Avançado", new AreaDeAtuacao(1L, "Tecnologia"));

        Curso cursoSalvo = new Curso(3L, novoCurso.getNome(), novoCurso.getAreaDeAtuacao());

        when(cursoService.save(any(Curso.class))).thenReturn(cursoSalvo);

        String cursoJson = objectMapper.writeValueAsString(novoCurso);

        mockMvc.perform(post("/cursos/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(cursoJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(3))
                .andExpect(jsonPath("$.nome").value("Java Avançado"))
                .andExpect(jsonPath("$.areaDeAtuacao.nome").value("Tecnologia"));

        verify(cursoService, times(1)).save(any(Curso.class));
    }

    @Test
    @DisplayName("Deve atualizar curso, retornar status OK e curso atualizado")
    void update_quandoValido_deveRetornarOKeCurso() throws Exception {

        Curso cursoUpdate = new Curso();
        cursoUpdate.setNome("Java Intermediário");
        cursoUpdate.setAreaDeAtuacao(new AreaDeAtuacao(1L, "Tecnologia"));

        Curso cursoAtualizado = new Curso(1L, "Java Intermediário", new AreaDeAtuacao(1L, "Tecnologia"));

        when(cursoService.update(eq(1L), any(Curso.class))).thenReturn(cursoAtualizado);

        mockMvc.perform(put("/cursos/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cursoUpdate)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.nome").value("Java Intermediário"))
                .andExpect(jsonPath("$.areaDeAtuacao.nome").value("Tecnologia"));

        verify(cursoService, times(1)).update(eq(1L), any(Curso.class));
    }

    @Test
    @DisplayName("Deve deletar curso e retornar status NO_CONTENT")
    void delete_quandoCursoExistir_deveRetornarNoContent() throws Exception {
        doNothing().when(cursoService).delete(1L);
        mockMvc.perform(delete("/cursos/delet/{id}", 1L))
                .andExpect(status().isNoContent());
        verify(cursoService, times(1)).delete(1L);
    }

    @Test
    @DisplayName("Salvar vários cursos de uma vez, deve retornar HTTP 201 CREATED com lista de cursos salvos")
    void saveAllCursos_deveRetornarCreatedComLista() throws Exception {
        List<Curso> cursosParaSalvar = cursos.stream()
                .map(c -> new Curso(null, c.getNome(), c.getAreaDeAtuacao()))
                .toList();
        List<Curso> cursosSalvos = cursos;

        when(cursoService.saveAll(anyList())).thenReturn(cursosParaSalvar);
        mockMvc.perform(post("/cursos/batch")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(cursosParaSalvar)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.size()").value(cursosSalvos.size()))
                .andExpect(jsonPath("$[0].nome").value("Java Básico"))
                .andExpect(jsonPath("$[1].nome").value("Python"));

        verify(cursoService, times(1)).saveAll(anyList());
    }

    @Test
    @DisplayName("Buscar cursos por nome, deve retornar HTTP 200 OK com lista de cursos encontrados")
    void getCursosPorNome_quandoExistir_deveRetornarOkComLista() throws Exception {
        String nomeCurso = "Java";

        List<Curso> encontrados = cursos.stream()
                .filter(c -> c.getNome().contains(nomeCurso))
                .toList();
        when(cursoService.buscarPorCurso(nomeCurso)).thenReturn(encontrados);

        mockMvc.perform(get("/cursos/buscar-por-curso")
                        .param("curso", nomeCurso))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(encontrados.size()))
                .andExpect(jsonPath("$[0].nome").value("Java Básico"));

        verify(cursoService, times(1)).buscarPorCurso(nomeCurso);
    }

    @Test
    @DisplayName("Buscar cursos por nome, deve retornar HTTP 204 NO CONTENT quando não encontrar")
    void getCursosPorNome_quandoNaoExistir_deveRetornarNoContent() throws Exception {
        String nomeCurso = "Ruby";

        when(cursoService.buscarPorCurso(nomeCurso)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cursos/buscar-por-curso")
                        .param("curso", nomeCurso))
                .andExpect(status().isNoContent());

        verify(cursoService, times(1)).buscarPorCurso(nomeCurso);
    }

    @Test
    @DisplayName("Buscar cursos por nome da área, deve retornar HTTP 200 OK com lista de cursos encontrados")
    void getCursosPorNomeDaArea_quandoExistir_deveRetornarOkComLista() throws Exception {
        String nomeArea = "Tecnologia";

        List<Curso> encontrados = cursos.stream()
                .filter(c -> c.getAreaDeAtuacao().getNome().contains(nomeArea))
                .toList();
        when(cursoService.buscarPorNomeDaArea(nomeArea)).thenReturn(encontrados);

        mockMvc.perform(get("/cursos/buscar-por-nome-area")
                        .param("nomeArea", nomeArea))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(encontrados.size()))
                .andExpect(jsonPath("$[0].areaDeAtuacao.nome").value("Tecnologia"));

        verify(cursoService, times(1)).buscarPorNomeDaArea(nomeArea);
    }

    @Test
    @DisplayName("Buscar cursos por nome da área, deve retornar HTTP 204 NO CONTENT quando não encontrar")
    void getCursosPorNomeDaArea_quandoNaoExistir_deveRetornarNoContent() throws Exception {
        String nomeArea = "Saúde";

        when(cursoService.buscarPorNomeDaArea(nomeArea)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cursos/buscar-por-nome-area")
                        .param("nomeArea", nomeArea))
                .andExpect(status().isNoContent());

        verify(cursoService, times(1)).buscarPorNomeDaArea(nomeArea);
    }

    @Test
    @DisplayName("Buscar cursos por coordenadorId, deve retornar HTTP 200 OK com lista de cursos")
    void getCursosByCoordenadorId_quandoExistir_deveRetornarOkComLista() throws Exception {
        Long coordenadorId = 1L;

        List<Curso> cursosDoCoordenador = cursos;
        when(cursoService.findByCoordenadorId(coordenadorId)).thenReturn(cursosDoCoordenador);

        mockMvc.perform(get("/cursos/coordenador/{coordenadorId}", coordenadorId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(cursosDoCoordenador.size()))
                .andExpect(jsonPath("$[0].nome").value("Java Básico"))
                .andExpect(jsonPath("$[1].nome").value("Python"));

        verify(cursoService, times(1)).findByCoordenadorId(coordenadorId);
    }

    @Test
    @DisplayName("Buscar cursos por coordenadorId, deve retornar HTTP 404 NOT FOUND quando não encontrar cursos")
    void getCursosByCoordenadorId_quandoNaoExistir_deveRetornarNotFound() throws Exception {
        Long coordenadorId = 99L;

        when(cursoService.findByCoordenadorId(coordenadorId)).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cursos/coordenador/{coordenadorId}", coordenadorId))
                .andExpect(status().isNotFound());

        verify(cursoService, times(1)).findByCoordenadorId(coordenadorId);
    }
}