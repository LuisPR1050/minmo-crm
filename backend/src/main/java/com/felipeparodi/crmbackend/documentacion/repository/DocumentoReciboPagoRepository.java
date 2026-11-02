package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.DocumentoReciboPago;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoReciboPagoRepository extends JpaRepository<DocumentoReciboPago, UUID> {

    List<DocumentoReciboPago> findByReciboPagoId(UUID reciboPagoId);
}
