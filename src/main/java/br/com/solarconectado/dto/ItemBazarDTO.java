package br.com.solarconectado.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemBazarDTO(
        @NotNull UUID produtoId,
        Integer entradaProdutoId,
        @NotNull @Min(1) Integer quantidade,
        BigDecimal valorUnitario,
        Boolean gratuito
) {}