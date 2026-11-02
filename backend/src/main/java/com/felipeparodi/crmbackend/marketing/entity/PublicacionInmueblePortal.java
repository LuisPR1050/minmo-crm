package com.felipeparodi.crmbackend.marketing.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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
@Table(name = "publicacion_inmueble_portal")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionInmueblePortal {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "publicacion_inmueble_id", nullable = false)
    private PublicacionInmueble publicacionInmueble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "portal_inmobiliario_id", nullable = false)
    private PortalInmobiliario portalInmobiliario;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacionPortal estado;
}
