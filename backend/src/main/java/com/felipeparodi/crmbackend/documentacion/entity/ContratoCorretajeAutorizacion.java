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
@Table(name = "contrato_corretaje_autorizacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoCorretajeAutorizacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "contrato_corretaje_id", nullable = false)
    private ContratoCorretaje contratoCorretaje;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "autorizacion_propietario_id", nullable = false)
    private AutorizacionPropietario autorizacionPropietario;

    private boolean autorizada;
}
