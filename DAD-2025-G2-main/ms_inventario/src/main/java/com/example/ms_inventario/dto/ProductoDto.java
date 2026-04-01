// src/main/java/com/example/ms_inventario/dto/ProductoDto.java
package com.example.ms_inventario.dto;

public class ProductoDto {
    private Long id;
    private String nombre;
    // getters y setters
    public ProductoDto() {}
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
