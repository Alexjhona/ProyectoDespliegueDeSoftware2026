package com.example.ms_compra.service;

import com.example.ms_compra.dto.CompraDto;
import java.util.List;

public interface CompraService {
    CompraDto crearCompra(CompraDto compraDto);
    CompraDto obtenerCompra(Long id);
    List<CompraDto> listarCompras();
    CompraDto actualizarCompra(Long id, CompraDto compraDto);
    void eliminarCompra(Long id);
}