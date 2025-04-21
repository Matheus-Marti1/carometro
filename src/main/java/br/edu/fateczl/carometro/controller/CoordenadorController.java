package br.edu.fateczl.carometro.controller;

import br.edu.fateczl.carometro.dto.PostagemDTO;
import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import br.edu.fateczl.carometro.service.PostagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/coordenador")
@PreAuthorize("hasRole('COORDENADOR')")
@RequiredArgsConstructor
public class CoordenadorController {

    private final PostagemService postagemService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "redirect:/admin/dashboard";
    }

    @GetMapping("/postagens")
    public String listarPostagens(
            @RequestParam(value = "status", required = false) StatusPostagem status,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        String queryParams = "";
        if (status != null) {
            queryParams = "?status=" + status + "&page=" + page + "&size=" + size;
        } else {
            queryParams = "?page=" + page + "&size=" + size;
        }

        return "redirect:/admin/postagens" + queryParams;
    }

    @GetMapping("/postagens/{id}")
    public String visualizarPostagem(@PathVariable Long id) {
        return "redirect:/admin/postagens/" + id;
    }

    @PostMapping("/postagens/{id}/habilitar")
    public String habilitarPostagem(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            PostagemDTO postagemDTO = postagemService.alterarStatusPostagem(id, StatusPostagem.APROVADA);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Postagem de " + postagemDTO.exAluno().nome() + " habilitada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao habilitar postagem: " + e.getMessage());
        }

        return "redirect:/admin/postagens/" + id;
    }

    @PostMapping("/postagens/{id}/desabilitar")
    public String desabilitarPostagem(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            PostagemDTO postagemDTO = postagemService.alterarStatusPostagem(id, StatusPostagem.DESABILITADA);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Postagem de " + postagemDTO.exAluno().nome() + " desabilitada.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao desabilitar postagem: " + e.getMessage());
        }

        return "redirect:/admin/postagens/" + id;
    }

    @GetMapping("/ex-alunos")
    public String listarExAlunos(
            @RequestParam(value = "ativo", required = false) Boolean ativo,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size) {
        String queryParams = "";
        if (ativo != null) {
            queryParams = "?ativo=" + ativo + "&page=" + page + "&size=" + size;
        } else {
            queryParams = "?page=" + page + "&size=" + size;
        }

        return "redirect:/admin/ex-alunos" + queryParams;
    }

    @GetMapping("/ex-alunos/{id}")
    public String visualizarExAluno(@PathVariable Long id) {
        return "redirect:/admin/ex-alunos/" + id;
    }
}
