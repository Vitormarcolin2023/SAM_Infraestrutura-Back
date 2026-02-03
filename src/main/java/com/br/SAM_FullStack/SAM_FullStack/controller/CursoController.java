package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.service.CursoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/cursos")
@RequiredArgsConstructor
public class CursoController {

    private final CursoService cursoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Curso>> findAll(){
        List<Curso> result = cursoService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<Curso> save(@RequestBody Curso curso) {
        Curso result = cursoService.save(curso);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Curso> update (@PathVariable Long id, @Valid @RequestBody Curso cursoUpdate){
        Curso cursoAtualizado = cursoService.update(id, cursoUpdate);
        return ResponseEntity.ok(cursoAtualizado);
    }

    @DeleteMapping("/delet/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        cursoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<Curso>> saveAll(@RequestBody List<Curso> cursos) {
        List<Curso> cursosSalvos = cursoService.saveAll(cursos);
        return ResponseEntity.status(HttpStatus.CREATED).body(cursosSalvos);
    }

    //buscar-por-curso?curso=sistemas
    @GetMapping("/buscar-por-curso")
    public ResponseEntity<List<Curso>> getCursosPorNome(@RequestParam("curso") String curso) {
        List<Curso> cursosEncontrados = cursoService.buscarPorCurso(curso);
        if (cursosEncontrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cursosEncontrados);
    }

    //buscar-por-nome-area?nomeArea=saude
    @GetMapping("/buscar-por-nome-area")
    public ResponseEntity<List<Curso>> getCursosPorNomeDaArea(@RequestParam("nomeArea") String nomeArea) {
        List<Curso> cursosEncontrados = cursoService.buscarPorNomeDaArea(nomeArea);
        if (cursosEncontrados.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(cursosEncontrados);
    }

    @GetMapping("/coordenador/{coordenadorId}")
    public ResponseEntity<List<Curso>> getCursosByCoordenadorId(@PathVariable Long coordenadorId) {
        List<Curso> cursos = cursoService.findByCoordenadorId(coordenadorId);
        if (cursos.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(cursos);
    }
}