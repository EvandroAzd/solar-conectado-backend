package br.com.solarconectado.entity;

import br.com.solarconectado.enums.TipoHistoricoPontos;
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
@Table(name = "historico_pontos")
public class HistoricoPontos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @ManyToOne
    @JoinColumn(name = "id_empresa")
    private Empresa empresa;

    @ManyToOne
    @JoinColumn(name = "id_campanha")
    private Campanha campanha;

    @Column(nullable = false)
    private Integer pontos;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoHistoricoPontos tipo;

    @Column(name = "id_referencia", length = 36)
    private String idReferencia;

    @Column(name = "tabela_referencia", length = 50)
    private String tabelaReferencia;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;
}