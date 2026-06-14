package br.com.solarconectado.controller;

import br.com.solarconectado.dto.*;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.service.EmpresaService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class EmpresaController {

    private final EmpresaService empresaService;

    // --- Empresa ---

    @PostMapping("/empresas")
    public ResponseEntity<EmpresaResponseDTO> cadastrar(
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid EmpresaDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(empresaService.cadastrar(usuario.getId(), dto));
    }

    @GetMapping("/empresas")
    public ResponseEntity<List<EmpresaResponseDTO>> listarAtivas() {
        return ResponseEntity.ok(empresaService.listarAtivas());
    }

    @GetMapping("/empresas/adm")
    public ResponseEntity<List<EmpresaResponseDTO>> listarTodas() {
        return ResponseEntity.ok(empresaService.listarTodas());
    }

    @GetMapping("/empresas/{id}")
    public ResponseEntity<EmpresaResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.buscarPorId(id));
    }

    @GetMapping("/empresas/minhas")
    public ResponseEntity<EmpresaResponseDTO> minhaEmpresa(@AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(empresaService.minhaEmpresa(usuario.getId()));
    }

    @PatchMapping("/empresas/{id}/aprovar")
    public ResponseEntity<EmpresaResponseDTO> aprovar(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.aprovar(id));
    }

    @PatchMapping("/empresas/{id}/inativar")
    public ResponseEntity<EmpresaResponseDTO> inativar(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.inativar(id));
    }

    @DeleteMapping("/empresas/{id}/rejeitar")
    public ResponseEntity<Void> rejeitar(@PathVariable UUID id) {
        empresaService.rejeitar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/empresas/{id}")
    public ResponseEntity<Void> deletar(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuario,
            org.springframework.security.core.Authentication authentication) {
        boolean isMaster = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MASTER"));
        empresaService.deletar(id, isMaster ? null : usuario.getId());
        return ResponseEntity.noContent().build();
    }

    // --- Login como representante ---

    @PostMapping("/empresas/login")
    public ResponseEntity<LoginResponseEmpresaDTO> login(@RequestBody @Valid EmpresaLoginDTO dto) {
        return ResponseEntity.ok(empresaService.login(dto));
    }

    // --- Representantes ---

    @PostMapping("/empresas/{id}/representantes")
    public ResponseEntity<RepresentanteResponseDTO> solicitarIngresso(
            @PathVariable UUID id,
            @AuthenticationPrincipal Usuario usuario,
            @RequestBody @Valid RepresentanteDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(empresaService.solicitarIngresso(id, usuario.getId(), dto));
    }

    @GetMapping("/empresas/{id}/representantes")
    public ResponseEntity<List<RepresentanteResponseDTO>> listarRepresentantes(@PathVariable UUID id) {
        return ResponseEntity.ok(empresaService.listarRepresentantes(id));
    }

    @PatchMapping("/representantes/{id}/aprovar")
    public ResponseEntity<RepresentanteResponseDTO> aprovarRepresentante(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(empresaService.aprovarRepresentante(id, usuario.getId()));
    }

    @PatchMapping("/representantes/{id}/inativar")
    public ResponseEntity<RepresentanteResponseDTO> inativarRepresentante(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuario) {
        return ResponseEntity.ok(empresaService.inativarRepresentante(id, usuario.getId()));
    }

    @DeleteMapping("/representantes/{id}")
    public ResponseEntity<Void> deletarRepresentante(
            @PathVariable Integer id,
            @AuthenticationPrincipal Usuario usuario,
            org.springframework.security.core.Authentication authentication) {
        boolean isMaster = authentication.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_MASTER"));
        empresaService.deletarRepresentante(id, isMaster ? null : usuario.getId());
        return ResponseEntity.noContent().build();
    }
}
