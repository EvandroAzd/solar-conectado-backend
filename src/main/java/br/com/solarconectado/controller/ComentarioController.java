package br.com.solarconectado.controller;

import br.com.solarconectado.dto.ComentarioDTO;
import br.com.solarconectado.dto.ComentarioResponseDTO;
import br.com.solarconectado.service.ComentarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/comentarios")
@RequiredArgsConstructor
public class ComentarioController {

    private final ComentarioService comentarioService;

    @PostMapping
    public ResponseEntity<ComentarioResponseDTO> comentar(@RequestBody @Valid ComentarioDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(comentarioService.comentar(dto));
    }

    @GetMapping("/aprovados")
    public ResponseEntity<List<ComentarioResponseDTO>> listarAprovados() {
        return ResponseEntity.ok(comentarioService.listarAprovados());
    }

    @GetMapping("/pendentes")
    public ResponseEntity<List<ComentarioResponseDTO>> listarPendentes() {
        return ResponseEntity.ok(comentarioService.listarPendentes());
    }

    @PatchMapping("/{id}/aprovar")
    public ResponseEntity<ComentarioResponseDTO> aprovar(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.aprovar(id));
    }

    @PatchMapping("/{id}/rejeitar")
    public ResponseEntity<ComentarioResponseDTO> rejeitar(@PathVariable Long id) {
        return ResponseEntity.ok(comentarioService.rejeitar(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        comentarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{id}/curtir")
    public ResponseEntity<Void> curtir(@PathVariable Long id) {
        comentarioService.curtir(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}/curtir")
    public ResponseEntity<Void> descurtir(@PathVariable Long id) {
        comentarioService.descurtir(id);
        return ResponseEntity.noContent().build();
    }
}