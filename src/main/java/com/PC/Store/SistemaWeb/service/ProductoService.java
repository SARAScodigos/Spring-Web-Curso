package com.PC.Store.SistemaWeb.service;

import com.PC.Store.SistemaWeb.dto.producto.ProductoRequestDTO;
import com.PC.Store.SistemaWeb.dto.producto.ProductoResponseDTO;

import java.util.List;

public interface ProductoService {
    List<ProductoResponseDTO> listarTodos();
    List<ProductoResponseDTO> listarProductosBajoStock(int stock);
    ProductoResponseDTO buscarPorId(Integer id);
    ProductoResponseDTO crear(ProductoRequestDTO dto);
    ProductoResponseDTO actualizar(Integer id, ProductoRequestDTO dto);
    void eliminarPorId(Integer id);
    void eliminarPorIds(List<Integer> ids);

    long contarProductos();
}
