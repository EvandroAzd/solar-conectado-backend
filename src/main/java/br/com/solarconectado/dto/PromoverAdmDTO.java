package br.com.solarconectado.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record PromoverAdmDTO(
        @NotBlank @Size(max = 60) String cargo
) {}