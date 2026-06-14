package br.com.solarconectado.controller;

import br.com.solarconectado.dto.ProdutoDTO;
import br.com.solarconectado.dto.ProdutoResponseDTO;
import br.com.solarconectado.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/produtos")
@RequiredArgsConstructor
public class ProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoDTO dto) {
        return ResponseEntity.status(201).body(produtoService.criarProduto(dto));
    }

    @GetMapping
    public ResponseEntity<List<ProdutoResponseDTO>> listar(
            @RequestParam(required = false) Boolean ativo,
            @RequestParam(required = false) Integer categoriaId) {
        return ResponseEntity.ok(produtoService.listarProdutos(ativo, categoriaId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscar(@PathVariable UUID id) {
        return ResponseEntity.ok(produtoService.buscarPorId(id));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        produtoService.inativarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable UUID id) {
        produtoService.reativarProduto(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable UUID id) {
        produtoService.excluirProduto(id);
        return ResponseEntity.noContent().build();
    }
}