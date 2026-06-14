package br.com.solarconectado.dto;

import java.util.UUID;

public record LoginResponseEmpresaDTO(
        String token,
        UUID empresaId,
        String nomeEmpresa,
        String cargo,
        boolean principal
) {}
