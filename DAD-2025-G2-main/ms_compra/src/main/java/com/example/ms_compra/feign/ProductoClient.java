// src/main/java/com/example/ms_compra/feign/ProductoClient.java
package com.example.ms_compra.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "ms-producto-service", url = "${feign.producto.url}")
public interface ProductoClient {

    @PutMapping("/api/productos/{id}/precio")
    void actualizarPrecioVenta(
            @PathVariable("id") Long id,
            @RequestParam("precioVenta") Double precioVenta
    );

    @PutMapping("/api/productos/{id}/compra")
    void actualizarPrecioCompra(
            @PathVariable("id") Long id,
            @RequestParam("precioCompra") Double precioCompra
    );

}
