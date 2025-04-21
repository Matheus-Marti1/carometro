package br.edu.fateczl.carometro.controller;

import br.edu.fateczl.carometro.dto.PostagemDTO;
import br.edu.fateczl.carometro.model.entity.ExAluno;
import br.edu.fateczl.carometro.model.entity.Usuario;
import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import br.edu.fateczl.carometro.service.ExAlunoService;
import br.edu.fateczl.carometro.service.PostagemService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class AdminController {

    private final PostagemService postagemService;
    private final ExAlunoService exAlunoService;

    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String dashboard(Model model) {
        long totalPostagens = postagemService.contarTotalPostagens();
        long postagensPendentes = postagemService.contarPostagensPorStatus(StatusPostagem.PENDENTE);
        long postagensAprovadas = postagemService.contarPostagensPorStatus(StatusPostagem.APROVADA);
        long postagensDesabilitadas = postagemService.contarPostagensPorStatus(StatusPostagem.DESABILITADA);
        long postagensRejeitadas = postagemService.contarPostagensPorStatus(StatusPostagem.REJEITADA);

        long totalExAlunos = exAlunoService.contarTotalExAlunos();
        long exAlunosPendentes = exAlunoService.contarExAlunosPendentes();
        long exAlunosAtivos = exAlunoService.contarExAlunosAtivos();

        model.addAttribute("totalPostagens", totalPostagens);
        model.addAttribute("postagensPendentes", postagensPendentes);
        model.addAttribute("postagensAprovadas", postagensAprovadas);
        model.addAttribute("postagensDesabilitadas", postagensDesabilitadas);
        model.addAttribute("postagensRejeitadas", postagensRejeitadas);

        model.addAttribute("totalExAlunos", totalExAlunos);
        model.addAttribute("exAlunosPendentes", exAlunosPendentes);
        model.addAttribute("exAlunosAtivos", exAlunosAtivos);

        return "admin/dashboard";
    }

    @GetMapping("/postagens/pendentes")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String listarPostagensPendentes() {
        return "redirect:/admin/postagens?status=PENDENTE";
    }

    @GetMapping("/postagens/todas")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String listarTodasPostagens() {
        return "redirect:/admin/postagens";
    }

    @GetMapping("/postagens")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String listarPostagens(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "status", required = false) StatusPostagem status,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "dataCriacao"));
        Page<PostagemDTO> postagens;

        if (status != null) {
            postagens = postagemService.listarPostagensPorStatusDTO(status, pageable);
            model.addAttribute("statusAtual", status);
        } else {
            postagens = postagemService.listarTodasPostagensDTO(pageable);
            model.addAttribute("statusAtual", "TODAS");
        }

        model.addAttribute("page", postagens);
        model.addAttribute("postagens", postagens.getContent());
        model.addAttribute("statusOptions", StatusPostagem.values());

        return "admin/postagens";
    }

    @GetMapping("/postagens/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String visualizarPostagem(@PathVariable Long id, Model model) {
        Optional<PostagemDTO> postagemDTO = postagemService.buscarDTOPorId(id);

        if (postagemDTO.isPresent()) {
            model.addAttribute("postagem", postagemDTO.get());
            return "admin/postagem-detalhes";
        } else {
            return "redirect:/admin/postagens";
        }
    }

    @PostMapping("/postagens/{id}/aprovar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String aprovarPostagem(
            @PathVariable Long id,
            @AuthenticationPrincipal Usuario usuarioLogado,
            RedirectAttributes redirectAttributes) {

        try {
            PostagemDTO postagemAprovada = postagemService.aprovarPostagem(id, usuarioLogado);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Postagem de " + postagemAprovada.exAluno().nome() + " aprovada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao aprovar postagem: " + e.getMessage());
        }

        return "redirect:/admin/postagens?status=PENDENTE";
    }

    @PostMapping("/postagens/{id}/rejeitar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String rejeitarPostagem(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            PostagemDTO postagemRejeitada = postagemService.rejeitarPostagem(id);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Postagem de " + postagemRejeitada.exAluno().nome() + " rejeitada com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao rejeitar postagem: " + e.getMessage());
        }

        return "redirect:/admin/postagens?status=PENDENTE";
    }

    @GetMapping("/ex-alunos")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String listarExAlunos(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(value = "ativo", required = false) Boolean ativo,
            Model model) {

        Pageable pageable = PageRequest.of(page, size, Sort.by("nome"));
        Page<ExAluno> exAlunos;

        if (ativo != null) {
            exAlunos = exAlunoService.listarPorStatus(ativo, pageable);
            model.addAttribute("statusAtual", ativo ? "ATIVO" : "PENDENTE");
        } else {
            exAlunos = exAlunoService.listarTodos(pageable);
            model.addAttribute("statusAtual", "TODOS");
        }

        model.addAttribute("page", exAlunos);
        model.addAttribute("exAlunos", exAlunos.getContent());

        model.addAttribute("totalExAlunos", exAlunoService.contarTotalExAlunos());
        model.addAttribute("exAlunosAtivos", exAlunoService.contarExAlunosAtivos());
        model.addAttribute("exAlunosPendentes", exAlunoService.contarExAlunosPendentes());

        return "admin/ex-alunos";
    }

    @GetMapping("/ex-alunos/{id}")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String visualizarExAluno(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        Optional<ExAluno> exAlunoOpt = exAlunoService.buscarPorId(id);

        if (exAlunoOpt.isPresent()) {
            model.addAttribute("exAluno", exAlunoOpt.get());
            return "admin/ex-aluno-detalhes";
        } else {
            redirectAttributes.addFlashAttribute("erro", "Ex-aluno não encontrado");
            return "redirect:/admin/ex-alunos";
        }
    }

    @PostMapping("/ex-alunos/{id}/ativar")
    @PreAuthorize("hasAnyRole('ADMIN', 'COORDENADOR')")
    public String ativarExAluno(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            ExAluno exAluno = exAlunoService.ativarExAluno(id);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Cadastro de " + exAluno.getNome() + " ativado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao ativar cadastro: " + e.getMessage());
        }

        return "redirect:/admin/ex-alunos/" + id;
    }

    @PostMapping("/ex-alunos/{id}/desativar")
    @PreAuthorize("hasRole('ADMIN')")
    public String desativarExAluno(
            @PathVariable Long id,
            RedirectAttributes redirectAttributes) {

        try {
            ExAluno exAluno = exAlunoService.desativarExAluno(id);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Cadastro de " + exAluno.getNome() + " desativado.");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro",
                    "Erro ao desativar cadastro: " + e.getMessage());
        }

        return "redirect:/admin/ex-alunos/" + id;
    }
}
