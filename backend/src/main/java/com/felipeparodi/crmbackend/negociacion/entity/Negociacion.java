package com.felipeparodi.crmbackend.negociacion.entity;

import com.felipeparodi.crmbackend.atencioncliente.entity.Cliente;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
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
@Table(name = "negociacion")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Negociacion {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String numeroNegocio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "propietario_id", nullable = false)
    private Propietario propietario;

    private UUID asesorId;

    private LocalDate fechaInicio;

    private LocalDate fechaFirmaPcv;

    private LocalDate fechaEntregaProgramada;

    private BigDecimal precioInicialPublicado;

    private BigDecimal ofertaInicialComprador;

    private BigDecimal contraofertaVendedor;

    private BigDecimal precioFinalAcordado;

    private String entidadFinanciera;

    private String financiacionDirectaCondiciones;

    private String permutaCondiciones;

    @Enumerated(EnumType.STRING)
    private EstadoFisicoEntrega estadoFisicoEntrega;

    @Enumerated(EnumType.STRING)
    private MomentoEntrega momentoEntrega;

    private boolean escrituraFirmada;

    private boolean pagoRecibido;

    private boolean entregaRealizada;

    @Enumerated(EnumType.STRING)
    private EstadoNegociacion estado;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoNegociacion.NEGOCIACION_INICIADA;
        }
    }
}