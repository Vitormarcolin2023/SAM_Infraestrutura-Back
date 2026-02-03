package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.service.AreaDeAtuacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/areas")
@RequiredArgsConstructor
@CrossOrigin("*")
public class AreaDeAtuacaoController {

    private final AreaDeAtuacaoService areaDeAtuacaoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<AreaDeAtuacao>> findAll(){
        List<AreaDeAtuacao> result = areaDeAtuacaoService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PostMapping("/save")
    public ResponseEntity<AreaDeAtuacao> save(@RequestBody AreaDeAtuacao areaDeAtuacao) {
        AreaDeAtuacao result = areaDeAtuacaoService.save(areaDeAtuacao);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/update/{id}")
    public ResponseEntity<AreaDeAtuacao> update (@PathVariable Long id, @Valid @RequestBody AreaDeAtuacao areaDeAtuacaoUpdate){
        AreaDeAtuacao areaDeAtuacaoAtualizado = areaDeAtuacaoService.update(id, areaDeAtuacaoUpdate);
        return ResponseEntity.ok(areaDeAtuacaoAtualizado);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete (@PathVariable Long id){
        areaDeAtuacaoService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/batch")
    public ResponseEntity<List<AreaDeAtuacao>> saveAll(@RequestBody List<AreaDeAtuacao> areaDeAtuacoes) {
        List<AreaDeAtuacao> areaDeAtuacoesSalvos = areaDeAtuacaoService.saveAll(areaDeAtuacoes);
        return ResponseEntity.status(HttpStatus.CREATED).body(areaDeAtuacoesSalvos);
    }

    //areas/buscar-por-inicio?prefixo=e
    @GetMapping("/buscar-por-inicio")
    public ResponseEntity<List<AreaDeAtuacao>> getAreaPorInicioDoNome(@RequestParam("prefixo") String prefixo) {
        List<AreaDeAtuacao> areasEncontradas = areaDeAtuacaoService.buscarPorInicioDoNome(prefixo);
        if (areasEncontradas.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(areasEncontradas);
    }

    @GetMapping("/por-aluno-logado")
    public ResponseEntity<AreaDeAtuacao> getAreaDeAtuacaoDoAlunoLogado() {
        AreaDeAtuacao area = areaDeAtuacaoService.findByAlunoLogado();

        if (area != null) {
            return ResponseEntity.ok(area);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}