package br.com.solarconectado.repository;

import br.com.solarconectado.entity.CategoriaProduto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoriaProdutoRepository extends JpaRepository<CategoriaProduto, Integer> {
    List<CategoriaProduto> findAllByAtivo(boolean ativo);
    boolean existsByNomeIgnoreCase(String nome);
}