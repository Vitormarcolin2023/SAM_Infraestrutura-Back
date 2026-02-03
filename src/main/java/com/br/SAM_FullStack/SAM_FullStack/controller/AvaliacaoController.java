package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.AvaliacaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Avaliacao;
import com.br.SAM_FullStack.SAM_FullStack.service.AvaliacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/avaliacoes")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class AvaliacaoController {

    private final AvaliacaoService avaliacaoService;

    @GetMapping("/buscar-todos/area-atuacao/{areaIds}")
    public ResponseEntity<List<AvaliacaoDTO>> buscarPorAreaDeAtuacao(@PathVariable List<Long> areaIds){
        List<AvaliacaoDTO> response = avaliacaoService.buscarAvaliacoesPorAreas(areaIds);
        return ResponseEntity.status(200).body(response);
    }

    /**
     * Endpoint para um aluno submeter uma nova avaliação para um projeto.
     * O ID do projeto é passado na URL.
     * O corpo da avaliação (respostas, comentario) é passado no Body.
     */
    @PostMapping("/projeto/{projetoId}")
    public ResponseEntity<Boolean> criarAvaliacao(
            @PathVariable Long projetoId,
            @Valid @RequestBody Avaliacao avaliacao) {

        avaliacao.setId(null);
        avaliacao.setMedia(null); // Será calculado no service

        boolean sucesso = avaliacaoService.salvarAvaliacao(avaliacao, projetoId);
        return ResponseEntity.status(201).body(sucesso);
    }


    @GetMapping("/verifica-pendencia-aluno/{alunoId}/projeto/{projetoId}")
    public ResponseEntity<Boolean> verificaPendenciaAluno(
            @PathVariable Long alunoId,
            @PathVariable Long projetoId) {

        boolean jaRespondeu = avaliacaoService.alunoRespondeuAvaliacao(alunoId, projetoId);
        return ResponseEntity.ok(jaRespondeu);
    }

}