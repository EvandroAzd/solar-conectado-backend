package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Campanha;
import br.com.solarconectado.entity.Compartilhamento;
import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompartilhamentoRepository extends JpaRepository<Compartilhamento, Long> {
    Optional<Compartilhamento> findByUsuarioAndCampanha(Usuario usuario, Campanha campanha);
    Optional<Compartilhamento> findByToken(String token);
}