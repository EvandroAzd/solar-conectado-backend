package br.com.solarconectado.entity;

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
@Table(name = "config_gamificacao")
public class ConfigGamificacao {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "multiplicador_dinheiro", nullable = false, precision = 5, scale = 2)
    private BigDecimal multiplicadorDinheiro;

    @Column(name = "pontos_compartilhamento", nullable = false)
    private Integer pontosCompartilhamento;

    @Column(name = "pontos_indicacao", nullable = false)
    private Integer pontosIndicacao;

    @Column(name = "pontos_por_produto", nullable = false)
    private Integer pontosPorProduto;

    @Column(name = "data_hora_update")
    private LocalDateTime dataHoraUpdate;
}