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
@Table(name = "doadores")
public class Doador {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @Column(nullable = false)
    private Boolean anonimo;

    @Column(length = 100)
    private String nome;

    @Column(length = 254)
    private String email;

    @Column(length = 14)
    private String cpf;

    @Column(updatable = false, name = "data_hora_cad")
    private LocalDateTime dataHoraCadastro;
}
