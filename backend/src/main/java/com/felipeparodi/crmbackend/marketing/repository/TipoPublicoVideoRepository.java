package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.TipoPublicoVideo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface TipoPublicoVideoRepository extends JpaRepository<TipoPublicoVideo, UUID> {
}
