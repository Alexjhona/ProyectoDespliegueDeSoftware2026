package com.example.ms_compra.dto;

public class StockDto {
    private Long id;
    private Long productoId;
    private Integer cantidad;
    // getters y setters
    public StockDto() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public Long getProductoId() { return productoId; }
    public void setProductoId(Long productoId) { this.productoId = productoId; }
    public Integer getCantidad() { return cantidad; }
    public void setCantidad(Integer cantidad) { this.cantidad = cantidad; }
}
