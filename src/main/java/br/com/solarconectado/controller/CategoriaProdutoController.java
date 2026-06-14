package br.com.solarconectado.controller;

import br.com.solarconectado.dto.CategoriaProdutoDTO;
import br.com.solarconectado.dto.CategoriaProdutoResponseDTO;
import br.com.solarconectado.service.ProdutoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/categorias")
@RequiredArgsConstructor
public class CategoriaProdutoController {

    private final ProdutoService produtoService;

    @PostMapping
    public ResponseEntity<CategoriaProdutoResponseDTO> criar(@Valid @RequestBody CategoriaProdutoDTO dto) {
        return ResponseEntity.status(201).body(produtoService.criarCategoria(dto));
    }

    @GetMapping
    public ResponseEntity<List<CategoriaProdutoResponseDTO>> listar(
            @RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(produtoService.listarCategorias(ativo));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable Integer id) {
        produtoService.inativarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable Integer id) {
        produtoService.reativarCategoria(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> excluir(@PathVariable Integer id) {
        produtoService.excluirCategoria(id);
        return ResponseEntity.noContent().build();
    }
}