package br.com.solarconectado.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

public record AdmPerfilEdicaoDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String cpf
) {}