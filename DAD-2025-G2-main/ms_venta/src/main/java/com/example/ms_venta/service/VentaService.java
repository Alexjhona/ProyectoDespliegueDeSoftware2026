package com.example.ms_venta.service;

import com.example.ms_venta.dto.VentaDto;
import java.util.List;

public interface VentaService {
    VentaDto crearVenta(VentaDto ventaDto);
    VentaDto obtenerVenta(Long id);
    List<VentaDto> listarVentas();
}