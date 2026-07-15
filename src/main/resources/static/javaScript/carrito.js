let ultimoPedido = null;

function obtenerUsuarioId() {
    const datosSesion = document.getElementById('datos-sesion');
    const usuarioId = datosSesion ? datosSesion.getAttribute('data-usuario-id') : null;
    return (usuarioId && usuarioId !== '') ? usuarioId : 'invitado';
}

function obtenerCarritoKey() {
    return 'pcstore_carrito_' + obtenerUsuarioId();
}

function obtenerCarrito() {
    const data = localStorage.getItem(obtenerCarritoKey());
    return data ? JSON.parse(data) : [];
}

function guardarCarrito(carrito) {
    localStorage.setItem(obtenerCarritoKey(), JSON.stringify(carrito));
    actualizarBadge();
    renderizarCarrito();
}

function agregarAlCarritoDesdeBoton(boton) {
    const id = parseInt(boton.dataset.id);
    const nombre = boton.dataset.nombre;
    const precio = parseFloat(boton.dataset.precio);
    agregarAlCarrito(id, nombre, precio);
}

function agregarAlCarrito(id, nombre, precio) {
    const carrito = obtenerCarrito();
    const existente = carrito.find(item => item.id === id);

    if (existente) {
        existente.cantidad += 1;
    } else {
        carrito.push({ id: id, nombre: nombre, precio: precio, cantidad: 1 });
    }

    guardarCarrito(carrito);
    mostrarToast(nombre + ' agregado al carrito');
}

function cambiarCantidad(id, delta) {
    let carrito = obtenerCarrito();
    const item = carrito.find(i => i.id === id);
    if (!item) return;

    item.cantidad += delta;

    if (item.cantidad <= 0) {
        carrito = carrito.filter(i => i.id !== id);
    }

    guardarCarrito(carrito);
}

function eliminarDelCarrito(id) {
    const carrito = obtenerCarrito().filter(i => i.id !== id);
    guardarCarrito(carrito);
}

function actualizarBadge() {
    const carrito = obtenerCarrito();
    const totalItems = carrito.reduce((sum, i) => sum + i.cantidad, 0);
    const badge = document.getElementById('cart-badge');
    if (!badge) return;

    if (totalItems > 0) {
        badge.textContent = totalItems;
        badge.style.display = 'flex';
    } else {
        badge.style.display = 'none';
    }
}

function renderizarCarrito() {
    const carrito = obtenerCarrito();
    const contenedor = document.getElementById('carrito-items');
    const vacio = document.getElementById('carrito-vacio');
    const totalMonto = document.getElementById('carrito-total-monto');
    const btnConfirmar = document.getElementById('btn-confirmar-pedido');

    if (!contenedor) return;

    if (carrito.length === 0) {
        contenedor.style.display = 'none';
        vacio.style.display = 'flex';
        btnConfirmar.disabled = true;
    } else {
        contenedor.style.display = 'block';
        vacio.style.display = 'none';
        btnConfirmar.disabled = false;
    }

    contenedor.innerHTML = '';
    let total = 0;

    carrito.forEach(item => {
        const subtotal = item.precio * item.cantidad;
        total += subtotal;

        const div = document.createElement('div');
        div.className = 'carrito-item';
        div.innerHTML = `
            <div class="carrito-item-info">
                <h6>${item.nombre}</h6>
                <div class="carrito-item-precio">S/ ${item.precio.toFixed(2)} c/u</div>
                <div class="carrito-item-cantidad">
                    <button type="button" onclick="cambiarCantidad(${item.id}, -1)">−</button>
                    <span>${item.cantidad}</span>
                    <button type="button" onclick="cambiarCantidad(${item.id}, 1)">+</button>
                </div>
            </div>
            <button type="button" class="carrito-item-eliminar" onclick="eliminarDelCarrito(${item.id})">
                <i class="bi bi-trash"></i>
            </button>
        `;
        contenedor.appendChild(div);
    });

    totalMonto.textContent = 'S/ ' + total.toFixed(2);
}

function toggleCarrito() {
    const sidebar = document.getElementById('carrito-sidebar');
    const overlay = document.getElementById('carrito-overlay');
    sidebar.classList.toggle('activo');
    overlay.classList.toggle('activo');
}

function mostrarToast(mensaje) {
    let toast = document.querySelector('.toast-carrito');
    if (!toast) {
        toast = document.createElement('div');
        toast.className = 'toast-carrito';
        document.body.appendChild(toast);
    }
    toast.textContent = mensaje;
    toast.classList.add('mostrar');
    setTimeout(() => toast.classList.remove('mostrar'), 2000);
}

function confirmarPedido() {
    const carrito = obtenerCarrito();
    if (carrito.length === 0) return;

    const datosSesion = document.getElementById('datos-sesion');
    const usuarioId = datosSesion ? datosSesion.getAttribute('data-usuario-id') : null;

    if (!usuarioId || usuarioId === '') {
        mostrarToast('Debes iniciar sesión para confirmar tu pedido');
        setTimeout(() => window.location.href = '/', 1200);
        return;
    }

    const metodoPagoSeleccionado = document.querySelector('input[name="metodoPago"]:checked');
    if (!metodoPagoSeleccionado) {
        mostrarToast('Por favor, selecciona un método de pago');
        return;
    }
    const metodoPago = metodoPagoSeleccionado.value;

    const payload = {
        idUsuario: parseInt(usuarioId),
        metodoPago: metodoPago,
        detalles: carrito.map(item => ({
            idProducto: item.id,
            cantidad: item.cantidad
        }))
    };

    const btn = document.getElementById('btn-confirmar-pedido');
    btn.disabled = true;
    btn.textContent = 'Procesando...';

    fetch('/api/v1/pedidos', {
        method: 'POST',
        headers: { 'Content-Type': 'application/json' },
        body: JSON.stringify(payload)
    })
    .then(response => {
        if (!response.ok) {
            return response.json().then(err => { throw new Error(err.mensaje || 'Error al crear el pedido'); });
        }
        return response.json();
    })
    .then(pedido => {
        localStorage.removeItem(obtenerCarritoKey());
        actualizarBadge();
        renderizarCarrito();
        toggleCarrito();
        mostrarBoleta(pedido);
    })
    .catch(err => {
        mostrarToast(err.message);
    })
    .finally(() => {
        btn.disabled = false;
        btn.innerHTML = 'Confirmar pedido <i class="bi bi-arrow-right-short"></i>';
    });
}

function mostrarBoleta(pedido) {
    ultimoPedido = pedido;
    document.getElementById('boleta-id').textContent = '#' + pedido.idPedido;
    document.getElementById('boleta-fecha').textContent = pedido.fechaRegistro;
    document.getElementById('boleta-cliente').textContent = pedido.nombreUsuario;
    document.getElementById('boleta-metodo-pago').textContent =
        pedido.metodoPago === 'TARJETA' ? 'Tarjeta' : 'Billetera virtual';

    const contenedorItems = document.getElementById('boleta-items');
    contenedorItems.innerHTML = '';

    pedido.detalles.forEach(d => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${d.nombreProducto}</td>
            <td>${d.cantidad}</td>
            <td>S/ ${d.precioUnitario.toFixed(2)}</td>
            <td>S/ ${d.subtotal.toFixed(2)}</td>
        `;
        contenedorItems.appendChild(fila);
    });

    document.getElementById('boleta-total-monto').textContent = 'S/ ' + pedido.total.toFixed(2);

    document.getElementById('boleta-modal').classList.add('activo');
    document.getElementById('boleta-overlay').classList.add('activo');
}

function cerrarBoleta() {
    document.getElementById('boleta-modal').classList.remove('activo');
    document.getElementById('boleta-overlay').classList.remove('activo');
}
function imprimirBoleta() {
    if (!ultimoPedido) return;
    construirReciboImprimible(ultimoPedido);
    window.print();
}

function construirReciboImprimible(pedido) {
    document.getElementById('recibo-fecha').textContent = pedido.fechaRegistro;
    document.getElementById('recibo-numero').textContent = 'N° de pedido: #' + pedido.idPedido;
    document.getElementById('recibo-cliente').textContent = pedido.nombreUsuario;
    document.getElementById('recibo-metodo-pago').textContent =
        pedido.metodoPago === 'TARJETA' ? 'Tarjeta' : 'Billetera virtual';

    const contenedor = document.getElementById('recibo-items');
    contenedor.innerHTML = '';

    pedido.detalles.forEach(d => {
        const fila = document.createElement('tr');
        fila.innerHTML = `
            <td>${d.cantidad}</td>
            <td>${d.nombreProducto}</td>
            <td>S/ ${d.precioUnitario.toFixed(2)}</td>
            <td>S/ ${d.subtotal.toFixed(2)}</td>
        `;
        contenedor.appendChild(fila);
    });

    document.getElementById('recibo-total-monto').textContent = 'S/ ' + pedido.total.toFixed(2);
}
function toggleMetodoPago() {
    const metodo = document.querySelector('input[name="metodoPago"]:checked').value;
    document.getElementById('pago-tarjeta-info').style.display = metodo === 'TARJETA' ? 'flex' : 'none';
    document.getElementById('pago-billetera-info').style.display = metodo === 'BILLETERA' ? 'block' : 'none';
}

function copiarNumero() {
    navigator.clipboard.writeText('943338774');
    mostrarToast('Número copiado');
}
document.addEventListener('DOMContentLoaded', () => {
    actualizarBadge();
    renderizarCarrito();
});