package br.edu.fateczl.carometro.model.entity;

import br.edu.fateczl.carometro.model.enums.Role;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "coordenadores")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@SuperBuilder
public class Coordenador extends Usuario {

    @Column(nullable = false)
    private String departamento;

    @Column(name = "area_coordenacao", nullable = false)
    private String areaCoordenacao;

    public Coordenador(Long id, String nome, String email, String senha, Role role,
                       boolean ativo, String departamento, String areaCoordenacao) {
        super(id, nome, email, senha, role, ativo);
        this.departamento = departamento;
        this.areaCoordenacao = areaCoordenacao;
    }

    public static Coordenador createCoordenador(String nome, String email, String senha,
                                                String departamento, String areaCoordenacao,
                                                boolean ativo) {
        return new Coordenador(null, nome, email, senha, Role.COORDENADOR, ativo,
                departamento, areaCoordenacao);
    }
}
