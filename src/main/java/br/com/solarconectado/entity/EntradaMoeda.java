package br.com.solarconectado.entity;

import br.com.solarconectado.enums.StatusPagamento;
import br.com.solarconectado.enums.TipoPagamento;
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
@Table(name = "entrada_moedas")
public class EntradaMoeda {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_campanha")
    private Campanha campanha;

    @ManyToOne
    @JoinColumn(name = "id_doador")
    private Doador doador; // null = anônimo

    @ManyToOne
    @JoinColumn(name = "id_promessa")
    private Promessa promessa;

    @ManyToOne
    @JoinColumn(name = "id_gerenciador", nullable = false)
    private Usuario gerenciador;

    @Column(updatable = false, name = "data_hora_entrada")
    private LocalDateTime dataHoraEntrada;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal valor;

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo_pagamento", nullable = false)
    private TipoPagamento tipoPagamento;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusPagamento status;
}