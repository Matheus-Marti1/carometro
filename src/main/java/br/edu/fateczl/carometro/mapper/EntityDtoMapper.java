package br.edu.fateczl.carometro.mapper;

import br.edu.fateczl.carometro.dto.*;
import br.edu.fateczl.carometro.model.entity.ExAluno;
import br.edu.fateczl.carometro.model.entity.LinkPessoal;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.model.entity.Usuario;
import br.edu.fateczl.carometro.model.enums.Role;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class EntityDtoMapper {

    public UsuarioResponseDTO toUsuarioDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                usuario.getRole()
        );
    }

    public ExAluno toExAluno(ExAlunoRegistroDTO dto) {
        return ExAluno.builder()
                .nome(dto.nome())
                .email(dto.email())
                .senha(dto.senha())
                .curso(dto.curso())
                .anoFormatura(dto.anoFormatura())
                .role(Role.EX_ALUNO)
                .build();
    }

    public ExAlunoResumoDTO toExAlunoResumoDTO(ExAluno exAluno) {
        return new ExAlunoResumoDTO(
                exAluno.getId(),
                exAluno.getNome(),
                exAluno.getCurso(),
                exAluno.getAnoFormatura()
        );
    }

    public LinkPessoal toLinkPessoal(LinkPessoalDTO dto, Postagem postagem) {
        return LinkPessoal.builder()
                .id(dto.id())
                .titulo(dto.titulo())
                .url(dto.url())
                .postagem(postagem)
                .build();
    }

    public List<LinkPessoal> toLinkPessoalList(List<LinkPessoalDTO> dtos, Postagem postagem) {
        return dtos.stream()
                .map(dto -> toLinkPessoal(dto, postagem))
                .collect(Collectors.toList());
    }

    public LinkPessoalDTO toLinkPessoalDTO(LinkPessoal link) {
        return new LinkPessoalDTO(
                link.getId(),
                link.getTitulo(),
                link.getUrl()
        );
    }

    public List<LinkPessoalDTO> toLinkPessoalDTOList(List<LinkPessoal> links) {
        return links.stream()
                .map(this::toLinkPessoalDTO)
                .collect(Collectors.toList());
    }

    public Postagem toPostagem(NovaPostagemDTO dto, ExAluno exAluno) {
        Postagem postagem = Postagem.builder()
                .exAluno(exAluno)
                .historicoProfissional(dto.historicoProfissional())
                .comentarioFaculdade(dto.comentarioFaculdade())
                .comentarioLivre(dto.comentarioLivre())
                .linksPessoais(List.of())
                .build();

        if (dto.linksPessoais() != null) {
            List<LinkPessoal> links = dto.linksPessoais().stream()
                    .map(linkDTO -> toLinkPessoal(linkDTO, postagem))
                    .collect(Collectors.toList());
            postagem.setLinksPessoais(links);
        }

        return postagem;
    }

    public NovaPostagemDTO toNovaPostagemDTO(Postagem postagem) {
        return new NovaPostagemDTO(
                postagem.getHistoricoProfissional(),
                postagem.getComentarioFaculdade(),
                postagem.getComentarioLivre(),
                toLinkPessoalDTOList(postagem.getLinksPessoais())
        );
    }

    public PostagemDTO toPostagemDTO(Postagem postagem) {
        return new PostagemDTO(
                postagem.getId(),
                postagem.getHistoricoProfissional(),
                postagem.getComentarioFaculdade(),
                postagem.getComentarioLivre(),
                postagem.getStatus(),
                postagem.getDataCriacao(),
                postagem.getDataAprovacao(),
                toLinkPessoalDTOList(postagem.getLinksPessoais()),
                toExAlunoResumoDTO(postagem.getExAluno())
        );
    }

    public List<PostagemDTO> toPostagemDTOList(List<Postagem> postagens) {
        return postagens.stream()
                .map(this::toPostagemDTO)
                .collect(Collectors.toList());
    }

    public PostagemResumoDTO toPostagemResumoDTO(Postagem postagem) {
        return new PostagemResumoDTO(
                postagem.getId(),
                postagem.getExAluno().getNome(),
                postagem.getExAluno().getCurso(),
                postagem.getExAluno().getAnoFormatura(),
                postagem.getStatus(),
                postagem.getDataCriacao(),
                postagem.getDataAprovacao()
        );
    }

    public List<PostagemResumoDTO> toPostagemResumoDTOList(List<Postagem> postagens) {
        return postagens.stream()
                .map(this::toPostagemResumoDTO)
                .collect(Collectors.toList());
    }

    public Postagem fromRegistroCompletoDTO(RegistroCompletoDTO dto) {
        ExAluno exAluno = toExAluno(dto.exAluno());
        return toPostagem(dto.postagem(), exAluno);
    }
}
