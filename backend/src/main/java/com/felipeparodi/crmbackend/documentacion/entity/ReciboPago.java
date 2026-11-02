package com.felipeparodi.crmbackend.documentacion.entity;

import com.felipeparodi.crmbackend.negociacion.entity.Negociacion;
import com.felipeparodi.crmbackend.negociacion.entity.PlanDePago;
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
@Table(name = "recibo_pago")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReciboPago {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numeroRecibo;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "negociacion_id", nullable = false)
    private Negociacion negociacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "promesa_compraventa_id")
    private PromesaCompraventa promesaCompraventa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "plan_de_pago_id")
    private PlanDePago planDePago;

    private LocalDate fechaElaboracion;

    @Enumerated(EnumType.STRING)
    private ConceptoPago conceptoPago;

    private Short numeroCuota;

    private BigDecimal valorRecibido;

    private BigDecimal saldoPendiente;

    private String moneda;

    @Enumerated(EnumType.STRING)
    private FormaPagoRecibo formaPago;

    @Enumerated(EnumType.STRING)
    private PagoRealizadoPor pagoRealizadoPor;

    @Enumerated(EnumType.STRING)
    private PagoRecibidoPor pagoRecibidoPor;

    private String observaciones;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
    }
}
