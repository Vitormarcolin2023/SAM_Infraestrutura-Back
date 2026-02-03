package com.br.SAM_FullStack.SAM_FullStack.controller;

import com.br.SAM_FullStack.SAM_FullStack.dto.AdicionarAlunoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.AnalizarExclusaoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoDTO;
import com.br.SAM_FullStack.SAM_FullStack.dto.GrupoUpdateDTO; // DTO para a função de update
import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import com.br.SAM_FullStack.SAM_FullStack.service.AlunoService;
import com.br.SAM_FullStack.SAM_FullStack.service.GrupoService;
import lombok.RequiredArgsConstructor;
import org.hibernate.event.spi.SaveOrUpdateEvent;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@RestController
@RequestMapping("/grupos")
@CrossOrigin("*")
public class GrupoController {

    private final GrupoService grupoService;
    private final AlunoService alunoService;

    @GetMapping("/findAll")
    public ResponseEntity<List<Grupo>> findAll() {
        List<Grupo> result = grupoService.findAll();
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/findById/{id}")
    public ResponseEntity<Grupo> findById(@PathVariable long id) {
        Grupo result = grupoService.findById(id);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/findByAluno/{alunoId}")
    public ResponseEntity<Grupo> findByAlunoId(@PathVariable Long alunoId) {
        Aluno aluno = alunoService.findById(alunoId);
        if (aluno != null) {
            Grupo result = grupoService.findByAluno(aluno);
            //System.out.println("GRUPO: " + result.getNome());
            return new ResponseEntity<>(result, HttpStatus.OK);
        }
        return new ResponseEntity<>(null, HttpStatus.NOT_FOUND);
    }

    @PostMapping("/save")
    public ResponseEntity<GrupoDTO> save(@RequestBody GrupoDTO grupo) {
        GrupoDTO result = grupoService.save(grupo);
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @PutMapping("/update/{groupId}/admin/{adminId}")
    public ResponseEntity<String> updateGrupoInfo(
            @PathVariable Long groupId,
            @PathVariable Long adminId,
            @RequestBody GrupoUpdateDTO grupoUpdateDTO) {
        String result = grupoService.updateGrupoInfo(groupId, adminId, grupoUpdateDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/adicionar-aluno")
    public ResponseEntity<String> adicionarAluno(@RequestBody AdicionarAlunoDTO dto) {
        String result = grupoService.adicionarAlunoAoGrupo(dto.getIdAdmin(), dto.getIdGrupo(), dto.getIdAluno());
        return new ResponseEntity<>(result, HttpStatus.CREATED);
    }

    @DeleteMapping("/{idGrupo}/remover-aluno/{idAlunoRemover}/admin/{idAdmin}")
    public ResponseEntity<String> removerAlunoDiretamente(
            @PathVariable Long idGrupo,
            @PathVariable Long idAlunoRemover,
            @PathVariable Long idAdmin) {
        String resultado = grupoService.removerAlunoDiretamente(idGrupo, idAlunoRemover, idAdmin);
        return ResponseEntity.ok(resultado);
    }

    @GetMapping("/findSolicitacoes")
    public ResponseEntity<List<Grupo>> findByAlunosStatusAlunoGrupo() {
        List<Grupo> result = grupoService.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @PutMapping("/analizarSolicitacao")
    public ResponseEntity<String> analizarExclusaoAluno(@RequestBody AnalizarExclusaoDTO pedido) {
        String result = grupoService.analizarExclusaoAluno(
                pedido.getSenhaProf(),
                pedido.getIdGrupo(),
                pedido.getIdAluno(),
                pedido.isResposta()
        );
        return ResponseEntity.ok(result);
    }

    @DeleteMapping("/delete/{idGrupo}/professor/{idProfessor}")
    public ResponseEntity<String> deletarGrupo(@PathVariable Long idGrupo, @PathVariable Long idProfessor) {
        String result = grupoService.deletarGrupo(idGrupo, idProfessor);
        return new ResponseEntity<>(result, HttpStatus.OK);
    }

    @GetMapping("/por-aluno-logado")
    public ResponseEntity<Grupo> getGrupoDoAlunoLogado(@AuthenticationPrincipal Aluno alunoLogado) {
        if (alunoLogado == null) {
            return ResponseEntity.status(401).build();
        }
        Grupo grupo = grupoService.findByAluno(alunoLogado);
        if (grupo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(grupo);
    }

    @PutMapping("/arquivar/{id}")
    public ResponseEntity<String> arquivarGrupo(@PathVariable long id){
        String result = grupoService.arquivarGrupo(id);
        return ResponseEntity.ok(result);
    }

        @GetMapping("/findByGruposArquivados/{id}")
        public ResponseEntity<List<Grupo>> findByGruposArquivados(@PathVariable long id){
        List<Grupo> response = grupoService.findByGruposArquivados(id);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/professor/{professorId}")
    public ResponseEntity<List<Grupo>> getGruposByProfessorId(@PathVariable Long professorId) {
        try {
            List<Grupo> grupos = grupoService.findGruposByProfessorId(professorId);
            if (grupos.isEmpty()) {
                return ResponseEntity.noContent().build();
            }
            return ResponseEntity.ok(grupos);
        } catch (Exception e) {
            // Adicione um log de erro aqui
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}