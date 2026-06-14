package br.com.solarconectado.dto;

public record CentroCustoResponseDTO(
        Integer id,
        String nome,
        String descricao,
        Boolean ativo
) {}