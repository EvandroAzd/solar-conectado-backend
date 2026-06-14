package br.com.solarconectado.dto;

public record CategoriaProdutoResponseDTO(
        Integer id,
        String nome,
        Boolean ativo
) {}