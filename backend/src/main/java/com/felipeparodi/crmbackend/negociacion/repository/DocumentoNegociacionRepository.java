package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.DocumentoNegociacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoNegociacionRepository extends JpaRepository<DocumentoNegociacion, UUID> {

    List<DocumentoNegociacion> findByNegociacionId(UUID negociacionId);
}