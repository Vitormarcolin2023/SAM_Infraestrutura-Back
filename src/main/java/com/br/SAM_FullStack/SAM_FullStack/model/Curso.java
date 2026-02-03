package com.br.SAM_FullStack.SAM_FullStack.model;

import jakarta.persistence.*;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@Data
@Entity
@Table(name = "tb_curso")
@Getter
@Setter
@ToString
public class Curso {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @ManyToOne
    @JoinColumn(name = "area_de_atuacao_id", nullable = false)
    @JsonIgnoreProperties("cursos")
    private AreaDeAtuacao areaDeAtuacao;

    @OneToMany(mappedBy = "curso")
    @ToString.Exclude
    @JsonIgnoreProperties("curso")
    private List<Aluno> alunos;

    @ManyToOne
    @JoinColumn(name = "coordenador_id")
    @JsonIgnoreProperties("cursos")
    private Coordenador coordenador;

    @ManyToMany(mappedBy = "cursos")
    @JsonIgnoreProperties("cursos")
    private List<Professor> professores;

    public Curso(Long id, String nome, AreaDeAtuacao areaDeAtuacao) {
        this.id = id;
        this.nome = nome;
        this.areaDeAtuacao = areaDeAtuacao;
    }

    public Curso() {

    }
}
