package com.PC.Store.SistemaWeb.service.impl;

import com.PC.Store.SistemaWeb.dto.detalle.DetallePedidoResponseDTO;
import com.PC.Store.SistemaWeb.dto.pedido.PedidoRequestDTO;
import com.PC.Store.SistemaWeb.dto.pedido.PedidoResponseDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.exception.ResourceNotFoundException;
import com.PC.Store.SistemaWeb.model.DetallePedido;
import com.PC.Store.SistemaWeb.model.Pedido;
import com.PC.Store.SistemaWeb.model.Producto;
import com.PC.Store.SistemaWeb.model.Usuario;
import com.PC.Store.SistemaWeb.repository.DetallePedidoRepository;
import com.PC.Store.SistemaWeb.repository.PedidoRepository;
import com.PC.Store.SistemaWeb.repository.ProductoRepository;
import com.PC.Store.SistemaWeb.repository.UsuarioRepository;
import com.PC.Store.SistemaWeb.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PedidoServiceImpl implements PedidoService {

    private final PedidoRepository pedidoRepository;
    private final UsuarioRepository usuarioRepository;
    private final ProductoRepository productoRepository;
    private final DetallePedidoRepository detallePedidoRepository;

    @Override
    public List<PedidoResponseDTO> listarTodos() {
        return pedidoRepository.findAll().stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    public PedidoResponseDTO buscarPorId(Integer id) {
        return toDTO(findOrThrow(id));
    }

    @Override
    public List<PedidoResponseDTO> listarPorUsuario(Integer idUsuario) {
        if (!usuarioRepository.existsById(idUsuario)) {
            throw new ResourceNotFoundException("Usuario", idUsuario);
        }
        return pedidoRepository.findByUsuarioIdUsuario(idUsuario).stream()
                .map(this::toDTO)
                .toList();
    }

    @Override
    @Transactional
    public PedidoResponseDTO crear(PedidoRequestDTO dto) {
        Usuario usuario = usuarioRepository.findById(dto.idUsuario())
                .orElseThrow(() -> new ResourceNotFoundException("Usuario", dto.idUsuario()));

        Pedido pedido = new Pedido();
        pedido.setUsuario(usuario);
        pedido.setFechaRegistro(LocalDate.now());
        pedido.setTotal(BigDecimal.ZERO);
        pedido.setMetodoPago(dto.metodoPago());
        Pedido pedidoGuardado = pedidoRepository.save(pedido);

        List<DetallePedido> detalles = new ArrayList<>();
        BigDecimal total = BigDecimal.ZERO;

        for (var detalleDTO : dto.detalles()) {
            Producto producto = productoRepository.findById(detalleDTO.idProducto())
                    .orElseThrow(() -> new ResourceNotFoundException("Producto", detalleDTO.idProducto()));

            if (producto.getStock() < detalleDTO.cantidad()) {
                throw new BusinessException("Stock insuficiente para el producto: " + producto.getNombre()
                        + " (disponible: " + producto.getStock() + ")");
            }

            producto.setStock(producto.getStock() - detalleDTO.cantidad());
            productoRepository.save(producto);

            BigDecimal subtotal = BigDecimal.valueOf(producto.getPrecio())
                    .multiply(BigDecimal.valueOf(detalleDTO.cantidad()));

            DetallePedido detalle = new DetallePedido();
            detalle.setPedido(pedidoGuardado);
            detalle.setProducto(producto);
            detalle.setCantidad(detalleDTO.cantidad());
            detalle.setSubtotal(subtotal);
            detalles.add(detalle);

            total = total.add(subtotal);
        }

        detallePedidoRepository.saveAll(detalles);
        pedidoGuardado.setTotal(total);
        pedidoGuardado.setDetalles(detalles);
        return toDTO(pedidoRepository.save(pedidoGuardado));
    }

    @Override
    @Transactional
    public void eliminarPorId(Integer id) {
        Pedido pedido = findOrThrow(id);
        restaurarStock(pedido);
        pedidoRepository.deleteById(id);
    }

    @Override
    @Transactional
    public void eliminarPorIds(List<Integer> ids) {
        List<Pedido> pedidos = pedidoRepository.findAllById(ids);
        if (pedidos.size() != ids.size()) {
            throw new BusinessException("Uno o más pedidos no fueron encontrados");
        }
        pedidos.forEach(this::restaurarStock);
        pedidoRepository.deleteAllById(ids);
    }

    private void restaurarStock(Pedido pedido) {
        if (pedido.getDetalles() != null) {
            pedido.getDetalles().forEach(d -> {
                Producto producto = d.getProducto();
                producto.setStock(producto.getStock() + d.getCantidad());
                productoRepository.save(producto);
            });
        }
    }

    private Pedido findOrThrow(Integer id) {
        return pedidoRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido", id));
    }

    private PedidoResponseDTO toDTO(Pedido p) {
        List<DetallePedidoResponseDTO> detallesDTO = p.getDetalles() == null ? List.of() :
                p.getDetalles().stream()
                        .map(d -> new DetallePedidoResponseDTO(
                                d.getIdDetalle(),
                                d.getProducto().getIdProducto(),
                                d.getProducto().getNombre(),
                                d.getProducto().getPrecio(),
                                d.getCantidad(),
                                d.getSubtotal()
                        ))
                        .toList();

        return new PedidoResponseDTO(
                p.getIdPedido(),
                p.getFechaRegistro(),
                p.getTotal(),
                p.getMetodoPago(),
                p.getUsuario().getIdUsuario(),
                p.getUsuario().getNombre(),
                detallesDTO
        );
    }
}
