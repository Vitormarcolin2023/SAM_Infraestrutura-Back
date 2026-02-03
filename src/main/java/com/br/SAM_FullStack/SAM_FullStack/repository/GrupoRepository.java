package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.Grupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusAlunoGrupo;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusGrupo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface GrupoRepository extends JpaRepository<Grupo, Long> {

    List<Grupo> findByAlunosStatusAlunoGrupo(StatusAlunoGrupo statusAlunoGrupo);

    List<Grupo> findByStatusGrupoAndAlunosId (StatusGrupo statusGrupo, long alunoId);

    List<Grupo> findByStatusGrupo(StatusGrupo statusGrupo);

    @Query("SELECT g FROM Grupo g LEFT JOIN FETCH g.alunos a LEFT JOIN FETCH a.curso c LEFT JOIN FETCH c.areaDeAtuacao WHERE g.id = :id")
    Optional<Grupo> findByIdWithAlunos(@Param("id") Long id);

    @Query("SELECT g FROM Grupo g JOIN g.professores p WHERE p.id = :professorId")
    List<Grupo> findGruposByProfessorId(@Param("professorId") Long professorId);
}
