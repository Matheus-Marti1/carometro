package br.edu.fateczl.carometro.repository;

import br.edu.fateczl.carometro.model.entity.LinkPessoal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface LinkPessoalRepository extends JpaRepository<LinkPessoal, Long> {

    List<LinkPessoal> findByPostagemId(Long postagemId);

    void deleteAllByPostagemId(Long postagemId);

    boolean existsByTituloAndPostagemId(String titulo, Long postagemId);

    long countByPostagemId(Long postagemId);
}
