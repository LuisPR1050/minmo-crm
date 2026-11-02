package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.BienAsociado;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface BienAsociadoRepository extends JpaRepository<BienAsociado, UUID> {

    List<BienAsociado> findByNegociacionId(UUID negociacionId);
}