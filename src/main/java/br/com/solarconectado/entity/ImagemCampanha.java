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
@Table(name = "imagens_campanhas")
public class ImagemCampanha {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_campanha",  nullable = false)
    private Campanha campanha;

    @Column(length = 500, nullable = false)
    private String url;

    @Column(nullable = false)
    private Boolean principal;

    @Column(updatable = false, name = "data_hora_cad")
    private LocalDateTime dataHoraCadastro;
}
