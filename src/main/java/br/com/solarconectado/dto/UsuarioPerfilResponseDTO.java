package br.com.solarconectado.dto;

import br.com.solarconectado.entity.Usuario;

import java.time.LocalDateTime;
import java.util.UUID;

public record UsuarioPerfilResponseDTO(
        UUID id,
        String nome,
        String email,
        String cpf,
        String urlFoto,
        Integer pontos,
        boolean ativo,
        LocalDateTime criadoEm
) {
    public static UsuarioPerfilResponseDTO de(Usuario u) {
        return new UsuarioPerfilResponseDTO(
                u.getId(),
                u.getNome(),
                u.getEmail(),
                u.getCpf(),
                u.getUrlFoto(),
                u.getPontos(),
                u.getAtivo(),
                u.getDataHoraCad()
        );
    }
}