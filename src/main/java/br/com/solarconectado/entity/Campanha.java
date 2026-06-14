package br.com.solarconectado.entity;

import br.com.solarconectado.enums.StatusCampanha;
import br.com.solarconectado.enums.TipoCampanha;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "campanhas")
public class Campanha {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_criador")
    private Adm admCriador;

    @ManyToOne
    @JoinColumn(name = "id_responsavel")
    private Adm admResponsavel;

    @Column(length = 100, nullable = false)
    private String titulo;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "data_hora_init")
    private LocalDateTime dataHoraInicio;

    @Column(name = "data_hora_final")
    private LocalDateTime dataHoraFinal;

    @Enumerated(EnumType.STRING)
    @Column(length = 20, nullable = false)
    private TipoCampanha tipoCampanha;

    @Column
    private BigDecimal metaValor;

    @Column
    private Integer metaQtd;

    @Column(updatable = false, name = "data_hora_cad")
    private LocalDateTime dataHoraCad;

    @Column(name = "data_hora_update")
    private LocalDateTime dataHoraAtualizacao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusCampanha status;
}