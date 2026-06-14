package br.com.solarconectado.repository;

import br.com.solarconectado.entity.TransacaoBazar;
import br.com.solarconectado.enums.StatusTransacaoBazar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface TransacaoBazarRepository extends JpaRepository<TransacaoBazar, Integer> {
    List<TransacaoBazar> findAllByStatus(StatusTransacaoBazar status);
    List<TransacaoBazar> findAllByDataHoraTransacaoBetween(LocalDateTime inicio, LocalDateTime fim);
    List<TransacaoBazar> findAllByStatusAndDataHoraTransacaoBetween(StatusTransacaoBazar status, LocalDateTime inicio, LocalDateTime fim);
}