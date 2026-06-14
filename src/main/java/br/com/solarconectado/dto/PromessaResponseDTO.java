package br.com.solarconectado.dto;

import br.com.solarconectado.enums.StatusPromessa;
import br.com.solarconectado.enums.TipoDoacao;

import java.math.BigDecimal;
import java.time.LocalDateTime;

public record PromessaResponseDTO(
        Long id,
        String nomeDoador,
        String nomeCampanha,
        TipoDoacao tipo,
        BigDecimal valor,
        String descricao,
        StatusPromessa status,
        LocalDateTime dataHora,
        String mensagemWhatsapp
) {}