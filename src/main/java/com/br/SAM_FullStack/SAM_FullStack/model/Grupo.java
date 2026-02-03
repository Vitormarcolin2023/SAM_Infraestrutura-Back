package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

@Entity
@Getter
@Setter
@NoArgsConstructor
@Table(name = "tb_grupo")
public class Grupo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo 'nome' não pode ser nulo")
    private String nome;

    @Enumerated(EnumType.STRING)
    private StatusGrupo statusGrupo;

    @ManyToOne
    @JoinColumn(name = "aluno_admin_id")
    @JsonIgnoreProperties("grupos")
    private Aluno alunoAdmin;


    @ManyToMany
    @JsonIgnoreProperties("grupo")
    @JoinTable(
            //name = "tb_aluno_grupo",
            name = "aluno_grupo",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "aluno_id")
    )
    private List<Aluno> alunos = new ArrayList<>();

    @OneToMany(mappedBy = "grupo", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("grupo")
    private List<Projeto> projetos;

    @ManyToMany
    @JsonIgnoreProperties("grupo")
    @JoinTable(
            name = "tb_grupo_professor",
            joinColumns = @JoinColumn(name = "grupo_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id")
    )
    private List<Professor> professores = new ArrayList<>();

    private String periodo;

    public Grupo(Long id, String nome, StatusGrupo statusGrupo, Aluno alunoAdmin, List<Aluno> alunos) {
        this.id = id;
        this.nome = nome;
        this.statusGrupo = statusGrupo;
        this.alunoAdmin = alunoAdmin;
        this.alunos = alunos;
    }
}
