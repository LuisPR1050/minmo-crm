package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.PublicacionInmueblePortal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PublicacionInmueblePortalRepository extends JpaRepository<PublicacionInmueblePortal, UUID> {

    List<PublicacionInmueblePortal> findByPublicacionInmuebleId(UUID publicacionInmuebleId);
}
