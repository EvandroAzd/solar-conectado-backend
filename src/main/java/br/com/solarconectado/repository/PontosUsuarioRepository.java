package br.com.solarconectado.repository;

import br.com.solarconectado.entity.PontosUsuario;
import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface PontosUsuarioRepository extends JpaRepository<PontosUsuario, Integer> {
    Optional<PontosUsuario> findByUsuario(Usuario usuario);

    @Modifying
    @Query(value = "DELETE FROM historico_pontos WHERE id_usuario = :id", nativeQuery = true)
    void deleteHistoricoByUsuarioId(@Param("id") UUID id);

    @Modifying
    @Query(value = "DELETE FROM indicacoes WHERE id_usuario_indicado = :id", nativeQuery = true)
    void deleteIndicacoesByUsuarioIndicadoId(@Param("id") UUID id);
}