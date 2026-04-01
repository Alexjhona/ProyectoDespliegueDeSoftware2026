// src/main/java/com/example/ms_inventario/service/impl/InventarioServiceImpl.java
package com.example.ms_inventario.service.impl;

import com.example.ms_inventario.dto.StockDto;
import com.example.ms_inventario.dto.ProductoDto;
import com.example.ms_inventario.entity.Stock;
import com.example.ms_inventario.feign.ProductoClient;
import com.example.ms_inventario.repository.StockRepository;
import com.example.ms_inventario.service.InventarioService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class InventarioServiceImpl implements InventarioService {

    private final StockRepository stockRepository;
    private final ProductoClient productoClient;

    public InventarioServiceImpl(StockRepository stockRepository, ProductoClient productoClient) {
        this.stockRepository = stockRepository;
        this.productoClient = productoClient;
    }

    @Override
    public StockDto crearStockInicial(StockDto stockDto) {
        Long productoId = stockDto.getProductoId();
        // Validar que el producto exista en ms_producto
        ProductoDto producto = productoClient.obtenerPorId(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no existe con id: " + productoId);
        }
        if (stockRepository.existsByProductoId(productoId)) {
            throw new IllegalArgumentException("Ya existe stock para producto id: " + productoId);
        }
        Stock stock = new Stock(productoId, stockDto.getCantidad());
        Stock guardado = stockRepository.save(stock);
        return mapToDto(guardado);
    }

    @Override
    public StockDto obtenerStock(Long productoId) {
        Stock stock = stockRepository.findByProductoId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe stock para producto id: " + productoId));
        return mapToDto(stock);
    }

    @Override
    public List<StockDto> listarStocks() {
        return stockRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    @Override
    public StockDto reponerStock(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad a reponer debe ser mayor que cero");
        }
        Stock stock = stockRepository.findByProductoId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe stock para producto id: " + productoId));
        stock.setCantidad(stock.getCantidad() + cantidad);
        Stock actualizado = stockRepository.save(stock);
        return mapToDto(actualizado);
    }

    @Override
    public StockDto reservarStock(Long productoId, Integer cantidad) {
        if (cantidad <= 0) {
            throw new IllegalArgumentException("Cantidad a reservar debe ser mayor que cero");
        }
        Stock stock = stockRepository.findByProductoId(productoId)
                .orElseThrow(() -> new IllegalArgumentException("No existe stock para producto id: " + productoId));
        if (stock.getCantidad() < cantidad) {
            throw new IllegalArgumentException("Stock insuficiente para producto id: " + productoId);
        }
        stock.setCantidad(stock.getCantidad() - cantidad);
        Stock actualizado = stockRepository.save(stock);
        return mapToDto(actualizado);
    }

    @Override
    public StockDto actualizarStock(Long productoId, Integer cantidad) {
        if (cantidad < 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor o igual a cero");
        }
        // Validar producto
        ProductoDto producto = productoClient.obtenerPorId(productoId);
        if (producto == null) {
            throw new IllegalArgumentException("Producto no existe con id: " + productoId);
        }
        Stock stock = stockRepository.findByProductoId(productoId)
                .orElseGet(() -> new Stock(productoId, 0));
        stock.setCantidad(cantidad);
        Stock actualizado = stockRepository.save(stock);
        return mapToDto(actualizado);
    }

    private StockDto mapToDto(Stock stock) {
        StockDto dto = new StockDto();
        dto.setId(stock.getId());
        dto.setProductoId(stock.getProductoId());
        dto.setCantidad(stock.getCantidad());
        return dto;
    }
}
