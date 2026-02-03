package com.br.SAM_FullStack.SAM_FullStack.service;

import com.br.SAM_FullStack.SAM_FullStack.model.Aluno;
import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import com.br.SAM_FullStack.SAM_FullStack.repository.CursoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class CursoService {

    private final CursoRepository cursoRepository;

    public Curso findById(Long id){
        return cursoRepository.findById(id).orElseThrow(() ->
                new RuntimeException("Curso não encontrado"));
    }

    public List<Curso> findAll(){
        return cursoRepository.findAll();
    }

    public Curso save(Curso curso){
        return cursoRepository.save(curso);
    }

    public Curso update(Long id, Curso cursoUpdate){
        Curso cursoExistente = findById(id);
        cursoExistente.setNome(cursoUpdate.getNome());

        return cursoRepository.save(cursoExistente);
    }

    public void delete(Long id){
        Curso curso = findById(id);
        cursoRepository.delete(curso);
    }

    public List<Curso> saveAll(List<Curso> cursos) {
        for (Curso curso : cursos) {
            this.save(curso);
        }
        return cursos;
    }

    public List<Curso> buscarPorCurso(String curso) {
        return cursoRepository.findByNomeContainingIgnoreCase(curso);
    }

    public List<Curso> buscarPorNomeDaArea(String nomeArea) {
        return cursoRepository.findByAreaDeAtuacaoNomeContainingIgnoreCase(nomeArea);
    }

    public List<Curso> findByCoordenadorId(Long coordenadorId) {
        return cursoRepository.findByCoordenadorId(coordenadorId);
    }
}