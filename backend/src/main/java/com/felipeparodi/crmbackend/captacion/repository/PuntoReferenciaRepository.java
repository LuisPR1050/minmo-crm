package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.PuntoReferencia;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PuntoReferenciaRepository extends JpaRepository<PuntoReferencia, UUID> {

    Optional<PuntoReferencia> findByNombreIgnoreCase(String nombre);
}
