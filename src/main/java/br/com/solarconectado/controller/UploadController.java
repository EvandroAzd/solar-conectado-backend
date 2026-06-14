package br.com.solarconectado.controller;

import br.com.solarconectado.service.UploadService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@RestController
@RequestMapping("/upload")
@RequiredArgsConstructor
public class UploadController {

    private final UploadService uploadService;

    @PostMapping("/campanha")
    public ResponseEntity<Map<String, String>> uploadCampanha(@RequestParam("arquivo") MultipartFile arquivo) {
        String url = uploadService.upload(arquivo, "campanhas");
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/perfil")
    public ResponseEntity<Map<String, String>> uploadPerfil(@RequestParam("arquivo") MultipartFile arquivo) {
        String url = uploadService.upload(arquivo, "perfis");
        return ResponseEntity.ok(Map.of("url", url));
    }

    @PostMapping("/empresa")
    public ResponseEntity<Map<String, String>> uploadEmpresa(@RequestParam("arquivo") MultipartFile arquivo) {
        String url = uploadService.upload(arquivo, "empresas");
        return ResponseEntity.ok(Map.of("url", url));
    }
}