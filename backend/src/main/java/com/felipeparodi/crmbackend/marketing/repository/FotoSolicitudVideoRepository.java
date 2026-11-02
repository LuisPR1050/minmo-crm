package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.FotoSolicitudVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface FotoSolicitudVideoRepository extends JpaRepository<FotoSolicitudVideo, UUID> {

    List<FotoSolicitudVideo> findBySolicitudVideoId(UUID solicitudVideoId);
}
