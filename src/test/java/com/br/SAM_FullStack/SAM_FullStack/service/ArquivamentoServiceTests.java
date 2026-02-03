package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusGrupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.GrupoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class ArquivamentoServiceProjetoTest {

    @InjectMocks
    private ArquivamentoService arquivamentoService;

    @Mock
    private ProjetoRepository projetoRepository;

    @Mock
    private GrupoRepository grupoRepository;

    private Projeto projetoAtivoComDataPassada;
    private Projeto projetoAtivoComDataFutura;
    private Projeto projetoAtivoSemData;

    private Grupo grupoAssociado;

    @BeforeEach
    void setup() {
        grupoAssociado = new Grupo();
        grupoAssociado.setId(1L);
        grupoAssociado.setNome("Grupo Associado");
        grupoAssociado.setStatusGrupo(StatusGrupo.ATIVO);

        projetoAtivoComDataPassada = new Projeto();
        projetoAtivoComDataPassada.setId(1L);
        projetoAtivoComDataPassada.setNomeDoProjeto("Projeto Passado");
        projetoAtivoComDataPassada.setStatusProjeto(StatusProjeto.ATIVO);
        projetoAtivoComDataPassada.setDataFinalProjeto(LocalDate.of(2025, 1, 1));
        projetoAtivoComDataPassada.setGrupo(grupoAssociado);

        projetoAtivoComDataFutura = new Projeto();
        projetoAtivoComDataFutura.setId(2L);
        projetoAtivoComDataFutura.setNomeDoProjeto("Projeto Futuro");
        projetoAtivoComDataFutura.setStatusProjeto(StatusProjeto.ATIVO);
        projetoAtivoComDataFutura.setDataFinalProjeto(LocalDate.of(2025, 12, 31));
        projetoAtivoComDataFutura.setGrupo(grupoAssociado);

        projetoAtivoSemData = new Projeto();
        projetoAtivoSemData.setId(3L);
        projetoAtivoSemData.setNomeDoProjeto("Projeto Sem Data");
        projetoAtivoSemData.setStatusProjeto(StatusProjeto.ATIVO);
        projetoAtivoSemData.setDataFinalProjeto(null);
        projetoAtivoSemData.setGrupo(grupoAssociado);
    }

    @Test
    @DisplayName("Deve arquivar projeto e grupo quando data final passada")
    void verificaArquivamento_quandoDataFinalPassada_deveArquivar() {
        when(projetoRepository.findAllByStatusProjeto(StatusProjeto.ATIVO))
                .thenReturn(List.of(projetoAtivoComDataPassada));

        LocalDate dataFake = LocalDate.of(2025, 2, 1);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(dataFake);

            arquivamentoService.verificaArquivamento();

            assertEquals(StatusProjeto.AGUARDANDO_AVALIACAO, projetoAtivoComDataPassada.getStatusProjeto());
            verify(projetoRepository, times(1)).save(projetoAtivoComDataPassada);

            assertEquals(StatusGrupo.ARQUIVADO, grupoAssociado.getStatusGrupo());
            verify(grupoRepository, times(1)).save(grupoAssociado);
        }
    }

    @Test
    @DisplayName("Não deve arquivar projeto com data final futura")
    void verificaArquivamento_quandoDataFutura_naoDeveArquivar() {
        when(projetoRepository.findAllByStatusProjeto(StatusProjeto.ATIVO))
                .thenReturn(List.of(projetoAtivoComDataFutura));

        LocalDate dataFake = LocalDate.of(2025, 2, 1);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(dataFake);

            arquivamentoService.verificaArquivamento();

            assertEquals(StatusProjeto.ATIVO, projetoAtivoComDataFutura.getStatusProjeto());
            verify(projetoRepository, never()).save(projetoAtivoComDataFutura);
            assertEquals(StatusGrupo.ATIVO, grupoAssociado.getStatusGrupo());
            verify(grupoRepository, never()).save(any());
        }
    }

    @Test
    @DisplayName("Deve direcionar projeto para AGUARADANDO_AVALIAÇÃO e grupo sem data final no dia 10 de julho ou dezembro")
    void verificaTrocaDeStatus_quandoDia10JulOuDez_deveTrocarStatus() {
        when(projetoRepository.findAllByStatusProjeto(StatusProjeto.ATIVO))
                .thenReturn(List.of(projetoAtivoSemData));

        LocalDate dataFake = LocalDate.of(2025, 7, 10);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(dataFake);

            arquivamentoService.verificaArquivamento();

            assertEquals(StatusProjeto.AGUARDANDO_AVALIACAO, projetoAtivoSemData.getStatusProjeto());
            assertEquals(StatusGrupo.ARQUIVADO, grupoAssociado.getStatusGrupo());
            verify(projetoRepository, times(1)).save(projetoAtivoSemData);
            verify(grupoRepository, times(1)).save(grupoAssociado);
        }
    }

    @Test
    @DisplayName("Não deve direcionar projeto para AGUARADANDO_AVALIAÇÃO  se o projeto tiver data final fora do dia 10 ou meses errados")
    void verificaArquivamento_quandoDiaNao10_ouMesErrado_naoDeveArquivar() {
        when(projetoRepository.findAllByStatusProjeto(StatusProjeto.ATIVO))
                .thenReturn(List.of(projetoAtivoSemData));

        LocalDate dataFake = LocalDate.of(2025, 8, 11);
        try (MockedStatic<LocalDate> mockedDate = mockStatic(LocalDate.class)) {
            mockedDate.when(LocalDate::now).thenReturn(dataFake);

            arquivamentoService.verificaArquivamento();

            assertEquals(StatusProjeto.ATIVO, projetoAtivoSemData.getStatusProjeto());
            assertEquals(StatusGrupo.ATIVO, grupoAssociado.getStatusGrupo());
            verify(projetoRepository, never()).save(any());
            verify(grupoRepository, never()).save(any());
        }
    }
}
