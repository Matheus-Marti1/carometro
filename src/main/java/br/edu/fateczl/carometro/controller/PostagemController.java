package br.edu.fateczl.carometro.controller;

import br.edu.fateczl.carometro.dto.FiltroPostagemDTO;
import br.edu.fateczl.carometro.dto.PostagemDTO;
import br.edu.fateczl.carometro.dto.PostagemFiltroDTO;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import br.edu.fateczl.carometro.service.PostagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

@Controller
@RequestMapping("/postagens")
@RequiredArgsConstructor
public class PostagemController {

    private final PostagemService postagemService;

    @GetMapping("/publicas")
    public String listarPostagensPublicas(
            @ModelAttribute("filtro") FiltroPostagemDTO filtro,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "9") int size,
            @RequestParam(defaultValue = "exAluno.nome") String sort,
            @RequestParam(defaultValue = "asc") String direction,
            Model model) {

        PostagemFiltroDTO filtroPostagem = new PostagemFiltroDTO(filtro.curso(), filtro.anoFormatura());

        Sort.Direction dir = direction.equalsIgnoreCase("desc") ? Sort.Direction.DESC : Sort.Direction.ASC;
        Pageable pageable = PageRequest.of(page, size, Sort.by(dir, sort));

        Page<PostagemDTO> postagensPage = postagemService.buscarPostagensAprovadasPaginadasDTO(filtroPostagem, pageable);

        model.addAttribute("page", postagensPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("postagens", postagensPage.getContent());  // Lista de DTOs na página atual

        model.addAttribute("cursos", postagemService.listarCursosDistintos());
        model.addAttribute("anosFormatura", postagemService.listarAnosFormaturaDistintos());
        model.addAttribute("filtro", filtro);

        return "postagens/lista-publica";
    }

    @GetMapping("/publicas/{id}")
    public String visualizarPostagem(@PathVariable Long id, Model model) {
        Optional<PostagemDTO> postagemDTO = postagemService.buscarDTOPorId(id);

        if (postagemDTO.isPresent() && postagemDTO.get().status() == StatusPostagem.APROVADA) {
            model.addAttribute("postagem", postagemDTO.get());
            return "postagens/visualizar";
        } else {
            return "redirect:/postagens/publicas";
        }
    }

    @GetMapping("/foto/{id}")
    public ResponseEntity<byte[]> exibirFoto(@PathVariable Long id) {
        Optional<Postagem> postagemOpt = postagemService.buscarPorId(id);

        if (postagemOpt.isPresent() && postagemOpt.get().getFoto() != null) {
            Postagem postagem = postagemOpt.get();
            return ResponseEntity
                    .ok()
                    .contentType(MediaType.parseMediaType(postagem.getTipoFoto()))
                    .body(postagem.getFoto());
        }

        return ResponseEntity.notFound().build();
    }
}
