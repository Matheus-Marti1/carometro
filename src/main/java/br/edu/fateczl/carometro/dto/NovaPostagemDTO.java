package br.edu.fateczl.carometro.dto;

import jakarta.validation.constraints.Size;

import java.util.List;

public record NovaPostagemDTO(
        @Size(max = 4000, message = "O histórico profissional não pode exceder 4000 caracteres")
        String historicoProfissional,

        @Size(max = 2000, message = "O comentário sobre a faculdade não pode exceder 2000 caracteres")
        String comentarioFaculdade,

        @Size(max = 2000, message = "O comentário livre não pode exceder 2000 caracteres")
        String comentarioLivre,

        List<LinkPessoalDTO> linksPessoais
) {}
