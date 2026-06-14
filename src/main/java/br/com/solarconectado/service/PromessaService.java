package br.com.solarconectado.service;

import br.com.solarconectado.dto.ConfirmarPromessaDTO;
import br.com.solarconectado.dto.PromessaCampanhaDTO;
import br.com.solarconectado.dto.PromessaEspontaneaDTO;
import br.com.solarconectado.dto.PromessaResponseDTO;
import br.com.solarconectado.entity.*;
import br.com.solarconectado.enums.*;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class PromessaService {

    private final PromessaRepository promessaRepository;
    private final CampanhaRepository campanhaRepository;
    private final DoadorRepository doadorRepository;
    private final UsuarioRepository usuarioRepository;
    private final AdmRepository admRepository;
    private final EntradaMoedaRepository entradaMoedaRepository;
    private final EntradaProdutoRepository entradaProdutoRepository;
    private final ProdutoRepository produtoRepository;
    private final PontosService pontosService;
    private final ConfigGamificacaoService configGamificacaoService;

    @Value("${contato.whatsapp.adm}")
    private String whatsappAdm;

    @Value("${contato.endereco}")
    private String enderecoAsilo;

    @Value("${contato.email}")
    private String emailAsilo;

    @Value("${contato.telefone}")
    private String telefoneAsilo;

    @Value("${gamificacao.pontos-por-doacao}")
    private int pontosPorDoacao;

    @Transactional
    public PromessaResponseDTO prometerParaCampanha(UUID idCampanha, PromessaCampanhaDTO dto) {
        Campanha campanha = campanhaRepository.findById(idCampanha)
                .orElseThrow(() -> new RegraDeNegocioException("Campanha não encontrada"));

        if (campanha.getStatus() != StatusCampanha.ATIVA)
            throw new RegraDeNegocioException("Só é possível prometer doação para campanhas ativas");

        validarCamposDoacao(dto.tipo(), dto.valor(), dto.descricao());

        boolean anonimo = Boolean.TRUE.equals(dto.anonimo());
        Doador doador = null;

        if (!anonimo) {
            doador = resolverDoadorAutenticado();
        }

        Promessa promessa = Promessa.builder()
                .doador(doador)
                .campanha(campanha)
                .tipo(dto.tipo())
                .valor(dto.valor())
                .descricao(dto.descricao())
                .status(StatusPromessa.PENDENTE)
                .dataHora(LocalDateTime.now())
                .build();

        promessaRepository.save(promessa);

        String nomeDoador = doador != null ? resolverNomeDoador(doador) : "Anônimo";
        String mensagem = montarMensagemWhatsapp(nomeDoador, campanha.getTitulo(), dto.tipo(), dto.valor(), dto.descricao());

        return toResponseDTO(promessa, nomeDoador, campanha.getTitulo(), mensagem);
    }

    @Transactional
    public PromessaResponseDTO prometerEspontanea(PromessaEspontaneaDTO dto) {
        validarCamposDoacao(dto.tipo(), dto.valor(), dto.descricao());

        Doador doador = doadorRepository.findByCpf(dto.cpf())
                .orElseGet(() -> {
                    // busca usuário do sistema pelo CPF e cria vínculo de doador se existir
                    Usuario usuario = usuarioRepository.findByCpf(dto.cpf())
                            .orElseThrow(() -> new RegraDeNegocioException(
                                    "CPF não encontrado. Cadastre o doador externo antes de registrar a doação."));
                    return criarDoadorParaUsuario(usuario);
                });

        Promessa promessa = Promessa.builder()
                .doador(doador)
                .campanha(null)
                .tipo(dto.tipo())
                .valor(dto.valor())
                .descricao(dto.descricao())
                .status(StatusPromessa.PENDENTE)
                .dataHora(LocalDateTime.now())
                .build();

        promessaRepository.save(promessa);

        String nomeDoador = resolverNomeDoador(doador);
        return toResponseDTO(promessa, nomeDoador, null, null);
    }

    public List<PromessaResponseDTO> listarPendentes() {
        return promessaRepository.findByStatusOrderByDataHoraAsc(StatusPromessa.PENDENTE)
                .stream()
                .map(p -> {
                    String nome = p.getDoador() != null ? resolverNomeDoador(p.getDoador()) : "Anônimo";
                    String campanha = p.getCampanha() != null ? p.getCampanha().getTitulo() : null;
                    return toResponseDTO(p, nome, campanha, null);
                })
                .toList();
    }

    @Transactional
    public PromessaResponseDTO confirmar(Long id, ConfirmarPromessaDTO dto) {
        Promessa promessa = buscarPendente(id);
        Adm adm = getAdmAutenticado();

        String idEntrada;
        TipoHistoricoPontos tipoHistorico;
        String tabelaEntrada;
        int pontosCalculados;

        if (promessa.getTipo() == TipoDoacao.MOEDA) {
            idEntrada = String.valueOf(confirmarMoeda(promessa, dto, adm).getId());
            tipoHistorico = TipoHistoricoPontos.DOACAO_DINHEIRO;
            tabelaEntrada = "entrada_moedas";
            pontosCalculados = pontosPorDoacao;
        } else {
            EntradaProduto entrada = confirmarProduto(promessa, dto, adm);
            idEntrada = String.valueOf(entrada.getId());
            tipoHistorico = TipoHistoricoPontos.DOACAO_PRODUTO;
            tabelaEntrada = "entrada_produtos";
            int pontosPorProduto = configGamificacaoService.getConfig().getPontosPorProduto();
            pontosCalculados = pontosPorProduto * entrada.getQuantidade();
        }

        promessa.setStatus(StatusPromessa.CONFIRMADA);
        promessaRepository.save(promessa);

        if (promessa.getDoador() != null && promessa.getDoador().getUsuario() != null) {
            pontosService.concederPontos(
                    promessa.getDoador().getUsuario(),
                    pontosCalculados,
                    tipoHistorico,
                    promessa.getCampanha(),
                    idEntrada,
                    tabelaEntrada
            );
        }

        String nomeDoador = promessa.getDoador() != null ? resolverNomeDoador(promessa.getDoador()) : "Anônimo";
        String nomeCampanha = promessa.getCampanha() != null ? promessa.getCampanha().getTitulo() : null;
        return toResponseDTO(promessa, nomeDoador, nomeCampanha, null);
    }

    @Transactional
    public void cancelar(Long id) {
        Promessa promessa = buscarPendente(id);
        promessa.setStatus(StatusPromessa.CANCELADA);
        promessaRepository.save(promessa);
    }

    // --- helpers ---

    private EntradaMoeda confirmarMoeda(Promessa promessa, ConfirmarPromessaDTO dto, Adm adm) {
        if (dto.valorReal() == null)
            throw new RegraDeNegocioException("Informe o valor real recebido");
        if (dto.tipoPagamento() == null)
            throw new RegraDeNegocioException("Informe o tipo de pagamento");

        EntradaMoeda entrada = EntradaMoeda.builder()
                .campanha(promessa.getCampanha())
                .doador(promessa.getDoador())
                .gerenciador(adm.getUsuario())
                .promessa(promessa)
                .valor(dto.valorReal())
                .tipoPagamento(dto.tipoPagamento())
                .status(StatusPagamento.PAGO)
                .dataHoraEntrada(LocalDateTime.now())
                .build();

        return entradaMoedaRepository.save(entrada);
    }

    private EntradaProduto confirmarProduto(Promessa promessa, ConfirmarPromessaDTO dto, Adm adm) {
        if (dto.idProduto() == null)
            throw new RegraDeNegocioException("Informe o produto recebido");
        if (dto.quantidadeReal() == null)
            throw new RegraDeNegocioException("Informe a quantidade recebida");

        Produto produto = produtoRepository.findById(dto.idProduto())
                .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado"));

        EntradaProduto entrada = EntradaProduto.builder()
                .campanha(promessa.getCampanha())
                .doador(promessa.getDoador())
                .responsavel(adm.getUsuario())
                .promessa(promessa)
                .produto(produto)
                .quantidade(dto.quantidadeReal())
                .destino(dto.destino())
                .status(StatusEntrada.CONFIRMADO)
                .dataHoraEntrada(LocalDateTime.now())
                .build();

        return entradaProdutoRepository.save(entrada);
    }

    private Doador resolverDoadorAutenticado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (!(principal instanceof Usuario usuario)) {
            return null; // não autenticado
        }

        return doadorRepository.findByUsuario(usuario)
                .orElseGet(() -> criarDoadorParaUsuario(usuario));
    }

    private Doador criarDoadorParaUsuario(Usuario usuario) {
        Doador novoDoador = Doador.builder()
                .usuario(usuario)
                .anonimo(false)
                .dataHoraCadastro(LocalDateTime.now())
                .build();
        return doadorRepository.save(novoDoador);
    }

    private String resolverNomeDoador(Doador doador) {
        if (doador.getNome() != null) return doador.getNome();
        if (doador.getUsuario() != null) return doador.getUsuario().getNome();
        return "Anônimo";
    }

    private void validarCamposDoacao(TipoDoacao tipo, java.math.BigDecimal valor, String descricao) {
        if (tipo == TipoDoacao.MOEDA && valor == null)
            throw new RegraDeNegocioException("Informe o valor prometido para doação em dinheiro");
        if (tipo == TipoDoacao.PRODUTO && (descricao == null || descricao.isBlank()))
            throw new RegraDeNegocioException("Informe a descrição do produto prometido");
    }

    private Promessa buscarPendente(Long id) {
        Promessa promessa = promessaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Promessa não encontrada"));
        if (promessa.getStatus() != StatusPromessa.PENDENTE)
            throw new RegraDeNegocioException("Esta promessa já foi " + promessa.getStatus().name().toLowerCase());
        return promessa;
    }

    private Adm getAdmAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return admRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("ADM não encontrado"));
    }

    private String montarMensagemWhatsapp(String nomeDoador, String nomeCampanha, TipoDoacao tipo,
                                          java.math.BigDecimal valor, String descricao) {
        StringBuilder sb = new StringBuilder();
        sb.append("Nova promessa de doacao!\n\n");
        sb.append("Campanha: ").append(nomeCampanha).append("\n");
        sb.append("Doador: ").append(nomeDoador).append("\n");

        if (tipo == TipoDoacao.MOEDA) {
            sb.append("Tipo: Dinheiro\n");
            sb.append("Valor prometido: R$ ").append(valor).append("\n\n");
            sb.append("O doador deve enviar o comprovante PIX para:\n");
            sb.append("Email: ").append(emailAsilo).append("\n");
            sb.append("Telefone: ").append(telefoneAsilo);
        } else {
            sb.append("Tipo: Produto\n");
            sb.append("Descricao: ").append(descricao).append("\n\n");
            sb.append("O doador deve comparecer ao local com o CPF:\n");
            sb.append("Endereco: ").append(enderecoAsilo).append("\n");
            sb.append("Telefone: ").append(telefoneAsilo);
        }

        return sb.toString();
    }

    private PromessaResponseDTO toResponseDTO(Promessa p, String nomeDoador, String nomeCampanha, String mensagem) {
        return new PromessaResponseDTO(
                p.getId(),
                nomeDoador,
                nomeCampanha,
                p.getTipo(),
                p.getValor(),
                p.getDescricao(),
                p.getStatus(),
                p.getDataHora(),
                mensagem
        );
    }
}