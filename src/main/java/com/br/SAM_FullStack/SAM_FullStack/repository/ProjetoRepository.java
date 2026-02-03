package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import com.br.SAM_FullStack.SAM_FullStack.model.Projeto;
import com.br.SAM_FullStack.SAM_FullStack.model.StatusProjeto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProjetoRepository extends JpaRepository <Projeto, Long> {

    List<Projeto> findByNomeDoProjetoIgnoreCaseContaining(String nomeDoProjeto);
    List<Projeto> findByPeriodo(String periodo);

    List<Projeto> findByMentorId(Long mentorId);
    List<Projeto> findByAreaDeAtuacao(AreaDeAtuacao areaDeAtuacao);
    List<Projeto> findAllByProfessoresId(Long professorId);
    List<Projeto> findAllByMentorIdAndStatusProjeto(Long professorId, StatusProjeto statusProjeto);

    List<Projeto> findAllByStatusProjeto(StatusProjeto statusProjeto);

    @Query("SELECT p FROM Projeto p JOIN p.grupo g JOIN g.alunos a " +
            "WHERE a.id = :alunoId AND p.statusProjeto = :status")
    Optional<Projeto> findProjetoPorStatusAluno(@Param("alunoId") Long alunoId,
                                                @Param("status") StatusProjeto status);

    Optional<Projeto> findByGrupoId(Long grupoId);


}
