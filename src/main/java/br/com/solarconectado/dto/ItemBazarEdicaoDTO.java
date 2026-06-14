package br.com.solarconectado.dto;

import java.math.BigDecimal;

public record ItemBazarEdicaoDTO(
        BigDecimal valorUnitario,
        Boolean gratuito
) {}