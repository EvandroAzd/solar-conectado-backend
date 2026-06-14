package br.com.solarconectado.controller;

import br.com.solarconectado.dto.CampanhaAdmResponseDTO;
import br.com.solarconectado.dto.CampanhaDTO;
import br.com.solarconectado.dto.CampanhaEdicaoDTO;
import br.com.solarconectado.dto.CampanhaReativacaoDTO;
import br.com.solarconectado.dto.CampanhaResponseDTO;
import br.com.solarconectado.dto.CompartilhamentoResponseDTO;
import br.com.solarconectado.service.CampanhaService;
import br.com.solarconectado.service.CompartilhamentoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import java.util.UUID;

@RestController
@RequestMapping("/campanhas")
@RequiredArgsConstructor
public class CampanhaController {

    private final CampanhaService campanhaService;
    private final CompartilhamentoService compartilhamentoService;

    @PostMapping
    public ResponseEntity<CampanhaAdmResponseDTO> criar(@RequestBody @Valid CampanhaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(campanhaService.criar(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<CampanhaAdmResponseDTO> editar(@PathVariable UUID id, @RequestBody CampanhaEdicaoDTO dto) {
        return ResponseEntity.ok(campanhaService.editar(id, dto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        campanhaService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable UUID id, @RequestBody @Valid CampanhaReativacaoDTO dto) {
        campanhaService.reativar(id, dto);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/encerrar")
    public ResponseEntity<Void> encerrar(@PathVariable UUID id) {
        campanhaService.encerrar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        campanhaService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping
    public ResponseEntity<List<CampanhaResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(campanhaService.listarAtivas());
    }

    @GetMapping("/adm")
    public ResponseEntity<List<CampanhaAdmResponseDTO>> listarTodas() {
        return ResponseEntity.ok(campanhaService.listarTodas());
    }

    @PostMapping("/{id}/compartilhar")
    public ResponseEntity<CompartilhamentoResponseDTO> compartilhar(@PathVariable UUID id) {
        return ResponseEntity.ok(compartilhamentoService.compartilhar(id));
    }
}