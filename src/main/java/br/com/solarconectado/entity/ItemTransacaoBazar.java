package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "itens_transacao_bazar")
public class ItemTransacaoBazar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_transacao", nullable = false)
    private TransacaoBazar transacao;

    @ManyToOne
    @JoinColumn(name = "id_item_bazar", nullable = false)
    private ItemBazar itemBazar;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "valor_unitario", precision = 10, scale = 2)
    private BigDecimal valorUnitario;
}