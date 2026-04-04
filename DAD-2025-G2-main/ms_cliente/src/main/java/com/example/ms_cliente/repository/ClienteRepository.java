package com.example.ms_cliente.repository;

import com.example.ms_cliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ClienteRepository extends JpaRepository<Cliente, Long> {
    boolean existsByDniOrRuc(String dniOrRuc);
}