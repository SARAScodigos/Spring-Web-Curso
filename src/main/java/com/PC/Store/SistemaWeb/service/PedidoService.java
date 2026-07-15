package com.PC.Store.SistemaWeb.service;

import com.PC.Store.SistemaWeb.dto.pedido.PedidoRequestDTO;
import com.PC.Store.SistemaWeb.dto.pedido.PedidoResponseDTO;

import java.util.List;

public interface PedidoService {
    List<PedidoResponseDTO> listarTodos();
    PedidoResponseDTO buscarPorId(Integer id);
    List<PedidoResponseDTO> listarPorUsuario(Integer idUsuario);
    PedidoResponseDTO crear(PedidoRequestDTO dto);
    void eliminarPorId(Integer id);
    void eliminarPorIds(List<Integer> ids);
    long contarPedidos();
}
