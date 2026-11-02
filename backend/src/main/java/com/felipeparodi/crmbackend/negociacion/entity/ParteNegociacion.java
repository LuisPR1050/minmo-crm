package com.felipeparodi.crmbackend.negociacion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "parte_negociacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ParteNegociacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negociacion_id", nullable = false)
    private Negociacion negociacion;

    @Enumerated(EnumType.STRING)
    private RolParteNegociacion rol;

    private Short orden;

    private String tipoDocumento;

    private String numeroDocumento;

    private String estadoCivil;

    private String direccion;

    private String correoElectronico;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.orden == null) {
            this.orden = 1;
        }
    }
}