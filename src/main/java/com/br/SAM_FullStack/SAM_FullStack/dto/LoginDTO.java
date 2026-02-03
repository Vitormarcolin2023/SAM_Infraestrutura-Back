package com.br.SAM_FullStack.SAM_FullStack.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class LoginDTO {

    private String email;
    private String senha;
    private String role;

    public LoginDTO(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
}
