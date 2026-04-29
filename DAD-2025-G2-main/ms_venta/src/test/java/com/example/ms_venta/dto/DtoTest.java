package com.example.ms_venta.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class DtoTest {

    @Test
    @DisplayName("ClienteDto - getters y setters")
    void clienteDto_GettersSetters() {
        ClienteDto dto = new ClienteDto();
        dto.setId(1L);
        dto.setRazonSocialONombre("Cliente Prueba");

        assertEquals(1L, dto.getId());
        assertEquals("Cliente Prueba", dto.getRazonSocialONombre());
    }

    @Test
    @DisplayName("ProductoDto - getters y setters")
    void productoDto_GettersSetters() {
        ProductoDto dto = new ProductoDto();
        dto.setId(1L);
        dto.setCategoriaId(2L);
        dto.setCodigoInterno("P001");
        dto.setNombre("Mouse");
        dto.setPrecioVenta(25.0);
        dto.setPrecioCompra(15.0);
        dto.setMoneda("PEN");

        assertEquals(1L, dto.getId());
        assertEquals(2L, dto.getCategoriaId());
        assertEquals("P001", dto.getCodigoInterno());
        assertEquals("Mouse", dto.getNombre());
        assertEquals(25.0, dto.getPrecioVenta());
        assertEquals(15.0, dto.getPrecioCompra());
        assertEquals("PEN", dto.getMoneda());
    }

    @Test
    @DisplayName("StockDto - getters y setters")
    void stockDto_GettersSetters() {
        StockDto dto = new StockDto();
        dto.setId(1L);
        dto.setProductoId(5L);
        dto.setCantidad(100);

        assertEquals(1L, dto.getId());
        assertEquals(5L, dto.getProductoId());
        assertEquals(100, dto.getCantidad());
    }

    @Test
    @DisplayName("ItemVentaDto - getters y setters")
    void itemVentaDto_GettersSetters() {
        ItemVentaDto dto = new ItemVentaDto();
        dto.setProductoId(1L);
        dto.setCantidad(3);
        dto.setProductoNombre("Teclado");

        assertEquals(1L, dto.getProductoId());
        assertEquals(3, dto.getCantidad());
        assertEquals("Teclado", dto.getProductoNombre());
    }

    @Test
    @DisplayName("VentaDto - getters y setters")
    void ventaDto_GettersSetters() {
        LocalDateTime fecha = LocalDateTime.of(2026, 4, 24, 10, 30);

        ItemVentaDto item = new ItemVentaDto();
        item.setProductoId(1L);
        item.setCantidad(2);
        item.setProductoNombre("Mouse");

        VentaDto dto = new VentaDto();
        dto.setId(100L);
        dto.setClienteId(10L);
        dto.setClienteNombre("Cliente Prueba");
        dto.setFecha(fecha);
        dto.setTotal(50.0);
        dto.setItems(List.of(item));

        assertEquals(100L, dto.getId());
        assertEquals(10L, dto.getClienteId());
        assertEquals("Cliente Prueba", dto.getClienteNombre());
        assertEquals(fecha, dto.getFecha());
        assertEquals(50.0, dto.getTotal());
        assertEquals(1, dto.getItems().size());
        assertEquals("Mouse", dto.getItems().get(0).getProductoNombre());
    }

    @Test
    @DisplayName("DTOs - valores por defecto")
    void dtos_ValoresPorDefecto() {
        ClienteDto clienteDto = new ClienteDto();
        ProductoDto productoDto = new ProductoDto();
        StockDto stockDto = new StockDto();
        ItemVentaDto itemVentaDto = new ItemVentaDto();
        VentaDto ventaDto = new VentaDto();

        assertNull(clienteDto.getId());
        assertNull(clienteDto.getRazonSocialONombre());

        assertNull(productoDto.getId());
        assertNull(productoDto.getCategoriaId());
        assertNull(productoDto.getCodigoInterno());
        assertNull(productoDto.getNombre());
        assertNull(productoDto.getPrecioVenta());
        assertNull(productoDto.getPrecioCompra());
        assertNull(productoDto.getMoneda());

        assertNull(stockDto.getId());
        assertNull(stockDto.getProductoId());
        assertNull(stockDto.getCantidad());

        assertNull(itemVentaDto.getProductoId());
        assertNull(itemVentaDto.getCantidad());
        assertNull(itemVentaDto.getProductoNombre());

        assertNull(ventaDto.getId());
        assertNull(ventaDto.getClienteId());
        assertNull(ventaDto.getClienteNombre());
        assertNull(ventaDto.getFecha());
        assertNull(ventaDto.getTotal());
        assertNull(ventaDto.getItems());
    }
}