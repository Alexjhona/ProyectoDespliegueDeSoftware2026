package com.example.ms_compra.service.impl;

import com.example.ms_compra.dto.CompraDto;
import com.example.ms_compra.dto.StockUpdateDto;
import com.example.ms_compra.entity.Compra;
import com.example.ms_compra.feign.InventarioClient;
import com.example.ms_compra.feign.ProductoClient;
import com.example.ms_compra.feign.ProveedorClient;
import com.example.ms_compra.repository.CompraRepository;
import com.example.ms_compra.service.CompraService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class CompraServiceImpl implements CompraService {

    private static final String COMPRA_NO_ENCONTRADA_MSG = "Compra no encontrada con id: ";

    private final CompraRepository compraRepository;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;
    private final ProveedorClient proveedorClient;

    @Override
    public CompraDto crearCompra(CompraDto compraDto) {
        try {
            proveedorClient.obtenerProveedor(compraDto.getProveedorId());
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Proveedor no encontrado con id: " + compraDto.getProveedorId(), e
            );
        }

        if (compraDto.getFechaCompra() == null) {
            compraDto.setFechaCompra(LocalDate.now());
        }

        Compra entidad = Compra.builder()
                .productoId(compraDto.getProductoId())
                .cantidad(compraDto.getCantidad())
                .precioCompra(compraDto.getPrecioCompra())
                .precioVenta(compraDto.getPrecioVenta())
                .proveedorId(compraDto.getProveedorId())
                .fechaCompra(compraDto.getFechaCompra())
                .build();

        Compra guardada = compraRepository.save(entidad);

        StockUpdateDto updateDto = new StockUpdateDto(guardada.getCantidad());
        inventarioClient.reponeStock(guardada.getProductoId(), updateDto);

        productoClient.actualizarPrecioVenta(guardada.getProductoId(), guardada.getPrecioVenta());

        return mapToDto(guardada);
    }

    @Override
    public CompraDto obtenerCompra(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(COMPRA_NO_ENCONTRADA_MSG + id));
        return mapToDto(compra);
    }

    @Override
    public List<CompraDto> listarCompras() {
        return compraRepository.findAll()
                .stream()
                .map(this::mapToDto)
                .toList();
    }

    @Override
    public CompraDto actualizarCompra(Long id, CompraDto compraDto) {
        Compra existente = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(COMPRA_NO_ENCONTRADA_MSG + id));

        int cantidadAnterior = existente.getCantidad();

        existente.setCantidad(compraDto.getCantidad());
        existente.setPrecioCompra(compraDto.getPrecioCompra());
        existente.setPrecioVenta(compraDto.getPrecioVenta());
        existente.setFechaCompra(
                compraDto.getFechaCompra() != null ? compraDto.getFechaCompra() : existente.getFechaCompra()
        );

        Compra actualizado = compraRepository.save(existente);

        int diff = actualizado.getCantidad() - cantidadAnterior;

        if (diff > 0) {
            StockUpdateDto dtoRep = new StockUpdateDto(diff);
            inventarioClient.reponeStock(actualizado.getProductoId(), dtoRep);
        } else if (diff < 0) {
            inventarioClient.reservarStock(actualizado.getProductoId(), Math.abs(diff));
        }

        productoClient.actualizarPrecioVenta(actualizado.getProductoId(), actualizado.getPrecioVenta());

        return mapToDto(actualizado);
    }

    @Override
    public void eliminarCompra(Long id) {
        Compra compra = compraRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException(COMPRA_NO_ENCONTRADA_MSG + id));

        inventarioClient.reservarStock(compra.getProductoId(), compra.getCantidad());

        compraRepository.deleteById(id);
    }

    private CompraDto mapToDto(Compra entidad) {
        return CompraDto.builder()
                .id(entidad.getId())
                .productoId(entidad.getProductoId())
                .cantidad(entidad.getCantidad())
                .precioCompra(entidad.getPrecioCompra())
                .precioVenta(entidad.getPrecioVenta())
                .proveedorId(entidad.getProveedorId())
                .fechaCompra(entidad.getFechaCompra())
                .build();
    }
}