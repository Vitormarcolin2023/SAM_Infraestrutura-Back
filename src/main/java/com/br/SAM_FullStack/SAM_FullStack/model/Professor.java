package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_professor")
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
public class Professor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(unique = true)
    @JsonIgnore
    private String keycloakId;

    @Column(unique = true)
    @Email
    private String email;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "tb_curso_professor",
            joinColumns = @JoinColumn(name = "professor_id"),
            inverseJoinColumns = @JoinColumn(name = "curso_id")
    )
    @JsonIgnoreProperties("professores")
    private List<Curso> cursos;

    @ManyToMany(mappedBy = "professores")
    @JsonIgnoreProperties("professores")
    private List<Projeto> projetos;

    @ManyToMany(mappedBy = "professores")
    @JsonIgnoreProperties("professores")
    @JsonIgnore
    private List<Grupo> grupos = new ArrayList<>();

}