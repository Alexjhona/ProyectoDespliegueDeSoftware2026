// src/main/java/com/example/ms_inventario/service/InventarioService.java
package com.example.ms_inventario.service;

import com.example.ms_inventario.dto.StockDto;

import java.util.List;

public interface InventarioService {
    StockDto crearStockInicial(StockDto stockDto);
    StockDto obtenerStock(Long productoId);
    List<StockDto> listarStocks();
    StockDto reponerStock(Long productoId, Integer cantidad);
    StockDto reservarStock(Long productoId, Integer cantidad);
    StockDto actualizarStock(Long productoId, Integer cantidad);
}
