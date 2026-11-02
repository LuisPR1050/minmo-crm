package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OtrosiPromesaRepository extends JpaRepository<OtrosiPromesa, UUID> {

    List<OtrosiPromesa> findByPromesaCompraventaId(UUID promesaCompraventaId);
}
