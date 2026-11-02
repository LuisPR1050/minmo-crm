package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.HistorialCaptacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialCaptacionRepository extends JpaRepository<HistorialCaptacion, UUID> {

    List<HistorialCaptacion> findByInmuebleIdOrderByFechaDesc(UUID inmuebleId);
}
