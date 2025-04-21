package br.edu.fateczl.carometro.service;

import br.edu.fateczl.carometro.dto.NovaPostagemDTO;
import br.edu.fateczl.carometro.dto.PostagemDTO;
import br.edu.fateczl.carometro.dto.PostagemFiltroDTO;
import br.edu.fateczl.carometro.dto.PostagemResumoDTO;
import br.edu.fateczl.carometro.mapper.EntityDtoMapper;
import br.edu.fateczl.carometro.model.entity.ExAluno;
import br.edu.fateczl.carometro.model.entity.LinkPessoal;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import br.edu.fateczl.carometro.repository.PostagemRepository;
import br.edu.fateczl.carometro.model.entity.Usuario;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PostagemService {

    private final PostagemRepository postagemRepository;
    private final EntityDtoMapper mapper;

    public List<Postagem> listarTodasPostagens() {
        return postagemRepository.findAll();
    }

    public List<PostagemDTO> listarTodasPostagensDTO() {
        return postagemRepository.findAll().stream()
                .map(mapper::toPostagemDTO)
                .collect(Collectors.toList());
    }

    public List<PostagemResumoDTO> listarTodasPostagensResumo() {
        return postagemRepository.findAll().stream()
                .map(mapper::toPostagemResumoDTO)
                .collect(Collectors.toList());
    }

    public List<Postagem> listarPostagensPendentes() {
        return postagemRepository.findByStatus(StatusPostagem.PENDENTE);
    }

    public List<PostagemDTO> listarPostagensPendentesDTO() {
        return postagemRepository.findByStatus(StatusPostagem.PENDENTE).stream()
                .map(mapper::toPostagemDTO)
                .collect(Collectors.toList());
    }

    public List<PostagemResumoDTO> listarPostagensResumo(StatusPostagem status) {
        return postagemRepository.findByStatus(status).stream()
                .map(mapper::toPostagemResumoDTO)
                .collect(Collectors.toList());
    }

    public List<Postagem> listarPostagensAprovadas() {
        return postagemRepository.findByStatus(StatusPostagem.APROVADA);
    }

    public List<PostagemDTO> listarPostagensAprovadasDTO() {
        return postagemRepository.findByStatus(StatusPostagem.APROVADA).stream()
                .map(mapper::toPostagemDTO)
                .collect(Collectors.toList());
    }

    public List<PostagemDTO> listarPostagensPorCursoDTO(String curso) {
        return postagemRepository.findByExAlunoCurso(curso).stream()
                .map(mapper::toPostagemDTO)
                .collect(Collectors.toList());
    }

    public List<PostagemDTO> listarPostagensPorAnoFormaturaDTO(Integer anoFormatura) {
        return postagemRepository.findByExAlunoAnoFormatura(anoFormatura).stream()
                .map(mapper::toPostagemDTO)
                .collect(Collectors.toList());
    }

    public Optional<Postagem> buscarPorId(Long id) {
        return postagemRepository.findById(id);
    }

    public Optional<PostagemDTO> buscarDTOPorId(Long id) {
        return postagemRepository.findById(id)
                .map(mapper::toPostagemDTO);
    }

    @Transactional
    public Postagem salvarPostagem(Postagem postagem) {
        return postagemRepository.save(postagem);
    }

    @Transactional
    public PostagemDTO salvarPostagemDTO(Postagem postagem) {
        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    @Transactional
    public Postagem criarPostagemDoDTO(NovaPostagemDTO dto, ExAluno exAluno) {
        Postagem postagem = mapper.toPostagem(dto, exAluno);
        return postagemRepository.save(postagem);
    }

    @Transactional
    public PostagemDTO criarPostagemRetornarDTO(NovaPostagemDTO dto, ExAluno exAluno) {
        Postagem postagem = mapper.toPostagem(dto, exAluno);
        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    @Transactional
    public Postagem salvarPostagemComFoto(Postagem postagem, MultipartFile foto) throws IOException {
        if (foto != null && !foto.isEmpty()) {
            postagem.setFoto(foto.getBytes());
            postagem.setTipoFoto(foto.getContentType());
        }

        return postagemRepository.save(postagem);
    }

    @Transactional
    public PostagemDTO salvarPostagemComFotoDTO(Postagem postagem, MultipartFile foto) throws IOException {
        if (foto != null && !foto.isEmpty()) {
            postagem.setFoto(foto.getBytes());
            postagem.setTipoFoto(foto.getContentType());
        }

        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    @Transactional
    public void adicionarLinkPessoal(Postagem postagem, LinkPessoal link) {
        postagem.adicionarLink(link);
        postagemRepository.save(postagem);
    }

    @Transactional
    public PostagemDTO atualizarDoDTO(Long postagemId, NovaPostagemDTO dto) {
        Postagem postagem = postagemRepository.findById(postagemId)
                .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));

        postagem.setHistoricoProfissional(dto.historicoProfissional());
        postagem.setComentarioFaculdade(dto.comentarioFaculdade());
        postagem.setComentarioLivre(dto.comentarioLivre());

        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    @Transactional
    public PostagemDTO aprovarPostagem(Long postagemId, Usuario aprovador) {
        Postagem postagem = postagemRepository.findById(postagemId)
                .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));

        postagem.setStatus(StatusPostagem.APROVADA);
        postagem.setAprovadoPor(aprovador);
        postagem.setDataAprovacao(LocalDateTime.now());

        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    @Transactional
    public PostagemDTO rejeitarPostagem(Long postagemId) {
        Postagem postagem = postagemRepository.findById(postagemId)
                .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));

        postagem.setStatus(StatusPostagem.REJEITADA);

        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    @Transactional
    public PostagemDTO alterarStatusPostagem(Long postagemId, StatusPostagem novoStatus) {
        Postagem postagem = postagemRepository.findById(postagemId)
                .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));

        postagem.setStatus(novoStatus);

        return mapper.toPostagemDTO(postagemRepository.save(postagem));
    }

    public Page<Postagem> listarTodasPostagens(Pageable pageable) {
        return postagemRepository.findAll(pageable);
    }

    public Page<PostagemDTO> listarTodasPostagensDTO(Pageable pageable) {
        return postagemRepository.findAll(pageable)
                .map(mapper::toPostagemDTO);
    }

    public Page<PostagemResumoDTO> listarTodasPostagensResumo(Pageable pageable) {
        return postagemRepository.findAll(pageable)
                .map(mapper::toPostagemResumoDTO);
    }

    public Page<Postagem> listarPostagensPorStatus(StatusPostagem status, Pageable pageable) {
        return postagemRepository.findByStatus(status, pageable);
    }

    public Page<PostagemDTO> listarPostagensPorStatusDTO(StatusPostagem status, Pageable pageable) {
        return postagemRepository.findByStatus(status, pageable)
                .map(mapper::toPostagemDTO);
    }

    public Page<PostagemResumoDTO> listarPostagensPorStatusResumo(StatusPostagem status, Pageable pageable) {
        return postagemRepository.findByStatus(status, pageable)
                .map(mapper::toPostagemResumoDTO);
    }

    public Page<Postagem> buscarPostagensAprovadasPaginadas(PostagemFiltroDTO filtro, Pageable pageable) {
        StatusPostagem status = StatusPostagem.APROVADA;

        if (filtro == null) {
            return postagemRepository.findByStatus(status, pageable);
        }

        if (filtro.temCurso() && filtro.temAnoFormatura()) {
            return postagemRepository.findByStatusAndExAlunoCursoAndExAlunoAnoFormatura(
                    status, filtro.curso(), filtro.anoFormatura(), pageable);
        } else if (filtro.temCurso()) {
            return postagemRepository.findByStatusAndExAlunoCurso(status, filtro.curso(), pageable);
        } else if (filtro.temAnoFormatura()) {
            return postagemRepository.findByStatusAndExAlunoAnoFormatura(status, filtro.anoFormatura(), pageable);
        } else {
            return postagemRepository.findByStatus(status, pageable);
        }
    }

    public Page<PostagemDTO> buscarPostagensAprovadasPaginadasDTO(PostagemFiltroDTO filtro, Pageable pageable) {
        return buscarPostagensAprovadasPaginadas(filtro, pageable)
                .map(mapper::toPostagemDTO);
    }

    public List<String> listarCursosDistintos() {
        return postagemRepository.findByStatus(StatusPostagem.APROVADA).stream()
                .map(p -> p.getExAluno().getCurso())
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public List<Integer> listarAnosFormaturaDistintos() {
        return postagemRepository.findByStatus(StatusPostagem.APROVADA).stream()
                .map(p -> p.getExAluno().getAnoFormatura())
                .filter(Objects::nonNull)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }

    public long contarTotalPostagens() {
        return postagemRepository.count();
    }

    public long contarPostagensPorStatus(StatusPostagem status) {
        return postagemRepository.countByStatus(status);
    }
}