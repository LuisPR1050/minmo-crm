package com.felipeparodi.crmbackend.atencioncliente.entity;

import jakarta.persistence.Column;
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
@Table(name = "resultado_visita")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultadoVisita {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visita_id", nullable = false, unique = true)
    private VisitaCliente visita;

    private Short numeroVisita;

    @Enumerated(EnumType.STRING)
    private EstadoVisitaCliente estado;

    @Enumerated(EnumType.STRING)
    private NivelInteres nivelInteres;

    @Enumerated(EnumType.STRING)
    private IntencionCompra intencionCompra;

    @Enumerated(EnumType.STRING)
    private ProximaAccion proximaAccion;

    private Short seguimientoNumero;

    private String seguimientoUnidad;

    private String observacionesAsesor;

    private boolean seguimientoAlertaEnviada;

    @Column(name = "resumen_generado_ia", columnDefinition = "TEXT")
    private String resumenGeneradoIA;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.numeroVisita == null) {
            this.numeroVisita = 1;
        }
    }
}