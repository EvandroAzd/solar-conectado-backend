package br.com.solarconectado.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record ItemTransacaoResponseDTO(
        UUID itemBazarId,
        String nomeProduto,
        Integer quantidade,
        BigDecimal valorUnitario,
        BigDecimal subtotal
) {}