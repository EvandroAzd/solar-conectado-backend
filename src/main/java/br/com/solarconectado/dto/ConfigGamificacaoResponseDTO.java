package br.com.solarconectado.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record ConfigGamificacaoResponseDTO(
        BigDecimal multiplicadorDinheiro,
        Integer pontosCompartilhamento,
        Integer pontosIndicacao,
        Integer pontosPorProduto,
        LocalDateTime dataHoraUpdate
) {}