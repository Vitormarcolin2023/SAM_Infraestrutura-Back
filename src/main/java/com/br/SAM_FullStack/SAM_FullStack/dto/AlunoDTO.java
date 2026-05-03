package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
public class AlunoDTO {
    private String nome;
    private String email;
    private String senha;
    private Integer ra;
    private Long cursoId;
}
