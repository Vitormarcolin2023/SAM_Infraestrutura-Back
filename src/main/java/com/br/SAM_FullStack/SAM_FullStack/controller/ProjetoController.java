package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.repository.ProjetoRepository;
import com.br.SAM_FullStack.SAM_FullStack.service.ProjetoService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/projetos")
@RequiredArgsConstructor
@CrossOrigin("*")
public class ProjetoController {

    private final ProjetoService projetoService;
    private ProjetoRepository projetoRepository;

    @GetMapping("/findAll")
    public ResponseEntity<List<Projeto>> listAll() {
        var result = projetoService.listAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Projeto> findById(@PathVariable Long id) {
        Projeto projeto = projetoService.findById(id);
        return ResponseEntity.ok(projeto);
    }

    @GetMapping("/buscar-por-nome")
    public List<Projeto> buscarPorNome(@RequestParam String nome) {
        return projetoService.buscarPorNome(nome);
    }

    @GetMapping("/buscar-por-atuacao")
    public List<Projeto> buscarPorAtuacao(@RequestParam String areaNome) {
        AreaDeAtuacao areaA = new AreaDeAtuacao();
        areaA.setNome(areaNome);
        return projetoService.buscarPorAreaAtuacao(areaA);
    }

    @GetMapping("/buscar-por-periodo")
    public List<Projeto> buscarPorPeriodo(@RequestParam String periodo) {
        return projetoService.findByPeriodo(periodo);
    }

    @PostMapping("/save")
    public ResponseEntity<Projeto> save(@RequestBody Projeto projeto) {
        Projeto savedProjeto = projetoService.save(projeto);
        return ResponseEntity.ok(savedProjeto);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<Projeto> update(@PathVariable Long id, @RequestBody Projeto projeto) {
        Projeto projetoAtualizado = projetoService.update(id, projeto);
        return ResponseEntity.ok(projetoAtualizado);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        projetoService.delete(id);
        return ResponseEntity.ok("Projeto excluído com sucesso");
    }

    @GetMapping("/mentor/{id}")
    public ResponseEntity<List<Projeto>> findByMentor(@PathVariable Long id) {
        List<Projeto> projetos = projetoService.findByMentor(id);
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Projeto>> buscarProjetosPorProfessor(@PathVariable("professorId") Long professorId) {

        List<Projeto> projetos = projetoService.buscarProjetosPorProfessor(professorId);

        if (projetos.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(projetos);
    }

    @GetMapping("/buscar-projeto-ativo/{alunoId}")
    public ResponseEntity<Projeto> buscarProjetoAtivo(@PathVariable Long alunoId){
        Projeto projeto = projetoService.buscarProjetoAtivo(alunoId);
        System.out.println(projeto);
        return ResponseEntity.ok(projeto);
    }

    @GetMapping ("/buscar-projetos-ativos-mentor/{mentorId}")
    public ResponseEntity<List<Projeto>> buscarProjetosAtivosMentor(@PathVariable Long mentorId){
        List<Projeto> response = projetoService.buscarProjetosAtivosMentores(mentorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/buscar-projetos-nao-avaliados-mentor/{mentorId}")
    public ResponseEntity<List<Projeto>> buscarProjetosNaoAvaliadosMentor(@PathVariable Long mentorId) {
        List<Projeto> response = projetoService.buscarProjetosAguardandoAvaliacaoMentor(mentorId);
        return ResponseEntity.ok(response);
    }

    @GetMapping ("/buscar-projeto-nao-avaliado-aluno/{alunoId}")
    public ResponseEntity<Projeto> buscarProjetoNaoAvalidadoAluno(@PathVariable Long alunoId) {
        Projeto response = projetoService.buscarProjetoAguardandoAvaliacaoAluno(alunoId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/grupo/{id}")
    public ResponseEntity<Projeto> findByGrupo(@PathVariable Long id) {
        // Tenta buscar o projeto pelo ID do grupo
        Projeto projeto = projetoService.findByGrupo(id);

        // Se encontrar, retorna 200 OK com o projeto
        if (projeto != null) {
            return ResponseEntity.ok(projeto);
        }

        // Se NÃO encontrar, retorna 404 Not Found.
        // Isso é importante para o seu Front-end saber que o grupo está livre.
        return ResponseEntity.notFound().build();
    }

}