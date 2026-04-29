package com.example.ms_compra.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

class CompraDtoTest {

    @Test
    @DisplayName("CompraDto - builder, getters y setters")
    void compraDto_BuilderGettersSetters() {
        LocalDate fecha = LocalDate.of(2026, 4, 24);

        CompraDto dto = CompraDto.builder()
                .id(1L)
                .productoId(2L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(fecha)
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getProductoId()).isEqualTo(2L);
        assertThat(dto.getCantidad()).isEqualTo(10);
        assertThat(dto.getPrecioCompra()).isEqualTo(20.0);
        assertThat(dto.getPrecioVenta()).isEqualTo(30.0);
        assertThat(dto.getProveedorId()).isEqualTo(5L);
        assertThat(dto.getFechaCompra()).isEqualTo(fecha);

        dto.setId(9L);
        dto.setProductoId(8L);
        dto.setCantidad(15);
        dto.setPrecioCompra(25.0);
        dto.setPrecioVenta(40.0);
        dto.setProveedorId(7L);
        dto.setFechaCompra(LocalDate.of(2026, 5, 1));

        assertThat(dto.getId()).isEqualTo(9L);
        assertThat(dto.getProductoId()).isEqualTo(8L);
        assertThat(dto.getCantidad()).isEqualTo(15);
        assertThat(dto.getPrecioCompra()).isEqualTo(25.0);
        assertThat(dto.getPrecioVenta()).isEqualTo(40.0);
        assertThat(dto.getProveedorId()).isEqualTo(7L);
        assertThat(dto.getFechaCompra()).isEqualTo(LocalDate.of(2026, 5, 1));
    }

    @Test
    @DisplayName("CompraDto - constructor vacío y constructor completo")
    void compraDto_Constructores() {
        CompraDto vacio = new CompraDto();
        assertThat(vacio).isNotNull();

        LocalDate fecha = LocalDate.of(2026, 4, 24);
        CompraDto completo = new CompraDto(1L, 2L, 10, 20.0, 30.0, 5L, fecha);

        assertThat(completo.getId()).isEqualTo(1L);
        assertThat(completo.getProductoId()).isEqualTo(2L);
        assertThat(completo.getCantidad()).isEqualTo(10);
        assertThat(completo.getPrecioCompra()).isEqualTo(20.0);
        assertThat(completo.getPrecioVenta()).isEqualTo(30.0);
        assertThat(completo.getProveedorId()).isEqualTo(5L);
        assertThat(completo.getFechaCompra()).isEqualTo(fecha);
    }
}