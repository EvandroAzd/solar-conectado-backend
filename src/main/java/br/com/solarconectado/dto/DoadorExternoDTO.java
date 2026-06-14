package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record DoadorExternoDTO(
        @NotBlank String nome,
        @NotBlank @Size(max = 14) String cpf,
        String email
) {}