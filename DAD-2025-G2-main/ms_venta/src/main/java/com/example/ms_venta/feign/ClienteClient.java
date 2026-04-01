package com.example.ms_venta.feign;

import com.example.ms_venta.dto.ClienteDto;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "ms-cliente-service", url = "${feign.cliente.url}")
public interface ClienteClient {
    @GetMapping("/{id}")
    ClienteDto obtenerPorId(@PathVariable("id") Long id);
}
