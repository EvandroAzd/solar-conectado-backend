package br.com.solarconectado.dto;

import br.com.solarconectado.enums.DestinoProduto;
import br.com.solarconectado.enums.TipoPagamento;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;
import java.util.UUID;

public record ConfirmarPromessaDTO(
        // para MOEDA
        @Positive BigDecimal valorReal,
        TipoPagamento tipoPagamento,

        // para PRODUTO
        UUID idProduto,
        @Positive Integer quantidadeReal,
        DestinoProduto destino
) {}