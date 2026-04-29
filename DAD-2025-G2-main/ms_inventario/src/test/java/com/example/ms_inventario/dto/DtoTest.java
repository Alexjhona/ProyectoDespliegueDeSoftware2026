package com.example.ms_inventario.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DtoTest {

    @Test
    @DisplayName("ProductoDto - getters y setters")
    void productoDto_GettersSetters() {
        ProductoDto dto = new ProductoDto();
        dto.setId(1L);
        dto.setNombre("Laptop");

        assertEquals(1L, dto.getId());
        assertEquals("Laptop", dto.getNombre());
    }

    @Test
    @DisplayName("StockDto - getters y setters")
    void stockDto_GettersSetters() {
        StockDto dto = new StockDto();
        dto.setId(10L);
        dto.setProductoId(5L);
        dto.setCantidad(80);

        assertEquals(10L, dto.getId());
        assertEquals(5L, dto.getProductoId());
        assertEquals(80, dto.getCantidad());
    }

    @Test
    @DisplayName("StockUpdateDto - getters y setters")
    void stockUpdateDto_GettersSetters() {
        StockUpdateDto dto = new StockUpdateDto();
        dto.setCantidad(30);

        assertEquals(30, dto.getCantidad());
    }

    @Test
    @DisplayName("DTOs - valores por defecto")
    void dtos_ValoresPorDefecto() {
        ProductoDto productoDto = new ProductoDto();
        StockDto stockDto = new StockDto();
        StockUpdateDto stockUpdateDto = new StockUpdateDto();

        assertNull(productoDto.getId());
        assertNull(productoDto.getNombre());

        assertNull(stockDto.getId());
        assertNull(stockDto.getProductoId());
        assertNull(stockDto.getCantidad());

        assertNull(stockUpdateDto.getCantidad());
    }
}