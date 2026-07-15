package com.PC.Store.SistemaWeb.controller.api;

import com.PC.Store.SistemaWeb.dto.pedido.PedidoRequestDTO;
import com.PC.Store.SistemaWeb.dto.pedido.PedidoResponseDTO;
import com.PC.Store.SistemaWeb.service.PedidoService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/admin/pedidos")
@RequiredArgsConstructor
public class PedidoApiController {

    private final PedidoService pedidoService;

    @GetMapping
    public ResponseEntity<List<PedidoResponseDTO>> listarTodos() {
        return ResponseEntity.ok(pedidoService.listarTodos());
    }

    @GetMapping("/{id}")
    public ResponseEntity<PedidoResponseDTO> buscarPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(pedidoService.buscarPorId(id));
    }

    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<PedidoResponseDTO>> listarPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(pedidoService.listarPorUsuario(idUsuario));
    }

    @PostMapping
    public ResponseEntity<PedidoResponseDTO> crear(@RequestBody @Valid PedidoRequestDTO dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(pedidoService.crear(dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarPorId(@PathVariable Integer id) {
        pedidoService.eliminarPorId(id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/batch")
    public ResponseEntity<Void> eliminarPorIds(@RequestBody List<Integer> ids) {
        pedidoService.eliminarPorIds(ids);
        return ResponseEntity.noContent().build();
    }
}
