package br.com.solarconectado.controller;

import br.com.solarconectado.dto.*;
import br.com.solarconectado.enums.StatusTransacaoBazar;
import br.com.solarconectado.service.BazarService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;


@RestController
@RequiredArgsConstructor
public class BazarController {

    private final BazarService bazarService;

    // ===== Centro de Custo =====

    @PostMapping("/centros-custo")
    public ResponseEntity<CentroCustoResponseDTO> criarCentro(@Valid @RequestBody CentroCustoDTO dto) {
        return ResponseEntity.status(201).body(bazarService.criarCentroCusto(dto));
    }

    @GetMapping("/centros-custo")
    public List<CentroCustoResponseDTO> listarCentros(@RequestParam(required = false) Boolean ativo) {
        return bazarService.listarCentros(ativo);
    }

    @PatchMapping("/centros-custo/{id}/inativar")
    public ResponseEntity<Void> inativarCentro(@PathVariable Integer id) {
        bazarService.inativarCentro(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/centros-custo/{id}/reativar")
    public ResponseEntity<Void> reativarCentro(@PathVariable Integer id) {
        bazarService.reativarCentro(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/centros-custo/{id}")
    public ResponseEntity<Void> excluirCentro(@PathVariable Integer id) {
        bazarService.excluirCentro(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Itens do Bazar =====

    @PostMapping("/itens-bazar")
    public ResponseEntity<ItemBazarResponseDTO> adicionarItem(@Valid @RequestBody ItemBazarDTO dto) {
        return ResponseEntity.status(201).body(bazarService.adicionarItem(dto));
    }

    @GetMapping("/itens-bazar")
    public List<ItemBazarResponseDTO> listarItens(@RequestParam(required = false) Boolean ativo) {
        return bazarService.listarItens(ativo);
    }

    @GetMapping("/itens-bazar/{id}")
    public ItemBazarResponseDTO buscarItem(@PathVariable UUID id) {
        return bazarService.buscarItemPorId(id);
    }

    @PutMapping("/itens-bazar/{id}")
    public ItemBazarResponseDTO editarItem(@PathVariable UUID id, @Valid @RequestBody ItemBazarEdicaoDTO dto) {
        return bazarService.editarItem(id, dto);
    }

    @PatchMapping("/itens-bazar/{id}/ativar")
    public ResponseEntity<Void> ativarItem(@PathVariable UUID id) {
        bazarService.ativarItem(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/itens-bazar/{id}/inativar")
    public ResponseEntity<Void> inativarItem(@PathVariable UUID id) {
        bazarService.inativarItem(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/itens-bazar/{id}")
    public ResponseEntity<Void> excluirItem(@PathVariable UUID id) {
        bazarService.excluirItem(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Saída Interna =====

    @PostMapping("/saidas-internas")
    public ResponseEntity<SaidaInternaResponseDTO> registrarSaida(@Valid @RequestBody SaidaInternaDTO dto) {
        return ResponseEntity.status(201).body(bazarService.registrarSaidaInterna(dto));
    }

    @GetMapping("/saidas-internas")
    public List<SaidaInternaResponseDTO> listarSaidas() {
        return bazarService.listarSaidasInternas();
    }

    // ===== Público =====

    @GetMapping("/bazar/itens")
    public List<ItemBazarResponseDTO> listarItensPublico() {
        return bazarService.listarItensPublico();
    }

    // ===== Pedidos =====

    @PostMapping("/bazar/pedidos")
    public ResponseEntity<PedidoBazarResponseDTO> criarPedido(@Valid @RequestBody PedidoBazarDTO dto) {
        return ResponseEntity.status(201).body(bazarService.criarPedido(dto));
    }

    @GetMapping("/bazar/pedidos")
    public List<PedidoBazarResponseDTO> listarPedidos(@RequestParam(required = false) StatusTransacaoBazar status) {
        return bazarService.listarPedidos(status);
    }

    @PatchMapping("/bazar/pedidos/{id}/confirmar")
    public PedidoBazarResponseDTO confirmarPedido(@PathVariable Integer id) {
        return bazarService.confirmarPedido(id);
    }

    @PatchMapping("/bazar/pedidos/{id}/cancelar")
    public ResponseEntity<Void> cancelarPedido(@PathVariable Integer id) {
        bazarService.cancelarPedido(id);
        return ResponseEntity.noContent().build();
    }

    // ===== Relatórios =====

    @GetMapping("/bazar/relatorios")
    public RelatorioBazarResponseDTO relatorio(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime inicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime fim) {
        return bazarService.relatorio(inicio, fim);
    }
}