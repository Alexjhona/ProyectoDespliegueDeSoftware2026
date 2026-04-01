package com.example.ms_venta.feign;

import com.example.ms_venta.dto.StockDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-inventario-service", url = "${feign.inventario.url}")
public interface InventarioClient {

    /**
     * Llama a PUT /api/stock/{productoId}/reservar?cantidad={cantidad}
     * Devuelve el StockDto actualizado o lanza 400 si no existe o no hay suficiente stock.
     */
    @PutMapping("/{productoId}/reservar")
    StockDto reservarStock(
            @PathVariable("productoId") Long productoId,
            @RequestParam("cantidad") Integer cantidad
    );
}