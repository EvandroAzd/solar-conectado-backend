package br.com.solarconectado.dto;

import br.com.solarconectado.enums.StatusItemBazar;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemBazarResponseDTO(
        UUID id,
        ProdutoResponseDTO produto,
        Integer quantidade,
        Integer qtdDisponivel,
        BigDecimal valorUnitario,
        Boolean gratuito,
        Boolean ativo,
        StatusItemBazar status
) {}