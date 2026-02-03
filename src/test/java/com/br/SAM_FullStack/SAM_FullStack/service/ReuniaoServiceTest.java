package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.*;
import com.br.SAM_FullStack.SAM_FullStack.repository.AlunoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ReuniaoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ReuniaoServiceTest {

    @InjectMocks
    private ReuniaoService1 reuniaoService;

    @Mock
    private ReuniaoRepository reuniaoRepository;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private AlunoRepository alunoRepository;

    private Projeto projeto;
    private Reuniao reuniaoAceita;
    private Reuniao reuniaoPendente;
    private ReuniaoDTO reuniaoDTO;
    private Date data;
    private LocalTime hora;

    @BeforeEach
    void setup() {

        LocalDate localDate = LocalDate.of(2025, 10, 23);
        this.data = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
        this.hora = LocalTime.of(14, 0);

        projeto = new Projeto();
        projeto.setId(1L);
        projeto.setNomeDoProjeto("SAM FullStack");
        projeto.setStatusProjeto(StatusProjeto.ATIVO);

        reuniaoAceita = new Reuniao();
        reuniaoAceita.setId(1L);
        reuniaoAceita.setAssunto("Validar requisitos");
        reuniaoAceita.setData(data);
        reuniaoAceita.setHora(hora);
        reuniaoAceita.setFormatoReuniao(FormatoReuniao.ONLINE);
        reuniaoAceita.setStatusReuniao(StatusReuniao.ACEITO);

        reuniaoPendente = new Reuniao();
        reuniaoPendente.setId(2L);
        reuniaoPendente.setAssunto("Assinar docs");
        reuniaoPendente.setData(data);
        reuniaoPendente.setHora(hora);
        reuniaoPendente.setFormatoReuniao(FormatoReuniao.PRESENCIAL);
        reuniaoPendente.setStatusReuniao(StatusReuniao.PENDENTE);
        reuniaoPendente.setProjeto(projeto);
    }


    @Test
    void buscarReunioes_deveRetornarListaDeReunioes() {
        when(reuniaoRepository.findAll()).thenReturn(List.of(reuniaoAceita, reuniaoPendente));
        List<Reuniao> retorno = reuniaoService.findAll();
        assertEquals(2, retorno.size());
    }

    @Test
    void buscarReunioesMentor_deveRetornarLista() {
        when(reuniaoRepository.findAllMentor(1L)).thenReturn(List.of(reuniaoAceita));
        List<Reuniao> retorno = reuniaoService.findAllByMentor(1L);
        assertEquals(1, retorno.size());
    }

    @Test
    void buscarReunioesGrupo_deveRetornarLista() {
        when(reuniaoRepository.findAllGrupo(1L)).thenReturn(List.of(reuniaoAceita));
        List<Reuniao> retorno = reuniaoService.findAllByGrupo(1L);
        assertEquals(1, retorno.size());
    }

    @Test
    void buscarReunioesProjeto_deveRetornarLista() {
        when(reuniaoRepository.findAllByProjetoId(1L)).thenReturn(List.of(reuniaoAceita));
        List<Reuniao> retorno = reuniaoService.findAllByProjeto(1L);
        assertEquals(1, retorno.size());
    }

    @Test
    void buscarReuniao_quandoIdExiste() {
        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniaoAceita));
        Reuniao retorno = reuniaoService.findById(1L);
        assertEquals("Validar requisitos", retorno.getAssunto());
    }

    @Test
    void buscarReuniao_quandoIdInexistente() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalArgumentException.class, () -> reuniaoService.findById(99L));
    }

    @Test
    void saveReuniao_quandoCorreto() {
        reuniaoDTO = new ReuniaoDTO("Nova", data, hora, FormatoReuniao.ONLINE, 1L, "MENTOR");
        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(reuniaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        String retorno = reuniaoService.save(reuniaoDTO);

        assertEquals("Solicitação de reunião enviada", retorno);
    }

    @Test
    void saveReuniao_quandoProjetoNaoExiste() {
        reuniaoDTO = new ReuniaoDTO();
        reuniaoDTO.setProjeto_id(55L);
        when(projetoRepository.findById(55L)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class, () -> reuniaoService.save(reuniaoDTO));
        assertEquals("Projeto não encontrado", ex.getMessage());
    }

    @Test
    @DisplayName("SAVE: deve definir status PENDENTE e solicitadoPor corretamente")
    void saveReuniao_deveSetarStatusEPessoaCorretamente() {
        reuniaoDTO = new ReuniaoDTO("Assunto", data, hora, FormatoReuniao.ONLINE, 1L, "ALUNO");

        when(projetoRepository.findById(1L)).thenReturn(Optional.of(projeto));
        when(reuniaoRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        reuniaoService.save(reuniaoDTO);

        verify(reuniaoRepository).save(argThat(r ->
                r.getStatusReuniao() == StatusReuniao.PENDENTE &&
                        "ALUNO".equals(r.getSolicitadoPor())
        ));
    }


    @Test
    void update_IdIncorreto() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
                () -> reuniaoService.update(99L, new Reuniao()));
    }

    @Test
    void update_StatusNaoPendente() {
        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniaoAceita));
        assertThrows(IllegalStateException.class,
                () -> reuniaoService.update(1L, new Reuniao()));
    }

    @Test
    void update_quandoCorreto() {
        Reuniao updates = new Reuniao();
        updates.setAssunto("Novo");
        updates.setFormatoReuniao(FormatoReuniao.ONLINE);

        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any())).thenReturn(reuniaoPendente);

        String retorno = reuniaoService.update(2L, updates);

        assertEquals("Reunião atualizada e reenviada para aprovação", retorno);
        assertEquals("Novo", reuniaoPendente.getAssunto());
        assertEquals(FormatoReuniao.ONLINE, reuniaoPendente.getFormatoReuniao());
    }

    @Test
    @DisplayName("UPDATE: deve atualizar motivoRecusa também")
    void update_deveAtualizarMotivoRecusa() {
        Reuniao updates = new Reuniao();
        updates.setMotivoRecusa("faltou info");

        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniaoPendente));
        when(reuniaoRepository.save(any())).thenReturn(reuniaoPendente);

        reuniaoService.update(2L, updates);

        assertEquals("faltou info", reuniaoPendente.getMotivoRecusa());
    }

    // ---------- ACEITAR ----------

    @Test
    void aceitarReuniao_Aceitar() {
        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniaoPendente));

        String retorno = reuniaoService.aceitarReuniao(2L, StatusReuniao.ACEITO, null);

        assertEquals("Status reunião: aceito", retorno);
        assertNull(reuniaoPendente.getMotivoRecusa());
    }

    @Test
    void aceitarReuniao_Recusar() {
        when(reuniaoRepository.findById(2L)).thenReturn(Optional.of(reuniaoPendente));

        String motivo = "indisponível";
        String retorno = reuniaoService.aceitarReuniao(2L, StatusReuniao.RECUSADO, motivo);

        assertEquals("Status reunião: recusado", retorno);
        assertEquals(motivo, reuniaoPendente.getMotivoRecusa());
    }

    @Test
    @DisplayName("ACEITAR: deve lançar exceção quando reunião não existe")
    void aceitarReuniao_IdInexistente() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(IllegalStateException.class,
                () -> reuniaoService.aceitarReuniao(99L, StatusReuniao.ACEITO, null));
    }


    @Test
    void deletarReuniao() {
        when(reuniaoRepository.findById(1L)).thenReturn(Optional.of(reuniaoAceita));
        doNothing().when(reuniaoRepository).delete(any());

        String retorno = reuniaoService.delete(1L);
        assertEquals("Reunião deletada com sucesso", retorno);
    }

    @Test
    @DisplayName("DELETE: deve lançar erro quando ID não existe")
    void deletarReuniao_IdInexistente() {
        when(reuniaoRepository.findById(anyLong())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class, () -> reuniaoService.delete(99L));
    }
}
