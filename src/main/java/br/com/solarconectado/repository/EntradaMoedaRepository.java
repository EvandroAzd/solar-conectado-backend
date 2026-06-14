package br.com.solarconectado.repository;

import br.com.solarconectado.entity.EntradaMoeda;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradaMoedaRepository extends JpaRepository<EntradaMoeda, Integer> {
}
