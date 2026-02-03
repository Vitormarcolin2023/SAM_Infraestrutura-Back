package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum StatusMentor {
    PENDENTE,
    ATIVO,
    INATIVO;

    @JsonCreator
    public static StatusMentor from(String value) {
        return StatusMentor.valueOf(value.toUpperCase());
    }
}
