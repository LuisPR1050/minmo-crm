package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ActaEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ActaEntregaRepository extends JpaRepository<ActaEntrega, UUID> {

    Optional<ActaEntrega> findByNegociacionId(UUID negociacionId);
}
