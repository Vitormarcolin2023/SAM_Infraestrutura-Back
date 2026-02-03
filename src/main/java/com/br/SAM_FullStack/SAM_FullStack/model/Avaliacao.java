package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tb_avaliacao")
public class Avaliacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // As respostas DEVEM ser @NotNull
    @NotNull(message = "O campo 'resposta1' não pode ser nulo")
    private Integer resposta1;

    @NotNull(message = "O campo 'resposta2' não pode ser nulo")
    private Integer resposta2;

    @NotNull(message = "O campo 'resposta3' não pode ser nulo")
    private Integer resposta3;

    @NotNull(message = "O campo 'resposta4' não pode ser nulo")
    private Integer resposta4;

    @NotNull(message = "O campo 'resposta5' não pode ser nulo")
    private Integer resposta5;

    @NotNull(message = "O campo 'resposta6' não pode ser nulo")
    private Integer resposta6;

    @Column(name = "media")
    private Double media;

    private String comentario;

    private Boolean recomendacao;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "projeto_id")
    @JsonIgnoreProperties("avaliacoes")
    private Projeto projeto;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "aluno_id")
    @JsonIgnoreProperties("avaliacoesRespondidas")
    private Aluno aluno;

}