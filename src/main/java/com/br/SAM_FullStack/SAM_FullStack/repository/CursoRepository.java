package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Curso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CursoRepository extends JpaRepository<Curso, Long> {

    //Busca uma lista de cursos cujo nome contenha um determinado texto.
    List<Curso> findByNomeContainingIgnoreCase(String curso);

    // Busca cursos cujo NOME da área de atuação contenha o texto informado.
    List<Curso> findByAreaDeAtuacaoNomeContainingIgnoreCase(String nomeArea);

    List<Curso> findByCoordenadorId(Long coordenadorId);

}
