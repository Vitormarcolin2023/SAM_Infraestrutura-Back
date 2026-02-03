package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RespostaLoginDTO {

    private String token;

    public RespostaLoginDTO(String token){this.token = token;}

    private String role;
    private String email;
    private String status;

}
