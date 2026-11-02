package com.felipeparodi.crmbackend.atencioncliente.repository;

import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitaClienteRepository extends JpaRepository<VisitaCliente, UUID> {

    List<VisitaCliente> findByClienteId(UUID clienteId);

    List<VisitaCliente> findByInmuebleId(UUID inmuebleId);

    boolean existsByInmuebleId(UUID inmuebleId);
}