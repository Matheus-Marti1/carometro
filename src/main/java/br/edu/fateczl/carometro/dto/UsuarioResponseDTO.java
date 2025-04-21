package br.edu.fateczl.carometro.dto;

import br.edu.fateczl.carometro.model.enums.Role;

public record UsuarioResponseDTO(
        Long id,
        String nome,
        String email,
        Role role
) {}
