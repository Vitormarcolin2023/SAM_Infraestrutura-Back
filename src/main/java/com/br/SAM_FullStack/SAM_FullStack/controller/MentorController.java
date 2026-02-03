package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.autenticacao.TokenService;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.service.MentorService;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/mentores")
@CrossOrigin("*")
public class MentorController {

    @Autowired
    TokenService tokenService;
    private final MentorService mentorService;
    @Autowired
    private ProjetoService projetoService;

    public MentorController(MentorService mentorService) {
        this.mentorService = mentorService;
    }

    //listar
    @GetMapping("/findAll")
    public ResponseEntity<List<Mentor>> listAll() {
        var result = mentorService.listAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    //buscar pelo Id
    @GetMapping("/findById/{id}")
    public ResponseEntity<Mentor> findById(@PathVariable Long id) {
        Mentor mentor = mentorService.findById(id);
        return ResponseEntity.ok(mentor);
    }

    //salvar
    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Mentor mentor) {
        Mentor savedMentor = mentorService.save(mentor);
        // Retorna o mentor salvo se a operação for bem-sucedida.
        return ResponseEntity.status(HttpStatus.CREATED).body("Mentor cadastrado com sucesso!");
    }


    //update
    @PutMapping("/update/{id}")
    public ResponseEntity<Mentor> update(@PathVariable Long id, @RequestBody Mentor mentor) {
        // Tenta realizar a atualização do mentor
        Mentor mentorAtualizado = mentorService.update(id, mentor);
        // Se a atualização for bem-sucedida, retorna o mentor com status 200 OK
        return ResponseEntity.ok(mentorAtualizado);
    }

    //delete
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        // Tenta excluir o mentor
        mentorService.delete(id);
        // Se a exclusão for bem-sucedida, retorna a mensagem de sucesso
        return ResponseEntity.ok("Mentor excluído com sucesso");
    }

    @PutMapping("/mentor/{id}/desvincular-projetos")
    public ResponseEntity<?> desvincularProjetos(@PathVariable Long id) {
        projetoService.desvincularMentor(id); // atualiza mentor_id para NULL
        return ResponseEntity.ok().build();
    }


    @GetMapping("/me")
    public ResponseEntity<Mentor> getMentorProfile(
            @RequestHeader("Authorization") String authorizationHeader) {

        String token = null;
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            token = authorizationHeader.substring(7); // remove "Bearer "
        }

        if (token == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String email = tokenService.extractEmail(token);

        Mentor mentor = mentorService.findByEmail(email);

        if (mentor != null) {
            return ResponseEntity.ok(mentor);
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
        }
    }

    @GetMapping("/area/{id}")
    public ResponseEntity<List<Mentor>> findByAreaDeAtuacao(@PathVariable("id") Long id) {
        List<Mentor> mentores = mentorService.findByArea(id);
        return ResponseEntity.ok(mentores);
    }
}