package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ContratoCorretajeClausula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ContratoCorretajeClausulaRepository extends JpaRepository<ContratoCorretajeClausula, UUID> {

    List<ContratoCorretajeClausula> findByContratoCorretajeId(UUID contratoCorretajeId);
}
