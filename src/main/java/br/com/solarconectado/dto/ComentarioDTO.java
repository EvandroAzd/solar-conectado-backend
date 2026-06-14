package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record ComentarioDTO(
        @NotBlank(message = "O texto não pode ser vazio")
        @Size(max = 1000, message = "Comentário deve ter no máximo 1000 caracteres")
        String texto
) {}