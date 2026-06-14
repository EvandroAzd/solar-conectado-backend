package br.com.solarconectado.dto;

import jakarta.validation.constraints.Size;

public record RepresentanteDTO(
        @Size(max = 60) String cargo
) {}
