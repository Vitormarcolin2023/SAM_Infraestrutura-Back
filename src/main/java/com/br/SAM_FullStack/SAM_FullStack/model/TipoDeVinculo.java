package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonCreator;

public enum TipoDeVinculo {
    CLT,
    PJ,
    AUTONOMO;

    @JsonCreator
    public static StatusMentor from(String value) {
        return StatusMentor.valueOf(value.toUpperCase());
    }
}
