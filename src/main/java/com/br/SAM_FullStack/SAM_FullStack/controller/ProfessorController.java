package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Professor;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.service.ProfessorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

@RestController
@RequestMapping("/api/professor")
public class ProfessorController {

    @Autowired
    private ProfessorService professorService;

    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody Professor professor) {
        String mensagem = this.professorService.save(professor);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@RequestBody Professor professor, @PathVariable long id) {
        String mensagem = this.professorService.update(professor, id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable long id) {
        String mensagem = this.professorService.delete(id);
        return new ResponseEntity<>(mensagem, HttpStatus.OK);
    }

    @GetMapping("/findAll")
    public ResponseEntity<List<Professor>> findAll() {
        List<Professor> lista = this.professorService.findAll();
        return new ResponseEntity<>(lista, HttpStatus.OK);
    }

    @GetMapping("/mentores")
    public ResponseEntity<List<Mentor>> findAllMentores() {
        List<Mentor> mentores = this.professorService.findAllMentores();
        return new ResponseEntity<>(mentores, HttpStatus.OK);
    }

    @GetMapping("/projetos")
    public ResponseEntity<List<Projeto>> findAllProjetos() {
        List<Projeto> projetos = this.professorService.findAllProjetos();
        return new ResponseEntity<>(projetos, HttpStatus.OK);
    }

    @GetMapping("/buscar-por-email")
    public ResponseEntity<Professor> getProfessorPorEmail(@RequestParam String email) {
        Professor professor = this.professorService.findByEmail(email);

        return Optional.ofNullable(professor)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Professor> getProfessorPorId(@PathVariable Long id) {
        Optional<Professor> optionalProfessor = Optional.ofNullable(this.professorService.findById(id));
        return optionalProfessor
                .map(professor -> new ResponseEntity<>(professor, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/me")
    public ResponseEntity<Professor> getMyProfile() {
        try {
            Professor professor = professorService.getMyProfile();
            return ResponseEntity.ok(professor);
        } catch (UsernameNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        } catch (SecurityException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(null);
        }
    }
}