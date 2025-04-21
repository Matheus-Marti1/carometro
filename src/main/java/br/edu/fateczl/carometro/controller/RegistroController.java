package br.edu.fateczl.carometro.controller;

import br.edu.fateczl.carometro.dto.ExAlunoRegistroDTO;
import br.edu.fateczl.carometro.dto.LinkPessoalDTO;
import br.edu.fateczl.carometro.dto.NovaPostagemDTO;
import br.edu.fateczl.carometro.model.entity.ExAluno;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.service.ExAlunoService;
import br.edu.fateczl.carometro.service.PostagemService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/registro")
@RequiredArgsConstructor
public class RegistroController {

    private final ExAlunoService exAlunoService;
    private final PostagemService postagemService;

    @GetMapping
    public String exibirFormularioRegistro(Model model) {
        model.addAttribute("exAlunoDTO", new ExAlunoRegistroDTO(null, null, null, null, null));
        model.addAttribute("postagemDTO", new NovaPostagemDTO(null, null, null, new ArrayList<>()));
        model.addAttribute("linkDTO", new LinkPessoalDTO(null, null, null));
        return "registro/formulario";
    }

    @PostMapping
    public String processarRegistro(
            @Valid @ModelAttribute("exAlunoDTO") ExAlunoRegistroDTO exAlunoDTO,
            BindingResult exAlunoResult,
            @Valid @ModelAttribute("postagemDTO") NovaPostagemDTO postagemDTO,
            BindingResult postagemResult,
            @RequestParam("foto") MultipartFile foto,
            @RequestParam(value = "linkTitulos", required = false) String[] linkTitulos,
            @RequestParam(value = "linkUrls", required = false) String[] linkUrls,
            Model model,
            RedirectAttributes redirectAttributes) {

        if (exAlunoResult.hasErrors() || postagemResult.hasErrors()) {
            return "registro/formulario";
        }

        try {
            ExAluno exAluno = exAlunoService.cadastrarExAluno(exAlunoDTO);

            List<LinkPessoalDTO> linkDTOs = new ArrayList<>();
            if (linkTitulos != null && linkUrls != null) {
                for (int i = 0; i < Math.min(linkTitulos.length, linkUrls.length); i++) {
                    if (!linkTitulos[i].isEmpty() && !linkUrls[i].isEmpty()) {
                        linkDTOs.add(new LinkPessoalDTO(null, linkTitulos[i], linkUrls[i]));
                    }
                }
            }

            NovaPostagemDTO postagemComLinks = new NovaPostagemDTO(
                    postagemDTO.historicoProfissional(),
                    postagemDTO.comentarioFaculdade(),
                    postagemDTO.comentarioLivre(),
                    linkDTOs
            );

            Postagem postagem = postagemService.criarPostagemDoDTO(postagemComLinks, exAluno);

            // Processar foto
            if (foto != null && !foto.isEmpty()) {
                postagemService.salvarPostagemComFoto(postagem, foto);
            }

            exAluno.setPostagem(postagem);

            redirectAttributes.addFlashAttribute("mensagemSucesso",
                    "Registro realizado com sucesso! Sua postagem será analisada em breve.");
            return "redirect:/login";

        } catch (IllegalArgumentException e) {
            model.addAttribute("erro", e.getMessage());
            return "registro/formulario";
        } catch (IOException e) {
            model.addAttribute("erro", "Erro ao processar a imagem. Por favor, tente novamente.");
            return "registro/formulario";
        }
    }
}
