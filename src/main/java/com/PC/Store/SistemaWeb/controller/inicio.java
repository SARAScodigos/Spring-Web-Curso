package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.service.ProductoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/user")
@RequiredArgsConstructor
public class inicio {

    private final ProductoService productoService;

    @GetMapping("/")
    public String home(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        return "Modulos/index";
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
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


}
