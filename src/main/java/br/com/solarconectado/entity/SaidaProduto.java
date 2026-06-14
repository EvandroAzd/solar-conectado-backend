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
@Table(name = "saida_produtos")
public class SaidaProduto {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_centro_custo")
    private CentroCusto centroCusto;

    @ManyToOne
    @JoinColumn(name = "id_responsavel", nullable = false)
    private Usuario responsavel;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_entrada_produto")
    private EntradaProduto entradaProduto;

    @ManyToOne
    @JoinColumn(name = "id_item_bazar")
    private ItemBazar itemBazar;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(columnDefinition = "TEXT")
    private String observacao;

    @Column(name = "data_hora_saida", updatable = false)
    private LocalDateTime dataHoraSaida;
}