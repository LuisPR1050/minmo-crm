package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.UUID;

@Getter
@Setter
public class RequerimientoClienteRequest {

    private UUID clienteId;
    private String tipoPropiedad;
    private String ciudad;
    private String zona;
    private String entorno;
    private String sitiosClavesCercanos;
    private BigDecimal presupuestoMin;
    private BigDecimal presupuestoMax;
    private String formaPago;
    private String tiempoParaComprar;
    private Short numeroHabitaciones;
    private Short numeroBanos;
    private String observaciones;
}