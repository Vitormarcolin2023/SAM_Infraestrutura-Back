package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@Entity
@Table(name = "tb_aluno")
@Getter
@Setter
@ToString
@NoArgsConstructor
public class Aluno implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @NotNull(message = "O campo RA é obrigatório")
    private Integer ra;

    private String senha;


    @NotBlank(message = "O campo e-mail é obrigatório")
    @Email(message = "O e-mail informado não é válido") // Validação extra para formato de e-mail
    @Email
    private String email;

    @NotNull(message = "O curso do aluno é obrigatório")
    @ManyToOne
    @JoinColumn(name = "curso_id", nullable = false)
    @JsonIgnoreProperties("alunos")
    private Curso curso;

    @Enumerated(EnumType.STRING)
    private StatusAlunoGrupo statusAlunoGrupo;

    @ManyToMany(mappedBy = "alunos") // lado inverso
    @JsonIgnore
    private List<Grupo> grupos = new ArrayList<>();

    @OneToMany(mappedBy = "aluno")
    @JsonIgnore
    private List<Avaliacao> avaliacoesRespondidas = new ArrayList<>();


    public Aluno(Long id, String nome, Integer ra, String senha, String email, Curso curso, StatusAlunoGrupo statusAlunoGrupo) {
        this.id = id;
        this.nome = nome;
        this.ra = ra;
        this.senha = senha;
        this.email = email;
        this.curso = curso;
        this.statusAlunoGrupo = statusAlunoGrupo;
    }

    // Metodos obrigatórios do Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_ALUNO"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @JsonIgnore
    private List<GrantedAuthority> authorities;

    @Override
    public String getUsername() {
        return email;
    }

}