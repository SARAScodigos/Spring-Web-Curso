package com.PC.Store.SistemaWeb.service;

import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioResponseDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRegistroRequestDTO;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<UsuarioResponseDTO> listarTodos();
    UsuarioResponseDTO buscarPorId(Integer id);
    UsuarioResponseDTO crear(UsuarioRequestDTO dto);
    UsuarioResponseDTO actualizar(Integer id, UsuarioRequestDTO dto);
    void eliminarPorId(Integer id);
    void eliminarPorIds(List<Integer> ids);
    Optional<UsuarioResponseDTO> autenticar(String correo, String password);
    UsuarioResponseDTO registrarPublico(UsuarioRegistroRequestDTO dto);
}