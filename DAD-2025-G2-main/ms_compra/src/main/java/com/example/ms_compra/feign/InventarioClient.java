// src/main/java/com/example/ms_compra/feign/InventarioClient.java
package com.example.ms_compra.feign;

import com.example.ms_compra.dto.StockDto;
import com.example.ms_compra.dto.StockUpdateDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Cliente Feign para ms-inventario-service.
 * Ahora usamos PUT /api/stock/{productoId}/reservar?cantidad=…
 * y PUT /api/stock/{productoId}/reponer (body = StockUpdateDto).
 */
@FeignClient(name = "ms-inventario-service", url = "${feign.inventario.url}")
public interface InventarioClient {
    @PutMapping("/api/stock/{productoId}/reservar")
    StockDto reservarStock(
            @PathVariable("productoId") Long productoId,
            @RequestParam("cantidad") Integer cantidad
    );

    @PutMapping("/api/stock/{productoId}/reponer")
    StockDto reponeStock(
            @PathVariable("productoId") Long productoId,
            @RequestBody StockUpdateDto updateDto
    );
}