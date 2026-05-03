package com.br.SAM_FullStack.SAM_FullStack.autenticacao;

import com.br.SAM_FullStack.SAM_FullStack.dto.LoginDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.RespostaLoginDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/auth")
@CrossOrigin("*")
public class AuthController {

    @Autowired
    private AuthService authService;


    @PostMapping("/login")
    public ResponseEntity<RespostaLoginDTO> token(@RequestBody LoginDTO loginDTO) {
        RespostaLoginDTO token = authService.get_token_login(loginDTO);
        return ResponseEntity.ok(token);
    }
}