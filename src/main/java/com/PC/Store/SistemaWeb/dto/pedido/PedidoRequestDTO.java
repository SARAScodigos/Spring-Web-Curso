package com.PC.Store.SistemaWeb.dto.pedido;

import com.PC.Store.SistemaWeb.dto.detalle.DetallePedidoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;

import java.util.List;

public record PedidoRequestDTO(

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        String metodoPago,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid
        List<DetallePedidoRequestDTO> detalles
) {}
