package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "adm")
public class Adm {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(length = 60)
    private String cargo;

    @Column(name = "is_master")
    private Boolean isMaster;

    @Column(nullable = false)
    private Boolean ativo;
}