package com.br.SAM_FullStack.SAM_FullStack.dto;

import com.br.SAM_FullStack.SAM_FullStack.model.FormatoReuniao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.Date;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ReuniaoDTO {

    @NotBlank(message = "O campo 'assunto' não pode ser nulo")
    private String assunto;

    @NotNull(message = "É necessário informar a data da reunião")
    private Date data;

    @NotNull(message = "É necessário informar a hora da reunião")
    private LocalTime hora;

    private FormatoReuniao formatoReuniao;

    @NotNull(message = "O ID do mentor é obrigatório")
    private long projeto_id;

    private String solicitadoPor;
}
