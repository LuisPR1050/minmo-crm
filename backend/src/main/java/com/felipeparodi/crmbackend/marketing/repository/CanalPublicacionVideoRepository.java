package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.CanalPublicacionVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface CanalPublicacionVideoRepository extends JpaRepository<CanalPublicacionVideo, UUID> {
}
