# Documentación de Implementación: Gestión de Productos y Categorías (Server-Side MVC)

Esta guía detalla la implementación del panel de administración de inventario (productos y categorías) y la vista general de productos mediante **Thymeleaf Fragments** y **Spring MVC Controller**. El procesamiento es 100% del lado del servidor, eliminando llamadas de red personalizadas via Javascript (no AJAX) y soportando la subida de fotos físicas del producto.

---

## 1. Estructura de Archivos y Responsabilidades

- **Controladores del Servidor (Spring MVC):**
  - [ProductoApiController.java](file:///home/sarah/Documentos/Solo%20Sarah/Marcos%20web/Proyecto_Marco_Desarrollo/src/main/java/com/PC/Store/SistemaWeb/controller/api/ProductoApiController.java): Convertido en `@Controller` (estándar). Administra el listado, los filtros de búsqueda, ordenamiento, la carga del formulario (tanto para creación como edición precargada) y la eliminación de productos (individual y en lote). Además procesa la **subida de imágenes físicas**.
  - [CategoriaApiController.java](file:///home/sarah/Documentos/Solo%20Sarah/Marcos%20web/Proyecto_Marco_Desarrollo/src/main/java/com/PC/Store/SistemaWeb/controller/api/CategoriaApiController.java): Convertido en `@Controller` (estándar). Procesa las peticiones POST de guardado de categorías y las peticiones de eliminación.
- **Fragments de Vista y Formularios:**
  - [adminCrearProductoForm.html](file:///home/sarah/Documentos/Solo%20Sarah/Marcos%20web/Proyecto_Marco_Desarrollo/src/main/resources/templates/fragments/adminCrearProductoForm.html): Contiene las tarjetas de formularios para crear y editar productos (con soporte para subida de archivos multipart `enctype="multipart/form-data"`) y categorías, bindeados al modelo con `th:object` y mostrando errores de validación con `th:errors`.
  - [adminProducto.html](file:///home/sarah/Documentos/Solo%20Sarah/Marcos%20web/Proyecto_Marco_Desarrollo/src/main/resources/templates/fragments/adminProducto.html): Contiene la vista general del panel (pestañas de productos/categorías, tablas de datos, filtros por GET en URL) y los fragments de renderizado público (`publicProductGrid` y `publicProductCard`).
- **Vista de Consumo:**
  - [AdminProductos.html](file:///home/sarah/Documentos/Solo%20Sarah/Marcos%20web/Proyecto_Marco_Desarrollo/src/main/resources/templates/Admin/AdminProductos.html): Inserta el fragmento de gestión de inventario.
- **Estilos:**
  - [adminProductos.css](file:///home/sarah/Documentos/Solo%20Sarah/Marcos%20web/Proyecto_Marco_Desarrollo/src/main/resources/static/css/adminProductos.css): Estilos avanzados para layouts, brillo en inputs con error y transiciones.

---

## 2. Flujo de Trabajo del Servidor (Server-Side Flow)

Toda la interactividad se maneja actualizando el estado de la página mediante parámetros en la URL (Query Parameters):

1. **Listar e ir al panel:** `/admin/productos`
2. **Filtrar y buscar:** Se envía un formulario GET que recarga la vista aplicando filtros en el servidor.
3. **Formulario de Nuevo Producto:** `/admin/productos?action=nuevo` (Muestra la tarjeta de creación de producto).
4. **Formulario de Editar Producto:** `/admin/productos?action=editar&id=5` (El servidor busca el producto, lo añade al modelo y muestra la tarjeta prellenada).
5. **Formulario de Nueva Categoría:** `/admin/productos?action=nuevaCategoria` (Muestra la tarjeta de creación de categorías).
6. **Formulario de Editar Categoría:** `/admin/productos?action=editarCategoria&id=2` (Muestra la tarjeta de categoría prellenada).
7. **Persistencia y Validación:**
   - Si ocurre un error de validación, el servidor no redirige, sino que renderiza la vista `/admin/productos` con los campos erróneos marcados en rojo mediante Thymeleaf.
   - Si se guarda correctamente, el servidor usa `RedirectAttributes` para inyectar un mensaje flash (`exito`) y redirigir limpiamente a `/admin/productos`.

---

## 3. Subida Física de Fotos de Producto

El campo de imagen ha dejado de ser un texto. Ahora, se incluye un elemento `<input type="file" name="imagenFile">` en el formulario.

### Mecanismo de Almacenamiento
Cuando el controlador recibe el archivo de imagen:
1. Genera un nombre único con `UUID.randomUUID()` conservando su extensión original.
2. Copia el archivo al directorio de desarrollo: `src/main/resources/static/img/` (garantizando persistencia en el código fuente).
3. Copia el archivo al directorio de compilación: `target/classes/static/img/` (lo que hace que la imagen sea accesible por el servidor web **inmediatamente** sin requerir reiniciar la aplicación).
4. Guarda el nombre de archivo resultante en el campo `image` del producto.
5. Si se edita un producto y no se sube un nuevo archivo, el servidor conserva automáticamente el nombre de la imagen ya registrada en base de datos.

---

## 4. Guía de Uso de los Fragments (Para Desarrolladores)

### A. Embeber Panel de Administración Completo
Para integrar el panel completo (con listas, buscador, filtros, barra de pestañas y formularios de creación/edición dinámicos por URL):

```html
<div th:replace="~{fragments/adminProducto :: adminGestionCompleta}"></div>
```

*Nota: Asegúrate de incluir el archivo CSS en la cabecera `<head>` de la página:*
```html
<link th:href="@{/css/adminProductos.css}" rel="stylesheet">
```

### B. Embeber Formulario de Producto por Separado
Si deseas colocar el formulario de producto en otra página separada del panel:

```html
<div th:replace="~{fragments/adminCrearProductoForm :: adminCrearProductoForm(${producto}, ${categorias}, ${productoId}, ${actionActual})}"></div>
```
*(Requiere que el controlador provea en el Model los atributos: `producto` (ProductoRequestDTO), `categorias` (List), `productoId` (Integer, null si es nuevo) y `actionActual` (String)).*

### C. Embeber Catálogo de Ventas (Público)
Para renderizar los productos en la tienda pública (como en `index.html` o `productos.html`):

```html
<div th:replace="~{fragments/adminProducto :: publicProductGrid(${productos})}"></div>
```

---

## 5. Mapeos de Rutas del Controlador (MVC)

### Productos (`/admin/productos`)
*   `GET /admin/productos`: Renderiza el panel. Parámetros opcionales: `buscar`, `categoriaId`, `orden`, `action`, `id`.
*   `POST /admin/productos/guardar`: Procesa el guardado. Recibe `@ModelAttribute("producto")`, `@RequestParam("imagenFile")` e `idProducto` opcional.
*   `GET /admin/productos/eliminar/{id}`: Elimina el producto especificado.
*   `POST /admin/productos/eliminar-seleccionados`: Procesa la eliminación en lote. Recibe la lista `selectedIds` mediante las casillas marcadas.

### Categorías (`/admin/categorias`)
*   `POST /admin/categorias/guardar`: Procesa el guardado. Recibe `@ModelAttribute("categoria")` e `idCategoria` opcional.
*   `GET /admin/categorias/eliminar/{id}`: Elimina la categoría especificada.
*   `POST /admin/categorias/eliminar-seleccionados`: Procesa la eliminación de categorías en lote.

---

## 6. Seguridad y Protección de Rutas

Para proteger estas rutas de administración, se debe verificar que el rol del usuario en la sesión sea `'ADMIN'`. El otro desarrollador puede hacerlo de tres formas:

### Opción A: Verificación Manual en Controlador
```java
@GetMapping
public String listar(HttpSession session, Model model) {
    String rol = (String) session.getAttribute("usuarioRol");
    if (rol == null || !"ADMIN".equalsIgnoreCase(rol)) {
        return "redirect:/?error=NoAutorizado";
    }
    // ... lógica del controlador ...
}
```

### Opción B: Mediante un `HandlerInterceptor` de Spring (Recomendado)
Crea una clase que intercepte todas las peticiones que inicien con `/admin/` y compruebe la sesión:

```java
@Component
public class AdminSecurityInterceptor implements HandlerInterceptor {
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        HttpSession session = request.getSession(false);
        if (session != null && "ADMIN".equalsIgnoreCase((String) session.getAttribute("usuarioRol"))) {
            return true;
        }
        response.sendRedirect("/?error=NoAutorizado");
        return false;
    }
}
```
Y regístrala en una clase de configuración:
```java
@Configuration
@RequiredArgsConstructor
public class WebConfig implements WebMvcConfigurer {
    private final AdminSecurityInterceptor securityInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(securityInterceptor)
                .addPathPatterns("/admin/**")
                .excludePathPatterns("/css/**", "/img/**", "/javaScript/**");
    }
}
```

### Opción C: Spring Security (Enterprise)
Si en el futuro deciden añadir la dependencia `spring-boot-starter-security`, se pueden bloquear las rutas con:
```java
http.authorizeHttpRequests(auth -> auth
    .requestMatchers("/admin/**").hasRole("ADMIN")
    .anyRequest().permitAll()
);
```
