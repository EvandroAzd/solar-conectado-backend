package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "representantes_empresa")
public class RepresentanteEmpresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_empresa", nullable = false)
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(length = 60)
    private String cargo;

    @Column(nullable = false)
    private Boolean principal;

    @Column(nullable = false)
    private Boolean ativo;

    @Column(updatable = false, name = "data_hora_cad")
    private LocalDateTime dataHoraCad;
}