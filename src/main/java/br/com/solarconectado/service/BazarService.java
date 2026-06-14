package br.com.solarconectado.service;

import br.com.solarconectado.dto.*;
import br.com.solarconectado.entity.*;
import br.com.solarconectado.enums.StatusItemBazar;
import br.com.solarconectado.enums.StatusTransacaoBazar;
import br.com.solarconectado.enums.TipoTransacaoBazar;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BazarService {

    private final CentroCustoRepository centroCustoRepository;
    private final ItemBazarRepository itemBazarRepository;
    private final SaidaProdutoRepository saidaProdutoRepository;
    private final TransacaoBazarRepository transacaoBazarRepository;
    private final ItemTransacaoBazarRepository itemTransacaoBazarRepository;
    private final ProdutoRepository produtoRepository;
    private final EntradaProdutoRepository entradaProdutoRepository;
    private final UsuarioRepository usuarioRepository;

    @Value("${contato.whatsapp.adm}")
    private String whatsappAdm;

    // ===== Centro de Custo =====

    @Transactional
    public CentroCustoResponseDTO criarCentroCusto(CentroCustoDTO dto) {
        if (centroCustoRepository.existsByNomeIgnoreCase(dto.nome()))
            throw new RegraDeNegocioException("Já existe um centro de custo com esse nome");

        CentroCusto centro = CentroCusto.builder()
                .nome(dto.nome())
                .descricao(dto.descricao())
                .ativo(true)
                .dataHoraCad(LocalDateTime.now())
                .build();

        return toCentroResponse(centroCustoRepository.save(centro));
    }

    public List<CentroCustoResponseDTO> listarCentros(Boolean ativo) {
        List<CentroCusto> lista = ativo != null
                ? centroCustoRepository.findAllByAtivo(ativo)
                : centroCustoRepository.findAll();
        return lista.stream().map(this::toCentroResponse).toList();
    }

    @Transactional
    public void inativarCentro(Integer id) {
        CentroCusto centro = buscarCentro(id);
        if (!centro.getAtivo()) throw new RegraDeNegocioException("Centro de custo já está inativo");
        centro.setAtivo(false);
        centroCustoRepository.save(centro);
    }

    @Transactional
    public void reativarCentro(Integer id) {
        CentroCusto centro = buscarCentro(id);
        if (centro.getAtivo()) throw new RegraDeNegocioException("Centro de custo já está ativo");
        centro.setAtivo(true);
        centroCustoRepository.save(centro);
    }

    @Transactional
    public void excluirCentro(Integer id) {
        centroCustoRepository.delete(buscarCentro(id));
    }

    // ===== Itens do Bazar =====

    @Transactional
    public ItemBazarResponseDTO adicionarItem(ItemBazarDTO dto) {
        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado"));

        EntradaProduto entrada = null;
        if (dto.entradaProdutoId() != null) {
            entrada = entradaProdutoRepository.findById(dto.entradaProdutoId())
                    .orElseThrow(() -> new RegraDeNegocioException("Entrada de produto não encontrada"));
        }

        validarPrecoBazar(dto.valorUnitario(), dto.gratuito());

        Usuario responsavel = getUsuarioAutenticado();
        boolean gratuito = Boolean.TRUE.equals(dto.gratuito());

        ItemBazar item = ItemBazar.builder()
                .produto(produto)
                .entradaProduto(entrada)
                .responsavel(responsavel)
                .quantidade(dto.quantidade())
                .qtdDisponivel(dto.quantidade())
                .valorUnitario(gratuito ? null : dto.valorUnitario())
                .gratuito(gratuito)
                .ativo(false)
                .status(StatusItemBazar.DISPONIVEL)
                .dataHoraAdd(LocalDateTime.now())
                .dataHoraUpdate(LocalDateTime.now())
                .build();

        return toItemResponse(itemBazarRepository.save(item));
    }

    public List<ItemBazarResponseDTO> listarItens(Boolean ativo) {
        List<ItemBazar> lista = ativo != null
                ? itemBazarRepository.findAllByAtivo(ativo)
                : itemBazarRepository.findAll();
        return lista.stream().map(this::toItemResponse).toList();
    }

    public List<ItemBazarResponseDTO> listarItensPublico() {
        return itemBazarRepository.findAllByAtivoAndStatus(true, StatusItemBazar.DISPONIVEL)
                .stream().map(this::toItemResponse).toList();
    }

    public ItemBazarResponseDTO buscarItemPorId(UUID id) {
        return toItemResponse(buscarItem(id));
    }

    @Transactional
    public ItemBazarResponseDTO editarItem(UUID id, ItemBazarEdicaoDTO dto) {
        ItemBazar item = buscarItem(id);
        validarPrecoBazar(dto.valorUnitario(), dto.gratuito());

        if (dto.gratuito() != null) item.setGratuito(dto.gratuito());
        if (Boolean.TRUE.equals(dto.gratuito())) {
            item.setValorUnitario(null);
        } else if (dto.valorUnitario() != null) {
            item.setValorUnitario(dto.valorUnitario());
        }
        item.setDataHoraUpdate(LocalDateTime.now());

        return toItemResponse(itemBazarRepository.save(item));
    }

    @Transactional
    public void ativarItem(UUID id) {
        ItemBazar item = buscarItem(id);
        if (item.getAtivo()) throw new RegraDeNegocioException("Item já está ativo");

        if (!Boolean.TRUE.equals(item.getGratuito()) && item.getValorUnitario() == null)
            throw new RegraDeNegocioException("Defina o valor unitário ou marque o item como gratuito antes de ativar");

        item.setAtivo(true);
        item.setDataHoraUpdate(LocalDateTime.now());
        itemBazarRepository.save(item);
    }

    @Transactional
    public void inativarItem(UUID id) {
        ItemBazar item = buscarItem(id);
        if (!item.getAtivo()) throw new RegraDeNegocioException("Item já está inativo");
        item.setAtivo(false);
        item.setDataHoraUpdate(LocalDateTime.now());
        itemBazarRepository.save(item);
    }

    @Transactional
    public void excluirItem(UUID id) {
        itemBazarRepository.delete(buscarItem(id));
    }

    // ===== Saída Interna =====

    @Transactional
    public SaidaInternaResponseDTO registrarSaidaInterna(SaidaInternaDTO dto) {
        CentroCusto centro = buscarCentro(dto.centroCustoId());
        if (!centro.getAtivo()) throw new RegraDeNegocioException("Centro de custo está inativo");

        Produto produto = produtoRepository.findById(dto.produtoId())
                .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado"));

        EntradaProduto entrada = null;
        if (dto.entradaProdutoId() != null) {
            entrada = entradaProdutoRepository.findById(dto.entradaProdutoId())
                    .orElseThrow(() -> new RegraDeNegocioException("Entrada de produto não encontrada"));
        }

        Usuario responsavel = getUsuarioAutenticado();

        SaidaProduto saida = SaidaProduto.builder()
                .centroCusto(centro)
                .responsavel(responsavel)
                .produto(produto)
                .entradaProduto(entrada)
                .quantidade(dto.quantidade())
                .observacao(dto.observacao())
                .dataHoraSaida(LocalDateTime.now())
                .build();

        return toSaidaResponse(saidaProdutoRepository.save(saida));
    }

    public List<SaidaInternaResponseDTO> listarSaidasInternas() {
        return saidaProdutoRepository.findAll().stream()
                .map(this::toSaidaResponse)
                .toList();
    }

    // ===== Pedidos do Bazar =====

    @Transactional
    public PedidoBazarResponseDTO criarPedido(PedidoBazarDTO dto) {
        if (dto.itens().isEmpty())
            throw new RegraDeNegocioException("O pedido deve ter pelo menos um item");

        Usuario recebedor = resolverRecebedor();
        String nomeComprador = resolverNomeComprador(recebedor, dto.nomeComprador());

        List<ItemTransacaoBazar> linhas = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        TransacaoBazar transacao = TransacaoBazar.builder()
                .recebedor(recebedor)
                .nomeComprador(nomeComprador)
                .tipo(dto.tipo())
                .status(StatusTransacaoBazar.PENDENTE)
                .dataHoraTransacao(LocalDateTime.now())
                .build();

        transacao = transacaoBazarRepository.save(transacao);

        for (ItemPedidoDTO itemDto : dto.itens()) {
            ItemBazar item = buscarItem(itemDto.itemBazarId());

            if (!item.getAtivo())
                throw new RegraDeNegocioException("Item '" + item.getProduto().getNome() + "' não está disponível no bazar");

            if (item.getStatus() != StatusItemBazar.DISPONIVEL)
                throw new RegraDeNegocioException("Item '" + item.getProduto().getNome() + "' não está com status DISPONIVEL");

            if (item.getQtdDisponivel() < itemDto.quantidade())
                throw new RegraDeNegocioException("Quantidade solicitada de '" + item.getProduto().getNome() + "' indisponível. Disponível: " + item.getQtdDisponivel());

            BigDecimal valorUnitario = Boolean.TRUE.equals(item.getGratuito()) ? BigDecimal.ZERO : item.getValorUnitario();

            ItemTransacaoBazar linha = ItemTransacaoBazar.builder()
                    .transacao(transacao)
                    .itemBazar(item)
                    .quantidade(itemDto.quantidade())
                    .valorUnitario(valorUnitario)
                    .build();

            linhas.add(itemTransacaoBazarRepository.save(linha));
            total = total.add(valorUnitario.multiply(BigDecimal.valueOf(itemDto.quantidade())));
        }

        String mensagem = montarMensagemWhatsapp(nomeComprador, linhas, total, dto.tipo());

        transacao.setValorTotal(total);
        transacao.setMensagemWhatsapp(mensagem);
        transacao.setItens(linhas);
        transacaoBazarRepository.save(transacao);

        return toPedidoResponse(transacao);
    }

    public List<PedidoBazarResponseDTO> listarPedidos(StatusTransacaoBazar status) {
        List<TransacaoBazar> lista = status != null
                ? transacaoBazarRepository.findAllByStatus(status)
                : transacaoBazarRepository.findAll();
        return lista.stream().map(this::toPedidoResponse).toList();
    }

    @Transactional
    public PedidoBazarResponseDTO confirmarPedido(Integer id) {
        TransacaoBazar transacao = buscarTransacao(id);
        if (transacao.getStatus() != StatusTransacaoBazar.PENDENTE)
            throw new RegraDeNegocioException("Pedido já foi " + transacao.getStatus().name().toLowerCase());

        transacao.setStatus(StatusTransacaoBazar.CONFIRMADA);
        transacaoBazarRepository.save(transacao);

        for (ItemTransacaoBazar linha : transacao.getItens()) {
            ItemBazar item = linha.getItemBazar();
            int novaQtd = item.getQtdDisponivel() - linha.getQuantidade();
            item.setQtdDisponivel(novaQtd);

            if (novaQtd <= 0) {
                item.setStatus(StatusItemBazar.ESGOTADO);
            }
            item.setDataHoraUpdate(LocalDateTime.now());
            itemBazarRepository.save(item);

            // registra saída do estoque
            SaidaProduto saida = SaidaProduto.builder()
                    .centroCusto(null)
                    .responsavel(getUsuarioAutenticado())
                    .produto(item.getProduto())
                    .entradaProduto(item.getEntradaProduto())
                    .itemBazar(item)
                    .quantidade(linha.getQuantidade())
                    .observacao("Pedido bazar #" + transacao.getId())
                    .dataHoraSaida(LocalDateTime.now())
                    .build();
            saidaProdutoRepository.save(saida);
        }

        return toPedidoResponse(transacao);
    }

    @Transactional
    public void cancelarPedido(Integer id) {
        TransacaoBazar transacao = buscarTransacao(id);
        if (transacao.getStatus() != StatusTransacaoBazar.PENDENTE)
            throw new RegraDeNegocioException("Pedido já foi " + transacao.getStatus().name().toLowerCase());
        transacao.setStatus(StatusTransacaoBazar.CANCELADA);
        transacaoBazarRepository.save(transacao);
    }

    // ===== Relatórios =====

    public RelatorioBazarResponseDTO relatorio(LocalDateTime inicio, LocalDateTime fim) {
        List<TransacaoBazar> confirmadas = (inicio != null && fim != null)
                ? transacaoBazarRepository.findAllByStatusAndDataHoraTransacaoBetween(
                        StatusTransacaoBazar.CONFIRMADA, inicio, fim)
                : transacaoBazarRepository.findAllByStatus(StatusTransacaoBazar.CONFIRMADA);

        BigDecimal totalArrecadado = BigDecimal.ZERO;
        java.util.Map<String, RelatorioBazarResponseDTO.RelatorioProdutoDTO> porProduto = new java.util.LinkedHashMap<>();

        for (TransacaoBazar t : confirmadas) {
            totalArrecadado = totalArrecadado.add(t.getValorTotal() != null ? t.getValorTotal() : BigDecimal.ZERO);

            for (ItemTransacaoBazar linha : t.getItens()) {
                String nome = linha.getItemBazar().getProduto().getNome();
                BigDecimal subtotal = linha.getValorUnitario().multiply(BigDecimal.valueOf(linha.getQuantidade()));

                RelatorioBazarResponseDTO.RelatorioProdutoDTO atual = porProduto.get(nome);
                if (atual == null) {
                    porProduto.put(nome, new RelatorioBazarResponseDTO.RelatorioProdutoDTO(nome, linha.getQuantidade(), subtotal));
                } else {
                    porProduto.put(nome, new RelatorioBazarResponseDTO.RelatorioProdutoDTO(
                            nome,
                            atual.quantidadeVendida() + linha.getQuantidade(),
                            atual.valorArrecadado().add(subtotal)
                    ));
                }
            }
        }

        return new RelatorioBazarResponseDTO(
                totalArrecadado,
                (long) confirmadas.size(),
                new ArrayList<>(porProduto.values())
        );
    }

    // ===== helpers =====

    private CentroCusto buscarCentro(Integer id) {
        return centroCustoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Centro de custo não encontrado"));
    }

    private ItemBazar buscarItem(UUID id) {
        return itemBazarRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Item do bazar não encontrado"));
    }

    private TransacaoBazar buscarTransacao(Integer id) {
        return transacaoBazarRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Pedido não encontrado"));
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
    }

    private Usuario resolverRecebedor() {
        var auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || "anonymousUser".equals(auth.getPrincipal())) {
            return null;
        }
        return usuarioRepository.findByEmail(auth.getName()).orElse(null);
    }

    private String resolverNomeComprador(Usuario recebedor, String nomeExterno) {
        if (recebedor != null) return recebedor.getNome();
        if (nomeExterno != null && !nomeExterno.isBlank()) return nomeExterno;
        return "Comprador externo";
    }

    private void validarPrecoBazar(BigDecimal valorUnitario, Boolean gratuito) {
        boolean isGratuito = Boolean.TRUE.equals(gratuito);
        if (!isGratuito && valorUnitario == null) return; // valor pode ser definido depois via PUT
    }

    private String montarMensagemWhatsapp(String nomeComprador, List<ItemTransacaoBazar> linhas,
                                           BigDecimal total, TipoTransacaoBazar tipo) {
        StringBuilder sb = new StringBuilder();
        sb.append("Novo pedido no bazar!\n\n");
        sb.append("Comprador: ").append(nomeComprador).append("\n");
        sb.append("Tipo: ").append(tipo == TipoTransacaoBazar.VENDA ? "Compra" : "Doacao").append("\n\n");
        sb.append("Itens:\n");
        for (ItemTransacaoBazar linha : linhas) {
            sb.append("- ").append(linha.getItemBazar().getProduto().getNome())
              .append(" x").append(linha.getQuantidade());
            if (!Boolean.TRUE.equals(linha.getItemBazar().getGratuito())) {
                sb.append(" (R$ ").append(linha.getValorUnitario()).append(" cada)");
            }
            sb.append("\n");
        }
        if (tipo == TipoTransacaoBazar.VENDA) {
            sb.append("\nTotal: R$ ").append(total).append("\n");
        }
        sb.append("\nEntre em contato para retirada!");
        return sb.toString();
    }

    private CentroCustoResponseDTO toCentroResponse(CentroCusto c) {
        return new CentroCustoResponseDTO(c.getId(), c.getNome(), c.getDescricao(), c.getAtivo());
    }

    private ItemBazarResponseDTO toItemResponse(ItemBazar item) {
        Produto p = item.getProduto();
        ProdutoResponseDTO produtoDTO = new ProdutoResponseDTO(
                p.getId(), p.getNome(), p.getDescricao(), p.getUrlImagem(),
                new CategoriaProdutoResponseDTO(p.getCategoria().getId(), p.getCategoria().getNome(), p.getCategoria().getAtivo()),
                p.getAtivo()
        );
        return new ItemBazarResponseDTO(
                item.getId(), produtoDTO, item.getQuantidade(), item.getQtdDisponivel(),
                item.getValorUnitario(), item.getGratuito(), item.getAtivo(), item.getStatus()
        );
    }

    private SaidaInternaResponseDTO toSaidaResponse(SaidaProduto s) {
        return new SaidaInternaResponseDTO(
                s.getId(),
                s.getCentroCusto() != null ? s.getCentroCusto().getNome() : null,
                s.getProduto().getNome(),
                s.getQuantidade(),
                s.getObservacao(),
                s.getDataHoraSaida()
        );
    }

    private PedidoBazarResponseDTO toPedidoResponse(TransacaoBazar t) {
        List<ItemTransacaoResponseDTO> itensDTO = t.getItens() == null ? List.of() :
                t.getItens().stream().map(linha -> {
                    BigDecimal valorUnit = linha.getValorUnitario() != null ? linha.getValorUnitario() : BigDecimal.ZERO;
                    return new ItemTransacaoResponseDTO(
                            linha.getItemBazar().getId(),
                            linha.getItemBazar().getProduto().getNome(),
                            linha.getQuantidade(),
                            valorUnit,
                            valorUnit.multiply(BigDecimal.valueOf(linha.getQuantidade()))
                    );
                }).toList();

        return new PedidoBazarResponseDTO(
                t.getId(),
                t.getNomeComprador(),
                t.getTipo(),
                t.getStatus(),
                t.getValorTotal(),
                t.getMensagemWhatsapp(),
                t.getDataHoraTransacao(),
                itensDTO
        );
    }
}