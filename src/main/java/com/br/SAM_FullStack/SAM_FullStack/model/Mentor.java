package com.br.SAM_FullStack.SAM_FullStack.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*; // Importa todas as anotações do JPA, incluindo @Id
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.br.CPF;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "tb_mentor")
public class Mentor implements UserDetails {

    @Id // Anotação correta do JPA
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "O campo nome é obrigatório")
    private String nome;

    @NotBlank(message = "O campo cpf é obrigatório")
    @CPF
    private String cpf;

    @NotBlank(message = "O campo email é obrigatório")
    @Email
    private String email;

    @NotBlank(message = "O campo senha é obrigatório")
    private String senha;

    @NotBlank(message = "O campo telefone é obrigatório")
    private String telefone;

    @NotNull(message = "A formação do mentor é obrigatório")
    private String formacaoDoMentor;

    private String tempoDeExperiencia;

    @Enumerated(EnumType.STRING)
    private StatusMentor statusMentor;

    @NotNull(message = "O tipo de vínculo do mentor é obrigatório")
    @Enumerated(EnumType.STRING)
    private TipoDeVinculo tipoDeVinculo;

    @ManyToOne
    @JoinColumn(name = "area_de_atuacao_id", nullable = false)
    private AreaDeAtuacao areaDeAtuacao;

    // Relacionamento com Endereco
    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "endereco_id", referencedColumnName = "id")
    @JsonManagedReference //evita o efeito sanduiche q estava acontecendo <mentor><endereco><mentor>
    private Endereco endereco;

    private String resumo;


    // Metodos obrigatórios do Spring Security

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority("ROLE_MENTOR"));
        return authorities;
    }

    @JsonIgnore
    private List<GrantedAuthority> authorities;


    @Override
    public String getPassword() {
        return senha;
    }

    @Override
    public String getUsername() {
        return email;
    }

}