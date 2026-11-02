package com.felipeparodi.crmbackend.captacion.repository;

import com.felipeparodi.crmbackend.captacion.entity.ChecklistJuridico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ChecklistJuridicoRepository extends JpaRepository<ChecklistJuridico, UUID> {

    Optional<ChecklistJuridico> findByInmuebleId(UUID inmuebleId);
}
