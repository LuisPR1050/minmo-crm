package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.PlanDePago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PlanDePagoRepository extends JpaRepository<PlanDePago, UUID> {

    List<PlanDePago> findByNegociacionId(UUID negociacionId);

    List<PlanDePago> findByPagadaFalseAndAlertaPagoTardioEnviadaFalseAndFechaEsperadaLessThan(LocalDate fecha);
}