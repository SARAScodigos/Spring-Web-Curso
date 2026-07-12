package com.PC.Store.SistemaWeb.dto.pedido;

import com.PC.Store.SistemaWeb.dto.detalle.DetallePedidoRequestDTO;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

import java.util.List;

public record PedidoRequestDTO(

        @NotNull(message = "El usuario es obligatorio")
        Integer idUsuario,

        @NotBlank(message = "El método de pago es obligatorio")
        @Pattern(regexp = "TARJETA|BILLETERA", message = "Método de pago inválido")
        String metodoPago,

        @NotEmpty(message = "El pedido debe tener al menos un producto")
        @Valid
        List<DetallePedidoRequestDTO> detalles
) {}