package br.com.solarconectado.service;

import br.com.solarconectado.dto.CompartilhamentoResponseDTO;
import br.com.solarconectado.entity.*;
import br.com.solarconectado.enums.TipoCampanha;
import br.com.solarconectado.enums.TipoHistoricoPontos;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CompartilhamentoService {

    private final CompartilhamentoRepository compartilhamentoRepository;
    private final IndicacaoRepository indicacaoRepository;
    private final CampanhaRepository campanhaRepository;
    private final UsuarioRepository usuarioRepository;
    private final ConfigGamificacaoService configService;
    private final PontosService pontosService;

    @Transactional
    public CompartilhamentoResponseDTO compartilhar(UUID idCampanha) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        Campanha campanha = campanhaRepository.findById(idCampanha)
                .orElseThrow(() -> new RegraDeNegocioException("Campanha não encontrada"));

        if (campanha.getTipoCampanha() != TipoCampanha.DIVULGACAO && campanha.getTipoCampanha() != TipoCampanha.CONSCIENTIZACAO)
            throw new RegraDeNegocioException("Pontos por compartilhamento estão disponíveis apenas em campanhas de divulgação");

        // Idempotente: segunda chamada retorna o token sem pontuar de novo
        return compartilhamentoRepository.findByUsuarioAndCampanha(usuario, campanha)
                .map(c -> new CompartilhamentoResponseDTO(c.getToken()))
                .orElseGet(() -> {
                    String token = UUID.randomUUID().toString();
                    Compartilhamento comp = Compartilhamento.builder()
                            .usuario(usuario)
                            .campanha(campanha)
                            .token(token)
                            .pontosCreditados(true)
                            .dataHoraCad(LocalDateTime.now())
                            .build();
                    compartilhamentoRepository.save(comp);

                    int pontos = configService.getConfig().getPontosCompartilhamento();
                    pontosService.concederPontos(usuario, pontos, TipoHistoricoPontos.COMPARTILHAMENTO,
                            campanha, String.valueOf(comp.getId()), "compartilhamentos");

                    return new CompartilhamentoResponseDTO(token);
                });
    }

    // Chamado após cadastro de novo usuário — credita pontos ao compartilhador e registra a indicação
    @Transactional
    public void processarIndicacao(String refToken, Usuario novoUsuario) {
        compartilhamentoRepository.findByToken(refToken).ifPresent(comp -> {
            Indicacao indicacao = Indicacao.builder()
                    .compartilhamento(comp)
                    .usuarioIndicado(novoUsuario)
                    .pontosCreditados(true)
                    .dataHoraCad(LocalDateTime.now())
                    .build();
            indicacaoRepository.save(indicacao);

            int pontos = configService.getConfig().getPontosIndicacao();
            pontosService.concederPontos(comp.getUsuario(), pontos, TipoHistoricoPontos.INDICACAO,
                    comp.getCampanha(), String.valueOf(indicacao.getId()), "indicacoes");
        });
    }
}