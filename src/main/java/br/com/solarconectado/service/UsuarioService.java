package br.com.solarconectado.service;

import br.com.solarconectado.dto.LoginResponseDTO;
import br.com.solarconectado.dto.UsuarioEdicaoDTO;
import br.com.solarconectado.dto.UsuarioLoginDTO;
import br.com.solarconectado.dto.UsuarioPerfilResponseDTO;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.dto.UsuarioCadastroDTO;
import br.com.solarconectado.dto.UsuarioResponseDTO;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.exception.UsuarioNaoEncontradoException;
import br.com.solarconectado.repository.AdmRepository;
import br.com.solarconectado.repository.ComentarioRepository;
import br.com.solarconectado.repository.CurtidaComentarioRepository;
import br.com.solarconectado.repository.PontosUsuarioRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import java.util.List;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

// @Service informa ao Spring que esta classe é um componente de serviço gerenciado por ele
// @RequiredArgsConstructor (Lombok) gera o construtor que injeta os campos final — isso é injeção de dependência
@Service
@RequiredArgsConstructor
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final AdmRepository admRepository;
    private final ComentarioRepository comentarioRepository;
    private final CurtidaComentarioRepository curtidaRepository;
    private final PontosUsuarioRepository pontosUsuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final CompartilhamentoService compartilhamentoService;

    @Lazy
    @Autowired
    private AuthenticationManager authenticationManager;

    // --- Chamado pelo Spring Security ao autenticar ---
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado: " + email));
    }



    // --- Usado internamente por outros serviços (ex: AdmService ao criar o master) ---
    @Transactional
    public Usuario cadastrarUsuarioInterno(UsuarioCadastroDTO dto) {
        validarDadosUnicos(dto.email(), dto.cpf());

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .ativo(Boolean.TRUE)
                .pontos(0)
                .dataHoraCad(LocalDateTime.now())
                .build();

        return usuarioRepository.save(usuario);
    }

    // --- Usuário se auto-cadastra ---
    @Transactional
    public UsuarioResponseDTO cadastrarUsuario(UsuarioCadastroDTO dto) {
        validarDadosUnicos(dto.email(), dto.cpf());

        Usuario usuario = Usuario.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .senha(passwordEncoder.encode(dto.senha()))
                .ativo(Boolean.TRUE)
                .pontos(0)
                .dataHoraCad(LocalDateTime.now())
                .build();

        Usuario salvo = usuarioRepository.save(usuario);

        if (dto.refToken() != null && !dto.refToken().isBlank()) {
            compartilhamentoService.processarIndicacao(dto.refToken(), salvo);
        }

        return UsuarioResponseDTO.de(salvo);
    }

    public List<UsuarioResponseDTO> listarTodos(Boolean ativo) {
        List<Usuario> usuarios = (ativo != null)
                ? usuarioRepository.findAllByAtivo(ativo)
                : usuarioRepository.findAll();
        return usuarios.stream().map(UsuarioResponseDTO::de).toList();
    }

    public UsuarioPerfilResponseDTO meuPerfil() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));
        return UsuarioPerfilResponseDTO.de(usuario);
    }

    @Transactional
    public UsuarioPerfilResponseDTO editarPerfil(UsuarioEdicaoDTO dto) {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        if (dto.urlFoto() != null) {
            usuario.setUrlFoto(dto.urlFoto());
        }
        if (dto.senha() != null && !dto.senha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.senha()));
        }

        usuario.setDataHoraAtualizacao(LocalDateTime.now());
        return UsuarioPerfilResponseDTO.de(usuarioRepository.save(usuario));
    }

    public UsuarioResponseDTO buscarPorId(UUID id) {
        return usuarioRepository.findById(id)
                .map(UsuarioResponseDTO::de)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id.toString()));
    }

    private void validarDadosUnicos(String email, String cpf) {
        if (usuarioRepository.existsByEmail(email)) {
            throw new RegraDeNegocioException("E-mail já cadastrado: " + email);
        }
        if (usuarioRepository.existsByCpf(cpf)) {
            throw new RegraDeNegocioException("CPF já cadastrado: " + cpf);
        }
    }

    @Transactional
    public void inativar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        if (!usuario.getAtivo())
            throw new RegraDeNegocioException("Usuário já está inativo");

        usuario.setAtivo(false);
    }

    @Transactional
    public void reativar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        if (usuario.getAtivo())
            throw new RegraDeNegocioException("Usuário já está ativo");

        usuario.setAtivo(true);
    }

    @Transactional
    public void deletar(UUID id) {
        Usuario usuario = usuarioRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não encontrado"));

        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (usuario.getEmail().equals(emailAutenticado))
            throw new RegraDeNegocioException("Não é possível excluir a si mesmo");

        admRepository.findByUsuarioEmail(usuario.getEmail())
                .ifPresent(admRepository::delete);

        pontosUsuarioRepository.deleteIndicacoesByUsuarioIndicadoId(id);
        pontosUsuarioRepository.deleteHistoricoByUsuarioId(id);
        usuarioRepository.deletePontosByUsuarioId(id);
        usuarioRepository.desvinculaDoador(id);
        curtidaRepository.deleteByUsuario(usuario);
        curtidaRepository.deleteByComentario_Usuario(usuario);
        comentarioRepository.deleteByUsuario(usuario);
        usuarioRepository.delete(usuario);
    }

    public LoginResponseDTO login(UsuarioLoginDTO dto) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.email(), dto.senha())
        );
        String token = jwtService.gerarToken(dto.email());
        boolean isAdm = admRepository.findByUsuarioEmail(dto.email()).isPresent();
        return new LoginResponseDTO(token, isAdm);
    }
}
