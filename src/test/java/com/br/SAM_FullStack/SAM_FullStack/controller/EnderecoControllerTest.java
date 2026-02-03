package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
import com.br.SAM_FullStack.SAM_FullStack.service.EnderecoService;
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
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnderecoController.class)
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
public class EnderecoControllerTest {

    @Autowired
    private MockMvc mockMvc; // Simula as requisições HTTP

    @Autowired
    private ObjectMapper objectMapper; // Converte objetos para/de JSON

    // Mock da dependência do Controller (Service)
    @MockitoBean
    private EnderecoService enderecoService;

    @MockitoBean
    private TokenService tokenService;

    private Endereco enderecoMock;
    private final String BASE_URL = "/endereco";

    @BeforeEach
    void setUp() {
        enderecoMock = new Endereco(
                1L,
                "Rua das Acácias",
                "123",
                "Centro",
                "Belo Horizonte",
                "MG",
                "30110-000",
                null
        );
    }

    //TESTES DE SUCESSO (STATUS 200/204)
    @Test
    @DisplayName("Deve retornar 200 OK e lista não vazia")
    void listAll_DeveRetornarStatusOkELista() throws Exception {

        List<Endereco> listaMock = Arrays.asList(enderecoMock, new Endereco());
        when(enderecoService.listAll()).thenReturn(listaMock);

        mockMvc.perform(get(BASE_URL + "/findAll")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(2))
                .andExpect(jsonPath("$[0].rua").value("Rua das Acácias"));

        verify(enderecoService, times(1)).listAll();
    }

    @Test
    @DisplayName("FIND_BY_ID- Deve retornar 200 OK e o Endereco")
    void findById_DeveRetornarStatusOkEEndereco() throws Exception {

        when(enderecoService.findById(1L)).thenReturn(enderecoMock);

        mockMvc.perform(get(BASE_URL + "/findById/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cidade").value("Belo Horizonte"));

        verify(enderecoService, times(1)).findById(1L);
    }

    @Test
    @DisplayName("GET- Deve retornar 200 OK com Endereco salvo")
    void save_DeveRetornarStatusOk() throws Exception {
        when(enderecoService.save(any(Endereco.class))).thenReturn(enderecoMock);

        mockMvc.perform(post(BASE_URL + "/save")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L));

        verify(enderecoService, times(1)).save(any(Endereco.class));
    }

    @Test
    @DisplayName("UPDATE- deve retornar 200 OK com Endereco atualizado")
    void update_DeveRetornarStatusOk() throws Exception {
        //Simula o Endereço com uma alteração (ex: novo número)
        Endereco enderecoAtualizado = new Endereco(
                1L, "Rua das Acácias", "456", "Centro",
                "Belo Horizonte", "MG", "30110-000", null
        );
        when(enderecoService.update(eq(1L), any(Endereco.class))).thenReturn(enderecoAtualizado);

        mockMvc.perform(put(BASE_URL + "/update/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(enderecoMock)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.numero").value("456"));

        verify(enderecoService, times(1)).update(eq(1L), any(Endereco.class));
    }

    @Test
    @DisplayName("DELETE- deve retornar 204 No Content")
    void delete_DeveRetornarStatusNoContent() throws Exception {
        //Mocka o metodo delete do Service (void)
        doNothing().when(enderecoService).delete(1L);

        mockMvc.perform(delete(BASE_URL + "/delete/1"))
                .andExpect(status().isNoContent()); // Verifica 204

        verify(enderecoService, times(1)).delete(1L);
    }

    //TESTES DE EXCEÇÃO (assertThrows Implícito)
    @Test
    @DisplayName("GET /findById/{id} deve retornar 400 Bad Request se não encontrado")
    void findById_DeveRetornarStatus400QuandoNaoExiste() throws Exception {
        //Simula a exceção do Service
        when(enderecoService.findById(99L)).thenThrow(new RuntimeException("Endereço não encontrado"));

        mockMvc.perform(get(BASE_URL + "/findById/99"))
                .andExpect(status().isBadRequest()); // Espera 400 (Bad Request) ou 500 (Internal Server Error)

        verify(enderecoService, times(1)).findById(99L);
    }

    @Test
    @DisplayName("DELETE /delete/{id} deve retornar 400 Bad Request se não encontrado")
    void delete_DeveRetornarStatus400QuandoNaoExiste() throws Exception {
        //Simula a exceção do Service no delete
        doThrow(new RuntimeException("Endereço não encontrado")).when(enderecoService).delete(99L);

        mockMvc.perform(delete(BASE_URL + "/delete/99"))
                .andExpect(status().isBadRequest()); // Espera 400 Bad Request

        verify(enderecoService, times(1)).delete(99L);
    }
}
