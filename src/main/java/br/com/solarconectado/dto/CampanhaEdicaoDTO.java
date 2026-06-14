package br.com.solarconectado.dto;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CampanhaEdicaoDTO(
        String titulo,
        String descricao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFinal,
        BigDecimal metaValor,
        Integer metaQtd,
        List<String> urlsImagens
) {}