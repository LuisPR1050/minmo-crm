package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.PromesaCompraventa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface PromesaCompraventaRepository extends JpaRepository<PromesaCompraventa, UUID> {

    Optional<PromesaCompraventa> findByNegociacionId(UUID negociacionId);
}
