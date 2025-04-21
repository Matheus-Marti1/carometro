package br.edu.fateczl.carometro.controller;

import br.edu.fateczl.carometro.dto.NovoUsuarioAdminDTO;
import br.edu.fateczl.carometro.model.enums.Role;
import br.edu.fateczl.carometro.repository.UsuarioRepository;
import br.edu.fateczl.carometro.service.UsuarioAdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;

@Controller
@RequestMapping("/admin/usuarios")
@PreAuthorize("hasRole('ADMIN')")
@RequiredArgsConstructor
public class UsuarioAdminController {

    private final UsuarioAdminService usuarioAdminService;
    private final UsuarioRepository usuarioRepository;

    @GetMapping
    public String listarUsuarios(Model model) {
        model.addAttribute("usuarios", usuarioRepository.findAll());
        return "admin/usuarios";
    }

    @GetMapping("/novo")
    public String formularioNovoUsuario(Model model) {
        model.addAttribute("usuario", new NovoUsuarioAdminDTO(null, null, null, null, null, null));
        model.addAttribute("roles", Arrays.asList(Role.ADMIN, Role.COORDENADOR));
        return "admin/usuario-form";
    }

    @PostMapping("/novo")
    public String salvarNovoUsuario(@Valid NovoUsuarioAdminDTO usuario, BindingResult result,
                                    RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            return "admin/usuario-form";
        }

        try {
            usuarioAdminService.criarUsuarioAdmin(usuario);
            redirectAttributes.addFlashAttribute("mensagem",
                    "Usuário " + usuario.nome() + " criado com sucesso!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("erro", "Erro ao criar usuário: " + e.getMessage());
        }

        return "redirect:/admin/dashboard";
    }
}
