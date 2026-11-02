package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.HistorialPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialPublicacionRepository extends JpaRepository<HistorialPublicacion, UUID> {

    List<HistorialPublicacion> findByPublicacionInmuebleId(UUID publicacionInmuebleId);

    List<HistorialPublicacion> findByPublicacionInmuebleIdOrderByFechaDesc(UUID publicacionInmuebleId);
}
