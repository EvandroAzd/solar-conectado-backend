package br.com.solarconectado.dto;

import br.com.solarconectado.entity.RepresentanteEmpresa;

import java.time.LocalDateTime;
import java.util.UUID;

public record RepresentanteResponseDTO(
        Integer id,
        UUID usuarioId,
        String nome,
        String cargo,
        boolean principal,
        boolean ativo,
        LocalDateTime dataHoraCad
) {
    public static RepresentanteResponseDTO de(RepresentanteEmpresa r) {
        return new RepresentanteResponseDTO(
                r.getId(),
                r.getUsuario().getId(),
                r.getUsuario().getNome(),
                r.getCargo(),
                r.getPrincipal(),
                r.getAtivo(),
                r.getDataHoraCad()
        );
    }
}
