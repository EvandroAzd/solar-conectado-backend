package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Comentario;
import br.com.solarconectado.entity.CurtidaComentario;
import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CurtidaComentarioRepository extends JpaRepository<CurtidaComentario, CurtidaComentario.CurtidaId> {
    long countByComentario(Comentario comentario);
    boolean existsByComentarioAndUsuario(Comentario comentario, Usuario usuario);
    void deleteByComentarioAndUsuario(Comentario comentario, Usuario usuario);
    void deleteByUsuario(Usuario usuario);
    void deleteByComentario_Usuario(Usuario usuario);
}