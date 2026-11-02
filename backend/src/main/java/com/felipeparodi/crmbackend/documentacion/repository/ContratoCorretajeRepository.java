package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretaje;
import com.felipeparodi.crmbackend.documentacion.entity.EstadoDocumento;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface ContratoCorretajeRepository extends JpaRepository<ContratoCorretaje, UUID> {

    List<ContratoCorretaje> findByInmuebleIdOrderByCreatedAtDesc(UUID inmuebleId);

    boolean existsByInmuebleId(UUID inmuebleId);

    List<ContratoCorretaje> findByEstado(EstadoDocumento estado);

    List<ContratoCorretaje> findByEstadoAndAlertaVencimientoEnviadaFalseAndFechaVigenciaHastaLessThanEqual(
            EstadoDocumento estado, LocalDate fechaLimite);

    List<ContratoCorretaje> findByEstadoAndFechaVigenciaHastaLessThan(EstadoDocumento estado, LocalDate fecha);
}
