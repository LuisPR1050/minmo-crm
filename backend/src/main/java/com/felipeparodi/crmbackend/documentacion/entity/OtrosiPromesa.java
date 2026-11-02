package com.felipeparodi.crmbackend.documentacion.entity;

import jakarta.persistence.Column;
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

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "otrosi_promesa")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtrosiPromesa {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numeroOtrosi;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promesa_compraventa_id", nullable = false)
    private PromesaCompraventa promesaCompraventa;

    private LocalDate fechaElaboracion;

    @Enumerated(EnumType.STRING)
    private MotivoOtrosi motivo;

    private String otroMotivoTexto;

    @Enumerated(EnumType.STRING)
    private JustificacionOtrosi justificacion;

    private BigDecimal nuevoPrecio;

    private LocalDate nuevaFechaEscritura;

    private LocalDate nuevaFechaEntrega;

    private String nuevaDistribucionGastos;

    private String nuevosBienesIncluidos;

    private String nuevasCondicionesEspeciales;

    private String observaciones;

    private boolean aplicado;

    private LocalDateTime fechaAplicacion;

    private String textoOtrosi;

    private LocalDateTime fechaGeneracionTexto;

    @Column(name = "pdf_url")
    private String pdfUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}
