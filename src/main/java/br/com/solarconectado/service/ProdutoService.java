package br.com.solarconectado.service;

import br.com.solarconectado.dto.*;
import br.com.solarconectado.entity.CategoriaProduto;
import br.com.solarconectado.entity.Produto;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.CategoriaProdutoRepository;
import br.com.solarconectado.repository.ProdutoRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ProdutoService {

    private final CategoriaProdutoRepository categoriaRepository;
    private final ProdutoRepository produtoRepository;
    private final UsuarioRepository usuarioRepository;

    // ---- Categoria ----

    @Transactional
    public CategoriaProdutoResponseDTO criarCategoria(CategoriaProdutoDTO dto) {
        if (categoriaRepository.existsByNomeIgnoreCase(dto.nome()))
            throw new RegraDeNegocioException("Já existe uma categoria com esse nome");

        CategoriaProduto categoria = CategoriaProduto.builder()
                .nome(dto.nome())
                .ativo(true)
                .build();

        return toCategoriaResponse(categoriaRepository.save(categoria));
    }

    public List<CategoriaProdutoResponseDTO> listarCategorias(Boolean ativo) {
        List<CategoriaProduto> lista = ativo != null
                ? categoriaRepository.findAllByAtivo(ativo)
                : categoriaRepository.findAll();

        return lista.stream().map(this::toCategoriaResponse).toList();
    }

    @Transactional
    public void inativarCategoria(Integer id) {
        CategoriaProduto categoria = buscarCategoria(id);

        if (!categoria.getAtivo())
            throw new RegraDeNegocioException("Categoria já está inativa");

        if (produtoRepository.existsByCategoriaIdAndAtivoTrue(id))
            throw new RegraDeNegocioException("Não é possível inativar uma categoria com produtos ativos vinculados");

        categoria.setAtivo(false);
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void reativarCategoria(Integer id) {
        CategoriaProduto categoria = buscarCategoria(id);

        if (categoria.getAtivo())
            throw new RegraDeNegocioException("Categoria já está ativa");

        categoria.setAtivo(true);
        categoriaRepository.save(categoria);
    }

    @Transactional
    public void excluirCategoria(Integer id) {
        CategoriaProduto categoria = buscarCategoria(id);

        if (produtoRepository.existsByCategoriaIdAndAtivoTrue(id))
            throw new RegraDeNegocioException("Não é possível excluir uma categoria com produtos ativos vinculados");

        categoriaRepository.delete(categoria);
    }

    // ---- Produto ----

    @Transactional
    public ProdutoResponseDTO criarProduto(ProdutoDTO dto) {
        CategoriaProduto categoria = buscarCategoria(dto.categoriaId());

        if (!categoria.getAtivo())
            throw new RegraDeNegocioException("Não é possível criar um produto em uma categoria inativa");

        Usuario criador = getUsuarioAutenticado();

        Produto produto = Produto.builder()
                .categoria(categoria)
                .criador(criador)
                .nome(dto.nome())
                .descricao(dto.descricao())
                .urlImagem(dto.urlImagem())
                .ativo(true)
                .build();

        return toProdutoResponse(produtoRepository.save(produto));
    }

    public List<ProdutoResponseDTO> listarProdutos(Boolean ativo, Integer categoriaId) {
        List<Produto> lista;

        if (categoriaId != null && ativo != null) {
            lista = produtoRepository.findAllByCategoriaIdAndAtivo(categoriaId, ativo);
        } else if (categoriaId != null) {
            lista = produtoRepository.findAllByCategoriaId(categoriaId);
        } else if (ativo != null) {
            lista = produtoRepository.findAllByAtivo(ativo);
        } else {
            lista = produtoRepository.findAll();
        }

        return lista.stream().map(this::toProdutoResponse).toList();
    }

    public ProdutoResponseDTO buscarPorId(UUID id) {
        return toProdutoResponse(buscarProduto(id));
    }

    @Transactional
    public void inativarProduto(UUID id) {
        Produto produto = buscarProduto(id);

        if (!produto.getAtivo())
            throw new RegraDeNegocioException("Produto já está inativo");

        produto.setAtivo(false);
        produtoRepository.save(produto);
    }

    @Transactional
    public void reativarProduto(UUID id) {
        Produto produto = buscarProduto(id);

        if (produto.getAtivo())
            throw new RegraDeNegocioException("Produto já está ativo");

        if (!produto.getCategoria().getAtivo())
            throw new RegraDeNegocioException("Não é possível reativar um produto de uma categoria inativa");

        produto.setAtivo(true);
        produtoRepository.save(produto);
    }

    @Transactional
    public void excluirProduto(UUID id) {
        produtoRepository.delete(buscarProduto(id));
    }

    // ---- helpers ----

    private CategoriaProduto buscarCategoria(Integer id) {
        return categoriaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Categoria não encontrada"));
    }

    private Produto buscarProduto(UUID id) {
        return produtoRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Produto não encontrado"));
    }

    private Usuario getUsuarioAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
    }

    private CategoriaProdutoResponseDTO toCategoriaResponse(CategoriaProduto c) {
        return new CategoriaProdutoResponseDTO(c.getId(), c.getNome(), c.getAtivo());
    }

    private ProdutoResponseDTO toProdutoResponse(Produto p) {
        return new ProdutoResponseDTO(
                p.getId(),
                p.getNome(),
                p.getDescricao(),
                p.getUrlImagem(),
                toCategoriaResponse(p.getCategoria()),
                p.getAtivo()
        );
    }
}