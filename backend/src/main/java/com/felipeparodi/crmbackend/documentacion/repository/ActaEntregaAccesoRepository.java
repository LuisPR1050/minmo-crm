package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaAcceso;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActaEntregaAccesoRepository extends JpaRepository<ActaEntregaAcceso, UUID> {

    List<ActaEntregaAcceso> findByActaEntregaId(UUID actaEntregaId);
}
