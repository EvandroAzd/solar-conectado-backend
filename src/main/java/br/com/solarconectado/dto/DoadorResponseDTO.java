package br.com.solarconectado.dto;

import br.com.solarconectado.enums.TipoDoador;

import java.util.UUID;

public record DoadorResponseDTO(
        UUID id,
        UUID idUsuario,
        String nome,
        String cpf,
        String email,
        TipoDoador tipo
) {}