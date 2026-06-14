package br.com.solarconectado.dto;

import java.util.UUID;

public record ProdutoResponseDTO(
        UUID id,
        String nome,
        String descricao,
        String urlImagem,
        CategoriaProdutoResponseDTO categoria,
        Boolean ativo
) {}