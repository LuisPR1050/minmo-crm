package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.RequerimientoCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface RequerimientoClienteRepository extends JpaRepository<RequerimientoCliente, UUID> {

    List<RequerimientoCliente> findByClienteId(UUID clienteId);
}
