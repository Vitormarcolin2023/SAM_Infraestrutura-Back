package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AnalizarExclusaoDTO {
    private String senhaProf;
    private long idGrupo;
    private Long idAluno;
    private boolean resposta;
}
