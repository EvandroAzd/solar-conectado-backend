package br.com.solarconectado.entity;

import br.com.solarconectado.enums.StatusItemBazar;
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
@Table(name = "itens_bazar")
public class ItemBazar {

    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_produto", nullable = false)
    private Produto produto;

    @ManyToOne
    @JoinColumn(name = "id_entrada_produto")
    private EntradaProduto entradaProduto;

    @ManyToOne
    @JoinColumn(name = "id_responsavel", nullable = false)
    private Usuario responsavel;

    @Column(nullable = false)
    private Integer quantidade;

    @Column(name = "qtd_disponivel", nullable = false)
    private Integer qtdDisponivel;

    @Column(name = "valor_unitario", precision = 10, scale = 2)
    private BigDecimal valorUnitario;

    @Column(nullable = false)
    private Boolean gratuito;

    @Column(nullable = false)
    private Boolean ativo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusItemBazar status;

    @Column(name = "data_hora_add", updatable = false)
    private LocalDateTime dataHoraAdd;

    @Column(name = "data_hora_update")
    private LocalDateTime dataHoraUpdate;
}