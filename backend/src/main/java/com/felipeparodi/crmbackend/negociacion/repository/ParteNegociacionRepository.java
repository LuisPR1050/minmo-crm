package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.ParteNegociacion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ParteNegociacionRepository extends JpaRepository<ParteNegociacion, UUID> {

    List<ParteNegociacion> findByNegociacionId(UUID negociacionId);
}