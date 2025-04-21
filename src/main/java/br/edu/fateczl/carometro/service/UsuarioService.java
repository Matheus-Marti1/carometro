package br.edu.fateczl.carometro.service;

import br.edu.fateczl.carometro.dto.UsuarioResponseDTO;
import br.edu.fateczl.carometro.mapper.EntityDtoMapper;
import br.edu.fateczl.carometro.model.entity.Usuario;
import br.edu.fateczl.carometro.repository.UsuarioRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UsuarioService implements UserDetailsService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final EntityDtoMapper mapper;

    public UsuarioService(UsuarioRepository usuarioRepository,
                          PasswordEncoder passwordEncoder,
                          EntityDtoMapper mapper) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.mapper = mapper;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return usuarioRepository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Usuário não encontrado com o email: " + username));
    }

    public Usuario salvarUsuario(Usuario usuario) {
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        return usuarioRepository.save(usuario);
    }

    public UsuarioResponseDTO buscarUsuarioDTOPorId(Long id) {
        return usuarioRepository.findById(id)
                .map(mapper::toUsuarioDTO)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));
    }

    public Optional<UsuarioResponseDTO> buscarUsuarioDTOPorEmail(String email) {
        return usuarioRepository.findByEmail(email)
                .map(mapper::toUsuarioDTO);
    }

    public boolean emailJaCadastrado(String email) {
        return usuarioRepository.existsByEmail(email);
    }
}
