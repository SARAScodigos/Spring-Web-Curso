package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.dto.pedido.PedidoResponseDTO;
import com.PC.Store.SistemaWeb.service.PedidoService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.Comparator;
import java.util.List;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class controllerAdmin {

    private final PedidoService pedidoService;

    @GetMapping("/dashboard")
    public String dashboard() {
        return "Admin/dashboard";
    }

    @GetMapping("/productos")
    public String AdminProductos(){
        return "Admin/AdminProductos";
    }

    @GetMapping("/pedidos")
    public String AdminPedidos(Model model){
        List<PedidoResponseDTO> pedidos = pedidoService.listarTodos().stream()
                .sorted(Comparator.comparing(PedidoResponseDTO::idPedido).reversed())
                .toList();
        model.addAttribute("pedidos", pedidos);
        return "Admin/AdminPedidos";
    }

    @GetMapping("/usuarios")
    public String AdminUsuarios(){
        return "Admin/AdminUsuarios";
    }
}