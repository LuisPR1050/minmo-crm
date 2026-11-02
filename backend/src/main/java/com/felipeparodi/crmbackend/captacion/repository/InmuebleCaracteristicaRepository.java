package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.InmuebleCaracteristica;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface InmuebleCaracteristicaRepository extends JpaRepository<InmuebleCaracteristica, UUID> {

    List<InmuebleCaracteristica> findByInmuebleId(UUID inmuebleId);
}