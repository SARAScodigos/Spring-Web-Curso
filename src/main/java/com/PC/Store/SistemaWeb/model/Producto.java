package com.PC.Store.SistemaWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.Positive;
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

public class Producto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idProducto;
    @Size(min = 3,max = 100)
    @Column(nullable = false,
            length = 100)
    private String nombre;
    @Positive(message = "presio no valido")
    @Column(nullable = false)
    private Double precio;
    @Min(value =0,message = "stock no valido")
    @Column(nullable = false)
    private Integer stock;
    @Column (length = 200)
    private String image;

    @ManyToOne
    @JoinColumn(name = "id_categoria", nullable = false)
    private Categoria categoria;

    @OneToMany(mappedBy = "producto")
    private List<DetallePedido> detalles;

}
