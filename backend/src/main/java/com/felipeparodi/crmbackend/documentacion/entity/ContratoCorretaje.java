package com.felipeparodi.crmbackend.documentacion.entity;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
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
@Table(name = "contrato_corretaje")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ContratoCorretaje {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numeroContrato;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    private LocalDate fechaElaboracion;

    private String ciudad;

    @Enumerated(EnumType.STRING)
    private TipoContrato tipoContrato;

    private BigDecimal precioAutorizadoVenta;

    private BigDecimal precioMinimoAutorizado;

    private BigDecimal porcentajeComision;

    private BigDecimal valorFijoComision;

    private BigDecimal sobreprecioComision;

    @Enumerated(EnumType.STRING)
    private FormaPagoComision formaPagoComision;

    private LocalDate fechaVigenciaHasta;

    @Enumerated(EnumType.STRING)
    private OpcionFirma opcionFirma;

    @Enumerated(EnumType.STRING)
    private EstadoDocumento estado;

    private boolean alertaVencimientoEnviada;

    private String textoContrato;

    private LocalDateTime fechaGeneracionTexto;

    @Column(name = "pdf_url")
    private String pdfUrl;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoDocumento.BORRADOR;
        }
    }
}
