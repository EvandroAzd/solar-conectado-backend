package br.com.solarconectado.dto;

import br.com.solarconectado.enums.TipoDoacao;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PromessaEspontaneaDTO(
        @NotBlank String cpf,
        @NotNull TipoDoacao tipo,
        @Positive BigDecimal valor,
        String descricao
) {}