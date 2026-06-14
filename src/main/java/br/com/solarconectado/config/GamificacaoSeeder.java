package br.com.solarconectado.config;

import br.com.solarconectado.service.ConfigGamificacaoService;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(2)
public class GamificacaoSeeder implements ApplicationRunner {

    private final ConfigGamificacaoService configGamificacaoService;

    @Override
    public void run(ApplicationArguments args) {
        configGamificacaoService.inicializar();
    }
}