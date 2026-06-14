package br.com.solarconectado.dto;

import java.math.BigDecimal;
import java.util.List;

public record RelatorioBazarResponseDTO(
        BigDecimal valorArrecadadoTotal,
        Long totalPedidosConfirmados,
        List<RelatorioProdutoDTO> porProduto
) {
    public record RelatorioProdutoDTO(
            String nomeProduto,
            Integer quantidadeVendida,
            BigDecimal valorArrecadado
    ) {}
}