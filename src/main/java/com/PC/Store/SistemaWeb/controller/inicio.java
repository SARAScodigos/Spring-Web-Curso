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
        model.addAttribute("productos", productoService.listarTodos());
        return "Modulos/index";
    }

    @GetMapping("/productos")
    public String productos(
            @RequestParam(value = "buscar", required = false) String buscar,
            @RequestParam(value = "categorias", required = false) List<Integer> categorias,
            @RequestParam(value = "precioMax", required = false) Double precioMax,
            @RequestParam(value = "orden", required = false) String orden,
            Model model) {
        
        List<ProductoResponseDTO> productos = productoService.listarTodos();

        // 1. Filtrar por término de búsqueda (nombre)
        if (buscar != null && !buscar.isBlank()) {
            String term = buscar.toLowerCase().trim();
            productos = productos.stream()
                    .filter(p -> p.nombre().toLowerCase().contains(term))
                    .toList();
        }

        // 2. Filtrar por categorías seleccionadas
        if (categorias != null && !categorias.isEmpty()) {
            productos = productos.stream()
                    .filter(p -> categorias.contains(p.idCategoria()))
                    .toList();
        }

        // 3. Filtrar por precio máximo
        if (precioMax != null) {
            productos = productos.stream()
                    .filter(p -> p.precio() <= precioMax)
                    .toList();
        }

        // 4. Ordenar según el criterio seleccionado
        if (orden != null && !orden.isBlank()) {
            productos = switch (orden) {
                case "menor" -> productos.stream()
                        .sorted(Comparator.comparing(ProductoResponseDTO::precio))
                        .toList();
                case "mayor" -> productos.stream()
                        .sorted(Comparator.comparing(ProductoResponseDTO::precio).reversed())
                        .toList();
                default -> productos;
            };
        }

        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarTodos());
        return "Modulos/productos";
    }

    @GetMapping("/nosotros")
    public String nosotros(){
        return "Modulos/nosotros";
    }

    @GetMapping("/contacto")
    public String contacto(){
        return "Modulos/contacto";
    }

    @GetMapping("/servicios")
    public String servicios(){
        return "Modulos/servicios";
    }
}
