package br.com.solarconectado.repository;

import br.com.solarconectado.entity.HistoricoPontos;
import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface HistoricoPontosRepository extends JpaRepository<HistoricoPontos, Integer> {
    List<HistoricoPontos> findByUsuarioOrderByDataHoraDesc(Usuario usuario);
}