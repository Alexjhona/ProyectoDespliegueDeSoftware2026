// src/main/java/com/example/ms_compra/dto/StockUpdateDto.java
package com.example.ms_compra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

public class StockUpdateDto {

    @NotNull
    @Min(0)
    private Integer cantidad;

    public StockUpdateDto() {}

    public StockUpdateDto(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public Integer getCantidad() {
        return cantidad;
    }
    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }
}
