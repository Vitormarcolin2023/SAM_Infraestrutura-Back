package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.dto.AvaliacaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Avaliacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.AvaliacaoRepository;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AvaliacaoService {

    private final AvaliacaoRepository avaliacaoRepository;
    private final ProjetoRepository projetoRepository;

    public List<Avaliacao> findAll() {
        return avaliacaoRepository.findAll();
    }

    public List<AvaliacaoDTO> buscarAvaliacoesPorAreas(List<Long> areaIds) {
        if (areaIds == null || areaIds.isEmpty()) {
            throw new IllegalArgumentException("A lista de áreas de atuação não pode ser vazia.");
        }
        List<Avaliacao> avaliacoes = avaliacaoRepository.findAllByAreasDeAtuacao(areaIds);

        List avaliacoesDTO = new ArrayList<>();
        for (Avaliacao avaliacao: avaliacoes) {
            avaliacoesDTO.add(avaliacao);
        }

        return avaliacoesDTO;
    }
    @Transactional
    public boolean salvarAvaliacao(Avaliacao avaliacao, Long projetoId) {

        Projeto projeto = projetoRepository.findById(projetoId)
                .orElseThrow(() -> new EntityNotFoundException("Projeto não encontrado com ID: " + projetoId));
        avaliacao.setProjeto(projeto);

        // Verifica se o aluno já respondeu
        if (avaliacaoRepository.existsByProjetoIdAndAlunoId(projetoId, avaliacao.getAluno().getId())) {
            throw new RuntimeException("Aluno já respondeu a avaliação deste projeto");
        }

        // Calcula a média desta avaliação (r1 a r6)
        double mediaCalculada = (
                avaliacao.getResposta1() +
                        avaliacao.getResposta2() +
                        avaliacao.getResposta3() +
                        avaliacao.getResposta4() +
                        avaliacao.getResposta5() +
                        avaliacao.getResposta6()
        ) / 6.0;
        avaliacao.setMedia(arredondar(mediaCalculada));

        try {
            // Salva a avaliação no banco
            avaliacaoRepository.save(avaliacao);

            // Verifica se todos os alunos do projeto já responderam
            List<Aluno> alunosDoProjeto = projeto.getGrupo().getAlunos();
            List<Aluno> alunosQueResponderam = avaliacaoRepository.findByProjetoId(projetoId).stream()
                    .map(Avaliacao::getAluno)
                    .distinct()
                    .toList();

            boolean todosResponderam = alunosQueResponderam.size() == alunosDoProjeto.size();

            if (todosResponderam) {
                projeto.setStatusProjeto(StatusProjeto.ARQUIVADO);
                projetoRepository.save(projeto);
            }

            return true; // Avaliação salva com sucesso

        } catch (Exception e) {
            return false; // Algum erro ao salvar
        }
    }


    // Helper para arredondar as médias para 2 casas decimais
    private double arredondar(double valor) {
        return Math.round(valor * 100.0) / 100.0;
    }

    public boolean alunoRespondeuAvaliacao(Long alunoId, Long projetoId) {
        return avaliacaoRepository.existsByProjetoIdAndAlunoId(projetoId, alunoId);
    }

}