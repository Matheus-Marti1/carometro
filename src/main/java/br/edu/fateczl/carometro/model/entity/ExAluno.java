package br.edu.fateczl.carometro.model.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

@Entity
@Table(name = "ex_alunos")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder
public class ExAluno extends Usuario {

    @Column(nullable = false)
    private String curso;

    @Column(name = "ano_formatura", nullable = false)
    private Integer anoFormatura;

    @OneToOne(mappedBy = "exAluno", cascade = CascadeType.ALL, orphanRemoval = true)
    private Postagem postagem;

    @Override
    public String toString() {
        return "ExAluno{" +
                "curso='" + curso + '\'' +
                ", anoFormatura=" + anoFormatura +
                "} " + super.toString();
    }
}