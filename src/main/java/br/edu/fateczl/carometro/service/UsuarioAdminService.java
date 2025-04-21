package br.edu.fateczl.carometro.service;

import br.edu.fateczl.carometro.dto.NovoUsuarioAdminDTO;
import br.edu.fateczl.carometro.model.entity.Administrador;
import br.edu.fateczl.carometro.model.entity.Coordenador;
import br.edu.fateczl.carometro.model.entity.Usuario;
import br.edu.fateczl.carometro.model.enums.Role;
import br.edu.fateczl.carometro.repository.AdministradorRepository;
import br.edu.fateczl.carometro.repository.CoordenadorRepository;
import br.edu.fateczl.carometro.repository.UsuarioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioAdminService {

    private final UsuarioRepository usuarioRepository;
    private final AdministradorRepository administradorRepository;
    private final CoordenadorRepository coordenadorRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public Usuario criarUsuarioAdmin(NovoUsuarioAdminDTO dto) {
        if (usuarioRepository.findByEmail(dto.email()).isPresent()) {
            throw new IllegalArgumentException("Email já cadastrado no sistema");
        }

        if (dto.departamento() == null || dto.departamento().trim().isEmpty()) {
            throw new IllegalArgumentException("Departamento é obrigatório");
        }

        if (dto.role() == Role.COORDENADOR &&
                (dto.areaCoordenacao() == null || dto.areaCoordenacao().trim().isEmpty())) {
            throw new IllegalArgumentException("Área de coordenação é obrigatória para coordenadores");
        }

        String senhaCriptografada = passwordEncoder.encode(dto.senha());

        if (dto.role() == Role.ADMIN) {
            Administrador admin = Administrador.createAdministrador(
                    dto.nome(),
                    dto.email(),
                    senhaCriptografada,
                    dto.departamento(),
                    true
            );

            return administradorRepository.save(admin);
        } else if (dto.role() == Role.COORDENADOR) {
            Coordenador coordenador = Coordenador.createCoordenador(
                    dto.nome(),
                    dto.email(),
                    senhaCriptografada,
                    dto.departamento(),
                    dto.areaCoordenacao(),
                    true
            );

            return coordenadorRepository.save(coordenador);
        } else {
            throw new IllegalArgumentException("Papel inválido para criação de usuário administrativo");
        }
    }

    public List<Usuario> listarTodosUsuariosAdmin() {
        List<Usuario> usuarios = new ArrayList<>();
        usuarios.addAll(administradorRepository.findAll());
        usuarios.addAll(coordenadorRepository.findAll());
        return usuarios;
    }

    @Transactional
    public void ativarUsuario(Long id, Role role) {
        if (role == Role.ADMIN) {
            Administrador admin = administradorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado"));
            admin.setAtivo(true);
            administradorRepository.save(admin);
        } else if (role == Role.COORDENADOR) {
            Coordenador coord = coordenadorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado"));
            coord.setAtivo(true);
            coordenadorRepository.save(coord);
        }
    }

    @Transactional
    public void desativarUsuario(Long id, Role role) {
        if (role == Role.ADMIN) {
            Administrador admin = administradorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Administrador não encontrado"));
            admin.setAtivo(false);
            administradorRepository.save(admin);
        } else if (role == Role.COORDENADOR) {
            Coordenador coord = coordenadorRepository.findById(id)
                    .orElseThrow(() -> new IllegalArgumentException("Coordenador não encontrado"));
            coord.setAtivo(false);
            coordenadorRepository.save(coord);
        }
    }
}
