package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ClausulaPromesa;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ClausulaPromesaRepository extends JpaRepository<ClausulaPromesa, UUID> {
}
