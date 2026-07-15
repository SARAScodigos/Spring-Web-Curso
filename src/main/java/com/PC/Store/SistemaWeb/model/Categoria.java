package com.PC.Store.SistemaWeb.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@Entity
@Table(name="categoria")
public class Categoria {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idCategoria;

    @Size(min=3, max = 30)
    @Column(nullable = false,
            unique = true,
            length = 60)
    private String nombre;


    @NotBlank(message = "descripcion obligatorio")
    @Size(min = 3, max = 200)
    @Column(nullable = false,
            length = 200)
    private String descripcion;

    @OneToMany(mappedBy = "categoria")
    private List<Producto> productos;




}
