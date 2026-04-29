package com.example.ms_venta.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class EntityTest {

    @Test
    @DisplayName("Venta - constructor vacío y setters")
    void venta_ConstructorVacioYSetters() {
        LocalDateTime fecha = LocalDateTime.of(2026, 4, 24, 10, 30);

        ItemVenta item = new ItemVenta();
        item.setId(1L);
        item.setProductoId(5L);
        item.setCantidad(2);
        item.setPrecioUnitario(25.0);

        Venta venta = new Venta();
        venta.setId(100L);
        venta.setClienteId(10L);
        venta.setFecha(fecha);
        venta.setTotal(50.0);
        venta.setItems(List.of(item));

        assertEquals(100L, venta.getId());
        assertEquals(10L, venta.getClienteId());
        assertEquals(fecha, venta.getFecha());
        assertEquals(50.0, venta.getTotal());
        assertEquals(1, venta.getItems().size());
        assertEquals(5L, venta.getItems().get(0).getProductoId());
    }

    @Test
    @DisplayName("Venta - constructor con parámetros")
    void venta_ConstructorConParametros() {
        LocalDateTime fecha = LocalDateTime.of(2026, 4, 24, 11, 0);

        Venta venta = new Venta(10L, fecha, 80.0);

        assertNull(venta.getId());
        assertEquals(10L, venta.getClienteId());
        assertEquals(fecha, venta.getFecha());
        assertEquals(80.0, venta.getTotal());
        assertNull(venta.getItems());
    }

    @Test
    @DisplayName("ItemVenta - constructor vacío y setters")
    void itemVenta_ConstructorVacioYSetters() {
        Venta venta = new Venta();
        venta.setId(100L);

        ItemVenta item = new ItemVenta();
        item.setId(1L);
        item.setProductoId(5L);
        item.setCantidad(3);
        item.setPrecioUnitario(20.0);
        item.setVenta(venta);

        assertEquals(1L, item.getId());
        assertEquals(5L, item.getProductoId());
        assertEquals(3, item.getCantidad());
        assertEquals(20.0, item.getPrecioUnitario());
        assertEquals(100L, item.getVenta().getId());
    }

    @Test
    @DisplayName("ItemVenta - constructor con parámetros")
    void itemVenta_ConstructorConParametros() {
        ItemVenta item = new ItemVenta(5L, 3, 20.0);

        assertNull(item.getId());
        assertEquals(5L, item.getProductoId());
        assertEquals(3, item.getCantidad());
        assertEquals(20.0, item.getPrecioUnitario());
        assertNull(item.getVenta());
    }
}