package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface ProdutoRepository extends JpaRepository<Produto, UUID> {
    List<Produto> findAllByAtivo(boolean ativo);
    List<Produto> findAllByCategoriaId(Integer categoriaId);
    List<Produto> findAllByCategoriaIdAndAtivo(Integer categoriaId, boolean ativo);
    boolean existsByCategoriaIdAndAtivoTrue(Integer categoriaId);
}
