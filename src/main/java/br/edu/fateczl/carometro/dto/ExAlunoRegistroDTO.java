package br.edu.fateczl.carometro.dto;

import jakarta.validation.constraints.*;

public record ExAlunoRegistroDTO(
        @NotBlank(message = "O nome é obrigatório")
        String nome,

        @NotBlank(message = "O e-mail é obrigatório")
        @Email(message = "Forneça um e-mail válido")
        String email,

        @NotBlank(message = "A senha é obrigatória")
        @Size(min = 6, message = "A senha deve ter pelo menos 6 caracteres")
        String senha,

        @NotBlank(message = "O curso é obrigatório")
        String curso,

        @NotNull(message = "O ano de formatura é obrigatório")
        Integer anoFormatura
) {}
