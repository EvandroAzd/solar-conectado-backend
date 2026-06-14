package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Promessa;
import br.com.solarconectado.enums.StatusPromessa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface PromessaRepository extends JpaRepository<Promessa, Long> {
    List<Promessa> findByStatusOrderByDataHoraAsc(StatusPromessa status);
}