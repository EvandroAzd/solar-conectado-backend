package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Adm;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface AdmRepository extends JpaRepository<Adm, UUID> {
    boolean existsByIsMaster(boolean isMaster);

    Optional<Adm> findByUsuarioEmail(String emailAdm);
}
