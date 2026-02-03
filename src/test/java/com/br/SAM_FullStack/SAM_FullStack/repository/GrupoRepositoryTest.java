package com.br.SAM_FullStack.SAM_FullStack.repository;


import com.br.SAM_FullStack.SAM_FullStack.model.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
public class GrupoRepositoryTest {

    @Autowired
    private TestEntityManager entityManager;

    @Autowired
    private GrupoRepository grupoRepository;

    private Aluno aluno1;
    private Aluno aluno2;
    private Aluno aluno3;
    private Grupo grupo;
    private Grupo grupo2;

    @BeforeEach
    void setUp(){
        AreaDeAtuacao area = new AreaDeAtuacao(null, "Tecnologia");
        entityManager.persistAndFlush(area);

        Curso curso = new Curso(null, "Engenharia de Software", area);
        entityManager.persistAndFlush(curso);

        aluno1 =  new Aluno(null, "Joana Silveira", 1001, "senha123", "joana@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno2 = new Aluno(null, "Anderson Ribeiro", 1002, "senha123", "ander@gmail.com", curso, StatusAlunoGrupo.ATIVO);
        aluno3  = new Aluno(null, "Benicio Fragoso", 1003, "senha123", "benicio@gmail.com", curso, StatusAlunoGrupo.AGUARDANDO);
        entityManager.persistAndFlush(aluno1);
        entityManager.persistAndFlush(aluno2);
        entityManager.persistAndFlush(aluno3);

        grupo = new Grupo(null, "Grupo Ativo", StatusGrupo.ATIVO, aluno2, List.of(aluno1, aluno2, aluno3));
        grupo2 = new Grupo(null, "Grupo Arquivado", StatusGrupo.ARQUIVADO, aluno1, List.of(aluno1, aluno2, aluno3));
        entityManager.persistAndFlush(grupo);
        entityManager.persistAndFlush(grupo2);
    }

    @Test
    @DisplayName("Deve retornar lista com os grupos que possuem alunos com status AGUARDANDO que estão aguardando exclusão")
    void findByAlunosStatusAlunoGrupo_deveRetornarListaDeGruposPeloStatusDoAluno(){
        List<Grupo> retorno = grupoRepository.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);

        assertTrue(!retorno.isEmpty(), "A lista de grupos não deve estar vazia");
        assertEquals("Grupo Ativo", retorno.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar lista de grupos arquivados do aluno")
    void findByStatusGrupoAndAlunosId_deveRetornarListaDeGruposArquivados(){
        List<Grupo> retorno = grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ARQUIVADO, aluno1.getId());

        assertTrue(!retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals("Grupo Arquivado", retorno.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar lista de grupos ativos do aluno")
    void findByStatusGrupoAndAlunosId_deveRetornarListaDeGruposAtivos(){
        List<Grupo> retorno = grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, aluno1.getId());

        assertTrue(!retorno.isEmpty(), "A lista não deve estar vazia");
        assertEquals("Grupo Ativo", retorno.get(0).getNome());
    }

    @Test
    @DisplayName("Deve retornar grupo com alunos com base no Id do grupo")
    void findByIdWithAlunos_deveRetornarGrupo(){
        Optional<Grupo> retorno = grupoRepository.findByIdWithAlunos(grupo.getId());

        assertTrue(retorno.isPresent(), "Grupo não deve estar vazio");
        assertEquals("Grupo Ativo", retorno.get().getNome());
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando não houver alunos aguardando")
    void findByAlunosStatusAlunoGrupo_quandoNaoHouverAlunosAguardando_deveRetornarListaVazia() {
        aluno3.setStatusAlunoGrupo(StatusAlunoGrupo.ATIVO);
        List<Grupo> retorno = grupoRepository.findByAlunosStatusAlunoGrupo(StatusAlunoGrupo.AGUARDANDO);

        assertTrue(retorno.isEmpty(), "A lista deveria estar vazia pois nenhum aluno tem status PENDENTE");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o aluno não fizer parte de grupos arquivados")
    void findByStatusGrupoAndAlunosId_quandoAlunoNaoTemGrupoArquivado_deveRetornarListaVazia() {
        Aluno alunoSemGrupoArquivado = new Aluno(null, "Laura Silva", 2001, "senha123", "laura@gmail.com", aluno1.getCurso(), StatusAlunoGrupo.ATIVO);
        entityManager.persistAndFlush(alunoSemGrupoArquivado);

        List<Grupo> retorno = grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ARQUIVADO, alunoSemGrupoArquivado.getId());
        assertTrue(retorno.isEmpty(), "A lista deveria estar vazia pois o aluno não tem grupo arquivado");
    }

    @Test
    @DisplayName("Deve retornar Optional vazio quando o grupo não existir")
    void findByIdWithAlunos_quandoGrupoNaoExiste_deveRetornarOptionalVazio() {
        Optional<Grupo> retorno = grupoRepository.findByIdWithAlunos(-99L);

        assertTrue(retorno.isEmpty(), "Era esperado Optional vazio pois o ID do grupo não existe");
    }

    @Test
    @DisplayName("Deve retornar lista vazia quando o aluno não estiver em grupos ativos")
    void findByStatusGrupoAndAlunosId_quandoAlunoNaoTemGrupoAtivo_deveRetornarListaVazia() {
        Aluno alunoSemGrupoAtivo = new Aluno(null, "Ricardo Alves", 2002, "senha123", "ricardo@gmail.com", aluno1.getCurso(), StatusAlunoGrupo.ATIVO);
        entityManager.persistAndFlush(alunoSemGrupoAtivo);

        List<Grupo> retorno = grupoRepository.findByStatusGrupoAndAlunosId(StatusGrupo.ATIVO, alunoSemGrupoAtivo.getId());
        assertTrue(retorno.isEmpty(), "A lista deveria estar vazia pois o aluno não tem grupo ativo");
    }



}
