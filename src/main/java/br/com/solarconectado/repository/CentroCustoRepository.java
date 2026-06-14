package br.com.solarconectado.repository;

import br.com.solarconectado.entity.CentroCusto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CentroCustoRepository extends JpaRepository<CentroCusto, Integer> {
    boolean existsByNomeIgnoreCase(String nome);
    List<CentroCusto> findAllByAtivo(boolean ativo);
}