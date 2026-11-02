package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.VideoFuenteSolicitud;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface VideoFuenteSolicitudRepository extends JpaRepository<VideoFuenteSolicitud, UUID> {

    List<VideoFuenteSolicitud> findBySolicitudVideoId(UUID solicitudVideoId);
}
