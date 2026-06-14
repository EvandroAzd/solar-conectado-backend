package br.com.solarconectado.controller;

import br.com.solarconectado.dto.LoginResponseDTO;
import br.com.solarconectado.dto.UsuarioCadastroDTO;
import br.com.solarconectado.dto.UsuarioEdicaoDTO;
import br.com.solarconectado.dto.UsuarioLoginDTO;
import br.com.solarconectado.dto.UsuarioPerfilResponseDTO;
import br.com.solarconectado.dto.UsuarioResponseDTO;
import br.com.solarconectado.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;

    @PostMapping("/login")
    public LoginResponseDTO login(@RequestBody UsuarioLoginDTO dto){
        return usuarioService.login(dto);
    }

    @PostMapping("/cadastrar")
    public UsuarioResponseDTO cadastro(@RequestBody UsuarioCadastroDTO dto){
        return usuarioService.cadastrarUsuario(dto);
    }

    @GetMapping("/perfil")
    public ResponseEntity<UsuarioPerfilResponseDTO> meuPerfil() {
        return ResponseEntity.ok(usuarioService.meuPerfil());
    }

    @PutMapping("/perfil")
    public ResponseEntity<UsuarioPerfilResponseDTO> editarPerfil(@RequestBody UsuarioEdicaoDTO dto) {
        return ResponseEntity.ok(usuarioService.editarPerfil(dto));
    }

    @GetMapping
    public ResponseEntity<List<UsuarioResponseDTO>> listarTodos(@RequestParam(required = false) Boolean ativo) {
        return ResponseEntity.ok(usuarioService.listarTodos(ativo));
    }

    @GetMapping("/{id}")
    public ResponseEntity<UsuarioResponseDTO> buscarPorId(@PathVariable UUID id) {
        return ResponseEntity.ok(usuarioService.buscarPorId(id));
    }

    @PatchMapping("/{id}/inativar")
    public ResponseEntity<Void> inativar(@PathVariable UUID id) {
        usuarioService.inativar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/reativar")
    public ResponseEntity<Void> reativar(@PathVariable UUID id) {
        usuarioService.reativar(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable UUID id) {
        usuarioService.deletar(id);
        return ResponseEntity.noContent().build();
    }

}
