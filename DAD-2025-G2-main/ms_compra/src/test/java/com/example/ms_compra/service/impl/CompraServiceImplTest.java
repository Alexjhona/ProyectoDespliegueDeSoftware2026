package com.example.ms_compra.service.impl;

import com.example.ms_compra.dto.CompraDto;
import com.example.ms_compra.dto.ProveedorDto;
import com.example.ms_compra.entity.Compra;
import com.example.ms_compra.feign.InventarioClient;
import com.example.ms_compra.feign.ProductoClient;
import com.example.ms_compra.feign.ProveedorClient;
import com.example.ms_compra.repository.CompraRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CompraServiceImplTest {

    @Mock
    private CompraRepository compraRepository;

    @Mock
    private ProductoClient productoClient;

    @Mock
    private InventarioClient inventarioClient;

    @Mock
    private ProveedorClient proveedorClient;

    @InjectMocks
    private CompraServiceImpl compraService;

    @Test
    @DisplayName("Crear compra - guarda compra, repone stock y actualiza precio de venta")
    void crearCompra_CuandoProveedorExiste_GuardaCorrectamente() {
        CompraDto request = CompraDto.builder()
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(null)
                .build();

        ProveedorDto proveedorDto = ProveedorDto.builder()
                .id(5L)
                .dniOrRuc("20123456789")
                .razonSocialONombre("Proveedor Test")
                .direccion("Av. Peru")
                .telefono("987654321")
                .build();

        when(proveedorClient.obtenerProveedor(5L)).thenReturn(proveedorDto);
        when(compraRepository.save(any(Compra.class))).thenAnswer(invocation -> {
            Compra compra = invocation.getArgument(0);
            compra.setId(1L);
            return compra;
        });

        CompraDto resultado = compraService.crearCompra(request);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getProductoId()).isEqualTo(1L);
        assertThat(resultado.getCantidad()).isEqualTo(10);
        assertThat(resultado.getPrecioCompra()).isEqualTo(20.0);
        assertThat(resultado.getPrecioVenta()).isEqualTo(30.0);
        assertThat(resultado.getProveedorId()).isEqualTo(5L);
        assertThat(resultado.getFechaCompra()).isEqualTo(LocalDate.now());

        verify(proveedorClient).obtenerProveedor(5L);
        verify(compraRepository).save(any(Compra.class));
        verify(inventarioClient).reponeStock(eq(1L), argThat(dto -> dto.getCantidad().equals(10)));
        verify(productoClient).actualizarPrecioVenta(1L, 30.0);
    }

    @Test
    @DisplayName("Crear compra - lanza excepción si proveedor no existe")
    void crearCompra_CuandoProveedorNoExiste_LanzaExcepcion() {
        CompraDto request = CompraDto.builder()
                .productoId(1L)
                .cantidad(5)
                .precioCompra(15.0)
                .precioVenta(25.0)
                .proveedorId(99L)
                .build();

        when(proveedorClient.obtenerProveedor(99L)).thenThrow(new RuntimeException("Proveedor no encontrado"));

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> compraService.crearCompra(request)
        );

        assertThat(exception.getMessage()).contains("Proveedor no encontrado");

        verify(proveedorClient).obtenerProveedor(99L);
        verify(compraRepository, never()).save(any(Compra.class));
        verify(inventarioClient, never()).reponeStock(anyLong(), any());
        verify(productoClient, never()).actualizarPrecioVenta(anyLong(), anyDouble());
    }

    @Test
    @DisplayName("Crear compra - respeta fecha enviada")
    void crearCompra_ConFechaEnviada_MantieneFecha() {
        LocalDate fecha = LocalDate.of(2026, 4, 24);

        CompraDto request = CompraDto.builder()
                .productoId(2L)
                .cantidad(3)
                .precioCompra(50.0)
                .precioVenta(70.0)
                .proveedorId(6L)
                .fechaCompra(fecha)
                .build();

        ProveedorDto proveedorDto = ProveedorDto.builder()
                .id(6L)
                .dniOrRuc("12345678")
                .razonSocialONombre("Proveedor Fecha")
                .direccion("Jr. Lima")
                .telefono("900111222")
                .build();

        when(proveedorClient.obtenerProveedor(6L)).thenReturn(proveedorDto);
        when(compraRepository.save(any(Compra.class))).thenAnswer(invocation -> {
            Compra compra = invocation.getArgument(0);
            compra.setId(2L);
            return compra;
        });

        CompraDto resultado = compraService.crearCompra(request);

        assertThat(resultado.getId()).isEqualTo(2L);
        assertThat(resultado.getFechaCompra()).isEqualTo(fecha);

        verify(inventarioClient).reponeStock(eq(2L), argThat(dto -> dto.getCantidad().equals(3)));
        verify(productoClient).actualizarPrecioVenta(2L, 70.0);
    }

    @Test
    @DisplayName("Obtener compra - retorna compra existente")
    void obtenerCompra_CuandoExiste_RetornaCompra() {
        Compra compra = Compra.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));

        CompraDto resultado = compraService.obtenerCompra(1L);

        assertThat(resultado).isNotNull();
        assertThat(resultado.getId()).isEqualTo(1L);
        assertThat(resultado.getProductoId()).isEqualTo(1L);
        assertThat(resultado.getCantidad()).isEqualTo(10);
        assertThat(resultado.getPrecioVenta()).isEqualTo(30.0);

        verify(compraRepository).findById(1L);
    }

    @Test
    @DisplayName("Obtener compra - lanza excepción si no existe")
    void obtenerCompra_CuandoNoExiste_LanzaExcepcion() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> compraService.obtenerCompra(99L)
        );

        assertThat(exception.getMessage()).contains("Compra no encontrada");

        verify(compraRepository).findById(99L);
    }

    @Test
    @DisplayName("Listar compras - retorna lista")
    void listarCompras_RetornaLista() {
        Compra compra1 = Compra.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        Compra compra2 = Compra.builder()
                .id(2L)
                .productoId(2L)
                .cantidad(4)
                .precioCompra(12.0)
                .precioVenta(18.0)
                .proveedorId(6L)
                .fechaCompra(LocalDate.of(2026, 4, 25))
                .build();

        when(compraRepository.findAll()).thenReturn(List.of(compra1, compra2));

        List<CompraDto> resultado = compraService.listarCompras();

        assertThat(resultado).hasSize(2);
        assertThat(resultado.get(0).getId()).isEqualTo(1L);
        assertThat(resultado.get(1).getId()).isEqualTo(2L);
        assertThat(resultado.get(0).getCantidad()).isEqualTo(10);
        assertThat(resultado.get(1).getCantidad()).isEqualTo(4);

        verify(compraRepository).findAll();
    }

    @Test
    @DisplayName("Actualizar compra - si cantidad aumenta, repone diferencia de stock")
    void actualizarCompra_CuandoCantidadAumenta_ReponeStock() {
        Compra existente = Compra.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 20))
                .build();

        CompraDto request = CompraDto.builder()
                .cantidad(15)
                .precioCompra(22.0)
                .precioVenta(35.0)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        when(compraRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(compraRepository.save(any(Compra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompraDto resultado = compraService.actualizarCompra(1L, request);

        assertThat(resultado.getCantidad()).isEqualTo(15);
        assertThat(resultado.getPrecioCompra()).isEqualTo(22.0);
        assertThat(resultado.getPrecioVenta()).isEqualTo(35.0);
        assertThat(resultado.getFechaCompra()).isEqualTo(LocalDate.of(2026, 4, 24));

        verify(inventarioClient).reponeStock(eq(1L), argThat(dto -> dto.getCantidad().equals(5)));
        verify(inventarioClient, never()).reservarStock(anyLong(), anyInt());
        verify(productoClient).actualizarPrecioVenta(1L, 35.0);
    }

    @Test
    @DisplayName("Actualizar compra - si cantidad disminuye, reserva diferencia de stock")
    void actualizarCompra_CuandoCantidadDisminuye_ReservaStock() {
        Compra existente = Compra.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 20))
                .build();

        CompraDto request = CompraDto.builder()
                .cantidad(6)
                .precioCompra(21.0)
                .precioVenta(33.0)
                .fechaCompra(null)
                .build();

        when(compraRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(compraRepository.save(any(Compra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompraDto resultado = compraService.actualizarCompra(1L, request);

        assertThat(resultado.getCantidad()).isEqualTo(6);
        assertThat(resultado.getPrecioCompra()).isEqualTo(21.0);
        assertThat(resultado.getPrecioVenta()).isEqualTo(33.0);
        assertThat(resultado.getFechaCompra()).isEqualTo(LocalDate.of(2026, 4, 20));

        verify(inventarioClient).reservarStock(1L, 4);
        verify(inventarioClient, never()).reponeStock(anyLong(), any());
        verify(productoClient).actualizarPrecioVenta(1L, 33.0);
    }

    @Test
    @DisplayName("Actualizar compra - si cantidad no cambia, no ajusta stock")
    void actualizarCompra_CuandoCantidadNoCambia_NoAjustaStock() {
        Compra existente = Compra.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 20))
                .build();

        CompraDto request = CompraDto.builder()
                .cantidad(10)
                .precioCompra(23.0)
                .precioVenta(36.0)
                .fechaCompra(null)
                .build();

        when(compraRepository.findById(1L)).thenReturn(Optional.of(existente));
        when(compraRepository.save(any(Compra.class))).thenAnswer(invocation -> invocation.getArgument(0));

        CompraDto resultado = compraService.actualizarCompra(1L, request);

        assertThat(resultado.getCantidad()).isEqualTo(10);
        assertThat(resultado.getPrecioCompra()).isEqualTo(23.0);
        assertThat(resultado.getPrecioVenta()).isEqualTo(36.0);

        verify(inventarioClient, never()).reponeStock(anyLong(), any());
        verify(inventarioClient, never()).reservarStock(anyLong(), anyInt());
        verify(productoClient).actualizarPrecioVenta(1L, 36.0);
    }

    @Test
    @DisplayName("Actualizar compra - lanza excepción si no existe")
    void actualizarCompra_CuandoNoExiste_LanzaExcepcion() {
        CompraDto request = CompraDto.builder()
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .build();

        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> compraService.actualizarCompra(99L, request)
        );

        assertThat(exception.getMessage()).contains("Compra no encontrada");

        verify(compraRepository).findById(99L);
        verify(compraRepository, never()).save(any(Compra.class));
    }

    @Test
    @DisplayName("Eliminar compra - reserva stock y elimina")
    void eliminarCompra_CuandoExiste_ReservaStockYElimina() {
        Compra compra = Compra.builder()
                .id(1L)
                .productoId(1L)
                .cantidad(10)
                .precioCompra(20.0)
                .precioVenta(30.0)
                .proveedorId(5L)
                .fechaCompra(LocalDate.of(2026, 4, 24))
                .build();

        when(compraRepository.findById(1L)).thenReturn(Optional.of(compra));
        doNothing().when(compraRepository).deleteById(1L);

        compraService.eliminarCompra(1L);

        verify(compraRepository).findById(1L);
        verify(inventarioClient).reservarStock(1L, 10);
        verify(compraRepository).deleteById(1L);
    }

    @Test
    @DisplayName("Eliminar compra - lanza excepción si no existe")
    void eliminarCompra_CuandoNoExiste_LanzaExcepcion() {
        when(compraRepository.findById(99L)).thenReturn(Optional.empty());

        IllegalArgumentException exception = assertThrows(
                IllegalArgumentException.class,
                () -> compraService.eliminarCompra(99L)
        );

        assertThat(exception.getMessage()).contains("Compra no encontrada");

        verify(compraRepository).findById(99L);
        verify(inventarioClient, never()).reservarStock(anyLong(), anyInt());
        verify(compraRepository, never()).deleteById(anyLong());
    }
}