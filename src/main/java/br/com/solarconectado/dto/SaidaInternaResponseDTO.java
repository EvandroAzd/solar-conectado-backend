package br.com.solarconectado.dto;

import java.time.LocalDateTime;

public record SaidaInternaResponseDTO(
        Integer id,
        String centroCusto,
        String produto,
        Integer quantidade,
        String observacao,
        LocalDateTime dataHoraSaida
) {}