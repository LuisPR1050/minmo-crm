package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.ArchivoAdjunto;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ArchivoAdjuntoRepository extends JpaRepository<ArchivoAdjunto, UUID> {

    List<ArchivoAdjunto> findByInmuebleId(UUID inmuebleId);
}