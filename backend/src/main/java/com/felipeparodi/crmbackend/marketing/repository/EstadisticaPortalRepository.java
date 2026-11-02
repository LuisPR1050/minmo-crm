package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.EstadisticaPortal;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface EstadisticaPortalRepository extends JpaRepository<EstadisticaPortal, UUID> {

    List<EstadisticaPortal> findByPublicacionInmueblePortalId(UUID publicacionInmueblePortalId);

    @Query("SELECT COALESCE(SUM(e.contactos), 0) FROM EstadisticaPortal e " +
            "WHERE e.publicacionInmueblePortal.publicacionInmueble.id = :publicacionInmuebleId")
    long sumContactosPorPublicacion(@Param("publicacionInmuebleId") UUID publicacionInmuebleId);

    @Query("SELECT COALESCE(SUM(e.vistas), 0) FROM EstadisticaPortal e " +
            "WHERE e.publicacionInmueblePortal.publicacionInmueble.id = :publicacionInmuebleId")
    long sumVistasPorPublicacion(@Param("publicacionInmuebleId") UUID publicacionInmuebleId);
}
