package com.example.ms_venta.service.impl;

import com.example.ms_venta.dto.ClienteDto;
import com.example.ms_venta.dto.ItemVentaDto;
import com.example.ms_venta.dto.ProductoDto;
import com.example.ms_venta.dto.StockDto;
import com.example.ms_venta.dto.VentaDto;
import com.example.ms_venta.entity.ItemVenta;
import com.example.ms_venta.entity.Venta;
import com.example.ms_venta.feign.ClienteClient;
import com.example.ms_venta.feign.InventarioClient;
import com.example.ms_venta.feign.ProductoClient;
import com.example.ms_venta.repository.ItemVentaRepository;
import com.example.ms_venta.repository.VentaRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class VentaServiceImplTest {

    @Mock
    private VentaRepository ventaRepository;

    @Mock
    private ItemVentaRepository itemVentaRepository;

    @Mock
    private ClienteClient clienteClient;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioClient inventarioClient;

    @InjectMocks
    private VentaServiceImpl ventaService;

    @Test
    @DisplayName("Crear venta - guarda venta correctamente")
    void crearVenta_GuardaCorrectamente() {
        VentaDto entrada = crearVentaDtoEntrada();

        ClienteDto cliente = crearClienteDto();
        ProductoDto producto = crearProductoDto(1L, "Mouse", 25.0);

        StockDto stockDto = new StockDto();
        stockDto.setId(1L);
        stockDto.setProductoId(1L);
        stockDto.setCantidad(90);

        when(clienteClient.obtenerPorId(10L)).thenReturn(cliente);
        when(productoClient.obtenerPorId(1L)).thenReturn(producto);
        when(inventarioClient.reservarStock(1L, 2)).thenReturn(stockDto);

        when(ventaRepository.save(any(Venta.class))).thenAnswer(invocation -> {
            Venta venta = invocation.getArgument(0);
            venta.setId(100L);
            return venta;
        });

        when(itemVentaRepository.saveAll(anyList())).thenAnswer(invocation -> invocation.getArgument(0));

        VentaDto resultado = ventaService.crearVenta(entrada);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(100L);
        assertThat(resultado.getClienteId()).isEqualTo(10L);
        assertThat(resultado.getClienteNombre()).isEqualTo("Cliente Prueba");
        assertThat(resultado.getTotal()).isEqualTo(50.0);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getProductoId()).isEqualTo(1L);
        assertThat(resultado.getItems().get(0).getCantidad()).isEqualTo(2);
        assertThat(resultado.getItems().get(0).getProductoNombre()).isEqualTo("Mouse");

        verify(clienteClient, atLeastOnce()).obtenerPorId(10L);
        verify(productoClient, atLeastOnce()).obtenerPorId(1L);
        verify(inventarioClient).reservarStock(1L, 2);
        verify(ventaRepository).save(any(Venta.class));
        verify(itemVentaRepository).saveAll(anyList());
    }

    @Test
    @DisplayName("Crear venta - lanza excepción si cliente no existe")
    void crearVenta_ClienteNoExiste_LanzaExcepcion() {
        VentaDto entrada = crearVentaDtoEntrada();

        when(clienteClient.obtenerPorId(10L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.crearVenta(entrada)
        );

        assertThat(exception.getMessage()).contains("Cliente no existe");

        verify(clienteClient).obtenerPorId(10L);
        verify(productoClient, never()).obtenerPorId(anyLong());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Crear venta - lanza excepción si producto no existe")
    void crearVenta_ProductoNoExiste_LanzaExcepcion() {
        VentaDto entrada = crearVentaDtoEntrada();

        when(clienteClient.obtenerPorId(10L)).thenReturn(crearClienteDto());
        when(productoClient.obtenerPorId(1L)).thenReturn(null);

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.crearVenta(entrada)
        );

        assertThat(exception.getMessage()).contains("Producto no existe");

        verify(clienteClient).obtenerPorId(10L);
        verify(productoClient).obtenerPorId(1L);
        verify(inventarioClient, never()).reservarStock(anyLong(), anyInt());
        verify(ventaRepository, never()).save(any(Venta.class));
    }

    @Test
    @DisplayName("Obtener venta - retorna venta existente")
    void obtenerVenta_RetornaVentaExistente() {
        Venta venta = crearVentaEntidad();

        when(ventaRepository.findById(100L)).thenReturn(Optional.of(venta));
        when(clienteClient.obtenerPorId(10L)).thenReturn(crearClienteDto());
        when(productoClient.obtenerPorId(1L)).thenReturn(crearProductoDto(1L, "Mouse", 25.0));

        VentaDto resultado = ventaService.obtenerVenta(100L);

        assertThat(resultado.getId()).isEqualTo(100L);
        assertThat(resultado.getClienteId()).isEqualTo(10L);
        assertThat(resultado.getClienteNombre()).isEqualTo("Cliente Prueba");
        assertThat(resultado.getTotal()).isEqualTo(50.0);
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getProductoNombre()).isEqualTo("Mouse");

        verify(ventaRepository).findById(100L);
    }

    @Test
    @DisplayName("Obtener venta - lanza excepción si no existe")
    void obtenerVenta_NoExiste_LanzaExcepcion() {
        when(ventaRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> ventaService.obtenerVenta(99L)
        );

        assertThat(exception.getMessage()).contains("Venta no encontrada");

        verify(ventaRepository).findById(99L);
        verify(clienteClient, never()).obtenerPorId(anyLong());
    }

    @Test
    @DisplayName("Listar ventas - retorna lista")
    void listarVentas_RetornaLista() {
        Venta venta = crearVentaEntidad();

        when(ventaRepository.findAll()).thenReturn(List.of(venta));
        when(clienteClient.obtenerPorId(10L)).thenReturn(crearClienteDto());
        when(productoClient.obtenerPorId(1L)).thenReturn(crearProductoDto(1L, "Mouse", 25.0));

        List<VentaDto> resultado = ventaService.listarVentas();

        assertThat(resultado).hasSize(1);
        assertThat(resultado.get(0).getId()).isEqualTo(100L);
        assertThat(resultado.get(0).getClienteNombre()).isEqualTo("Cliente Prueba");
        assertThat(resultado.get(0).getItems()).hasSize(1);

        verify(ventaRepository).findAll();
    }

    @Test
    @DisplayName("Mapeo de venta - usa N/A cuando cliente o producto son nulos")
    void obtenerVenta_ClienteYProductoNulos_RetornaNA() {
        Venta venta = crearVentaEntidad();

        when(ventaRepository.findById(100L)).thenReturn(Optional.of(venta));
        when(clienteClient.obtenerPorId(10L)).thenReturn(null);
        when(productoClient.obtenerPorId(1L)).thenReturn(null);

        VentaDto resultado = ventaService.obtenerVenta(100L);

        assertThat(resultado.getClienteNombre()).isEqualTo("N/A");
        assertThat(resultado.getItems()).hasSize(1);
        assertThat(resultado.getItems().get(0).getProductoNombre()).isEqualTo("N/A");

        verify(ventaRepository).findById(100L);
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

    private ClienteDto crearClienteDto() {
        ClienteDto cliente = new ClienteDto();
        cliente.setId(10L);
        cliente.setRazonSocialONombre("Cliente Prueba");
        return cliente;
    }

    private ProductoDto crearProductoDto(Long id, String nombre, Double precioVenta) {
        ProductoDto producto = new ProductoDto();
        producto.setId(id);
        producto.setNombre(nombre);
        producto.setPrecioVenta(precioVenta);
        return producto;
    }

    private Venta crearVentaEntidad() {
        Venta venta = new Venta();
        venta.setId(100L);
        venta.setClienteId(10L);
        venta.setFecha(LocalDateTime.of(2026, 4, 24, 10, 30));
        venta.setTotal(50.0);

        ItemVenta item = new ItemVenta();
        item.setId(1L);
        item.setProductoId(1L);
        item.setCantidad(2);
        item.setPrecioUnitario(25.0);
        item.setVenta(venta);

        venta.setItems(List.of(item));

        return venta;
    }
}