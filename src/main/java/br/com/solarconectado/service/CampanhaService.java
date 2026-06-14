package br.com.solarconectado.service;

import br.com.solarconectado.dto.CampanhaAdmResponseDTO;
import br.com.solarconectado.dto.CampanhaDTO;
import br.com.solarconectado.dto.CampanhaEdicaoDTO;
import br.com.solarconectado.dto.CampanhaReativacaoDTO;
import br.com.solarconectado.dto.CampanhaResponseDTO;
import br.com.solarconectado.entity.Adm;
import br.com.solarconectado.entity.Campanha;
import br.com.solarconectado.entity.ImagemCampanha;
import br.com.solarconectado.enums.StatusCampanha;
import br.com.solarconectado.enums.TipoCampanha;
import br.com.solarconectado.exception.RegraDeNegocioException;
import br.com.solarconectado.repository.AdmRepository;
import br.com.solarconectado.repository.CampanhaRepository;
import br.com.solarconectado.repository.ImagemCampanhaRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CampanhaService {

    private final CampanhaRepository campanhaRepository;
    private final ImagemCampanhaRepository imagemCampanhaRepository;
    private final AdmRepository admRepository;

    @Transactional
    public CampanhaAdmResponseDTO criar(CampanhaDTO dto) {
        validarMetaPorTipo(dto.tipo(), dto.metaValor(), dto.metaQtd());

        Adm admCriador = getAdmAutenticado();

        Campanha campanha = Campanha.builder()
                .titulo(dto.titulo())
                .tipoCampanha(dto.tipo())
                .descricao(dto.descricao())
                .dataHoraInicio(dto.dataHoraInicio() != null ? dto.dataHoraInicio() : LocalDateTime.now())
                .dataHoraFinal(dto.dataHoraFinal())
                .metaValor(dto.metaValor())
                .metaQtd(dto.metaQtd())
                .admCriador(admCriador)
                .admResponsavel(admCriador)
                .dataHoraCad(LocalDateTime.now())
                .status(StatusCampanha.ATIVA)
                .build();

        campanhaRepository.save(campanha);

        List<String> urlsSalvas = salvarImagens(campanha, dto.urlsImagens());

        return toAdmResponseDTO(campanha, admCriador, urlsSalvas);
    }

    @Transactional
    public CampanhaAdmResponseDTO editar(UUID id, CampanhaEdicaoDTO dto) {
        Campanha campanha = buscarPorId(id);

        if (campanha.getStatus() == StatusCampanha.ENCERRADA)
            throw new RegraDeNegocioException("Campanhas encerradas não podem ser editadas");

        boolean iniciada = campanha.getDataHoraInicio() != null
                && campanha.getDataHoraInicio().isBefore(LocalDateTime.now());

        if (iniciada) {
            if (dto.titulo() != null || dto.descricao() != null
                    || dto.dataHoraInicio() != null || dto.urlsImagens() != null)
                throw new RegraDeNegocioException("Campanha já iniciada: só é permitido alterar meta e data de encerramento");

            if (dto.metaValor() != null) campanha.setMetaValor(dto.metaValor());
            if (dto.metaQtd() != null) campanha.setMetaQtd(dto.metaQtd());
            if (dto.dataHoraFinal() != null) campanha.setDataHoraFinal(dto.dataHoraFinal());
        } else {
            if (dto.titulo() != null) campanha.setTitulo(dto.titulo());
            if (dto.descricao() != null) campanha.setDescricao(dto.descricao());
            if (dto.dataHoraInicio() != null) campanha.setDataHoraInicio(dto.dataHoraInicio());
            if (dto.dataHoraFinal() != null) campanha.setDataHoraFinal(dto.dataHoraFinal());
            if (dto.metaValor() != null) campanha.setMetaValor(dto.metaValor());
            if (dto.metaQtd() != null) campanha.setMetaQtd(dto.metaQtd());

            if (dto.urlsImagens() != null) {
                imagemCampanhaRepository.deleteByCampanha(campanha);
                salvarImagens(campanha, dto.urlsImagens());
            }
        }

        campanha.setDataHoraAtualizacao(LocalDateTime.now());

        List<String> urls = imagemCampanhaRepository.findByCampanha(campanha)
                .stream().map(ImagemCampanha::getUrl).toList();

        return toAdmResponseDTO(campanha, campanha.getAdmCriador(), urls);
    }

    @Transactional
    public void inativar(UUID id) {
        Campanha campanha = buscarPorId(id);

        if (campanha.getStatus() != StatusCampanha.ATIVA)
            throw new RegraDeNegocioException("Apenas campanhas ativas podem ser inativadas");

        campanha.setStatus(StatusCampanha.INATIVA);
        campanha.setDataHoraInicio(null);
        campanha.setDataHoraFinal(null);
        campanha.setDataHoraAtualizacao(LocalDateTime.now());
    }

    @Transactional
    public void reativar(UUID id, CampanhaReativacaoDTO dto) {
        Campanha campanha = buscarPorId(id);

        if (campanha.getStatus() != StatusCampanha.INATIVA)
            throw new RegraDeNegocioException("Apenas campanhas inativas podem ser reativadas");

        campanha.setStatus(StatusCampanha.ATIVA);
        campanha.setDataHoraInicio(dto.dataHoraInicio());
        campanha.setDataHoraFinal(dto.dataHoraFinal());
        campanha.setDataHoraAtualizacao(LocalDateTime.now());
    }

    @Transactional
    public void encerrar(UUID id) {
        Campanha campanha = buscarPorId(id);

        if (campanha.getStatus() == StatusCampanha.ENCERRADA)
            throw new RegraDeNegocioException("Campanha já está encerrada");

        campanha.setStatus(StatusCampanha.ENCERRADA);
        campanha.setDataHoraAtualizacao(LocalDateTime.now());
    }

    @Transactional
    public void deletar(UUID id) {
        Campanha campanha = buscarPorId(id);
        imagemCampanhaRepository.deleteByCampanha(campanha);
        campanhaRepository.delete(campanha);
    }

    public List<CampanhaResponseDTO> listarAtivas() {
        return campanhaRepository.findByStatus(StatusCampanha.ATIVA)
                .stream()
                .map(this::toResponseDTO)
                .toList();
    }

    public List<CampanhaAdmResponseDTO> listarTodas() {
        return campanhaRepository.findAll()
                .stream()
                .map(c -> {
                    List<String> urls = imagemCampanhaRepository.findByCampanha(c)
                            .stream().map(ImagemCampanha::getUrl).toList();
                    return toAdmResponseDTO(c, c.getAdmCriador(), urls);
                })
                .toList();
    }

    // --- métodos auxiliares ---

    private Campanha buscarPorId(UUID id) {
        return campanhaRepository.findById(id)
                .orElseThrow(() -> new RegraDeNegocioException("Campanha não encontrada"));
    }

    private Adm getAdmAutenticado() {
        String email = SecurityContextHolder.getContext().getAuthentication().getName();
        return admRepository.findByUsuarioEmail(email)
                .orElseThrow(() -> new RegraDeNegocioException("Adm não encontrado"));
    }

    private List<String> salvarImagens(Campanha campanha, List<String> urls) {
        if (urls == null || urls.isEmpty()) return List.of();
        List<ImagemCampanha> imagens = urls.stream()
                .map(url -> ImagemCampanha.builder()
                        .campanha(campanha)
                        .url(url)
                        .principal(false)
                        .dataHoraCadastro(LocalDateTime.now())
                        .build())
                .toList();
        imagemCampanhaRepository.saveAll(imagens);
        return urls;
    }

    private void validarMetaPorTipo(TipoCampanha tipo, java.math.BigDecimal metaValor, Integer metaQtd) {
        if (tipo == TipoCampanha.DINHEIRO && metaQtd != null)
            throw new RegraDeNegocioException("Campanha de dinheiro não aceita meta de quantidade");

        if (tipo == TipoCampanha.DIVULGACAO && metaValor != null)
            throw new RegraDeNegocioException("Campanha de divulgação não aceita meta de valor");

        if (tipo == TipoCampanha.PRODUTO && metaValor != null)
            throw new RegraDeNegocioException("Campanha de produto não aceita meta de valor");

        if (tipo == TipoCampanha.CONSCIENTIZACAO && (metaValor != null || metaQtd != null))
            throw new RegraDeNegocioException("Campanha de conscientização não aceita meta");
    }

    private CampanhaResponseDTO toResponseDTO(Campanha campanha) {
        List<String> urls = imagemCampanhaRepository.findByCampanha(campanha)
                .stream().map(ImagemCampanha::getUrl).toList();
        return new CampanhaResponseDTO(
                campanha.getId().toString(),
                campanha.getTitulo(),
                campanha.getDescricao(),
                campanha.getDataHoraInicio(),
                campanha.getDataHoraFinal(),
                campanha.getTipoCampanha(),
                campanha.getMetaValor(),
                campanha.getMetaQtd(),
                urls,
                campanha.getStatus()
        );
    }

    private CampanhaAdmResponseDTO toAdmResponseDTO(Campanha campanha, Adm admCriador, List<String> urls) {
        return new CampanhaAdmResponseDTO(
                campanha.getId().toString(),
                admCriador.getId().toString(),
                campanha.getAdmResponsavel().getId().toString(),
                campanha.getTitulo(),
                campanha.getDescricao(),
                campanha.getDataHoraInicio(),
                campanha.getDataHoraFinal(),
                campanha.getTipoCampanha(),
                campanha.getMetaValor(),
                campanha.getMetaQtd(),
                urls,
                campanha.getDataHoraCad(),
                campanha.getDataHoraAtualizacao(),
                campanha.getStatus()
        );
    }
}