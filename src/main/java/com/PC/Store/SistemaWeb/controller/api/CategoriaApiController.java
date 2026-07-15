package com.PC.Store.SistemaWeb.controller.api;

import com.PC.Store.SistemaWeb.dto.categoria.CategoriaRequestDTO;
import com.PC.Store.SistemaWeb.dto.producto.ProductoRequestDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.service.CategoriaService;
import com.PC.Store.SistemaWeb.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/admin/categorias")
@RequiredArgsConstructor
public class CategoriaApiController {

    private final CategoriaService categoriaService;
    private final ProductoService productoService;

    @PostMapping("/guardar")
    public String guardarCategoria(
            @RequestParam(value = "idCategoria", required = false) Integer idCategoria,
            @ModelAttribute("categoria") @Valid CategoriaRequestDTO dto,
            BindingResult bindingResult,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            // Recargar datos y volver a la página del panel de control
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("categorias", categoriaService.listarTodos());
            model.addAttribute("actionActual", idCategoria != null ? "editarCategoria" : "nuevaCategoria");
            if (idCategoria != null) {
                model.addAttribute("categoriaId", idCategoria);
            }
            if (!model.containsAttribute("producto")) {
                model.addAttribute("producto", new ProductoRequestDTO("", null, null, "", null));
            }
            model.addAttribute("error", "Revisa los campos obligatorios de la categoría.");
            return "Admin/AdminProductos";
        }

        try {
            if (idCategoria != null) {
                categoriaService.actualizar(idCategoria, dto);
                redirectAttributes.addFlashAttribute("exito", "Categoría actualizada correctamente.");
            } else {
                categoriaService.crear(dto);
                redirectAttributes.addFlashAttribute("exito", "Categoría creada correctamente.");
            }
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar la categoría.");
        }

        return "redirect:/admin/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarCategoria(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            categoriaService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exito", "Categoría eliminada con éxito.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "No se puede eliminar la categoría porque contiene productos asociados.");
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/eliminar-seleccionados")
    public String eliminarSeleccionados(@RequestParam(value = "selectedIds", required = false) List<Integer> selectedIds, RedirectAttributes redirectAttributes) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos una categoría para eliminar.");
            return "redirect:/admin/productos";
        }
        try {
            categoriaService.eliminarPorIds(selectedIds);
            redirectAttributes.addFlashAttribute("exito", "Categorías seleccionadas eliminadas con éxito.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Una o más categorías seleccionadas no pueden eliminarse porque tienen productos vinculados.");
        }
        return "redirect:/admin/productos";
    }
}
