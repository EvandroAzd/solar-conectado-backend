package br.com.solarconectado.controller;

import br.com.solarconectado.dto.DoadorExternoDTO;
import br.com.solarconectado.dto.DoadorResponseDTO;
import br.com.solarconectado.service.DoadorService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/doadores")
@RequiredArgsConstructor
public class DoadorController {

    private final DoadorService doadorService;

    @GetMapping("/buscar")
    public ResponseEntity<DoadorResponseDTO> buscarPorCpf(@RequestParam String cpf) {
        return doadorService.buscarPorCpf(cpf)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PostMapping("/externo")
    public ResponseEntity<DoadorResponseDTO> cadastrarExterno(@RequestBody @Valid DoadorExternoDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(doadorService.cadastrarExterno(dto));
    }
}