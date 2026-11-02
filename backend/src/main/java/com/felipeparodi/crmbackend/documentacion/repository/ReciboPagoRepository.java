package com.felipeparodi.crmbackend.documentacion.repository;

import com.felipeparodi.crmbackend.documentacion.entity.ReciboPago;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

public interface ReciboPagoRepository extends JpaRepository<ReciboPago, UUID> {

    List<ReciboPago> findByNegociacionId(UUID negociacionId);

    boolean existsByPlanDePagoId(UUID planDePagoId);

    @Query("SELECT COALESCE(SUM(r.valorRecibido), 0) FROM ReciboPago r WHERE r.negociacion.id = :negociacionId")
    BigDecimal sumarValorRecibidoPorNegociacion(@Param("negociacionId") UUID negociacionId);
}
