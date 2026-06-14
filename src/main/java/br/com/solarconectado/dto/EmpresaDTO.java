package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record EmpresaDTO(
        @NotBlank @Size(max = 100) String nome,
        @NotBlank @Size(max = 18) String cnpj,
        @Size(max = 254) String email,
        String descricao,
        @Size(max = 500) String urlLogo,
        @Size(max = 60) String cargo
) {}
