package br.com.solarconectado.entity;

import br.com.solarconectado.enums.StatusTransacaoBazar;
import br.com.solarconectado.enums.TipoTransacaoBazar;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "transacoes_bazar")
public class TransacaoBazar {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_recebedor")
    private Usuario recebedor;

    @Column(name = "nome_comprador", length = 120)
    private String nomeComprador;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoTransacaoBazar tipo;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusTransacaoBazar status;

    @Column(name = "mensagem_whatsapp", columnDefinition = "TEXT")
    private String mensagemWhatsapp;

    @Column(name = "valor_total", precision = 10, scale = 2)
    private BigDecimal valorTotal;

    @Column(name = "data_hora_transacao", updatable = false)
    private LocalDateTime dataHoraTransacao;

    @OneToMany(mappedBy = "transacao", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<ItemTransacaoBazar> itens;
}