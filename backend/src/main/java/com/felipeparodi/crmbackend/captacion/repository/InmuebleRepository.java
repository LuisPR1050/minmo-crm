package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface InmuebleRepository extends JpaRepository<Inmueble, UUID> {
    boolean existsByPropietarioId(UUID propietarioId);
}