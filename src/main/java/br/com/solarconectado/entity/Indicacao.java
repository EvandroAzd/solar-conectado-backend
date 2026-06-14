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
@Table(name = "indicacoes")
public class Indicacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_compartilhamento", nullable = false)
    private Compartilhamento compartilhamento;

    @ManyToOne
    @JoinColumn(name = "id_usuario_indicado", nullable = false)
    private Usuario usuarioIndicado;

    @Column(name = "pontos_creditados", nullable = false)
    private Boolean pontosCreditados;

    @Column(name = "data_hora_cad", nullable = false, updatable = false)
    private LocalDateTime dataHoraCad;
}