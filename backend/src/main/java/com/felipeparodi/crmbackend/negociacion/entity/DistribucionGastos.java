package com.felipeparodi.crmbackend.negociacion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "distribucion_gastos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DistribucionGastos {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negociacion_id", nullable = false, unique = true)
    private Negociacion negociacion;

    @Enumerated(EnumType.STRING)
    private QuienPaga gastosEscrituracion;

    @Enumerated(EnumType.STRING)
    private QuienPaga retencionFuente;

    @Enumerated(EnumType.STRING)
    private QuienPaga gastosRegistro;

    @Enumerated(EnumType.STRING)
    private QuienPaga gastosCancelacionGravamen;

    @Enumerated(EnumType.STRING)
    private QuienPaga gastosConstitucionGravamen;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}