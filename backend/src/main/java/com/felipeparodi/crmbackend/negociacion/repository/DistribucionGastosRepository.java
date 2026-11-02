package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.DistribucionGastos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface DistribucionGastosRepository extends JpaRepository<DistribucionGastos, UUID> {

    Optional<DistribucionGastos> findByNegociacionId(UUID negociacionId);
}