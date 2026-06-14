package br.com.solarconectado.dto;

import br.com.solarconectado.enums.StatusComentario;

import java.time.LocalDateTime;

public record ComentarioResponseDTO(
        Long id,
        String nomeAutor,
        String texto,
        LocalDateTime dataHora,
        long totalCurtidas,
        StatusComentario status
) {}