// src/main/java/com/example/ms_inventario/dto/StockDto.java
package com.example.ms_inventario.dto;

public class StockDto {
    private Long id;
    private Long productoId;
    private Integer cantidad;

    public StockDto() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

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
}
