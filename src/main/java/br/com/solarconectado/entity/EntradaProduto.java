package br.com.solarconectado.entity;

import br.com.solarconectado.enums.DestinoProduto;
import br.com.solarconectado.enums.StatusEntrada;
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
@Table(name = "entrada_produtos")
public class EntradaProduto {
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
    @JoinColumn(name = "id_responsavel", nullable = false)
    private Usuario responsavel;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @Enumerated(EnumType.STRING)
    @Column
    private DestinoProduto destino;

    @Column(updatable = false, name = "data_hora_entrada")
    private LocalDateTime dataHoraEntrada;

    @Column(nullable = false)
    private Integer quantidade;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusEntrada status;
}