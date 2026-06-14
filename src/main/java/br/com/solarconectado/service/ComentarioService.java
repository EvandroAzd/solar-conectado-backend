package br.com.solarconectado.service;

import br.com.solarconectado.dto.ComentarioDTO;
import br.com.solarconectado.dto.ComentarioResponseDTO;
import br.com.solarconectado.entity.Comentario;
import br.com.solarconectado.entity.CurtidaComentario;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.enums.StatusComentario;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.ComentarioRepository;
import br.com.solarconectado.repository.CurtidaComentarioRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ComentarioService {

    private final ComentarioRepository comentarioRepository;
    private final CurtidaComentarioRepository curtidaRepository;
    private final UsuarioRepository usuarioRepository;

    @Transactional
    public ComentarioResponseDTO comentar(ComentarioDTO dto) {
        Optional<Usuario> usuario = getUsuarioAutenticado();

        Comentario comentario = Comentario.builder()
                .usuario(usuario.orElse(null))
                .texto(dto.texto())
                .status(StatusComentario.PENDENTE)
                .dataHora(LocalDateTime.now())
                .build();

        comentarioRepository.save(comentario);
        return toResponseDTO(comentario, 0L);
    }

    public List<ComentarioResponseDTO> listarAprovados() {
        return comentarioRepository.findByStatus(StatusComentario.APROVADO)
                .stream()
                .map(c -> toResponseDTO(c, curtidaRepository.countByComentario(c)))
                .toList();
    }

    public List<ComentarioResponseDTO> listarPendentes() {
        return comentarioRepository.findByStatus(StatusComentario.PENDENTE)
                .stream()
                .map(c -> toResponseDTO(c, 0L))
                .toList();
    }

    @Transactional
    public ComentarioResponseDTO aprovar(Long id) {
        Comentario comentario = buscarPorId(id);

        if (comentario.getStatus() != StatusComentario.PENDENTE)
            throw new RegraDeNegocioException("Apenas comentários pendentes podem ser aprovados");

        comentario.setStatus(StatusComentario.APROVADO);
        return toResponseDTO(comentario, 0L);
    }

    @Transactional
    public ComentarioResponseDTO rejeitar(Long id) {
        Comentario comentario = buscarPorId(id);

        if (comentario.getStatus() == StatusComentario.REJEITADO)
            throw new RegraDeNegocioException("Comentário já está rejeitado");

        comentario.setStatus(StatusComentario.REJEITADO);
        return toResponseDTO(comentario, curtidaRepository.countByComentario(comentario));
    }

    @Transactional
    public void deletar(Long id) {
        Comentario comentario = buscarPorId(id);
        curtidaRepository.deleteByComentarioAndUsuario(comentario, null);
        comentarioRepository.delete(comentario);
    }

    @Transactional
    public void curtir(Long id) {
        Usuario usuario = getUsuarioAutenticadoOuErro();
        Comentario comentario = buscarPorId(id);

        if (comentario.getStatus() != StatusComentario.APROVADO)
            throw new RegraDeNegocioException("Só é possível curtir comentários aprovados");

        if (curtidaRepository.existsByComentarioAndUsuario(comentario, usuario))
            throw new RegraDeNegocioException("Você já curtiu este comentário");

        CurtidaComentario curtida = CurtidaComentario.builder()
                .id(new CurtidaComentario.CurtidaId(comentario.getId(), usuario.getId()))
                .comentario(comentario)
                .usuario(usuario)
                .dataHora(LocalDateTime.now())
                .build();

        curtidaRepository.save(curtida);
    }

    @Transactional
    public void descurtir(Long id) {
        Usuario usuario = getUsuarioAutenticadoOuErro();
        Comentario comentario = buscarPorId(id);

        if (!curtidaRepository.existsByComentarioAndUsuario(comentario, usuario))
            throw new RegraDeNegocioException("Você não curtiu este comentário");

        curtidaRepository.deleteByComentarioAndUsuario(comentario, usuario);
    }

    // --- métodos auxiliares ---

    private Comentario buscarPorId(Long id) {
        return comentarioRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Comentário não encontrado"));
    }

    private Optional<Usuario> getUsuarioAutenticado() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated() || auth instanceof AnonymousAuthenticationToken)
            return Optional.empty();
        return usuarioRepository.findByEmail(auth.getName());
    }

    private Usuario getUsuarioAutenticadoOuErro() {
        return getUsuarioAutenticado()
                .orElseThrow(() -> new RegraDeNegocioException("É necessário estar logado para realizar esta ação"));
    }

    private ComentarioResponseDTO toResponseDTO(Comentario comentario, long totalCurtidas) {
        String nomeAutor = comentario.getUsuario() != null
                ? comentario.getUsuario().getNome()
                : "Anônimo";
        return new ComentarioResponseDTO(
                comentario.getId(),
                nomeAutor,
                comentario.getTexto(),
                comentario.getDataHora(),
                totalCurtidas,
                comentario.getStatus()
        );
    }
}