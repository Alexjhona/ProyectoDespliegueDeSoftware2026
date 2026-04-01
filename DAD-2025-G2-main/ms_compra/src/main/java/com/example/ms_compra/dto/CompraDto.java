package com.example.ms_compra.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDate;
import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CompraDto {
    private Long id;

    @NotNull
    private Long productoId;

    @NotNull
    @Min(1)
    private Integer cantidad;

    @NotNull
    private Double precioCompra;

    @NotNull
    private Double precioVenta;

    @NotNull
    private Long proveedorId;

    private LocalDate fechaCompra;
}