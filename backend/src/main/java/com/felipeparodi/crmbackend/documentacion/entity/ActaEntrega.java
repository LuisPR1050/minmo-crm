package com.felipeparodi.crmbackend.documentacion.entity;

import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "acta_entrega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActaEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numeroActa;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negociacion_id", nullable = false, unique = true)
    private Negociacion negociacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promesa_compraventa_id")
    private PromesaCompraventa promesaCompraventa;

    private LocalDate fechaElaboracion;

    private LocalDateTime fechaHoraEntrega;

    private String ciudad;

    private String lecturaEnergia;

    private String lecturaAgua;

    private String lecturaGas;

    private String danosExistentes;

    private String reparacionesPendientes;

    private String acuerdosPosteriores;

    private String compromisosAsumidos;

    @Enumerated(EnumType.STRING)
    private DeclaracionComprador declaracionComprador;

    private boolean vendedorEntregaConforme;

    private String observaciones;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}
