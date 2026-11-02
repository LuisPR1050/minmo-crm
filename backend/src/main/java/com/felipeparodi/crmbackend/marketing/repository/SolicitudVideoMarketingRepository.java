package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoMarketing;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitudVideoMarketingRepository extends JpaRepository<SolicitudVideoMarketing, UUID> {

    List<SolicitudVideoMarketing> findByInmuebleId(UUID inmuebleId);

    boolean existsByInmuebleId(UUID inmuebleId);
}
