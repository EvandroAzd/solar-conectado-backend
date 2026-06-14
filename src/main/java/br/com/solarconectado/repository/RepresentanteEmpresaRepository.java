package br.com.solarconectado.repository;

import br.com.solarconectado.entity.Empresa;
import br.com.solarconectado.entity.RepresentanteEmpresa;
import br.com.solarconectado.entity.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RepresentanteEmpresaRepository extends JpaRepository<RepresentanteEmpresa, Integer> {

    boolean existsByUsuario(Usuario usuario);

    Optional<RepresentanteEmpresa> findByUsuario(Usuario usuario);

    Optional<RepresentanteEmpresa> findByEmpresaAndPrincipalTrue(Empresa empresa);

    List<RepresentanteEmpresa> findAllByEmpresa(Empresa empresa);

    void deleteAllByEmpresa(Empresa empresa);
}
