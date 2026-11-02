package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClienteRepository extends JpaRepository<Cliente, UUID> {

    boolean existsByAsesorId(UUID asesorId);
}