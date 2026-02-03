package com.br.SAM_FullStack.SAM_FullStack.dto;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AvaliacaoDTO {
    private Long id;
    private Integer resposta1;
    private Integer resposta2;
    private Integer resposta3;
    private Integer resposta4;
    private Integer resposta5;
    private Integer resposta6;
    private Double media;
    private String comentario;
    private boolean recomendacao;
    private Projeto projeto;
    private Aluno aluno;
}
