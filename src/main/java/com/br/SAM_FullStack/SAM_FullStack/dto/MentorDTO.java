package com.br.SAM_FullStack.SAM_FullStack.dto;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
import com.br.SAM_FullStack.SAM_FullStack.model.TipoDeVinculo;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class MentorDTO {
    private String nome;
    private String email;
    private String senha;
    private String cpf;
    private String telefone;
    private TipoDeVinculo tipoDeVinculo;
    private String formacaoDoMentor;
    private String tempoDeExperiencia;
    private AreaDeAtuacao areaDeAtuacao;
    private Endereco endereco;
}
