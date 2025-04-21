package br.edu.fateczl.carometro.dto;

import br.edu.fateczl.carometro.model.enums.StatusPostagem;

import java.time.LocalDateTime;

public record PostagemResumoDTO(
        Long id,
        String nomeExAluno,
        String cursoExAluno,
        Integer anoFormaturaExAluno,
        StatusPostagem status,
        LocalDateTime dataCriacao,
        LocalDateTime dataAprovacao
) {}
