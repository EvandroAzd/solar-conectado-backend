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
@Table(name = "log_campanhas")
public class LogCampanha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_campanha")
    private Campanha campanha;

    @Column(length = 50, nullable = false)
    private String acao;

    @Column(length = 500)
    private String detalhe;

    @Column(updatable = false, name = "data_hora")
    private LocalDateTime dataHoraRegistro;
}
