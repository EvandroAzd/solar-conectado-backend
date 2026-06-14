package br.com.solarconectado.dto;

import br.com.solarconectado.entity.Empresa;

import java.time.LocalDateTime;
import java.util.UUID;

public record EmpresaResponseDTO(
        UUID id,
        String nome,
        String cnpj,
        String email,
        String descricao,
        String urlLogo,
        boolean ativo,
        int pontos,
        LocalDateTime dataHoraCad
) {
    public static EmpresaResponseDTO de(Empresa e) {
        return new EmpresaResponseDTO(
                e.getId(), e.getNome(), e.getCnpj(), e.getEmail(),
                e.getDescricao(), e.getUrlLogo(), e.getAtivo(),
                e.getPontos(), e.getDataHoraCad()
        );
    }
}
