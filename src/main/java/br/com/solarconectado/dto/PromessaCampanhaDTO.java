package br.com.solarconectado.dto;

import br.com.solarconectado.enums.TipoDoacao;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

import java.math.BigDecimal;

public record PromessaCampanhaDTO(
        @NotNull TipoDoacao tipo,
        @Positive BigDecimal valor,
        String descricao,
        Boolean anonimo
) {}