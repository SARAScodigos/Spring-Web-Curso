package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.dto.producto.ProductoResponseDTO;
import com.PC.Store.SistemaWeb.service.PedidoService;
import com.PC.Store.SistemaWeb.service.ProductoService;
import com.PC.Store.SistemaWeb.service.UsuarioService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

@WebMvcTest(controllerAdmin.class)
class controllerAdminTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private ProductoService productoService;

    @MockBean
    private UsuarioService usuarioService;

    @MockBean
    private PedidoService pedidoService;

    @Test
    void dashboardShouldLoadStatsAndLowStockProducts() throws Exception {
        when(productoService.contarProductos()).thenReturn(7L);
        when(usuarioService.contarUsuarios()).thenReturn(4L);
        when(pedidoService.contarPedidos()).thenReturn(2L);
        when(productoService.listarProductosBajoStock(5)).thenReturn(List.of(
                new ProductoResponseDTO(1, "Teclado", 25.0, 2, "", 1, "Periféricos")
        ));

        mockMvc.perform(get("/admin/dashboard"))
                .andExpect(status().isOk())
                .andExpect(view().name("Admin/dashboard"))
                .andExpect(model().attribute("totalProductos", 7L))
                .andExpect(model().attributeExists("productosBajoStock"));
    }
}
