package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.ReuniaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.model.Reuniao;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusReuniao;
import com.br.SAM_FullStack.SAM_FullStack.service.ReuniaoService1;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/reunioes")
public class ReuniaoController1 {

    @Autowired
    private final ReuniaoService1 reuniaoService;

    // Listar todas as reuniões
    @GetMapping("/findAll")
    public ResponseEntity<List<Reuniao>> findAll() {
        List<Reuniao> result = reuniaoService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Buscar reunião por ID
    @GetMapping("/findById/{id}")
    public ResponseEntity<Reuniao> findById(@PathVariable long id) {
        Reuniao result = reuniaoService.findById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Listar reuniões de um grupo
    @GetMapping("/findByGrupo/{id}")
    public ResponseEntity<List<Reuniao>> findAllByGrupo(@PathVariable long id) {
        List<Reuniao> result = reuniaoService.findAllByGrupo(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Listar reuniões de um mentor
    @GetMapping("/findByMentor/{idMentor}")
    public ResponseEntity<List<Reuniao>> findAllByMentor(@PathVariable long idMentor) {
        List<Reuniao> result = reuniaoService.findAllByMentor(idMentor);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/findByProjeto/{projetoId}")
    public ResponseEntity<List<Reuniao>> findAllByProjeto(@PathVariable Long projetoId){
        List<Reuniao> result = reuniaoService.findAllByProjeto(projetoId);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }


    @PostMapping("/save")
    public ResponseEntity<String> save(@RequestBody ReuniaoDTO reuniaoDTO) {
        String result = reuniaoService.save(reuniaoDTO);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    // Atualizar reunião existente
    @PutMapping("/update/{id}")
    public ResponseEntity<String> update(@PathVariable long id, @RequestBody Reuniao reuniao) {
        String result = reuniaoService.update(id, reuniao);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/confirmarReuniao/{id}/status/{status}/motivo-cancelamento/{motivo}")
    public ResponseEntity<String> confirmarReuniao(@PathVariable long id, @PathVariable String status, @PathVariable String motivo) {
        StatusReuniao statusEnum = StatusReuniao.valueOf(status.toUpperCase());
        String result = reuniaoService.aceitarReuniao(id, statusEnum, motivo);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    // Deletar reunião
    @DeleteMapping("delete/{id}")
    public ResponseEntity<String> delete(@PathVariable Long id) {
        String result = reuniaoService.delete(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }
}