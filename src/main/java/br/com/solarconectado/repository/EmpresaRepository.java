package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.UUID;

public interface EmpresaRepository extends JpaRepository<Empresa, UUID> {
}
