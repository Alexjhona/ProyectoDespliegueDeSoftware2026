package com.example.ms_venta.controller;

import com.example.ms_venta.dto.VentaDto;
import com.example.ms_venta.service.VentaService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/ventas")
public class VentaController {

    private final VentaService ventaService;

    public VentaController(VentaService ventaService) {
        this.ventaService = ventaService;
    }

    @PostMapping
    public ResponseEntity<VentaDto> crear(@RequestBody @Valid VentaDto ventaDto) {
        return ResponseEntity.ok(ventaService.crearVenta(ventaDto));
    }

    @GetMapping("/{id}")
    public ResponseEntity<VentaDto> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(ventaService.obtenerVenta(id));
    }

    @GetMapping
    public ResponseEntity<List<VentaDto>> listar() {
        return ResponseEntity.ok(ventaService.listarVentas());
    }
}