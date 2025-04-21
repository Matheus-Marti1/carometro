package br.edu.fateczl.carometro.service;

import br.edu.fateczl.carometro.dto.ExAlunoRegistroDTO;
import br.edu.fateczl.carometro.dto.ExAlunoResumoDTO;
import br.edu.fateczl.carometro.dto.NovaPostagemDTO;
import br.edu.fateczl.carometro.mapper.EntityDtoMapper;
import br.edu.fateczl.carometro.model.entity.ExAluno;
import br.edu.fateczl.carometro.model.entity.Postagem;
import br.edu.fateczl.carometro.repository.ExAlunoRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ExAlunoService {

    private final ExAlunoRepository exAlunoRepository;
    private final PasswordEncoder passwordEncoder;
    private final UsuarioService usuarioService;
    private final EntityDtoMapper mapper;

    @Transactional
    public ExAluno cadastrarExAluno(ExAlunoRegistroDTO dto) {
        if (usuarioService.emailJaCadastrado(dto.email())) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }

        ExAluno exAluno = mapper.toExAluno(dto);
        exAluno.setSenha(passwordEncoder.encode(exAluno.getSenha()));

        return exAlunoRepository.save(exAluno);
    }

    @Transactional
    public ExAluno cadastrarExAlunoComPostagem(ExAlunoRegistroDTO exAlunoDTO, NovaPostagemDTO postagemDTO) {
        ExAluno exAluno = cadastrarExAluno(exAlunoDTO);

        Postagem postagem = mapper.toPostagem(postagemDTO, exAluno);
        exAluno.setPostagem(postagem);

        return exAlunoRepository.save(exAluno);
    }

    public Optional<ExAluno> buscarPorId(Long id) {
        return exAlunoRepository.findById(id);
    }

    public Optional<ExAlunoResumoDTO> buscarResumoPorId(Long id) {
        return exAlunoRepository.findById(id)
                .map(mapper::toExAlunoResumoDTO);
    }

    public List<ExAlunoResumoDTO> buscarPorCursoDTO(String curso) {
        return exAlunoRepository.findByCurso(curso).stream()
                .map(mapper::toExAlunoResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ExAlunoResumoDTO> buscarPorAnoFormaturaDTO(Integer anoFormatura) {
        return exAlunoRepository.findByAnoFormatura(anoFormatura).stream()
                .map(mapper::toExAlunoResumoDTO)
                .collect(Collectors.toList());
    }

    public List<ExAluno> listarTodos() {
        return exAlunoRepository.findAll();
    }

    public List<ExAlunoResumoDTO> listarTodosResumo() {
        return exAlunoRepository.findAll().stream()
                .map(mapper::toExAlunoResumoDTO)
                .collect(Collectors.toList());
    }

    public List<String> listarCursosDistintos() {
        return exAlunoRepository.findAll().stream()
                .map(ExAluno::getCurso)
                .distinct()
                .collect(Collectors.toList());
    }

    public List<Integer> listarAnosFormaturaDistintos() {
        return exAlunoRepository.findAll().stream()
                .map(ExAluno::getAnoFormatura)
                .distinct()
                .collect(Collectors.toList());
    }


    public long contarTotalExAlunos() {
        return exAlunoRepository.count();
    }


    public long contarExAlunosPendentes() {
        return exAlunoRepository.countByAtivo(false);
    }


    public long contarExAlunosAtivos() {
        return exAlunoRepository.countByAtivo(true);
    }

    @Transactional
    public ExAluno ativarExAluno(Long id) {
        ExAluno exAluno = exAlunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ex-aluno não encontrado"));
        exAluno.setAtivo(true);
        return exAlunoRepository.save(exAluno);
    }

    @Transactional
    public ExAluno desativarExAluno(Long id) {
        ExAluno exAluno = exAlunoRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Ex-aluno não encontrado"));
        exAluno.setAtivo(false);
        return exAlunoRepository.save(exAluno);
    }

    public Page<ExAluno> listarTodos(Pageable pageable) {
        return exAlunoRepository.findAll(pageable);
    }

    public Page<ExAluno> listarPorStatus(boolean ativo, Pageable pageable) {
        return exAlunoRepository.findByAtivo(ativo, pageable);
    }

    public Page<ExAlunoResumoDTO> listarTodosResumo(Pageable pageable) {
        return exAlunoRepository.findAll(pageable)
                .map(mapper::toExAlunoResumoDTO);
    }

    public Page<ExAlunoResumoDTO> listarPorStatusResumo(boolean ativo, Pageable pageable) {
        return exAlunoRepository.findByAtivo(ativo, pageable)
                .map(mapper::toExAlunoResumoDTO);
    }
}
