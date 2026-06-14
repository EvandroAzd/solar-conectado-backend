package br.com.solarconectado.dto;

import br.com.solarconectado.enums.StatusCampanha;
import br.com.solarconectado.enums.TipoCampanha;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

public record CampanhaAdmResponseDTO (
    String id,
    String idCriador,
    String idResponsavel,
    String titulo,
    String descricao,
    LocalDateTime dataInicio,
    LocalDateTime dataFinal,
    TipoCampanha tipoCampanha,
    BigDecimal metaValor,
    Integer metaQtd,
    List<String> urlsImagens,
    LocalDateTime dataHoraCriada,
    LocalDateTime dataHoraAtualizada,
    StatusCampanha statusCampanha
){}
