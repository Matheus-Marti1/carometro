package br.edu.fateczl.carometro.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;

public record RegistroCompletoDTO(
        @Valid
        @NotNull(message = "Os dados do ex-aluno são obrigatórios")
        ExAlunoRegistroDTO exAluno,

        @Valid
        @NotNull(message = "Os dados da postagem são obrigatórios")
        NovaPostagemDTO postagem
) {}
