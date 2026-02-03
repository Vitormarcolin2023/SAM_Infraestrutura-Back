package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.Data;
import java.util.List;

@Data
public class CoordenadorDTO {
    private String nome;
    private String email;
    private String senha;
    private List<Long> cursosIds;
}