package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.CondicionCaptacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface CondicionCaptacionRepository extends JpaRepository<CondicionCaptacion, UUID> {

    Optional<CondicionCaptacion> findByInmuebleId(UUID inmuebleId);

    boolean existsByAsesorId(UUID asesorId);
}
