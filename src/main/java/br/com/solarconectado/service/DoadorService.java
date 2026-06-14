package br.com.solarconectado.service;

import br.com.solarconectado.dto.DoadorExternoDTO;
import br.com.solarconectado.dto.DoadorResponseDTO;
import br.com.solarconectado.entity.Doador;
import br.com.solarconectado.entity.Usuario;
import br.com.solarconectado.enums.TipoDoador;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.DoadorRepository;
import br.com.solarconectado.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DoadorService {

    private final DoadorRepository doadorRepository;
    private final UsuarioRepository usuarioRepository;

    public Optional<DoadorResponseDTO> buscarPorCpf(String cpf) {
        // 1. busca doador externo pelo CPF
        Optional<Doador> doadorExterno = doadorRepository.findByCpf(cpf);
        if (doadorExterno.isPresent()) {
            return Optional.of(toResponseDTO(doadorExterno.get(), TipoDoador.EXTERNO));
        }

        // 2. busca usuário do sistema pelo CPF
        Optional<Usuario> usuario = usuarioRepository.findByCpf(cpf);
        if (usuario.isPresent()) {
            Optional<Doador> doadorUsuario = doadorRepository.findByUsuario(usuario.get());
            if (doadorUsuario.isPresent()) {
                return Optional.of(toResponseDTO(doadorUsuario.get(), TipoDoador.USUARIO));
            }
            // usuário existe mas ainda nunca doou — retorna sem id de doador
            Usuario u = usuario.get();
            return Optional.of(new DoadorResponseDTO(null, u.getId(), u.getNome(), u.getCpf(), u.getEmail(), TipoDoador.USUARIO));
        }

        return Optional.empty();
    }

    @Transactional
    public DoadorResponseDTO cadastrarExterno(DoadorExternoDTO dto) {
        if (doadorRepository.existsByCpf(dto.cpf()))
            throw new RegraDeNegocioException("Já existe um doador externo com este CPF");

        if (usuarioRepository.existsByCpf(dto.cpf()))
            throw new RegraDeNegocioException("Este CPF pertence a um usuário cadastrado no sistema");

        Doador doador = Doador.builder()
                .nome(dto.nome())
                .cpf(dto.cpf())
                .email(dto.email())
                .anonimo(false)
                .dataHoraCadastro(LocalDateTime.now())
                .build();

        doadorRepository.save(doador);
        return toResponseDTO(doador, TipoDoador.EXTERNO);
    }

    private DoadorResponseDTO toResponseDTO(Doador doador, TipoDoador tipo) {
        Usuario u = doador.getUsuario();
        return new DoadorResponseDTO(
                doador.getId(),
                u != null ? u.getId() : null,
                doador.getNome() != null ? doador.getNome() : (u != null ? u.getNome() : null),
                doador.getCpf() != null ? doador.getCpf() : (u != null ? u.getCpf() : null),
                doador.getEmail() != null ? doador.getEmail() : (u != null ? u.getEmail() : null),
                tipo
        );
    }
}