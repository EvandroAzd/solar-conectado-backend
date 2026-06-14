package br.com.solarconectado.entity;

import br.com.solarconectado.enums.StatusComentario;
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
@Table(name = "comentarios")
public class Comentario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(columnDefinition = "TEXT", nullable = false)
    private String texto;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private StatusComentario status;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;
}