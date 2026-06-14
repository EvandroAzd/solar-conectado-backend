package br.com.solarconectado.dto;

import br.com.solarconectado.enums.TipoTransacaoBazar;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoBazarDTO(
        @NotNull TipoTransacaoBazar tipo,
        String nomeComprador,
        @NotEmpty @Valid List<ItemPedidoDTO> itens
) {}