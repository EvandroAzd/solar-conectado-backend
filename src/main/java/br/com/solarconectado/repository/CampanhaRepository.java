package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Campanha;
import br.com.solarconectado.enums.StatusCampanha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CampanhaRepository extends JpaRepository<Campanha, UUID> {
    List<Campanha> findByStatus(StatusCampanha status);
}