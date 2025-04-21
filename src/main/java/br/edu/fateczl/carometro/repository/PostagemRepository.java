package br.edu.fateczl.carometro.repository;

import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.model.enums.StatusPostagem;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostagemRepository extends JpaRepository<Postagem, Long> {
    List<Postagem> findByStatus(StatusPostagem status);
    List<Postagem> findByExAlunoId(Long exAlunoId);
    List<Postagem> findByExAlunoCurso(String curso);
    List<Postagem> findByExAlunoAnoFormatura(Integer anoFormatura);
    Page<Postagem> findByStatus(StatusPostagem status, Pageable pageable);

    default Page<Postagem> findByStatusAndFiltros(StatusPostagem status, String curso, Integer anoFormatura, Pageable pageable) {
        if (curso != null && !curso.isEmpty() && anoFormatura != null) {
            return findByStatusAndExAlunoCursoAndExAlunoAnoFormatura(status, curso, anoFormatura, pageable);
        } else if (curso != null && !curso.isEmpty()) {
            return findByStatusAndExAlunoCurso(status, curso, pageable);
        } else if (anoFormatura != null) {
            return findByStatusAndExAlunoAnoFormatura(status, anoFormatura, pageable);
        } else {
            return findByStatus(status, pageable);
        }
    }

    Page<Postagem> findByStatusAndExAlunoCurso(StatusPostagem status, String curso, Pageable pageable);
    Page<Postagem> findByStatusAndExAlunoAnoFormatura(StatusPostagem status, Integer anoFormatura, Pageable pageable);
    Page<Postagem> findByStatusAndExAlunoCursoAndExAlunoAnoFormatura(StatusPostagem status, String curso, Integer anoFormatura, Pageable pageable);

    long countByStatus(StatusPostagem status);
}