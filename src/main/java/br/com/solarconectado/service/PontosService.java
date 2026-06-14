package br.com.solarconectado.service;

import br.com.solarconectado.entity.Campanha;
import br.com.solarconectado.entity.HistoricoPontos;
import br.com.solarconectado.entity.PontosUsuario;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.enums.TipoHistoricoPontos;
import br.com.solarconectado.repository.HistoricoPontosRepository;
import br.com.solarconectado.repository.PontosUsuarioRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
public class PontosService {

    private final UsuarioRepository usuarioRepository;
    private final PontosUsuarioRepository pontosUsuarioRepository;
    private final HistoricoPontosRepository historicoPontosRepository;

    /**
     * Concede pontos a um usuário, sincronizando usuarios.pontos + pontos_usuarios.total_pontos
     * e registrando a transação no historico_pontos.
     *
     * @param campanha        campanha relacionada (pode ser null)
     * @param idReferencia    id do registro que gerou os pontos (ex: id da entrada)
     * @param tabelaReferencia nome da tabela que gerou os pontos
     */
    @Transactional
    public void concederPontos(Usuario usuario, int pontos, TipoHistoricoPontos tipo,
                               Campanha campanha, String idReferencia, String tabelaReferencia) {
        // 1. atualiza usuarios.pontos
        usuario.setPontos(usuario.getPontos() + pontos);
        usuarioRepository.save(usuario);

        // 2. sincroniza pontos_usuarios
        PontosUsuario pu = pontosUsuarioRepository.findByUsuario(usuario)
                .orElse(PontosUsuario.builder().usuario(usuario).totalPontos(0).build());
        pu.setTotalPontos(pu.getTotalPontos() + pontos);
        pu.setDataHoraUpdate(LocalDateTime.now());
        pontosUsuarioRepository.save(pu);

        // 3. registra no histórico
        HistoricoPontos historico = HistoricoPontos.builder()
                .usuario(usuario)
                .campanha(campanha)
                .pontos(pontos)
                .tipo(tipo)
                .idReferencia(idReferencia)
                .tabelaReferencia(tabelaReferencia)
                .dataHora(LocalDateTime.now())
                .build();
        historicoPontosRepository.save(historico);
    }
}