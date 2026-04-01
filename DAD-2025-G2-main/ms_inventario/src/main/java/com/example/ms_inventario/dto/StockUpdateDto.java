// src/main/java/com/example/ms_inventario/dto/StockUpdateDto.java
package com.example.ms_inventario.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockUpdateDto {

    @NotNull
    @Min(0)
    private Integer cantidad;

    public StockUpdateDto() {}

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
