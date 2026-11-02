package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ActaEntregaPazYSalvo;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ActaEntregaPazYSalvoRepository extends JpaRepository<ActaEntregaPazYSalvo, UUID> {

    List<ActaEntregaPazYSalvo> findByActaEntregaId(UUID actaEntregaId);
}
