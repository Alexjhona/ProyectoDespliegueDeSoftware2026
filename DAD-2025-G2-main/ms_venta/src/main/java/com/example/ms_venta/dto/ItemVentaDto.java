package com.example.ms_venta.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class ItemVentaDto {
    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidad;

    // Nuevo campo para devolver el nombre del producto
    private String productoNombre;

    public ItemVentaDto() {}

    public Long getProductoId() {
        return productoId;
    }
    public void setProductoId(Long productoId) {
        this.productoId = productoId;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public String getProductoNombre() {
        return productoNombre;
    }
    public void setProductoNombre(String productoNombre) {
        this.productoNombre = productoNombre;
    }
}