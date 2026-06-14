package br.com.solarconectado.controller;

import br.com.solarconectado.dto.ConfirmarPromessaDTO;
import br.com.solarconectado.dto.PromessaCampanhaDTO;
import br.com.solarconectado.dto.PromessaEspontaneaDTO;
import br.com.solarconectado.dto.PromessaResponseDTO;
import br.com.solarconectado.service.PromessaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/promessas")
@RequiredArgsConstructor
public class PromessaController {

    private final PromessaService promessaService;

    @PostMapping("/campanha/{idCampanha}")
    public ResponseEntity<PromessaResponseDTO> prometerParaCampanha(
            @PathVariable UUID idCampanha,
            @RequestBody @Valid PromessaCampanhaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promessaService.prometerParaCampanha(idCampanha, dto));
    }

    @PostMapping("/espontanea")
    public ResponseEntity<PromessaResponseDTO> prometerEspontanea(
            @RequestBody @Valid PromessaEspontaneaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(promessaService.prometerEspontanea(dto));
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<PromessaResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(promessaService.listarPendentes());
    }

    @PatchMapping("/{id}/confirmar")
    public ResponseEntity<PromessaResponseDTO> confirmar(
            @PathVariable Long id,
            @RequestBody @Valid ConfirmarPromessaDTO dto) {
        return ResponseEntity.ok(promessaService.confirmar(id, dto));
    }

    @PatchMapping("/{id}/cancelar")
    public ResponseEntity<Void> cancelar(@PathVariable Long id) {
        promessaService.cancelar(id);
        return ResponseEntity.noContent().build();
    }
}