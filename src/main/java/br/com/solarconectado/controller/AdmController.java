package br.com.solarconectado.controller;

import br.com.solarconectado.dto.AdmResponseDTO;
import br.com.solarconectado.dto.PromoverAdmDTO;
import br.com.solarconectado.service.AdmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/adm")
@RequiredArgsConstructor
public class AdmController {

    private final AdmService admService;

    @GetMapping
    public ResponseEntity<List<AdmResponseDTO>> listarTodos() {
        return ResponseEntity.ok(admService.listarTodos());
    }

    @PostMapping("/promover/{usuarioId}")
    public ResponseEntity<AdmResponseDTO> promover(
            @PathVariable UUID usuarioId,
            @Valid @RequestBody PromoverAdmDTO dto) {
        return ResponseEntity.status(201).body(admService.promover(usuarioId, dto));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        admService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable UUID id) {
        admService.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> remover(@PathVariable UUID id) {
        admService.remover(id);
        return ResponseEntity.noContent().build();
    }
}