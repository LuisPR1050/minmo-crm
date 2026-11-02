package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Builder
@AllArgsConstructor
public class ReciboPagoResponse {

    private UUID id;
    private String numeroRecibo;
    private UUID negociacionId;
    private String numeroNegocio;
    private UUID promesaCompraventaId;
    private UUID planDePagoId;
    private LocalDate fechaElaboracion;
    private String conceptoPago;
    private Short numeroCuota;
    private BigDecimal valorRecibido;
    private BigDecimal saldoPendiente;
    private String moneda;
    private String formaPago;
    private String pagoRealizadoPor;
    private String pagoRecibidoPor;
    private String observaciones;

    private UUID inmuebleId;
    private String inmuebleCodigo;
    private String compradorNombre;
    private String compradorDocumento;
    private String compradorTelefono;
    private String compradorCorreo;
    private String vendedorNombre;
    private String vendedorDocumento;
    private String vendedorTelefono;
    private String vendedorCorreo;

    private LocalDateTime createdAt;
}
