package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventaClausula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface PromesaCompraventaClausulaRepository extends JpaRepository<PromesaCompraventaClausula, UUID> {

    List<PromesaCompraventaClausula> findByPromesaCompraventaId(UUID promesaCompraventaId);
}
