package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.CopiaVideoCanal;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface CopiaVideoCanalRepository extends JpaRepository<CopiaVideoCanal, UUID> {

    List<CopiaVideoCanal> findBySolicitudVideoId(UUID solicitudVideoId);

    void deleteBySolicitudVideoId(UUID solicitudVideoId);
}
