package com.example.ms_compra.dto;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class OtrosDtoTest {

    @Test
    @DisplayName("ProveedorDto - builder, getters y setters")
    void proveedorDto_BuilderGettersSetters() {
        ProveedorDto dto = ProveedorDto.builder()
                .id(1L)
                .dniOrRuc("20123456789")
                .razonSocialONombre("Proveedor SAC")
                .direccion("Av. Peru 123")
                .telefono("987654321")
                .build();

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getDniOrRuc()).isEqualTo("20123456789");
        assertThat(dto.getRazonSocialONombre()).isEqualTo("Proveedor SAC");
        assertThat(dto.getDireccion()).isEqualTo("Av. Peru 123");
        assertThat(dto.getTelefono()).isEqualTo("987654321");

        dto.setId(2L);
        dto.setDniOrRuc("12345678");
        dto.setRazonSocialONombre("Proveedor Nuevo");
        dto.setDireccion("Jr. Lima 456");
        dto.setTelefono("900111222");

        assertThat(dto.getId()).isEqualTo(2L);
        assertThat(dto.getDniOrRuc()).isEqualTo("12345678");
        assertThat(dto.getRazonSocialONombre()).isEqualTo("Proveedor Nuevo");
        assertThat(dto.getDireccion()).isEqualTo("Jr. Lima 456");
        assertThat(dto.getTelefono()).isEqualTo("900111222");
    }

    @Test
    @DisplayName("StockDto - getters y setters")
    void stockDto_GettersSetters() {
        StockDto dto = new StockDto();

        dto.setId(1L);
        dto.setProductoId(2L);
        dto.setCantidad(30);

        assertThat(dto.getId()).isEqualTo(1L);
        assertThat(dto.getProductoId()).isEqualTo(2L);
        assertThat(dto.getCantidad()).isEqualTo(30);
    }

    @Test
    @DisplayName("StockUpdateDto - constructor, getters y setters")
    void stockUpdateDto_ConstructoresGettersSetters() {
        StockUpdateDto vacio = new StockUpdateDto();
        vacio.setCantidad(10);

        assertThat(vacio.getCantidad()).isEqualTo(10);

        StockUpdateDto conCantidad = new StockUpdateDto(25);

        assertThat(conCantidad.getCantidad()).isEqualTo(25);
    }
}