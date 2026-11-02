package com.felipeparodi.crmbackend.documentacion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "promesa_compraventa_clausula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PromesaCompraventaClausula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promesa_compraventa_id", nullable = false)
    private PromesaCompraventa promesaCompraventa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clausula_promesa_id", nullable = false)
    private ClausulaPromesa clausulaPromesa;

    private boolean incluida;
}
