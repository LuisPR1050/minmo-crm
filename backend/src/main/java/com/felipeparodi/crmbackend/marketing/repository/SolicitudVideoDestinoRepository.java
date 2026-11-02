package com.felipeparodi.crmbackend.marketing.repository;

import com.felipeparodi.crmbackend.marketing.entity.SolicitudVideoDestino;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface SolicitudVideoDestinoRepository extends JpaRepository<SolicitudVideoDestino, UUID> {

    List<SolicitudVideoDestino> findBySolicitudVideoId(UUID solicitudVideoId);
}
