package br.com.solarconectado.repository;

import br.com.solarconectado.entity.SaidaProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface SaidaProdutoRepository extends JpaRepository<SaidaProduto, Integer> {
    List<SaidaProduto> findAllByDataHoraSaidaBetween(LocalDateTime inicio, LocalDateTime fim);
}