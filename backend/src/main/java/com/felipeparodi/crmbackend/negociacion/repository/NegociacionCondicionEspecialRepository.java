package com.felipeparodi.crmbackend.negociacion.repository;

import com.felipeparodi.crmbackend.negociacion.entity.NegociacionCondicionEspecial;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface NegociacionCondicionEspecialRepository extends JpaRepository<NegociacionCondicionEspecial, UUID> {

    List<NegociacionCondicionEspecial> findByNegociacionId(UUID negociacionId);
}