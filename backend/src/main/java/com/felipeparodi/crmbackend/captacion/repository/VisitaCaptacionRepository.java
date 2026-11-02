package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.VisitaCaptacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VisitaCaptacionRepository extends JpaRepository<VisitaCaptacion, UUID> {

    List<VisitaCaptacion> findByInmuebleId(UUID inmuebleId);

    boolean existsByAsesorId(UUID asesorId);
}