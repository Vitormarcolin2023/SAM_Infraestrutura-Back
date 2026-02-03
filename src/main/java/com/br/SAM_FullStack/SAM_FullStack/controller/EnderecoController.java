package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.model.Endereco;
import com.br.SAM_FullStack.SAM_FullStack.service.EnderecoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/endereco")
@CrossOrigin("*")
public class EnderecoController {


    //injeção de dependencia via construtor
    private final EnderecoService enderecoService;

    public EnderecoController(EnderecoService enderecoService) {
        this.enderecoService = enderecoService;
    }

    // Listar todos
    @GetMapping("/findAll")
    public ResponseEntity<List<Endereco>> listAll() {
        var result = enderecoService.listAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Buscar por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<Endereco> findById(@PathVariable Long id) {
        return ResponseEntity.ok(enderecoService.findById(id));
    }

    // Salvar
    @PostMapping("/save")
    public ResponseEntity<Endereco> save(@RequestBody Endereco endereco) {
        return ResponseEntity.ok(enderecoService.save(endereco));
    }

    // Atualizar
    @PutMapping("/update/{id}")
    public ResponseEntity<Endereco> update(@PathVariable Long id, @RequestBody Endereco endereco) {
        return ResponseEntity.ok(enderecoService.update(id, endereco));
    }

    // Deletar
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        enderecoService.delete(id);
        return ResponseEntity.noContent().build();
    }
}