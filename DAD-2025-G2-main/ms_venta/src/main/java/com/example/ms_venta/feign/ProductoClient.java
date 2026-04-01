package com.example.ms_venta.feign;

import com.example.ms_venta.dto.ProductoDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-producto-service", url = "${feign.producto.url}")
public interface ProductoClient {
    @GetMapping("/{id}")
    ProductoDto obtenerPorId(@PathVariable("id") Long id);
}
