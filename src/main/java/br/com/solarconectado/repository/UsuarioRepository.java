package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UsuarioRepository extends JpaRepository<Usuario, UUID> {

    Optional<Usuario> findByEmail(String email);

    boolean existsByEmail(String email);

    boolean existsByCpf(String cpf);
    Optional<Usuario> findByCpf(String cpf);

    List<Usuario> findAllByAtivo(boolean ativo);

    @Modifying
    @Query(value = "DELETE FROM pontos_usuarios WHERE id_usuario = :id", nativeQuery = true)
    void deletePontosByUsuarioId(@Param("id") UUID id);

    @Modifying
    @Query(value = "UPDATE doadores SET id_usuario = NULL WHERE id_usuario = :id", nativeQuery = true)
    void desvinculaDoador(@Param("id") UUID id);
}
