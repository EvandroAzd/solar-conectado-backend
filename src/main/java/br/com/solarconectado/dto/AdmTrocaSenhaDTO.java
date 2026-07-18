package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record AdmTrocaSenhaDTO(
        @NotBlank String senhaAtual,
        @NotBlank @Size(min = 6) String novaSenha
) {}