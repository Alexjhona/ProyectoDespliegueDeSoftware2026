package com.example.ms_venta.dto;

public class ClienteDto {
    private Long id;
    private String razonSocialONombre;

    public ClienteDto() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getRazonSocialONombre() { return razonSocialONombre; }
    public void setRazonSocialONombre(String razonSocialONombre) { this.razonSocialONombre = razonSocialONombre; }
}