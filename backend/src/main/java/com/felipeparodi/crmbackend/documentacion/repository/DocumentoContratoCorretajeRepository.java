package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.DocumentoContratoCorretaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface DocumentoContratoCorretajeRepository extends JpaRepository<DocumentoContratoCorretaje, UUID> {

    List<DocumentoContratoCorretaje> findByContratoCorretajeId(UUID contratoCorretajeId);
}
