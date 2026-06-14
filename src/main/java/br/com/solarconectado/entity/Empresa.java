package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "empresas")
public class Empresa {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(length = 100, nullable = false)
    private String nome;

    @Column(length = 18, nullable = false, unique = true)
    private String cnpj;

    @Column(length = 254)
    private String email;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "url_logo", length = 500)
    private String urlLogo;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(nullable = false)
    private Integer pontos;

    @Column(updatable = false, name = "data_hora_cad")
    private LocalDateTime dataHoraCad;

    @Column(name = "data_hora_update")
    private LocalDateTime dataHoraUpdate;
}