package com.br.SAM_FullStack.SAM_FullStack.dto;

import com.br.SAM_FullStack.SAM_FullStack.model.Professor;

import java.util.List;

public record GrupoDTO(
        long id, String nome,
        Long alunoAdminId,
        List<Long> alunosIds,
        List<Long> professoresIds,
        String periodo
) {}
