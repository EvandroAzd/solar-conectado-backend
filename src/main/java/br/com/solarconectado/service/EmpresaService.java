package br.com.solarconectado.service;

import br.com.solarconectado.dto.*;
import br.com.solarconectado.entity.Empresa;
import br.com.solarconectado.entity.RepresentanteEmpresa;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.exception.UsuarioNaoEncontradoException;
import br.com.solarconectado.repository.EmpresaRepository;
import br.com.solarconectado.repository.RepresentanteEmpresaRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class EmpresaService {

    private final EmpresaRepository empresaRepository;
    private final RepresentanteEmpresaRepository representanteRepository;
    private final UsuarioRepository usuarioRepository;
    private final JwtService jwtService;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public EmpresaResponseDTO cadastrar(UUID usuarioId, EmpresaDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (representanteRepository.existsByUsuario(usuario)) {
            throw new RegraDeNegocioException("Usuário já é representante de uma empresa");
        }

        Empresa empresa = Empresa.builder()
                .usuario(usuario)
                .nome(dto.nome())
                .cnpj(dto.cnpj())
                .email(dto.email())
                .descricao(dto.descricao())
                .urlLogo(dto.urlLogo())
                .ativo(false)
                .pontos(0)
                .dataHoraCad(LocalDateTime.now())
                .build();
        empresaRepository.save(empresa);

        RepresentanteEmpresa rep = RepresentanteEmpresa.builder()
                .empresa(empresa)
                .usuario(usuario)
                .cargo(dto.cargo())
                .principal(true)
                .ativo(false)
                .dataHoraCad(LocalDateTime.now())
                .build();
        representanteRepository.save(rep);

        return EmpresaResponseDTO.de(empresa);
    }

    @Transactional
    public EmpresaResponseDTO aprovar(UUID empresaId) {
        Empresa empresa = buscarOuLancar(empresaId);

        if (empresa.getAtivo()) {
            throw new RegraDeNegocioException("Empresa já está ativa");
        }

        empresa.setAtivo(true);
        empresa.setDataHoraUpdate(LocalDateTime.now());
        empresaRepository.save(empresa);

        representanteRepository.findByEmpresaAndPrincipalTrue(empresa).ifPresent(rep -> {
            rep.setAtivo(true);
            representanteRepository.save(rep);
        });

        return EmpresaResponseDTO.de(empresa);
    }

    @Transactional
    public EmpresaResponseDTO inativar(UUID empresaId) {
        Empresa empresa = buscarOuLancar(empresaId);

        if (!empresa.getAtivo()) {
            throw new RegraDeNegocioException("Empresa já está inativa");
        }

        empresa.setAtivo(false);
        empresa.setDataHoraUpdate(LocalDateTime.now());
        empresaRepository.save(empresa);

        representanteRepository.findAllByEmpresa(empresa).forEach(rep -> {
            rep.setAtivo(false);
            representanteRepository.save(rep);
        });

        return EmpresaResponseDTO.de(empresa);
    }

    @Transactional
    public void deletar(UUID empresaId, UUID usuarioId) {
        Empresa empresa = buscarOuLancar(empresaId);
        validarPrincipalOuIgnorar(empresa, usuarioId);
        empresaRepository.delete(empresa);
    }

    public List<EmpresaResponseDTO> listarAtivas() {
        return empresaRepository.findAll().stream()
                .filter(Empresa::getAtivo)
                .map(EmpresaResponseDTO::de)
                .toList();
    }

    public List<EmpresaResponseDTO> listarTodas() {
        return empresaRepository.findAll().stream()
                .map(EmpresaResponseDTO::de)
                .toList();
    }

    public EmpresaResponseDTO buscarPorId(UUID id) {
        return EmpresaResponseDTO.de(buscarOuLancar(id));
    }

    // --- Representantes ---

    @Transactional
    public RepresentanteResponseDTO solicitarIngresso(UUID empresaId, UUID usuarioId, RepresentanteDTO dto) {
        Empresa empresa = buscarOuLancar(empresaId);

        if (!empresa.getAtivo()) {
            throw new RegraDeNegocioException("Empresa inativa não aceita novos representantes");
        }

        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        if (representanteRepository.existsByUsuario(usuario)) {
            throw new RegraDeNegocioException("Usuário já é representante de uma empresa");
        }

        RepresentanteEmpresa rep = RepresentanteEmpresa.builder()
                .empresa(empresa)
                .usuario(usuario)
                .cargo(dto.cargo())
                .principal(false)
                .ativo(false)
                .dataHoraCad(LocalDateTime.now())
                .build();

        return RepresentanteResponseDTO.de(representanteRepository.save(rep));
    }

    @Transactional
    public RepresentanteResponseDTO aprovarRepresentante(Integer repId, UUID usuarioIdPrincipal) {
        RepresentanteEmpresa rep = buscarRepOuLancar(repId);

        if (rep.getPrincipal()) {
            throw new RegraDeNegocioException("Representante principal é aprovado junto com a empresa");
        }
        if (rep.getAtivo()) {
            throw new RegraDeNegocioException("Representante já está ativo");
        }

        validarEhPrincipal(rep.getEmpresa(), usuarioIdPrincipal);

        rep.setAtivo(true);
        return RepresentanteResponseDTO.de(representanteRepository.save(rep));
    }

    @Transactional
    public RepresentanteResponseDTO inativarRepresentante(Integer repId, UUID usuarioId) {
        RepresentanteEmpresa rep = buscarRepOuLancar(repId);

        if (rep.getPrincipal()) {
            throw new RegraDeNegocioException("Não é possível inativar o representante principal");
        }
        if (!rep.getAtivo()) {
            throw new RegraDeNegocioException("Representante já está inativo");
        }

        rep.setAtivo(false);
        return RepresentanteResponseDTO.de(representanteRepository.save(rep));
    }

    @Transactional
    public void deletarRepresentante(Integer repId, UUID usuarioId) {
        RepresentanteEmpresa rep = buscarRepOuLancar(repId);

        if (rep.getPrincipal()) {
            throw new RegraDeNegocioException("Não é possível remover o representante principal individualmente");
        }

        if (usuarioId != null) {
            boolean ehPrincipalDaEmpresa = representanteRepository
                    .findByEmpresaAndPrincipalTrue(rep.getEmpresa())
                    .map(r -> r.getUsuario().getId().equals(usuarioId))
                    .orElse(false);

            if (!ehPrincipalDaEmpresa) {
                throw new RegraDeNegocioException("Apenas o representante principal pode remover outros representantes");
            }
        }

        representanteRepository.delete(rep);
    }

    public List<RepresentanteResponseDTO> listarRepresentantes(UUID empresaId) {
        Empresa empresa = buscarOuLancar(empresaId);
        return representanteRepository.findAllByEmpresa(empresa).stream()
                .map(RepresentanteResponseDTO::de)
                .toList();
    }

    // --- Minha empresa ---

    public EmpresaResponseDTO minhaEmpresa(UUID usuarioId) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException("Usuário não encontrado"));

        RepresentanteEmpresa rep = representanteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não possui empresa"));

        return EmpresaResponseDTO.de(rep.getEmpresa());
    }

    // --- Rejeitar empresa pendente ---

    @Transactional
    public void rejeitar(UUID empresaId) {
        Empresa empresa = buscarOuLancar(empresaId);

        if (empresa.getAtivo()) {
            throw new RegraDeNegocioException("Use /inativar para empresas já aprovadas");
        }

        empresaRepository.delete(empresa);
    }

    // --- Login como representante ---

    public LoginResponseEmpresaDTO login(EmpresaLoginDTO dto) {
        Usuario usuario = usuarioRepository.findByEmail(dto.email())
                .orElseThrow(() -> new RegraDeNegocioException("Credenciais inválidas"));

        if (!passwordEncoder.matches(dto.senha(), usuario.getSenha())) {
            throw new RegraDeNegocioException("Credenciais inválidas");
        }

        RepresentanteEmpresa rep = representanteRepository.findByUsuario(usuario)
                .orElseThrow(() -> new RegraDeNegocioException("Usuário não é representante de nenhuma empresa"));

        if (!rep.getAtivo()) {
            throw new RegraDeNegocioException("Representante inativo ou aguardando aprovação");
        }

        if (!rep.getEmpresa().getAtivo()) {
            throw new RegraDeNegocioException("Empresa inativa");
        }

        String token = jwtService.gerarTokenRepresentante(usuario.getEmail(), rep.getEmpresa().getId().toString());

        return new LoginResponseEmpresaDTO(
                token,
                rep.getEmpresa().getId(),
                rep.getEmpresa().getNome(),
                rep.getCargo(),
                rep.getPrincipal()
        );
    }

    // --- Helpers ---

    private Empresa buscarOuLancar(UUID id) {
        return empresaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Empresa não encontrada"));
    }

    private RepresentanteEmpresa buscarRepOuLancar(Integer id) {
        return representanteRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Representante não encontrado"));
    }

    private void validarEhPrincipal(Empresa empresa, UUID usuarioId) {
        boolean ehPrincipal = representanteRepository.findByEmpresaAndPrincipalTrue(empresa)
                .map(r -> r.getUsuario().getId().equals(usuarioId))
                .orElse(false);

        if (!ehPrincipal) {
            throw new RegraDeNegocioException("Apenas o representante principal pode realizar esta ação");
        }
    }

    private void validarPrincipalOuIgnorar(Empresa empresa, UUID usuarioId) {
        // chamado também pelo MASTER (usuarioId = null nesse caso)
        if (usuarioId == null) return;
        validarEhPrincipal(empresa, usuarioId);
    }
}
