package com.PC.Store.SistemaWeb.dto.pedido;

import com.PC.Store.SistemaWeb.dto.detalle.DetallePedidoResponseDTO;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public record PedidoResponseDTO(
        Integer idPedido,
        LocalDate fechaRegistro,
        BigDecimal total,
        String metodoPago,
        Integer idUsuario,
        String nombreUsuario,
        List<DetallePedidoResponseDTO> detalles
) {}