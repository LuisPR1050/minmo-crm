package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretajeAutorizacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContratoCorretajeAutorizacionRepository extends JpaRepository<ContratoCorretajeAutorizacion, UUID> {

    List<ContratoCorretajeAutorizacion> findByContratoCorretajeId(UUID contratoCorretajeId);
}
