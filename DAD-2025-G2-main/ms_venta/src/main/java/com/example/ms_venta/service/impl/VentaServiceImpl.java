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
import com.example.ms_venta.service.VentaService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class VentaServiceImpl implements VentaService {

    private final VentaRepository ventaRepository;
    private final ItemVentaRepository itemVentaRepository;
    private final ClienteClient clienteClient;
    private final ProductoClient productoClient;
    private final InventarioClient inventarioClient;

    public VentaServiceImpl(VentaRepository ventaRepository,
                            ItemVentaRepository itemVentaRepository,
                            ClienteClient clienteClient,
                            ProductoClient productoClient,
                            InventarioClient inventarioClient) {
        this.ventaRepository = ventaRepository;
        this.itemVentaRepository = itemVentaRepository;
        this.clienteClient = clienteClient;
        this.productoClient = productoClient;
        this.inventarioClient = inventarioClient;
    }

    @Override
    public VentaDto crearVenta(VentaDto ventaDto) {
        // 1) Validar cliente existe
        ClienteDto cliente = clienteClient.obtenerPorId(ventaDto.getClienteId());
        if (cliente == null) {
            throw new IllegalArgumentException("Cliente no existe con id: " + ventaDto.getClienteId());
        }

        // 2) Calcular total y reservar stock para cada ítem
        double total = 0.0;
        for (ItemVentaDto itemDto : ventaDto.getItems()) {
            // 2.1) Validar producto existe y obtener precio de venta
            ProductoDto producto = productoClient.obtenerPorId(itemDto.getProductoId());
            if (producto == null) {
                throw new IllegalArgumentException("Producto no existe con id: " + itemDto.getProductoId());
            }
            double precioUnitario = producto.getPrecioVenta();
            total += precioUnitario * itemDto.getCantidad();

            // 2.2) Reservar stock en ms_inventario
            StockDto stockActualizado = inventarioClient.reservarStock(
                    itemDto.getProductoId(),
                    itemDto.getCantidad()
            );
            // (Opcional) log de stock restante
            // System.out.println("Stock reservado. Queda: " + stockActualizado.getCantidad());
        }

        // 3) Crear y guardar la entidad Venta
        Venta venta = new Venta();
        venta.setClienteId(ventaDto.getClienteId());
        venta.setFecha(LocalDateTime.now());
        venta.setTotal(total);
        Venta ventaGuardada = ventaRepository.save(venta);

        // 4) Crear y guardar cada ItemVenta asociado
        List<ItemVenta> items = ventaDto.getItems().stream().map(itemDto -> {
            ProductoDto prod = productoClient.obtenerPorId(itemDto.getProductoId());
            ItemVenta item = new ItemVenta();
            item.setProductoId(itemDto.getProductoId());
            item.setCantidad(itemDto.getCantidad());
            item.setPrecioUnitario(prod.getPrecioVenta());
            item.setVenta(ventaGuardada);
            return item;
        }).collect(Collectors.toList());

        itemVentaRepository.saveAll(items);
        ventaGuardada.setItems(items);

        // 5) Mapear a DTO y devolver
        return mapToDto(ventaGuardada);
    }

    @Override
    public VentaDto obtenerVenta(Long id) {
        Venta venta = ventaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Venta no encontrada con id: " + id));
        return mapToDto(venta);
    }

    @Override
    public List<VentaDto> listarVentas() {
        return ventaRepository.findAll().stream()
                .map(this::mapToDto)
                .collect(Collectors.toList());
    }

    private VentaDto mapToDto(Venta venta) {
        // Obtener nombre de cliente
        ClienteDto cliente = clienteClient.obtenerPorId(venta.getClienteId());
        String nombreCliente = (cliente != null) ? cliente.getRazonSocialONombre() : "N/A";

        // Mapear cada ítem con nombre de producto
        List<ItemVentaDto> itemsDto = venta.getItems().stream().map(item -> {
            ProductoDto producto = productoClient.obtenerPorId(item.getProductoId());
            String nombreProd = (producto != null) ? producto.getNombre() : "N/A";

            ItemVentaDto itemDto = new ItemVentaDto();
            itemDto.setProductoId(item.getProductoId());
            itemDto.setCantidad(item.getCantidad());
            itemDto.setProductoNombre(nombreProd);
            return itemDto;
        }).collect(Collectors.toList());

        VentaDto dto = new VentaDto();
        dto.setId(venta.getId());
        dto.setClienteId(venta.getClienteId());
        dto.setClienteNombre(nombreCliente);
        dto.setFecha(venta.getFecha());
        dto.setTotal(venta.getTotal());
        dto.setItems(itemsDto);

        return dto;
    }
}
