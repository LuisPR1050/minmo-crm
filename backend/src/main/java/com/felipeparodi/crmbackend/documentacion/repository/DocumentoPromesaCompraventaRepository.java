package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.DocumentoPromesaCompraventa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoPromesaCompraventaRepository extends JpaRepository<DocumentoPromesaCompraventa, UUID> {

    List<DocumentoPromesaCompraventa> findByPromesaCompraventaId(UUID promesaCompraventaId);
}
