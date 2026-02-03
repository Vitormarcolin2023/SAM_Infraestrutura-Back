package com.br.SAM_FullStack.SAM_FullStack.repository;

import com.br.SAM_FullStack.SAM_FullStack.model.StatusMentor;
import com.br.SAM_FullStack.SAM_FullStack.model.Mentor;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface MentorRepository extends JpaRepository<Mentor, Long> {

    Optional<Mentor> findByEmailAndSenha(String email, String senha);
    Optional<Mentor> findByEmail(String email);

    List<Mentor> findByAreaDeAtuacaoIdAndStatusMentor(Long idArea, StatusMentor status);
}
