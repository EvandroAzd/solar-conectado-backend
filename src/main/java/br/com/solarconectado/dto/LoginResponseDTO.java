package br.com.solarconectado.dto;

public record LoginResponseDTO(
    String token,
    boolean adm
) {
}
