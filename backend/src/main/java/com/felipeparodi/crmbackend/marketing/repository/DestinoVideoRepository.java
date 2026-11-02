package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.DestinoVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface DestinoVideoRepository extends JpaRepository<DestinoVideo, UUID> {
}
