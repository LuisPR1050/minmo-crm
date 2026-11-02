package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.NegociacionItemEntrega;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface NegociacionItemEntregaRepository extends JpaRepository<NegociacionItemEntrega, UUID> {

    List<NegociacionItemEntrega> findByNegociacionId(UUID negociacionId);

    Optional<NegociacionItemEntrega> findByNegociacionIdAndItemEntregaId(UUID negociacionId, UUID itemEntregaId);
}