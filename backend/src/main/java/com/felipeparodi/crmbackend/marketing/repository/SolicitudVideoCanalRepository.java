package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoCanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitudVideoCanalRepository extends JpaRepository<SolicitudVideoCanal, UUID> {

    List<SolicitudVideoCanal> findBySolicitudVideoId(UUID solicitudVideoId);
}
