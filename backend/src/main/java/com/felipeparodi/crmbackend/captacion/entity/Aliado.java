package com.felipeparodi.crmbackend.captacion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Inmobiliaria o asesor externo (no es un Usuario del sistema) que puede traer una propiedad en
 * calidad de aliado. Catalogo liviano: se puede elegir uno existente o crear uno nuevo al vuelo
 * desde "Condiciones de captacion" cuando el origen de la propiedad es ALIADO.
 */
@Entity
@Table(name = "aliado")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Aliado {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;

    private String telefono;

    private String tipo;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}
