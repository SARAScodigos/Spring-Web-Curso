package com.PC.Store.SistemaWeb.controller.api;

import com.PC.Store.SistemaWeb.dto.categoria.CategoriaRequestDTO;
import com.PC.Store.SistemaWeb.dto.categoria.CategoriaResponseDTO;
import com.PC.Store.SistemaWeb.dto.producto.ProductoRequestDTO;
import com.PC.Store.SistemaWeb.dto.producto.ProductoResponseDTO;
import com.PC.Store.SistemaWeb.exception.BusinessException;
import com.PC.Store.SistemaWeb.service.CategoriaService;
import com.PC.Store.SistemaWeb.service.ProductoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/productos")
@RequiredArgsConstructor
public class ProductoApiController {

    private final ProductoService productoService;
    private final CategoriaService categoriaService;

    @GetMapping
    public String listarProductos(
            @RequestParam(required = false) String buscar,
            @RequestParam(required = false) Integer categoriaId,
            @RequestParam(required = false) String orden,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) Integer id,
            Model model) {

        List<ProductoResponseDTO> productos = productoService.listarTodos();

        // 1. Filtrar por búsqueda
        if (buscar != null && !buscar.isBlank()) {
            String texto = buscar.toLowerCase();
            productos = productos.stream()
                    .filter(p -> p.nombre().toLowerCase().contains(texto) || 
                                (p.nombreCategoria() != null && p.nombreCategoria().toLowerCase().contains(texto)))
                    .toList();
        }

        // 2. Filtrar por categoría
        if (categoriaId != null) {
            productos = productos.stream()
                    .filter(p -> p.idCategoria().equals(categoriaId))
                    .toList();
        }

        // 3. Ordenar
        if (orden != null && !orden.isBlank()) {
            productos = switch (orden) {
                case "nombreAsc" -> productos.stream().sorted(Comparator.comparing(p -> p.nombre().toLowerCase())).toList();
                case "nombreDesc" -> productos.stream().sorted((p1, p2) -> p2.nombre().toLowerCase().compareTo(p1.nombre().toLowerCase())).toList();
                case "precioAsc" -> productos.stream().sorted(Comparator.comparing(ProductoResponseDTO::precio)).toList();
                case "precioDesc" -> productos.stream().sorted(Comparator.comparing(ProductoResponseDTO::precio).reversed()).toList();
                case "stockAsc" -> productos.stream().sorted(Comparator.comparing(ProductoResponseDTO::stock)).toList();
                case "stockDesc" -> productos.stream().sorted(Comparator.comparing(ProductoResponseDTO::stock).reversed()).toList();
                default -> productos;
            };
        }

        // Cargar listas en el modelo
        model.addAttribute("productos", productos);
        model.addAttribute("categorias", categoriaService.listarTodos());

        // Manejar el objeto Producto para el Formulario
        if ("editar".equalsIgnoreCase(action) && id != null) {
            try {
                ProductoResponseDTO p = productoService.buscarPorId(id);
                model.addAttribute("producto", new ProductoRequestDTO(p.nombre(), p.precio(), p.stock(), p.image(), p.idCategoria()));
                model.addAttribute("productoId", id);
            } catch (Exception e) {
                model.addAttribute("error", "No se pudo cargar el producto para editar.");
                model.addAttribute("producto", new ProductoRequestDTO("", 0.0, 0, "", null));
            }
        } else {
            if (!model.containsAttribute("producto")) {
                model.addAttribute("producto", new ProductoRequestDTO("", null, null, "", null));
            }
        }

        // Manejar el objeto Categoría para el Formulario (en caso de que se necesite crear/editar categoría)
        if ("editarCategoria".equalsIgnoreCase(action) && id != null) {
            try {
                CategoriaResponseDTO c = categoriaService.buscarPorId(id);
                model.addAttribute("categoria", new CategoriaRequestDTO(c.nombre(), c.descripcion()));
                model.addAttribute("categoriaId", id);
            } catch (Exception e) {
                model.addAttribute("error", "No se pudo cargar la categoría para editar.");
                model.addAttribute("categoria", new CategoriaRequestDTO("", ""));
            }
        } else {
            if (!model.containsAttribute("categoria")) {
                model.addAttribute("categoria", new CategoriaRequestDTO("", ""));
            }
        }

        // Conservar parámetros de filtrado y orden en la paginación/filtros
        model.addAttribute("buscarActual", buscar);
        model.addAttribute("categoriaIdActual", categoriaId);
        model.addAttribute("ordenActual", orden);
        model.addAttribute("actionActual", action);

        return "Admin/AdminProductos";
    }

    @PostMapping("/guardar")
    public String guardarProducto(
            @RequestParam(value = "idProducto", required = false) Integer idProducto,
            @ModelAttribute("producto") @Valid ProductoRequestDTO dto,
            BindingResult bindingResult,
            @RequestParam(value = "imagenFile", required = false) MultipartFile imagenFile,
            RedirectAttributes redirectAttributes,
            Model model) {

        if (bindingResult.hasErrors()) {
            // Recargar listas y parámetros para volver a mostrar el formulario con errores
            model.addAttribute("productos", productoService.listarTodos());
            model.addAttribute("categorias", categoriaService.listarTodos());
            model.addAttribute("actionActual", idProducto != null ? "editar" : "nuevo");
            if (idProducto != null) {
                model.addAttribute("productoId", idProducto);
            }
            if (!model.containsAttribute("categoria")) {
                model.addAttribute("categoria", new CategoriaRequestDTO("", ""));
            }
            model.addAttribute("error", "Revisa los campos obligatorios.");
            return "Admin/AdminProductos";
        }

        try {
            // Manejar subida de archivo de imagen
            String nombreImagen = null;
            if (imagenFile != null && !imagenFile.isEmpty()) {
                nombreImagen = guardarArchivoImagen(imagenFile);
            } else if (idProducto != null) {
                // Si es edición y no se subió una nueva imagen, mantener la imagen actual
                ProductoResponseDTO pExistente = productoService.buscarPorId(idProducto);
                nombreImagen = pExistente.image();
            }

            ProductoRequestDTO dtoFinal = new ProductoRequestDTO(
                    dto.nombre(),
                    dto.precio(),
                    dto.stock(),
                    nombreImagen,
                    dto.idCategoria()
            );

            if (idProducto != null) {
                productoService.actualizar(idProducto, dtoFinal);
                redirectAttributes.addFlashAttribute("exito", "Producto actualizado correctamente.");
            } else {
                productoService.crear(dtoFinal);
                redirectAttributes.addFlashAttribute("exito", "Producto creado correctamente.");
            }
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al procesar el producto.");
        }

        return "redirect:/admin/productos";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarProducto(@PathVariable Integer id, RedirectAttributes redirectAttributes) {
        try {
            productoService.eliminarPorId(id);
            redirectAttributes.addFlashAttribute("exito", "Producto eliminado con éxito.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error al intentar eliminar el producto.");
        }
        return "redirect:/admin/productos";
    }

    @PostMapping("/eliminar-seleccionados")
    public String eliminarSeleccionados(@RequestParam(value = "selectedIds", required = false) List<Integer> selectedIds, RedirectAttributes redirectAttributes) {
        if (selectedIds == null || selectedIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Debe seleccionar al menos un producto para eliminar.");
            return "redirect:/admin/productos";
        }
        try {
            productoService.eliminarPorIds(selectedIds);
            redirectAttributes.addFlashAttribute("exito", "Productos seleccionados eliminados con éxito.");
        } catch (BusinessException e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Uno o más productos seleccionados están asociados a pedidos y no pueden eliminarse.");
        }
        return "redirect:/admin/productos";
    }

    // Método auxiliar para guardar físicamente la foto subida en la carpeta estática
    private String guardarArchivoImagen(MultipartFile file) {
        try {
            String originalName = file.getOriginalFilename();
            String ext = "";
            if (originalName != null && originalName.contains(".")) {
                ext = originalName.substring(originalName.lastIndexOf("."));
            }
            String uniqueName = UUID.randomUUID().toString() + ext;

            // Carpeta de recursos del código fuente (para que se persista en el proyecto)
            Path srcPath = Paths.get("src/main/resources/static/img").toAbsolutePath();
            if (!Files.exists(srcPath)) {
                Files.createDirectories(srcPath);
            }
            Files.copy(file.getInputStream(), srcPath.resolve(uniqueName), StandardCopyOption.REPLACE_EXISTING);

            // Carpeta del build target/classes (para que esté disponible inmediatamente al usuario)
            Path targetPath = Paths.get("target/classes/static/img").toAbsolutePath();
            if (Files.exists(targetPath)) {
                Files.copy(file.getInputStream(), targetPath.resolve(uniqueName), StandardCopyOption.REPLACE_EXISTING);
            }

            return uniqueName;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
