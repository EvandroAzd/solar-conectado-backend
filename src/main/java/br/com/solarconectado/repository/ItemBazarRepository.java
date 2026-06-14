package br.com.solarconectado.repository;

import br.com.solarconectado.entity.ItemBazar;
import br.com.solarconectado.enums.StatusItemBazar;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ItemBazarRepository extends JpaRepository<ItemBazar, UUID> {
    List<ItemBazar> findAllByAtivo(boolean ativo);
    List<ItemBazar> findAllByStatus(StatusItemBazar status);
    List<ItemBazar> findAllByAtivoAndStatus(boolean ativo, StatusItemBazar status);
}