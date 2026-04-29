package com.example.ms_inventario.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class StockTest {

    @Test
    @DisplayName("Stock - constructor vacío y setters")
    void stock_ConstructorVacioYSetters() {
        Stock stock = new Stock();
        stock.setId(1L);
        stock.setProductoId(2L);
        stock.setCantidad(50);

        assertEquals(1L, stock.getId());
        assertEquals(2L, stock.getProductoId());
        assertEquals(50, stock.getCantidad());
    }

    @Test
    @DisplayName("Stock - constructor con parámetros")
    void stock_ConstructorConParametros() {
        Stock stock = new Stock(7L, 35);

        assertNull(stock.getId());
        assertEquals(7L, stock.getProductoId());
        assertEquals(35, stock.getCantidad());
    }
}