package com.br.SAM_FullStack.SAM_FullStack.repository;


import com.br.SAM_FullStack.SAM_FullStack.model.AreaDeAtuacao;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AreaDeAtuacaoRepository extends JpaRepository<AreaDeAtuacao, Long> {

    //Busca uma área de atuação pelo nome exato.
    Optional<AreaDeAtuacao> findByNome(String nome);

    //Busca áreas de atuação cujo nome comece com um determinado texto (útil para autocompletar).
    List<AreaDeAtuacao> findByNomeStartingWithIgnoreCase(String prefixo);


}
