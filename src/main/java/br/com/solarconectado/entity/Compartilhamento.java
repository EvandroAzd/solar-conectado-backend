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
@Table(name = "compartilhamentos")
public class Compartilhamento {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_campanha", nullable = false)
    private Campanha campanha;

    @Column(length = 36, nullable = false, unique = true)
    private String token;

    @Column(name = "pontos_creditados", nullable = false)
    private Boolean pontosCreditados;

    @Column(name = "data_hora_cad", nullable = false, updatable = false)
    private LocalDateTime dataHoraCad;
}