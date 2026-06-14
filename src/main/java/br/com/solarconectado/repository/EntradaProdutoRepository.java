package br.com.solarconectado.repository;

import br.com.solarconectado.entity.EntradaProduto;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EntradaProdutoRepository extends JpaRepository<EntradaProduto, Integer> {
}
