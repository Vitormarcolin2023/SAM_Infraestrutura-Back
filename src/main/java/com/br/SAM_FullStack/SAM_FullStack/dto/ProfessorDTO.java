package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ProfessorDTO {
    private String nome;
    private String email;
    private String senha;
    private List<Long> cursosIds;
}
