package com.PC.Store.SistemaWeb.controller;

import com.PC.Store.SistemaWeb.dto.categoria.CategoriaRequestDTO;
import com.PC.Store.SistemaWeb.dto.producto.ProductoRequestDTO;
import com.PC.Store.SistemaWeb.dto.usuario.UsuarioRequestDTO;
import com.PC.Store.SistemaWeb.service.CategoriaService;
import com.PC.Store.SistemaWeb.service.PedidoService;
import com.PC.Store.SistemaWeb.service.ProductoService;
import com.PC.Store.SistemaWeb.service.UsuarioService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class controllerAdmin {

    private final ProductoService productoService;
    private final UsuarioService usuarioService;
    private final PedidoService pedidoService;
    private final CategoriaService categoriaService;

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalProductos", productoService.contarProductos());
        model.addAttribute("totalUsuarios", usuarioService.contarUsuarios());
        model.addAttribute("totalPedidos", pedidoService.contarPedidos());
        model.addAttribute("productosBajoStock", productoService.listarProductosBajoStock(5));
        return "Admin/dashboard";
    }

    @GetMapping("/productos")
    public String productos(Model model) {
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("productoForm", new ProductoRequestDTO(null, null, null, null, null));
        return "Admin/AdminProductos";
    }

    @PostMapping("/productos")
    public String guardarProducto(@ModelAttribute("productoForm") @Valid ProductoRequestDTO productoForm,
                                  BindingResult result,
                                  Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("categorias", categoriaService.listarTodos());
            return "Admin/AdminProductos";
        }

        productoService.crear(productoForm);
        return "redirect:/admin/productos";
    }

    @GetMapping("/productos/{id}/editar")
    public String editarProducto(@PathVariable Integer id, Model model) {
        var producto = productoService.buscarPorId(id);
        model.addAttribute("productoForm", new ProductoRequestDTO(
                producto.nombre(),
                producto.precio(),
                producto.stock(),
                producto.image(),
                producto.idCategoria()));
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("productos", productoService.listarTodos());
        model.addAttribute("modoEdicion", true);
        model.addAttribute("productoId", id);
        return "Admin/AdminProductos";
    }

    @PostMapping("/productos/{id}/editar")
    public String actualizarProducto(@PathVariable Integer id,
                                     @ModelAttribute("productoForm") @Valid ProductoRequestDTO productoForm,
                                     BindingResult result,
                                     Model model) {
        if (result.hasErrors()) {
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("categorias", categoriaService.listarTodos());
            return "Admin/AdminProductos";
        }

        productoService.actualizar(id, productoForm);
        return "redirect:/admin/productos";
    }

    @PostMapping("/productos/{id}/eliminar")
    public String eliminarProducto(@PathVariable Integer id) {
        productoService.eliminarPorId(id);
        return "redirect:/admin/productos";
    }

    @GetMapping("/usuarios")
    public String usuarios(Model model) {
        model.addAttribute("usuarios", usuarioService.listarTodos());
        model.addAttribute("adminForm", new UsuarioRequestDTO("", "", "", "ADMIN"));
        return "Admin/AdminUsuarios";
    }

    @PostMapping("/usuarios/admin")
    public String crearAdministrador(@ModelAttribute("adminForm") @Valid UsuarioRequestDTO adminForm,
                                     BindingResult result,
                                     Model model) {
        if (adminForm.rol() == null || adminForm.rol().isBlank()) {
            adminForm = new UsuarioRequestDTO(adminForm.nombre(), adminForm.correo(), adminForm.password(), "ADMIN");
        }

        if (result.hasErrors()) {
            model.addAttribute("usuarios", usuarioService.listarTodos());
            model.addAttribute("adminForm", adminForm);
            return "Admin/AdminUsuarios";
        }

        usuarioService.crearAdmin(adminForm);
        return "redirect:/admin/usuarios";
    }

    @GetMapping("/pedidos")
    public String pedidos(Model model) {
        model.addAttribute("pedidos", pedidoService.listarTodos());
        return "Admin/AdminPedidos";
    }

    @GetMapping("/categorias")
    public String categorias(Model model) {
        model.addAttribute("categorias", categoriaService.listarTodos());
        model.addAttribute("categoriaForm", new CategoriaRequestDTO(null, null));
        return "Admin/AdminCategorias";
    }

    @PostMapping("/categorias")
    public String guardarCategoria(@ModelAttribute("categoriaForm") @Valid CategoriaRequestDTO categoriaForm,
                                   BindingResult result,
                                   Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categorias", categoriaService.listarTodos());
            return "Admin/AdminCategorias";
        }

        categoriaService.crear(categoriaForm);
        return "redirect:/admin/categorias";
    }
}
