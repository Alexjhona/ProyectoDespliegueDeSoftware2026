// src/main/java/com/example/ms_inventario/controller/InventarioController.java
package com.example.ms_inventario.controller;

import com.example.ms_inventario.dto.StockDto;
import com.example.ms_inventario.dto.StockUpdateDto;
import com.example.ms_inventario.service.InventarioService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/stock")
public class InventarioController {

    private final InventarioService inventarioService;

    public InventarioController(InventarioService inventarioService) {
        this.inventarioService = inventarioService;
    }

    // Crear stock inicial (si no existía)
    @PostMapping
    public ResponseEntity<StockDto> crearStock(@RequestBody @Valid StockDto stockDto) {
        return ResponseEntity.ok(inventarioService.crearStockInicial(stockDto));
    }

    // Obtener stock actual de un producto
    @GetMapping("/{productoId}")
    public ResponseEntity<StockDto> obtenerStock(@PathVariable Long productoId) {
        return ResponseEntity.ok(inventarioService.obtenerStock(productoId));
    }

    // Listar todos los registros de stock
    @GetMapping
    public ResponseEntity<List<StockDto>> listarStocks() {
        return ResponseEntity.ok(inventarioService.listarStocks());
    }

    // Reservar stock (disminuir) vía parámetro de consulta
    @PutMapping("/{productoId}/reservar")
    public ResponseEntity<StockDto> reservarStock(
            @PathVariable Long productoId,
            @RequestParam Integer cantidad) {
        return ResponseEntity.ok(inventarioService.reservarStock(productoId, cantidad));
    }

    // Reponer stock (sumar) recibiendo JSON en el body
    @PutMapping("/{productoId}/reponer")
    public ResponseEntity<StockDto> reponerStock(
            @PathVariable Long productoId,
            @RequestBody @Valid StockUpdateDto updateDto) {
        return ResponseEntity.ok(inventarioService.reponerStock(productoId, updateDto.getCantidad()));
    }

    // Actualizar stock a un valor exacto, recibiendo JSON en el body
    @PutMapping("/{productoId}")
    public ResponseEntity<StockDto> actualizarStock(
            @PathVariable Long productoId,
            @RequestBody @Valid StockUpdateDto updateDto) {
        return ResponseEntity.ok(inventarioService.actualizarStock(productoId, updateDto.getCantidad()));
    }
}
