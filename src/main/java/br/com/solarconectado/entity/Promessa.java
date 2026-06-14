package br.com.solarconectado.entity;

import br.com.solarconectado.enums.StatusPromessa;
import br.com.solarconectado.enums.TipoDoacao;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promessas")
public class Promessa {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_doador")
    private Doador doador; // null = anônimo

    @ManyToOne
    @JoinColumn(name = "id_campanha")
    private Campanha campanha; // null = espontânea

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoDoacao tipo;

    @Column(precision = 10, scale = 2)
    private BigDecimal valor;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPromessa status;

    @Column(updatable = false, name = "data_hora")
    private LocalDateTime dataHora;
}