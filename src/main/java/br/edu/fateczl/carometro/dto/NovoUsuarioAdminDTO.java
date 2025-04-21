package br.edu.fateczl.carometro.dto;

import br.edu.fateczl.carometro.model.enums.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record NovoUsuarioAdminDTO(
        @NotBlank String nome,
        @NotBlank @Email String email,
        @NotBlank String senha,
        @NotNull Role role,
        @NotBlank String departamento,
        String areaCoordenacao
) {}
