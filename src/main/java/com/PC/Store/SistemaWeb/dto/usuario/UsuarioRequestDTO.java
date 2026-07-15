package com.PC.Store.SistemaWeb.dto.usuario;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UsuarioRequestDTO(

        @NotBlank(message = "El nombre es obligatorio")
        @Size(min = 3, max = 30, message = "El nombre debe tener entre 3 y 30 caracteres")
        String nombre,

        @NotBlank(message = "El correo es obligatorio")
        String correo,

        @NotBlank(message = "La contraseña es obligatoria")
        @Size(min = 8, max = 100, message = "La contraseña debe tener al menos 8 caracteres")
        String password,

        @NotBlank(message = "El rol es obligatorio")
        String rol
) {}
