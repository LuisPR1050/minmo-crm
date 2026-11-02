package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.HistorialNegociacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialNegociacionRepository extends JpaRepository<HistorialNegociacion, UUID> {

    List<HistorialNegociacion> findByNegociacionId(UUID negociacionId);

    List<HistorialNegociacion> findByNegociacionIdOrderByFechaDesc(UUID negociacionId);
}