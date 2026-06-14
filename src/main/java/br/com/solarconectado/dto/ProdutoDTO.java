package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ProdutoDTO(
        @NotNull Integer categoriaId,
        @NotBlank @Size(max = 60) String nome,
        String descricao,
        @Size(max = 500) String urlImagem
) {}