package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.dto.producto.ProductoResponseDTO;
import com.PC.Store.SistemaWeb.service.CategoriaService;
import com.PC.Store.SistemaWeb.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class inicio {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping("/")
    public String home(Model model) {
        List<ProductoResponseDTO> productos = productoService.listarTodos();
        List<ProductoResponseDTO> destacados = productos.size() > 3 ? productos.subList(0, 3) : productos;
        model.addAttribute("productosDestacados", destacados);
        return "Modulos/index";
    }

    @GetMapping("/productos")
    public String productos(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) List<Integer> categorias,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) Double precioMax,
            Model model) {

        List<ProductoResponseDTO> productos = productoService.listarTodos();

        if (buscar != null && !buscar.isBlank()) {
            String texto = buscar.toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.nombre().toLowerCase().contains(texto))
                    .toList();
        }

        if (categorias != null && !categorias.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> categorias.contains(p.idCategoria()))
                    .toList();
        }

        if (precioMax != null) {
            productos = productos.stream()
                    .filter(p -> p.precio().doubleValue() <= precioMax)
                    .toList();
        }

        if (orden != null) {
            productos = switch (orden) {
                case "menor" -> productos.stream()
                        .sorted(Comparator.comparing(ProductoResponseDTO::precio))
                        .toList();
                case "mayor" -> productos.stream()
                        .sorted(Comparator.comparing(ProductoResponseDTO::precio).reversed())
                        .toList();
                case "recientes" -> productos.stream()
                        .sorted(Comparator.comparing(ProductoResponseDTO::idProducto).reversed())
                        .toList();
                default -> productos;
            };
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("categoriasSeleccionadas", categorias != null ? categorias : List.of());
        model.addAttribute("buscarActual", buscar);
        model.addAttribute("ordenActual", orden);
        model.addAttribute("precioMaxActual", precioMax);

        return "Modulos/productos";
    }

    @GetMapping("/nosotros")
    public String nosotros() {
        return "Modulos/nosotros";
    }

    @GetMapping("/contacto")
    public String contacto() {
        return "Modulos/contacto";
    }
}