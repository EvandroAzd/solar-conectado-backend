package br.com.solarconectado.dto;

import br.com.solarconectado.enums.TipoCampanha;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CampanhaDTO(
        @NotBlank(message = "Um título é obrigatório") String titulo,
        @NotNull(message = "Informe o tipo da campanha") TipoCampanha tipo,
        String descricao,
        LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFinal,
        BigDecimal metaValor,
        Integer metaQtd,
        List<String> urlsImagens
) {}
