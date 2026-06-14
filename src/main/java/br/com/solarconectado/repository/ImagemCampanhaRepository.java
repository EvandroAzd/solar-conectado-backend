package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Campanha;
import br.com.solarconectado.entity.ImagemCampanha;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ImagemCampanhaRepository extends JpaRepository<ImagemCampanha, Integer> {
    List<ImagemCampanha> findByCampanha(Campanha campanha);
    void deleteByCampanha(Campanha campanha);
}