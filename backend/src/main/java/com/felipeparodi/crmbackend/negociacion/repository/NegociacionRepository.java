package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface NegociacionRepository extends JpaRepository<Negociacion, UUID> {
    boolean existsByInmuebleId(UUID inmuebleId);
    boolean existsByClienteId(UUID clienteId);
    boolean existsByAsesorId(UUID asesorId);
}