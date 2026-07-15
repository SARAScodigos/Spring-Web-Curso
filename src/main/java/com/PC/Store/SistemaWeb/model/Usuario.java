package com.PC.Store.SistemaWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
@NoArgsConstructor

@Entity
public class Usuario {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private  Integer idUsuario;

    @Size(min=3, max = 30)
    @Column(nullable = false,
            length = 60)
    private String nombre;

    @NotBlank
    @Column(nullable = false,
            unique = true,
            length =  100)
    private String correo;

    @NotBlank
    @Size(min=3, max = 100)
    @Column(nullable = false)
    private String password;

    @NotBlank
    @Column(nullable = false)
    private String rol;

    @OneToMany(mappedBy = "usuario")
    private List<Pedido> pedidos;



}
