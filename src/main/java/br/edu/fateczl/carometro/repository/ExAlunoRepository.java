package br.edu.fateczl.carometro.repository;

import br.edu.fateczl.carometro.model.entity.ExAluno;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ExAlunoRepository extends JpaRepository<ExAluno, Long> {
    List<ExAluno> findByCurso(String curso);
    List<ExAluno> findByAnoFormatura(Integer anoFormatura);
    List<ExAluno> findByCursoAndAnoFormatura(String curso, Integer anoFormatura);

    long countByAtivo(boolean ativo);

    Page<ExAluno> findByAtivo(boolean ativo, Pageable pageable);
}
