package com.felipeparodi.crmbackend.negociacion.entity;

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
@Table(name = "negociacion_condicion_especial")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NegociacionCondicionEspecial {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negociacion_id", nullable = false)
    private Negociacion negociacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "condicion_especial_id", nullable = false)
    private CondicionEspecial condicionEspecial;
}