package com.felipeparodi.crmbackend.captacion.entity;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "propietario")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Propietario {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;

    private String telefonoPrincipal;

    private String whatsapp;

    private String correoElectronico;

    private String tipoContacto;

    private String tipoIncentivo;

    private BigDecimal valorIncentivo;

    private String referenciadorNombre;

    private String referenciadorTelefono;

    private LocalDateTime createdAt;
    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}