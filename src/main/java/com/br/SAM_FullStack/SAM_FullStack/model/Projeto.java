package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_projeto")

public class Projeto {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotBlank(message = "O campo nome do projeto é obrigatório")
    @Column(name = "nome_do_projeto", nullable = false, length = 45)
    private String nomeDoProjeto;

    @NotBlank(message = "Favor adicionar uma descrição do projeto")
    @Column(name = "descricao", nullable = false)
    private String descricao;

    @ManyToOne
    @JoinColumn(name = "area_de_atuacao_id", nullable = false)
    private AreaDeAtuacao areaDeAtuacao;

    @NotNull(message = "A data de inicio é obrigatória")
    @Column(name = "data_inicio_projeto", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataInicioProjeto;

    @NotNull(message = "A data para conclusão do projeto é obrigatório")
    @Column(name = "data_final_projeto", nullable = false)
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dataFinalProjeto;

    @NotBlank(message = "É obrigatório adicionar o periodo da faculdade")
    @Column(name = "periodo", nullable = false)
    private String periodo;

    @ManyToOne
    @JoinColumn(name = "mentor_id")
    @JsonIgnoreProperties("projetos")
    private Mentor mentor;

    @Enumerated(EnumType.STRING)
    private StatusProjeto statusProjeto;

    @ManyToOne(cascade = CascadeType.ALL)
    @JsonIgnoreProperties("projetos")
    private Grupo grupo;

    @ManyToMany
    @JoinTable(
            name = "tb_projeto_professor",
            joinColumns = @JoinColumn(name = "projeto_id"),
            inverseJoinColumns = @JoinColumn(name = "professor_id"))
    @JsonIgnoreProperties("projetos")
    private List<Professor> professores;

    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("projeto")
    private Set<Avaliacao> avaliacoes;


    @OneToMany(mappedBy = "projeto", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnoreProperties("projeto")
    private List<Reuniao> reunioes = new ArrayList<>();

    private Boolean avaliadoPorMentor;
}
