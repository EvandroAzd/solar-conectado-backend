package br.com.solarconectado.dto;

import br.com.solarconectado.enums.StatusTransacaoBazar;
import br.com.solarconectado.enums.TipoTransacaoBazar;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record PedidoBazarResponseDTO(
        Integer id,
        String nomeComprador,
        TipoTransacaoBazar tipo,
        StatusTransacaoBazar status,
        BigDecimal valorTotal,
        String mensagemWhatsapp,
        LocalDateTime dataHoraTransacao,
        List<ItemTransacaoResponseDTO> itens
) {}