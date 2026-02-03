package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
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
@Table(name = "tb_coordenacao")
public class Coordenador implements UserDetails {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String nome;

    @Column(unique = true, nullable = false)
    @Email
    private String email;

    private String senha;

    @OneToMany(mappedBy = "coordenador", cascade = CascadeType.ALL)
    @JsonIgnoreProperties("coordenador")
    private List<Curso> cursos;



    public void addCurso(Curso curso) {
        if (cursos == null) {
            cursos = new ArrayList<>();
        }
        cursos.add(curso);
        curso.setCoordenador(this);
    }

    public void removeCurso(Curso curso) {
        cursos.remove(curso);
        curso.setCoordenador(null);
    }


    // Metodos obrigatórios do Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_COORDENADOR"));
        return authorities;
    }

    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

    @JsonIgnore
    private List<GrantedAuthority> authorities;
}