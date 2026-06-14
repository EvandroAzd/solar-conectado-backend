package br.com.solarconectado.config;

import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.service.AdmService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

// ApplicationRunner: roda automaticamente assim que o Spring Boot termina de inicializar
@Slf4j
@Component
@RequiredArgsConstructor
public class AdminMasterSeeder implements ApplicationRunner {

    private final AdmService admService;

    @Value("${admin.master.nome}")
    private String nome;

    @Value("${admin.master.email}")
    private String email;

    @Value("${admin.master.senha}")
    private String senha;

    @Value("${admin.master.cpf}")
    private String cpf;

    @Override
    public void run(ApplicationArguments args) {
        try {
            admService.cadastrarAdmMaster(nome, email, senha, cpf);
            log.info("ADMIN_MASTER criado com sucesso.");
        } catch (RegraDeNegocioException e) {
            // Já existe — comportamento esperado a partir da segunda inicialização
            log.info("ADMIN_MASTER já existe. Nenhuma ação necessária.");
        }
    }
}
