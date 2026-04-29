package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/alunos")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://localhost:4200")
public class AlunoController {

    private final AlunoService alunoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Aluno>> findAll(){
        List<Aluno> result = alunoService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Aluno> findById(@PathVariable Long id) {
        Aluno aluno = alunoService.findById(id);
        return ResponseEntity.ok(aluno);
    }

    @PostMapping("/save")
    public ResponseEntity<String> save(@Valid @RequestBody Aluno aluno) {
        Aluno result = alunoService.save(aluno);
        return ResponseEntity.status(HttpStatus.CREATED).body("Aluno cadastrado com sucesso!");
    }

    //atualizar
    @PutMapping("/update/{id}")
    public ResponseEntity<Aluno> update (@PathVariable Long id, @Valid @RequestBody Aluno alunoUpdate){
        Aluno alunoAtualizado = alunoService.update(id, alunoUpdate);
        return ResponseEntity.ok(alunoAtualizado);
    }

    //deletar
    @DeleteMapping("/delet/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        alunoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Aluno>> saveAll(@RequestBody List<Aluno> alunos) {
        List<Aluno> alunosSalvos = alunoService.saveAll(alunos);
        return ResponseEntity.status(HttpStatus.CREATED).body(alunosSalvos);
    }

    //buscar-por-nome?nome=silva
    @GetMapping("/buscar-por-nome")
    public ResponseEntity<List<Aluno>> getAlunosPorNome(@RequestParam("nome") String nome) {
        List<Aluno> alunosEncontrados = alunoService.buscarPorNome(nome);
        if (alunosEncontrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(alunosEncontrados);
    }

    //alunos/ordenados-por-nome
    @GetMapping("/ordenados-por-nome")
    public ResponseEntity<List<Aluno>> getAlunoOrdenadosPorNome() {
        List<Aluno> alunosOrdenados = alunoService.buscarTodosOrdenadoPorNome();
        return ResponseEntity.ok(alunosOrdenados);
    }

    @GetMapping("/findByEmail")
    public ResponseEntity<Aluno> findByEmail(@RequestParam("email") String email) {
        Aluno aluno = alunoService.findByEmail(email);
        return ResponseEntity.ok(aluno);
    }


    @GetMapping("/findByCurso/{cursoId}")
    public ResponseEntity<List<Aluno>> findByCurso(@PathVariable Long cursoId) {
        List<Aluno> alunos = alunoService.findByCurso(cursoId);
        return ResponseEntity.ok(alunos);
    }

    @GetMapping("/me")
    public ResponseEntity<Aluno> getAlunoProfile(
            @AuthenticationPrincipal Jwt jwt) {

        String email = jwt.getClaimAsString("email");

        if (email == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        Aluno aluno = alunoService.findByEmail(email);

        if (aluno != null) {
            return ResponseEntity.ok(aluno);
        }

        return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }
}