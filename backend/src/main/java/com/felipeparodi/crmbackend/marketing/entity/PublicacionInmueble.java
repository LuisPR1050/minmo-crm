package com.felipeparodi.crmbackend.marketing.entity;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "publicacion_inmueble")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PublicacionInmueble {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    @Enumerated(EnumType.STRING)
    private TipoOperacionPublicacion tipoOperacion;

    private boolean mostrarPrecio;

    private boolean mostrarContacto;

    private boolean mostrarCorreo;

    private boolean comisionCompartida;

    private boolean destacada;

    private String descripcionCorta;

    private String descripcionLarga;

    private String descripcionOptimizada;

    @Enumerated(EnumType.STRING)
    private EstadoPublicacion estado;

    private LocalDate fechaPublicacion;

    private boolean alertaSinContactosEnviada;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoPublicacion.BORRADOR;
        }
    }
}
