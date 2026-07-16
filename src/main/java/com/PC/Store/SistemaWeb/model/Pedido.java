package com.PC.Store.SistemaWeb.model;

import jakarta.persistence.*;
import jakarta.persistence.GeneratedValue;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Setter
@Getter
@NoArgsConstructor

@Entity
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer idPedido;

    @Column(nullable = false)
    private LocalDate fechaRegistro;

    @Column(nullable = false,
            precision = 10,
            scale = 2)
    private BigDecimal total;

    @Column(name = "metodo_pago", nullable = false)
    private String metodoPago;

    @ManyToOne
    @JoinColumn(name = "id_Usuario", nullable = false)
    private Usuario usuario;

    @OneToMany(mappedBy = "pedido")
    private List<DetallePedido> detalles;





}
