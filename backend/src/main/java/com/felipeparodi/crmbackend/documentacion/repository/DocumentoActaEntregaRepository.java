package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.DocumentoActaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoActaEntregaRepository extends JpaRepository<DocumentoActaEntrega, UUID> {

    List<DocumentoActaEntrega> findByActaEntregaId(UUID actaEntregaId);
}
