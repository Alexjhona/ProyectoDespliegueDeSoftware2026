package com.example.ms_venta.controller;

import com.example.ms_venta.dto.ItemVentaDto;
import com.example.ms_venta.dto.VentaDto;
import com.example.ms_venta.service.VentaService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class VentaControllerTest {

    private MockMvc mockMvc;
    private VentaService ventaService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        ventaService = Mockito.mock(VentaService.class);
        VentaController ventaController = new VentaController(ventaService);
        mockMvc = MockMvcBuilders.standaloneSetup(ventaController).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/ventas - crear venta")
    void crearVenta_DebeRetornarOk() throws Exception {
        VentaDto entrada = crearVentaDtoEntrada();

        VentaDto salida = crearVentaDtoSalida();
        salida.setId(100L);
        salida.setTotal(50.0);
        salida.setClienteNombre("Cliente Prueba");

        when(ventaService.crearVenta(Mockito.any(VentaDto.class))).thenReturn(salida);

        mockMvc.perform(post("/api/ventas")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.clienteId").value(10))
                .andExpect(jsonPath("$.clienteNombre").value("Cliente Prueba"))
                .andExpect(jsonPath("$.total").value(50.0))
                .andExpect(jsonPath("$.items[0].productoId").value(1))
                .andExpect(jsonPath("$.items[0].cantidad").value(2))
                .andExpect(jsonPath("$.items[0].productoNombre").value("Mouse"));

        verify(ventaService).crearVenta(Mockito.any(VentaDto.class));
    }

    @Test
    @DisplayName("GET /api/ventas/{id} - obtener venta")
    void obtenerVenta_DebeRetornarOk() throws Exception {
        VentaDto salida = crearVentaDtoSalida();
        salida.setId(100L);
        salida.setTotal(50.0);
        salida.setClienteNombre("Cliente Prueba");

        when(ventaService.obtenerVenta(100L)).thenReturn(salida);

        mockMvc.perform(get("/api/ventas/100"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(100))
                .andExpect(jsonPath("$.clienteId").value(10))
                .andExpect(jsonPath("$.clienteNombre").value("Cliente Prueba"))
                .andExpect(jsonPath("$.total").value(50.0))
                .andExpect(jsonPath("$.items[0].productoNombre").value("Mouse"));

        verify(ventaService).obtenerVenta(100L);
    }

    @Test
    @DisplayName("GET /api/ventas - listar ventas")
    void listarVentas_DebeRetornarOk() throws Exception {
        VentaDto venta1 = crearVentaDtoSalida();
        venta1.setId(100L);
        venta1.setTotal(50.0);
        venta1.setClienteNombre("Cliente Prueba");

        VentaDto venta2 = crearVentaDtoSalida();
        venta2.setId(101L);
        venta2.setTotal(75.0);
        venta2.setClienteNombre("Cliente Dos");

        when(ventaService.listarVentas()).thenReturn(List.of(venta1, venta2));

        mockMvc.perform(get("/api/ventas"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(100))
                .andExpect(jsonPath("$[0].clienteNombre").value("Cliente Prueba"))
                .andExpect(jsonPath("$[0].total").value(50.0))
                .andExpect(jsonPath("$[1].id").value(101))
                .andExpect(jsonPath("$[1].clienteNombre").value("Cliente Dos"))
                .andExpect(jsonPath("$[1].total").value(75.0));

        verify(ventaService).listarVentas();
    }

    private VentaDto crearVentaDtoEntrada() {
        ItemVentaDto item = new ItemVentaDto();
        item.setProductoId(1L);
        item.setCantidad(2);

        VentaDto ventaDto = new VentaDto();
        ventaDto.setClienteId(10L);
        ventaDto.setItems(List.of(item));

        return ventaDto;
    }

    private VentaDto crearVentaDtoSalida() {
        ItemVentaDto item = new ItemVentaDto();
        item.setProductoId(1L);
        item.setCantidad(2);
        item.setProductoNombre("Mouse");

        VentaDto ventaDto = new VentaDto();
        ventaDto.setClienteId(10L);
        ventaDto.setItems(List.of(item));

        return ventaDto;
    }
}