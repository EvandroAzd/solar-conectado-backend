package br.com.solarconectado.service;

import br.com.solarconectado.dto.ConfigGamificacaoDTO;
import br.com.solarconectado.dto.ConfigGamificacaoResponseDTO;
import br.com.solarconectado.entity.ConfigGamificacao;
import br.com.solarconectado.repository.ConfigGamificacaoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class ConfigGamificacaoService {

    private static final int CONFIG_ID = 1;

    private final ConfigGamificacaoRepository configRepository;

    // Garante que sempre existe uma linha na tabela ao subir a aplicação
    @Transactional
    public void inicializar() {
        if (!configRepository.existsById(CONFIG_ID)) {
            configRepository.save(ConfigGamificacao.builder()
                    .multiplicadorDinheiro(new BigDecimal("1.00"))
                    .pontosCompartilhamento(5)
                    .pontosIndicacao(10)
                    .pontosPorProduto(1)
                    .dataHoraUpdate(LocalDateTime.now())
                    .build());
        }
    }

    public ConfigGamificacaoResponseDTO buscar() {
        return toResponse(getConfig());
    }

    @Transactional
    public ConfigGamificacaoResponseDTO atualizar(ConfigGamificacaoDTO dto) {
        ConfigGamificacao config = getConfig();
        config.setMultiplicadorDinheiro(dto.multiplicadorDinheiro());
        config.setPontosCompartilhamento(dto.pontosCompartilhamento());
        config.setPontosIndicacao(dto.pontosIndicacao());
        config.setPontosPorProduto(dto.pontosPorProduto());
        config.setDataHoraUpdate(LocalDateTime.now());
        return toResponse(configRepository.save(config));
    }

    public ConfigGamificacao getConfig() {
        return configRepository.findById(CONFIG_ID)
                .orElseThrow(() -> new IllegalStateException("Configuração de gamificação não encontrada"));
    }

    private ConfigGamificacaoResponseDTO toResponse(ConfigGamificacao c) {
        return new ConfigGamificacaoResponseDTO(
                c.getMultiplicadorDinheiro(),
                c.getPontosCompartilhamento(),
                c.getPontosIndicacao(),
                c.getPontosPorProduto(),
                c.getDataHoraUpdate()
        );
    }
}