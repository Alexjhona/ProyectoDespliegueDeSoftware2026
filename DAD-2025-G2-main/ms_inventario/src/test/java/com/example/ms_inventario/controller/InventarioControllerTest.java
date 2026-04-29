package com.example.ms_inventario.controller;

import com.example.ms_inventario.dto.StockDto;
import com.example.ms_inventario.dto.StockUpdateDto;
import com.example.ms_inventario.service.InventarioService;
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
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class InventarioControllerTest {

    private MockMvc mockMvc;
    private InventarioService inventarioService;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        inventarioService = Mockito.mock(InventarioService.class);
        InventarioController controller = new InventarioController(inventarioService);
        mockMvc = MockMvcBuilders.standaloneSetup(controller).build();
        objectMapper = new ObjectMapper();
    }

    @Test
    @DisplayName("POST /api/stock - crear stock")
    void crearStock_DebeRetornarOk() throws Exception {
        StockDto entrada = new StockDto();
        entrada.setProductoId(1L);
        entrada.setCantidad(50);

        StockDto salida = new StockDto();
        salida.setId(1L);
        salida.setProductoId(1L);
        salida.setCantidad(50);

        when(inventarioService.crearStockInicial(Mockito.any(StockDto.class))).thenReturn(salida);

        mockMvc.perform(post("/api/stock")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(50));

        verify(inventarioService).crearStockInicial(Mockito.any(StockDto.class));
    }

    @Test
    @DisplayName("GET /api/stock/{productoId} - obtener stock")
    void obtenerStock_DebeRetornarOk() throws Exception {
        StockDto salida = new StockDto();
        salida.setId(1L);
        salida.setProductoId(1L);
        salida.setCantidad(25);

        when(inventarioService.obtenerStock(1L)).thenReturn(salida);

        mockMvc.perform(get("/api/stock/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(25));

        verify(inventarioService).obtenerStock(1L);
    }

    @Test
    @DisplayName("GET /api/stock - listar stocks")
    void listarStocks_DebeRetornarLista() throws Exception {
        StockDto s1 = new StockDto();
        s1.setId(1L);
        s1.setProductoId(1L);
        s1.setCantidad(10);

        StockDto s2 = new StockDto();
        s2.setId(2L);
        s2.setProductoId(2L);
        s2.setCantidad(20);

        when(inventarioService.listarStocks()).thenReturn(List.of(s1, s2));

        mockMvc.perform(get("/api/stock"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].productoId").value(1))
                .andExpect(jsonPath("$[0].cantidad").value(10))
                .andExpect(jsonPath("$[1].id").value(2))
                .andExpect(jsonPath("$[1].productoId").value(2))
                .andExpect(jsonPath("$[1].cantidad").value(20));

        verify(inventarioService).listarStocks();
    }

    @Test
    @DisplayName("PUT /api/stock/{productoId}/reservar - reservar stock")
    void reservarStock_DebeRetornarOk() throws Exception {
        StockDto salida = new StockDto();
        salida.setId(1L);
        salida.setProductoId(1L);
        salida.setCantidad(15);

        when(inventarioService.reservarStock(1L, 5)).thenReturn(salida);

        mockMvc.perform(put("/api/stock/1/reservar")
                        .param("cantidad", "5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(15));

        verify(inventarioService).reservarStock(1L, 5);
    }

    @Test
    @DisplayName("PUT /api/stock/{productoId}/reponer - reponer stock")
    void reponerStock_DebeRetornarOk() throws Exception {
        StockUpdateDto entrada = new StockUpdateDto();
        entrada.setCantidad(10);

        StockDto salida = new StockDto();
        salida.setId(1L);
        salida.setProductoId(1L);
        salida.setCantidad(40);

        when(inventarioService.reponerStock(1L, 10)).thenReturn(salida);

        mockMvc.perform(put("/api/stock/1/reponer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(40));

        verify(inventarioService).reponerStock(1L, 10);
    }

    @Test
    @DisplayName("PUT /api/stock/{productoId} - actualizar stock")
    void actualizarStock_DebeRetornarOk() throws Exception {
        StockUpdateDto entrada = new StockUpdateDto();
        entrada.setCantidad(99);

        StockDto salida = new StockDto();
        salida.setId(1L);
        salida.setProductoId(1L);
        salida.setCantidad(99);

        when(inventarioService.actualizarStock(1L, 99)).thenReturn(salida);

        mockMvc.perform(put("/api/stock/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(entrada)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.productoId").value(1))
                .andExpect(jsonPath("$.cantidad").value(99));

        verify(inventarioService).actualizarStock(1L, 99);
    }
}