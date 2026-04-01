package com.example.ms_venta.dto;

import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.util.List;

public class VentaDto {
    private Long id;

    @NotNull
    private Long clienteId;

    private String clienteNombre; // nuevo campo

    private LocalDateTime fecha;
    private Double total;

    @NotNull
    private List<ItemVentaDto> items;

    public VentaDto() {}

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public Long getClienteId() {
        return clienteId;
    }
    public void setClienteId(Long clienteId) {
        this.clienteId = clienteId;
    }

    public String getClienteNombre() {
        return clienteNombre;
    }
    public void setClienteNombre(String clienteNombre) {
        this.clienteNombre = clienteNombre;
    }

    public LocalDateTime getFecha() {
        return fecha;
    }
    public void setFecha(LocalDateTime fecha) {
        this.fecha = fecha;
    }

    public Double getTotal() {
        return total;
    }
    public void setTotal(Double total) {
        this.total = total;
    }

    public List<ItemVentaDto> getItems() {
        return items;
    }
    public void setItems(List<ItemVentaDto> items) {
        this.items = items;
    }
}
