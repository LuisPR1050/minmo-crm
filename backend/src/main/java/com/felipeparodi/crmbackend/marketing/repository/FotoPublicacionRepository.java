package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.FotoPublicacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FotoPublicacionRepository extends JpaRepository<FotoPublicacion, UUID> {

    List<FotoPublicacion> findByPublicacionInmuebleId(UUID publicacionInmuebleId);
}
