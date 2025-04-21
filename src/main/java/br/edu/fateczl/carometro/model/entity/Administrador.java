package br.edu.fateczl.carometro.model.entity;

import br.edu.fateczl.carometro.model.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "administradores")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Administrador extends Usuario {

    @Column(nullable = false)
    private String departamento;

    public Administrador(Long id, String nome, String email, String senha, Role role,
                         boolean ativo, String departamento) {
        super(id, nome, email, senha, role, ativo);
        this.departamento = departamento;
    }

    public static Administrador createAdministrador(String nome, String email, String senha,
                                                    String departamento, boolean ativo) {
        return new Administrador(null, nome, email, senha, Role.ADMIN, ativo, departamento);
    }
}
