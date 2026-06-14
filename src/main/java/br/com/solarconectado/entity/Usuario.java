package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "usuarios")
public class Usuario implements UserDetails {
    @Id
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 100)
    private String nome;

    @Column(nullable = false, unique = true, length = 14)
    private String cpf;

    @Column(nullable = true, unique = true, length = 254)
    private String email;

    @Column(nullable = false, columnDefinition = "CHAR(60)")
    private String senha;

    @Column(nullable = true, length = 500, name = "url_foto")
    private String urlFoto;

    @Column(updatable = false, name = "data_hora_cad")
    private LocalDateTime dataHoraCad;

    @Column(name = "data_hora_update")
    private LocalDateTime dataHoraAtualizacao;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(nullable = false)
    private Integer pontos = 0;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return List.of();
    }

    @Override
    public String getPassword() {
        return this.senha;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isEnabled() {
        return this.ativo;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }
}