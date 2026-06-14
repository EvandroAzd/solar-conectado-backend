package br.com.solarconectado.controller;

import br.com.solarconectado.dto.ConfigGamificacaoDTO;
import br.com.solarconectado.dto.ConfigGamificacaoResponseDTO;
import br.com.solarconectado.service.ConfigGamificacaoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/config/gamificacao")
@RequiredArgsConstructor
public class ConfigGamificacaoController {

    private final ConfigGamificacaoService configService;

    @GetMapping
    public ResponseEntity<ConfigGamificacaoResponseDTO> buscar() {
        return ResponseEntity.ok(configService.buscar());
    }

    @PutMapping
    public ResponseEntity<ConfigGamificacaoResponseDTO> atualizar(@Valid @RequestBody ConfigGamificacaoDTO dto) {
        return ResponseEntity.ok(configService.atualizar(dto));
    }
}