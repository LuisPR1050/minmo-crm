package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaItemEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActaEntregaItemEntregaRepository extends JpaRepository<ActaEntregaItemEntrega, UUID> {

    List<ActaEntregaItemEntrega> findByActaEntregaId(UUID actaEntregaId);
}
