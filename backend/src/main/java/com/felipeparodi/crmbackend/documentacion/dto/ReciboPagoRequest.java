package com.felipeparodi.crmbackend.documentacion.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
public class ReciboPagoRequest {

    private UUID negociacionId;
    private UUID promesaCompraventaId;
    private UUID planDePagoId;
    private LocalDate fechaElaboracion;
    private String conceptoPago;
    private Short numeroCuota;
    private BigDecimal valorRecibido;
    private String moneda;
    private String formaPago;
    private String pagoRealizadoPor;
    private String pagoRecibidoPor;
    private String observaciones;
}
