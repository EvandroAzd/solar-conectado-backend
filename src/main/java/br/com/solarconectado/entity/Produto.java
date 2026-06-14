package br.com.solarconectado.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.util.UUID;

@Data
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "produtos")
public class Produto {
    @Id
    @UuidGenerator
    private UUID id;

    @ManyToOne
    @JoinColumn(name = "id_criador", nullable = false)
    private Usuario criador;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private CategoriaProduto categoria;

    @Column(length = 60, nullable = false)
    private String nome;

    @Column(columnDefinition = "TEXT")
    private String descricao;

    @Column(name = "url_imagem", length = 500)
    private String urlImagem;

    @Column(nullable = false)
    private Boolean ativo;
}