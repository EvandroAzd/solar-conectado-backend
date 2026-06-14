package br.com.solarconectado.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public record ConfigGamificacaoDTO(
        @NotNull @DecimalMin("0.01") BigDecimal multiplicadorDinheiro,
        @NotNull @Min(0) Integer pontosCompartilhamento,
        @NotNull @Min(0) Integer pontosIndicacao,
        @NotNull @Min(0) Integer pontosPorProduto
) {}