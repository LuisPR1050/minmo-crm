package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaCondicion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActaEntregaCondicionRepository extends JpaRepository<ActaEntregaCondicion, UUID> {

    List<ActaEntregaCondicion> findByActaEntregaId(UUID actaEntregaId);
}
