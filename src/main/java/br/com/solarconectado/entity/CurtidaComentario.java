package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "curtidas_comentarios")
public class CurtidaComentario {

    @EmbeddedId
    private CurtidaId id;

    @ManyToOne
    @MapsId("idComentario")
    @JoinColumn(name = "id_comentario")
    private Comentario comentario;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "id_usuario")
    private Usuario usuario;

    @Column(name = "data_hora", nullable = false, updatable = false)
    private LocalDateTime dataHora;

    @Data
    @Embeddable
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CurtidaId implements Serializable {
        private Long idComentario;
        private UUID idUsuario;
    }
}