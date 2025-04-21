package br.edu.fateczl.carometro.service;

import br.edu.fateczl.carometro.dto.LinkPessoalDTO;
import br.edu.fateczl.carometro.mapper.EntityDtoMapper;
import br.edu.fateczl.carometro.model.entity.LinkPessoal;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.repository.LinkPessoalRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class LinkPessoalService {

    private final LinkPessoalRepository linkPessoalRepository;
    private final PostagemService postagemService;
    private final EntityDtoMapper mapper;

    public List<LinkPessoalDTO> listarLinksPorPostagemDTO(Long postagemId) {
        return linkPessoalRepository.findByPostagemId(postagemId).stream()
                .map(mapper::toLinkPessoalDTO)
                .collect(Collectors.toList());
    }

    public Optional<LinkPessoal> buscarPorId(Long id) {
        return linkPessoalRepository.findById(id);
    }

    public Optional<LinkPessoalDTO> buscarDTOPorId(Long id) {
        return linkPessoalRepository.findById(id)
                .map(mapper::toLinkPessoalDTO);
    }

    @Transactional
    public LinkPessoalDTO salvarLink(LinkPessoalDTO dto, Long postagemId) {
        Postagem postagem = postagemService.buscarPorId(postagemId)
                .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));

        LinkPessoal link = mapper.toLinkPessoal(dto, postagem);
        return mapper.toLinkPessoalDTO(linkPessoalRepository.save(link));
    }

    @Transactional
    public LinkPessoalDTO adicionarLinkAPostagem(Long postagemId, LinkPessoalDTO dto) {
        Postagem postagem = postagemService.buscarPorId(postagemId)
                .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));

        LinkPessoal link = mapper.toLinkPessoal(dto, postagem);
        postagem.adicionarLink(link);
        LinkPessoal savedLink = linkPessoalRepository.save(link);
        return mapper.toLinkPessoalDTO(savedLink);
    }

    @Transactional
    public void excluirLink(Long linkId) {
        linkPessoalRepository.deleteById(linkId);
    }

    @Transactional
    public void excluirTodosLinksDaPostagem(Long postagemId) {
        linkPessoalRepository.deleteAllByPostagemId(postagemId);
    }

    public boolean linkExisteNaPostagem(String titulo, Long postagemId) {
        return linkPessoalRepository.existsByTituloAndPostagemId(titulo, postagemId);
    }

    public long contarLinksDaPostagem(Long postagemId) {
        return linkPessoalRepository.countByPostagemId(postagemId);
    }
}
