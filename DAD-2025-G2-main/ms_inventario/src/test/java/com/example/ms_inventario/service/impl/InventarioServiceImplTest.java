package com.example.ms_inventario.service.impl;

import com.example.ms_inventario.dto.ProductoDto;
import com.example.ms_inventario.dto.StockDto;
import com.example.ms_inventario.entity.Stock;
import com.example.ms_inventario.feign.ProductoClient;
import com.example.ms_inventario.repository.StockRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class InventarioServiceImplTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private ProductoClient productoClient;

    @InjectMocks
    private InventarioServiceImpl inventarioService;

    @Test
    @DisplayName("Crear stock inicial - guarda correctamente cuando producto existe")
    void crearStockInicial_GuardaCorrectamente() {
        StockDto entrada = new StockDto();
        entrada.setProductoId(1L);
        entrada.setCantidad(10);

        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(1L);
        productoDto.setNombre("Producto prueba");

        when(productoClient.obtenerPorId(1L)).thenReturn(productoDto);
        when(stockRepository.existsByProductoId(1L)).thenReturn(false);
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> {
            Stock stock = invocation.getArgument(0);
            stock.setId(100L);
            return stock;
        });

        StockDto resultado = inventarioService.crearStockInicial(entrada);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(100L);
        assertThat(resultado.getProductoId()).isEqualTo(1L);
        assertThat(resultado.getCantidad()).isEqualTo(10);

        verify(productoClient).obtenerPorId(1L);
        verify(stockRepository).existsByProductoId(1L);
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("Crear stock inicial - lanza excepción cuando producto no existe")
    void crearStockInicial_ProductoNoExiste_LanzaExcepcion() {
        StockDto entrada = new StockDto();
        entrada.setProductoId(99L);
        entrada.setCantidad(10);

        when(productoClient.obtenerPorId(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.crearStockInicial(entrada)
        );

        assertThat(exception.getMessage()).contains("Producto no existe");

        verify(productoClient).obtenerPorId(99L);
        verify(stockRepository, never()).existsByProductoId(anyLong());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Crear stock inicial - lanza excepción cuando ya existe stock")
    void crearStockInicial_StockYaExiste_LanzaExcepcion() {
        StockDto entrada = new StockDto();
        entrada.setProductoId(1L);
        entrada.setCantidad(10);

        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(1L);
        productoDto.setNombre("Producto prueba");

        when(productoClient.obtenerPorId(1L)).thenReturn(productoDto);
        when(stockRepository.existsByProductoId(1L)).thenReturn(true);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.crearStockInicial(entrada)
        );

        assertThat(exception.getMessage()).contains("Ya existe stock");

        verify(productoClient).obtenerPorId(1L);
        verify(stockRepository).existsByProductoId(1L);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Obtener stock - retorna stock existente")
    void obtenerStock_RetornaStockExistente() {
        Stock stock = new Stock(1L, 20);
        stock.setId(5L);

        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stock));

        StockDto resultado = inventarioService.obtenerStock(1L);

        assertThat(resultado.getId()).isEqualTo(5L);
        assertThat(resultado.getProductoId()).isEqualTo(1L);
        assertThat(resultado.getCantidad()).isEqualTo(20);

        verify(stockRepository).findByProductoId(1L);
    }

    @Test
    @DisplayName("Obtener stock - lanza excepción cuando no existe")
    void obtenerStock_NoExiste_LanzaExcepcion() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.obtenerStock(1L)
        );

        assertThat(exception.getMessage()).contains("No existe stock");

        verify(stockRepository).findByProductoId(1L);
    }

    @Test
    @DisplayName("Listar stocks - retorna lista de stocks")
    void listarStocks_RetornaLista() {
        Stock stock1 = new Stock(1L, 10);
        stock1.setId(1L);

        Stock stock2 = new Stock(2L, 30);
        stock2.setId(2L);

        when(stockRepository.findAll()).thenReturn(List.of(stock1, stock2));

        List<StockDto> resultado = inventarioService.listarStocks();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getProductoId()).isEqualTo(1L);
        assertThat(resultado.get(0).getCantidad()).isEqualTo(10);
        assertThat(resultado.get(1).getProductoId()).isEqualTo(2L);
        assertThat(resultado.get(1).getCantidad()).isEqualTo(30);

        verify(stockRepository).findAll();
    }

    @Test
    @DisplayName("Reponer stock - suma cantidad correctamente")
    void reponerStock_SumaCantidadCorrectamente() {
        Stock stock = new Stock(1L, 20);
        stock.setId(1L);

        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);

        StockDto resultado = inventarioService.reponerStock(1L, 15);

        assertThat(resultado.getCantidad()).isEqualTo(35);

        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Reponer stock - lanza excepción con cantidad cero")
    void reponerStock_CantidadCero_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.reponerStock(1L, 0)
        );

        assertThat(exception.getMessage()).contains("mayor que cero");

        verify(stockRepository, never()).findByProductoId(anyLong());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Reponer stock - lanza excepción si no existe stock")
    void reponerStock_NoExisteStock_LanzaExcepcion() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.reponerStock(1L, 5)
        );

        assertThat(exception.getMessage()).contains("No existe stock");

        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Reservar stock - descuenta cantidad correctamente")
    void reservarStock_DescuentaCantidadCorrectamente() {
        Stock stock = new Stock(1L, 50);
        stock.setId(1L);

        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);

        StockDto resultado = inventarioService.reservarStock(1L, 20);

        assertThat(resultado.getCantidad()).isEqualTo(30);

        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Reservar stock - lanza excepción con cantidad cero")
    void reservarStock_CantidadCero_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.reservarStock(1L, 0)
        );

        assertThat(exception.getMessage()).contains("mayor que cero");

        verify(stockRepository, never()).findByProductoId(anyLong());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Reservar stock - lanza excepción si no existe stock")
    void reservarStock_NoExisteStock_LanzaExcepcion() {
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.reservarStock(1L, 5)
        );

        assertThat(exception.getMessage()).contains("No existe stock");

        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Reservar stock - lanza excepción si stock es insuficiente")
    void reservarStock_StockInsuficiente_LanzaExcepcion() {
        Stock stock = new Stock(1L, 3);
        stock.setId(1L);

        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stock));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.reservarStock(1L, 10)
        );

        assertThat(exception.getMessage()).contains("Stock insuficiente");

        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Actualizar stock - actualiza stock existente")
    void actualizarStock_ActualizaStockExistente() {
        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(1L);
        productoDto.setNombre("Producto prueba");

        Stock stock = new Stock(1L, 8);
        stock.setId(1L);

        when(productoClient.obtenerPorId(1L)).thenReturn(productoDto);
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.of(stock));
        when(stockRepository.save(stock)).thenReturn(stock);

        StockDto resultado = inventarioService.actualizarStock(1L, 25);

        assertThat(resultado.getCantidad()).isEqualTo(25);
        assertThat(resultado.getProductoId()).isEqualTo(1L);

        verify(productoClient).obtenerPorId(1L);
        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository).save(stock);
    }

    @Test
    @DisplayName("Actualizar stock - crea stock si no existe")
    void actualizarStock_CreaStockSiNoExiste() {
        ProductoDto productoDto = new ProductoDto();
        productoDto.setId(1L);
        productoDto.setNombre("Producto prueba");

        when(productoClient.obtenerPorId(1L)).thenReturn(productoDto);
        when(stockRepository.findByProductoId(1L)).thenReturn(Optional.empty());
        when(stockRepository.save(any(Stock.class))).thenAnswer(invocation -> {
            Stock stock = invocation.getArgument(0);
            stock.setId(50L);
            return stock;
        });

        StockDto resultado = inventarioService.actualizarStock(1L, 40);

        assertThat(resultado.getId()).isEqualTo(50L);
        assertThat(resultado.getProductoId()).isEqualTo(1L);
        assertThat(resultado.getCantidad()).isEqualTo(40);

        verify(productoClient).obtenerPorId(1L);
        verify(stockRepository).findByProductoId(1L);
        verify(stockRepository).save(any(Stock.class));
    }

    @Test
    @DisplayName("Actualizar stock - lanza excepción con cantidad negativa")
    void actualizarStock_CantidadNegativa_LanzaExcepcion() {
        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.actualizarStock(1L, -1)
        );

        assertThat(exception.getMessage()).contains("mayor o igual a cero");

        verify(productoClient, never()).obtenerPorId(anyLong());
        verify(stockRepository, never()).findByProductoId(anyLong());
        verify(stockRepository, never()).save(any(Stock.class));
    }

    @Test
    @DisplayName("Actualizar stock - lanza excepción cuando producto no existe")
    void actualizarStock_ProductoNoExiste_LanzaExcepcion() {
        when(productoClient.obtenerPorId(99L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> inventarioService.actualizarStock(99L, 20)
        );

        assertThat(exception.getMessage()).contains("Producto no existe");

        verify(productoClient).obtenerPorId(99L);
        verify(stockRepository, never()).findByProductoId(anyLong());
        verify(stockRepository, never()).save(any(Stock.class));
    }
}