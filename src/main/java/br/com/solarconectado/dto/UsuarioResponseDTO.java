package br.com.solarconectado.dto;

import br.com.solarconectado.entity.Usuario;
import java.time.LocalDateTime;
import java.util.UUID;

// Nunca inclui a senha — este é o dado que vai na resposta da API
public record UsuarioResponseDTO(
        UUID id,
        String nome,
        String email,
        String cpf,
        boolean ativo,
        int pontos,
        LocalDateTime criadoEm
) {
    public static UsuarioResponseDTO de(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getCpf(),
                usuario.getAtivo(),
                usuario.getPontos(),
                usuario.getDataHoraCad()
        );
    }
}
