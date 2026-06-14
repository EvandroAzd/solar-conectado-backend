package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;

public record EmpresaLoginDTO(
        @NotBlank String email,
        @NotBlank String senha
) {}
