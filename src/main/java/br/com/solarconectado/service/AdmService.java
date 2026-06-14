package br.com.solarconectado.service;

import br.com.solarconectado.dto.AdmResponseDTO;
import br.com.solarconectado.dto.PromoverAdmDTO;
import br.com.solarconectado.entity.Adm;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.dto.UsuarioCadastroDTO;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.exception.UsuarioNaoEncontradoException;
import br.com.solarconectado.repository.AdmRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AdmService {

    private final AdmRepository admRepository;
    private final UsuarioRepository usuarioRepository;
    private final UsuarioService usuarioService;

    @Transactional
    public void cadastrarAdmMaster(String nome, String email, String senha, String cpf) {
        if (admRepository.existsByIsMaster(true)) {
            throw new RegraDeNegocioException("Admin master já existe.");
        }

        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseGet(() -> {
                    UsuarioCadastroDTO dto = new UsuarioCadastroDTO(nome, cpf, email, senha, null);
                    return usuarioService.cadastrarUsuarioInterno(dto);
                });

        Adm adm = Adm.builder()
                .usuario(usuario)
                .cargo("MASTER")
                .isMaster(Boolean.TRUE)
                .ativo(Boolean.TRUE)
                .build();

        admRepository.save(adm);
    }

    public List<AdmResponseDTO> listarTodos() {
        return admRepository.findAll()
                .stream()
                .map(AdmResponseDTO::de)
                .toList();
    }

    @Transactional
    public AdmResponseDTO promover(UUID usuarioId, PromoverAdmDTO dto) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(usuarioId.toString()));

        if (admRepository.findByUsuarioEmail(usuario.getEmail()).isPresent())
            throw new RegraDeNegocioException("Usuário já é ADM");

        Adm adm = Adm.builder()
                .usuario(usuario)
                .cargo(dto.cargo())
                .isMaster(false)
                .ativo(true)
                .build();

        return AdmResponseDTO.de(admRepository.save(adm));
    }

    public Adm buscarPorId(UUID id) {
        return admRepository.findById(id)
                .orElseThrow(() -> new UsuarioNaoEncontradoException(id.toString()));
    }

    @Transactional
    public void remover(UUID id) {
        Adm adm = buscarPorId(id);

        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (adm.getUsuario().getEmail().equals(emailAutenticado))
            throw new RegraDeNegocioException("Não é possível remover a si mesmo como ADM");

        if (adm.getIsMaster())
            throw new RegraDeNegocioException("Não é possível remover o ADM master");

        admRepository.delete(adm);
    }

    @Transactional
    public void inativar(UUID id) {
        Adm adm = buscarPorId(id);

        String emailAutenticado = SecurityContextHolder.getContext().getAuthentication().getName();
        if (adm.getUsuario().getEmail().equals(emailAutenticado))
            throw new RegraDeNegocioException("Não é possível inativar a si mesmo");

        if (!adm.getAtivo())
            throw new RegraDeNegocioException("Este ADM já está inativo");

        adm.setAtivo(false);
    }

    @Transactional
    public void reativar(UUID id) {
        Adm adm = buscarPorId(id);

        if (adm.getAtivo())
            throw new RegraDeNegocioException("Este ADM já está ativo");

        adm.setAtivo(true);
    }
}

