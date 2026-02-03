package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.CoordenadorUpdateDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Coordenador;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.CoordenadorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/coordenador")
public class CoordenadorController {

    @Autowired
    private CoordenadorService coordenadorService;
    @Autowired
    private TokenService tokenService;

    @PostMapping("/save")
    public ResponseEntity<Coordenador> save(@Valid @RequestBody CoordenadorDTO coordenadorDTO) {
        Coordenador novoCoordenador = this.coordenadorService.save(coordenadorDTO);
        return new ResponseEntity<>(novoCoordenador, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody CoordenadorUpdateDTO coordenadorDTO, @PathVariable long id) {
        String mensagem = this.coordenadorService.update(coordenadorDTO, id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        coordenadorService.delete(id);
        return ResponseEntity.ok("Coordenador excluído com sucesso");
    }

    @PutMapping("/ativarMentor/{id}")
    public ResponseEntity<String> ativarMentor(@PathVariable long id) {
        String mensagem = this.coordenadorService.ativarMentor(id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @PutMapping("/inativarMentor/{id}")
    public ResponseEntity<String> inativarMentor(@PathVariable long id) {
        String mensagem = this.coordenadorService.inativarMentor(id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @GetMapping("/mentores")
    public ResponseEntity<List<Mentor>> findAllMentores() {
        List<Mentor> mentores = this.coordenadorService.findAllMentores();
        return new ResponseEntity<>(mentores, HttpStatus.OK);
    }

    @GetMapping("/projetos")
    public ResponseEntity<List<Projeto>> findAllProjetos() {
        List<Projeto> projetos = this.coordenadorService.findAllProjetos();
        return new ResponseEntity<>(projetos, HttpStatus.OK);
    }

    @GetMapping("/buscar-por-email")
    public ResponseEntity<Coordenador> buscarPorEmail(@RequestParam("email") String email) {
        Coordenador coordenador = coordenadorService.buscarPorEmail(email);
        if (coordenador != null) {
            return ResponseEntity.ok(coordenador);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Coordenador> getCoordenadorPorId(@PathVariable Long id) {
        Optional<Coordenador> optionalCoordenador = Optional.ofNullable(this.coordenadorService.findById(id));

        return optionalCoordenador
                .map(coordenador -> new ResponseEntity<>(coordenador, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/me")
    public ResponseEntity<Coordenador> getCoordenadorProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // remove "Bearer "
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = tokenService.extractEmail(token);

        Coordenador coordenador = coordenadorService.buscarPorEmail(email);

        if (coordenador != null) {
            return ResponseEntity.ok(coordenador);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }
}