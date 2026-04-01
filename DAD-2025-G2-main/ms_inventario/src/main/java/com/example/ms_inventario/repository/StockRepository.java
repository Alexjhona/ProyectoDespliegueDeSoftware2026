// src/main/java/com/example/ms_inventario/repository/StockRepository.java
package com.example.ms_inventario.repository;

import com.example.ms_inventario.entity.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {
    Optional<Stock> findByProductoId(Long productoId);
    boolean existsByProductoId(Long productoId);
}
