package br.com.solarconectado.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

public record ItemPedidoDTO(
        @NotNull UUID itemBazarId,
        @NotNull @Min(1) Integer quantidade
) {}