package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class CoordenadorUpdateDTO {
    private String nome;
    private String email;
    private String senha;
    private List<Long> cursosIds;
}
