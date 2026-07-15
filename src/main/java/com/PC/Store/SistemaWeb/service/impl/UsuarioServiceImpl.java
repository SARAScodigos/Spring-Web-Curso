package com.PC.Store.SistemaWeb.service.impl;

import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioResponseDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.exception.ResourceNotFoundException;
import com.PC.Store.SistemaWeb.model.Usuario;
import com.PC.Store.SistemaWeb.repository.UsuarioRepository;
import com.PC.Store.SistemaWeb.service.UsuarioService;
import com.PC.Store.SistemaWeb.util.PasswordHashUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UsuarioServiceImpl implements UsuarioService {

    private final UsuarioRepository usuarioRepository;

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
        mapToEntity(dto, usuario);
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    @Transactional
    public UsuarioResponseDTO crearAdmin(UsuarioRequestDTO dto) {
        if (usuarioRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + dto.correo());
        }
        Usuario usuario = new Usuario();
        usuario.setNombre(dto.nombre());
        usuario.setCorreo(dto.correo());
        usuario.setPassword(PasswordHashUtil.encode(dto.password()));
        usuario.setRol("ADMIN");
        return toDTO(usuarioRepository.save(usuario));
    }

    @Override
    public UsuarioResponseDTO autenticar(String correo, String password) {
        Usuario usuario = usuarioRepository.findByCorreo(correo)
                .orElseThrow(() -> new BusinessException("Credenciales inválidas"));
        if (!PasswordHashUtil.matches(password, usuario.getPassword())) {
            throw new BusinessException("Credenciales inválidas");
        }
        return toDTO(usuario);
    }

    @Override
    @Transactional
    public UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto) {
        Usuario usuario = findOrThrow(id);
        if (!usuario.getCorreo().equals(dto.correo()) && usuarioRepository.existsByCorreo(dto.correo())) {
            throw new BusinessException("Ya existe un usuario con el correo: " + dto.correo());
        }
        mapToEntity(dto, usuario);
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
    public long contarUsuarios() {
        return usuarioRepository.count();
    }

    private Usuario findOrThrow(Integer id) {
        return usuarioRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", id));
    }

    private void mapToEntity(UsuarioRequestDTO dto, Usuario usuario) {
        usuario.setNombre(dto.nombre());
        usuario.setCorreo(dto.correo());
        usuario.setPassword(PasswordHashUtil.encode(dto.password()));
        usuario.setRol(dto.rol() == null || dto.rol().isBlank() ? "USER" : dto.rol());
    }

    private UsuarioResponseDTO toDTO(Usuario u) {
        return new UsuarioResponseDTO(u.getIdUsuario(), u.getNombre(), u.getCorreo(), u.getRol());
    }
}
