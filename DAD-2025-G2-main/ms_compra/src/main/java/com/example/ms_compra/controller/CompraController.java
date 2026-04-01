package com.example.ms_compra.controller;

import com.example.ms_compra.dto.CompraDto;
import com.example.ms_compra.service.CompraService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/compras")
@RequiredArgsConstructor
public class CompraController {
    private final CompraService compraService;

    @PostMapping
    public ResponseEntity<CompraDto> crearCompra(@RequestBody @Valid CompraDto compraDto) {
        return ResponseEntity.status(201).body(compraService.crearCompra(compraDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CompraDto> obtenerCompra(@PathVariable Long id) {
        return ResponseEntity.ok(compraService.obtenerCompra(id));
    }

    @GetMapping
    public ResponseEntity<List<CompraDto>> listarCompras() {
        return ResponseEntity.ok(compraService.listarCompras());
    }

    @PutMapping("/{id}")
    public ResponseEntity<CompraDto> actualizarCompra(
            @PathVariable Long id,
            @RequestBody @Valid CompraDto compraDto) {
        return ResponseEntity.ok(compraService.actualizarCompra(id, compraDto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarCompra(@PathVariable Long id) {
        compraService.eliminarCompra(id);
        return ResponseEntity.noContent().build();
    }
}