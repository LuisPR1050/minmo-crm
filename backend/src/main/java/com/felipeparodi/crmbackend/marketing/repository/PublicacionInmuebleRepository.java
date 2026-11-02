package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.EstadoPublicacion;
import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueble;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface PublicacionInmuebleRepository extends JpaRepository<PublicacionInmueble, UUID> {

    List<PublicacionInmueble> findByInmuebleId(UUID inmuebleId);

    boolean existsByInmuebleId(UUID inmuebleId);

    List<PublicacionInmueble> findByEstado(EstadoPublicacion estado);

    List<PublicacionInmueble> findByEstadoAndAlertaSinContactosEnviadaFalseAndFechaPublicacionLessThanEqual(
            EstadoPublicacion estado, LocalDate fechaLimite);

    List<PublicacionInmueble> findByEstadoAndAlertaSinContactosEnviadaTrueAndFechaPublicacionLessThanEqual(
            EstadoPublicacion estado, LocalDate fechaLimite);
}
