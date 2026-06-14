package br.com.solarconectado.dto;

import br.com.solarconectado.entity.Adm;

import java.util.UUID;

public record AdmResponseDTO(
        UUID admId,
        UUID usuarioId,
        String nome,
        String email,
        String cargo,
        boolean isMaster,
        boolean ativo
) {
    public static AdmResponseDTO de(Adm adm) {
        return new AdmResponseDTO(
                adm.getId(),
                adm.getUsuario().getId(),
                adm.getUsuario().getNome(),
                adm.getUsuario().getEmail(),
                adm.getCargo(),
                adm.getIsMaster(),
                adm.getAtivo()
        );
    }
}