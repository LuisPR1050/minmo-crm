package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.OtrosiPromesaClausula;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface OtrosiPromesaClausulaRepository extends JpaRepository<OtrosiPromesaClausula, UUID> {

    List<OtrosiPromesaClausula> findByOtrosiPromesaId(UUID otrosiPromesaId);
}
