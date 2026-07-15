package com.PC.Store.SistemaWeb.service.impl;

import com.PC.Store.SistemaWeb.dto.producto.ProductoRequestDTO;
import com.PC.Store.SistemaWeb.dto.producto.ProductoResponseDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.exception.ResourceNotFoundException;
import com.PC.Store.SistemaWeb.model.Categoria;
import com.PC.Store.SistemaWeb.model.DetallePedido;
import com.PC.Store.SistemaWeb.model.Producto;
import com.PC.Store.SistemaWeb.repository.CategoriaRepository;
import com.PC.Store.SistemaWeb.repository.DetallePedidoRepository;
import com.PC.Store.SistemaWeb.repository.ProductoRepository;
import com.PC.Store.SistemaWeb.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductoServiceImpl implements ProductoService {

    private final ProductoRepository productoRepository;
    private final CategoriaRepository categoriaRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @Override
    public List<ProductoResponseDTO> listarTodos() {
        return productoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public List<ProductoResponseDTO> listarProductosBajoStock(int stock) {
        return productoRepository.findByStockLessThanEqualOrderByStockAsc(stock).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public ProductoResponseDTO buscarPorId(Integer id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    @Transactional
    public ProductoResponseDTO crear(ProductoRequestDTO dto) {
        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", dto.idCategoria()));
        Producto producto = new Producto();
        mapToEntity(dto, producto, categoria);
        return toDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public ProductoResponseDTO actualizar(Integer id, ProductoRequestDTO dto) {
        Producto producto = findOrThrow(id);
        Categoria categoria = categoriaRepository.findById(dto.idCategoria())
                .orElseThrow(() -> new ResourceNotFoundException("Categoria", dto.idCategoria()));
        mapToEntity(dto, producto, categoria);
        return toDTO(productoRepository.save(producto));
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        Producto producto = findOrThrow(id);
        if (producto.getDetalles() != null && !producto.getDetalles().isEmpty()) {
            detallePedidoRepository.deleteAll(producto.getDetalles());
        }
        productoRepository.delete(producto);
    }

    @Override
    @Transactional
    public void eliminarPorIds(List<Integer> ids) {
        List<Producto> productos = productoRepository.findAllById(ids);
        if (productos.size() != ids.size()) {
            throw new BusinessException("Uno o más productos no fueron encontrados");
        }

        for (Producto producto : productos) {
            if (producto.getDetalles() != null && !producto.getDetalles().isEmpty()) {
                detallePedidoRepository.deleteAll(producto.getDetalles());
            }
        }

        productoRepository.deleteAll(productos);
    }

    @Override
    public long contarProductos() {
        return productoRepository.count();
    }

    private Producto findOrThrow(Integer id) {
        return productoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto", id));
    }

    private void mapToEntity(ProductoRequestDTO dto, Producto producto, Categoria categoria) {
        producto.setNombre(dto.nombre());
        producto.setPrecio(dto.precio());
        producto.setStock(dto.stock());
        producto.setImage(dto.image());
        producto.setCategoria(categoria);
    }

    private ProductoResponseDTO toDTO(Producto p) {
        return new ProductoResponseDTO(
                p.getIdProducto(),
                p.getNombre(),
                p.getPrecio(),
                p.getStock(),
                p.getImage(),
                p.getCategoria().getIdCategoria(),
                p.getCategoria().getNombre()
        );
    }
}
