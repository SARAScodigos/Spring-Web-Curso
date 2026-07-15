package com.PC.Store.SistemaWeb.service;

import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioResponseDTO;

import java.util.List;

public interface UsuarioService {
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO buscarPorId(Integer id);
    UsuarioResponseDTO crear(UsuarioRequestDTO dto);
    UsuarioResponseDTO crearAdmin(UsuarioRequestDTO dto);
    UsuarioResponseDTO autenticar(String correo, String password);
    UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto);
    void eliminarPorId(Integer id);
    void eliminarPorIds(List<Integer> ids);
    long contarUsuarios();
}
