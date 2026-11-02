package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoTipoPublico;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitudVideoTipoPublicoRepository extends JpaRepository<SolicitudVideoTipoPublico, UUID> {

    List<SolicitudVideoTipoPublico> findBySolicitudVideoId(UUID solicitudVideoId);
}
