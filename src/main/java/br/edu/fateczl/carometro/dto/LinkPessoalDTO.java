package br.edu.fateczl.carometro.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;

public record LinkPessoalDTO(
        Long id,

        @NotBlank(message = "O título do link é obrigatório")
        String titulo,

        @NotBlank(message = "A URL é obrigatória")
        @Pattern(regexp = "^(https?|ftp)://[^\\s/$.?#].[^\\s]*$", message = "URL inválida")
        String url
) {}
