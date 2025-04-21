package br.edu.fateczl.carometro.controller;

import br.edu.fateczl.carometro.dto.ExAlunoResumoDTO;
import br.edu.fateczl.carometro.dto.LinkPessoalDTO;
import br.edu.fateczl.carometro.dto.NovaPostagemDTO;
import br.edu.fateczl.carometro.dto.PostagemDTO;
import br.edu.fateczl.carometro.mapper.EntityDtoMapper;
import br.edu.fateczl.carometro.model.entity.ExAluno;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.service.ExAlunoService;
import br.edu.fateczl.carometro.service.LinkPessoalService;
import br.edu.fateczl.carometro.service.PostagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.Optional;

@Controller
@RequestMapping("/ex-aluno")
@RequiredArgsConstructor
public class ExAlunoController {

    private final ExAlunoService exAlunoService;
    private final PostagemService postagemService;
    private final LinkPessoalService linkPessoalService;
    private final EntityDtoMapper mapper;

    @GetMapping("/dashboard")
    public String dashboard(@AuthenticationPrincipal ExAluno exAluno, Model model) {
        Optional<ExAlunoResumoDTO> exAlunoDTO = exAlunoService.buscarResumoPorId(exAluno.getId());
        model.addAttribute("exAlunoDTO", exAlunoDTO.orElseThrow());

        if (exAluno.getPostagem() != null) {
            Optional<PostagemDTO> postagemDTO = postagemService.buscarDTOPorId(exAluno.getPostagem().getId());
            model.addAttribute("postagem", postagemDTO.orElseThrow());
        }

        return "ex-aluno/dashboard";
    }

    @GetMapping("/postagem/editar")
    public String editarPostagem(@AuthenticationPrincipal ExAluno exAluno, Model model) {
        if (exAluno.getPostagem() == null) {
            return "redirect:/ex-aluno/dashboard";
        }

        Long postagemId = exAluno.getPostagem().getId();
        Optional<Postagem> postagemOpt = postagemService.buscarPorId(postagemId);

        if (postagemOpt.isEmpty()) {
            return "redirect:/ex-aluno/dashboard";
        }

        NovaPostagemDTO postagemDTO = mapper.toNovaPostagemDTO(postagemOpt.get());

        model.addAttribute("postagemDTO", postagemDTO);
        model.addAttribute("novoLink", new LinkPessoalDTO(null, "", ""));
        model.addAttribute("postagemId", postagemId);

        return "ex-aluno/editar-postagem";
    }

    @PostMapping("/postagem/editar")
    public String atualizarPostagem(
            @AuthenticationPrincipal ExAluno exAluno,
            @Valid @ModelAttribute("postagemDTO") NovaPostagemDTO postagemDTO,
            BindingResult bindingResult,
            @RequestParam(value = "novaFoto", required = false) MultipartFile novaFoto,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            return "ex-aluno/editar-postagem";
        }

        try {
            Long postagemId = exAluno.getPostagem().getId();

            postagemService.atualizarDoDTO(postagemId, postagemDTO);


            if (novaFoto != null && !novaFoto.isEmpty()) {
                Postagem postagem = postagemService.buscarPorId(postagemId)
                        .orElseThrow(() -> new IllegalArgumentException("Postagem não encontrada"));
                postagemService.salvarPostagemComFoto(postagem, novaFoto);
            }

            redirectAttributes.addFlashAttribute("mensagem", "Postagem atualizada com sucesso!");
            return "redirect:/ex-aluno/dashboard";

        } catch (IOException e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao processar a imagem");
            return "redirect:/ex-aluno/postagem/editar";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao atualizar postagem: " + e.getMessage());
            return "redirect:/ex-aluno/postagem/editar";
        }
    }

    @PostMapping("/postagem/link/adicionar")
    public String adicionarLink(
            @AuthenticationPrincipal ExAluno exAluno,
            @Valid @ModelAttribute("novoLink") LinkPessoalDTO novoLink,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes) {

        if (bindingResult.hasErrors()) {
            redirectAttributes.addFlashAttribute("erroLink", "Verifique os dados do link");
            return "redirect:/ex-aluno/postagem/editar";
        }

        try {
            Long postagemId = exAluno.getPostagem().getId();
            linkPessoalService.adicionarLinkAPostagem(postagemId, novoLink);

            redirectAttributes.addFlashAttribute("mensagem", "Link adicionado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao adicionar link: " + e.getMessage());
        }

        return "redirect:/ex-aluno/postagem/editar";
    }

    @GetMapping("/postagem/link/{id}/excluir")
    public String excluirLink(
            @AuthenticationPrincipal ExAluno exAluno,
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            Optional<LinkPessoalDTO> linkDTO = linkPessoalService.buscarDTOPorId(id);

            if (linkDTO.isPresent()) {
                linkPessoalService.excluirLink(id);
                redirectAttributes.addFlashAttribute("mensagem", "Link excluído com sucesso!");
            } else {
                redirectAttributes.addFlashAttribute("erro", "Link não encontrado");
            }
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao excluir link: " + e.getMessage());
        }

        return "redirect:/ex-aluno/postagem/editar";
    }

    @GetMapping("/perfil")
    public String verPerfil(@AuthenticationPrincipal ExAluno exAluno, Model model) {
        Optional<ExAlunoResumoDTO> exAlunoDTO = exAlunoService.buscarResumoPorId(exAluno.getId());
        model.addAttribute("exAlunoDTO", exAlunoDTO.orElseThrow());
        model.addAttribute("exAluno", exAluno);
        return "ex-aluno/perfil";
    }
}
