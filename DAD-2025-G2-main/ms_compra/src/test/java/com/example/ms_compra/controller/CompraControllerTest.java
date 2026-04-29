package com.example.ms_compra.controller;

import com.example.ms_compra.dto.CompraDto;
import com.example.ms_compra.service.CompraService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(CompraController.class)
class CompraControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CompraService compraService;

    @Test
    @DisplayName("POST /api/compras - crea compra correctamente")
    void crearCompra_RetornaCreated() throws Exception {
        CompraDto request = CompraDto.builder()
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        CompraDto response = CompraDto.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        when(compraService.crearCompra(any(CompraDto.class))).thenReturn(response);

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productoId").value(1L))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.precioCompra").value(20.0))
                .andExpect(jsonPath("$.precioVenta").value(30.0))
                .andExpect(jsonPath("$.proveedorId").value(5L))
                .andExpect(jsonPath("$.fechaCompra").value("2026-04-24"));

        verify(compraService).crearCompra(any(CompraDto.class));
    }

    @Test
    @DisplayName("GET /api/compras/{id} - obtiene compra por id")
    void obtenerCompra_RetornaOk() throws Exception {
        CompraDto response = CompraDto.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        when(compraService.obtenerCompra(1L)).thenReturn(response);

        mockMvc.perform(get("/api/compras/{id}", 1L))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.productoId").value(1L))
                .andExpect(jsonPath("$.cantidad").value(10))
                .andExpect(jsonPath("$.proveedorId").value(5L));

        verify(compraService).obtenerCompra(1L);
    }

    @Test
    @DisplayName("GET /api/compras - lista compras")
    void listarCompras_RetornaOk() throws Exception {
        CompraDto compra1 = CompraDto.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        CompraDto compra2 = CompraDto.builder()
                .id(2L)
                .productoId(2L)
                .cantidad(4)
                .precioCompra(12.0)
                .precioVenta(18.0)
                .proveedorId(6L)
                .fechaCompra(LocalDate.of(2026, 4, 25))
                .build();

        when(compraService.listarCompras()).thenReturn(List.of(compra1, compra2));

        mockMvc.perform(get("/api/compras"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)))
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].cantidad").value(10))
                .andExpect(jsonPath("$[1].id").value(2L))
                .andExpect(jsonPath("$[1].cantidad").value(4));

        verify(compraService).listarCompras();
    }

    @Test
    @DisplayName("PUT /api/compras/{id} - actualiza compra")
    void actualizarCompra_RetornaOk() throws Exception {
        CompraDto request = CompraDto.builder()
                .productoId(1L)
                .cantidad(15)
                .precioCompra(22.0)
                .precioVenta(35.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        CompraDto response = CompraDto.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(15)
                .precioCompra(22.0)
                .precioVenta(35.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        when(compraService.actualizarCompra(eq(1L), any(CompraDto.class))).thenReturn(response);

        mockMvc.perform(put("/api/compras/{id}", 1L)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1L))
                .andExpect(jsonPath("$.cantidad").value(15))
                .andExpect(jsonPath("$.precioCompra").value(22.0))
                .andExpect(jsonPath("$.precioVenta").value(35.0));

        verify(compraService).actualizarCompra(eq(1L), any(CompraDto.class));
    }

    @Test
    @DisplayName("DELETE /api/compras/{id} - elimina compra")
    void eliminarCompra_RetornaNoContent() throws Exception {
        doNothing().when(compraService).eliminarCompra(1L);

        mockMvc.perform(delete("/api/compras/{id}", 1L))
                .andExpect(status().isNoContent());

        verify(compraService).eliminarCompra(1L);
    }

    @Test
    @DisplayName("POST /api/compras - retorna Bad Request si datos son inválidos")
    void crearCompra_DatosInvalidos_RetornaBadRequest() throws Exception {
        CompraDto request = CompraDto.builder()
                .productoId(null)
                .cantidad(0)
                .precioCompra(null)
                .precioVenta(null)
                .proveedorId(null)
                .build();

        mockMvc.perform(post("/api/compras")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}