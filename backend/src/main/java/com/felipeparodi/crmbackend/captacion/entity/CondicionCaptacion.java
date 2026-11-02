package com.felipeparodi.crmbackend.captacion.entity;

import jakarta.persistence.Entity;
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

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "condicion_captacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CondicionCaptacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false, unique = true)
    private Inmueble inmueble;

    private UUID asesorId;

    private String origenPropiedad;

    private String aliadoNombre;

    private String aliadoTelefono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "aliado_id")
    private Aliado aliado;

    private String whatsappAsesor;

    private String tipoComision;

    private BigDecimal valorComision;

    private boolean exclusividad;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}