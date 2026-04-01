package com.example.ms_cliente.repository;
import com.example.ms_cliente.entity.Cliente;
import feign.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    @Query("SELECT COUNT(c) > 0 FROM Cliente c WHERE c.dniOrRuc = :numero")
    boolean existsByDniOrRuc(@Param("numero") String numero);
}