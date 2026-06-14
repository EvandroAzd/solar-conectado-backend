package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Doador;
import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DoadorRepository extends JpaRepository<Doador, UUID> {
    Optional<Doador> findByCpf(String cpf);
    Optional<Doador> findByUsuario(Usuario usuario);
    boolean existsByCpf(String cpf);
}
