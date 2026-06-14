package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Indicacao;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IndicacaoRepository extends JpaRepository<Indicacao, Integer> {
}