package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.HistorialContratoCorretaje;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface HistorialContratoCorretajeRepository extends JpaRepository<HistorialContratoCorretaje, UUID> {

    List<HistorialContratoCorretaje> findByContratoCorretajeId(UUID contratoCorretajeId);

    List<HistorialContratoCorretaje> findByContratoCorretajeIdOrderByFechaDesc(UUID contratoCorretajeId);
}
