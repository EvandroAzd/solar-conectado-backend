package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Comentario;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.enums.StatusComentario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ComentarioRepository extends JpaRepository<Comentario, Long> {
    List<Comentario> findByStatus(StatusComentario status);
    void deleteByUsuario(Usuario usuario);
}