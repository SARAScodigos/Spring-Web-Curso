package com.PC.Store.SistemaWeb.service.impl;

import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRegistroRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioResponseDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.exception.ResourceNotFoundException;
import com.PC.Store.SistemaWeb.model.Usuario;
import com.PC.Store.SistemaWeb.repository.UsuarioRepository;
import com.PC.Store.SistemaWeb.service.UsuarioService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public List<UsuarioResponseDTO> listarTodos() {
        return usuarioRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public UsuarioResponseDTO buscarPorId(Integer id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crear(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + dto.correo());
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setCorreo(dto.correo());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO registrarPublico(UsuarioRegistroRequestDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + dto.correo());
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setCorreo(dto.correo());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol("CLIENTE");
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = findOrThrow(id);
        if (!usuario.getCorreo().equals(dto.correo()) && usuarioRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + dto.correo());
        }
        usuario.setNombre(dto.nombre());
        usuario.setCorreo(dto.correo());
        usuario.setPassword(passwordEncoder.encode(dto.password()));
        usuario.setRol(dto.rol());
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        findOrThrow(id);
        usuarioRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarPorIds(List<Integer> ids) {
        List<Usuario> usuarios = usuarioRepository.findAllById(ids);
        if (usuarios.size() != ids.size()) {
            throw new BusinessException("Uno o más usuarios no fueron encontrados");
        }
        usuarioRepository.deleteAllById(ids);
    }

    @Override
    public Optional<UsuarioResponseDTO> autenticar(String correo, String password) {
        return usuarioRepository.findByCorreo(correo)
                .filter(u -> passwordEncoder.matches(password, u.getPassword()))
                .map(this::toDTO);
    }

    private Usuario findOrThrow(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private UsuarioResponseDTO toDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getIdUsuario(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}