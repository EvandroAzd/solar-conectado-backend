package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record CategoriaProdutoDTO(
        @NotBlank @Size(max = 30) String nome
) {}