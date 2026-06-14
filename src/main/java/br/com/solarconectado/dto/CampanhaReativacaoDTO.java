package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

public record CampanhaReativacaoDTO(
        @NotNull(message = "Informe a data de início da campanha") LocalDateTime dataHoraInicio,
        LocalDateTime dataHoraFinal
) {}