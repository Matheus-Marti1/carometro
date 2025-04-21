package br.edu.fateczl.carometro.dto;

import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import jakarta.validation.constraints.Size;

import java.time.LocalDateTime;
import java.util.List;

public record PostagemDTO(
        Long id,

        @Size(max = 4000, message = "O histórico profissional não pode exceder 4000 caracteres")
        String historicoProfissional,

        @Size(max = 2000, message = "O comentário sobre a faculdade não pode exceder 2000 caracteres")
        String comentarioFaculdade,

        @Size(max = 2000, message = "O comentário livre não pode exceder 2000 caracteres")
        String comentarioLivre,

        StatusPostagem status,

        LocalDateTime dataCriacao,

        LocalDateTime dataAprovacao,

        List<LinkPessoalDTO> linksPessoais,

        ExAlunoResumoDTO exAluno
) {}
