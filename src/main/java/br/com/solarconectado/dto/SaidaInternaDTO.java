package br.com.solarconectado.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record SaidaInternaDTO(
        @NotNull Integer centroCustoId,
        @NotNull UUID produtoId,
        Integer entradaProdutoId,
        @NotNull @Min(1) Integer quantidade,
        String observacao
) {}