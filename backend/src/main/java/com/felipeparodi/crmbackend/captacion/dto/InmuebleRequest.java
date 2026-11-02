package com.felipeparodi.crmbackend.captacion.dto;

import jakarta.validation.constraints.PositiveOrZero;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@Getter
@Setter
public class InmuebleRequest {

    private UUID propietarioId;
    private String tipoPropiedad;
    private String ciudad;
    private String direccionExacta;
    private String zona;
    private String entorno;

    @PositiveOrZero(message = "El area total no puede ser negativa")
    private BigDecimal areaTotal;

    @PositiveOrZero(message = "El area construida no puede ser negativa")
    private BigDecimal areaConstruida;

    @PositiveOrZero(message = "El area privada no puede ser negativa")
    private BigDecimal areaPrivada;

    @PositiveOrZero(message = "El numero de habitaciones no puede ser negativo")
    private Short numeroHabitaciones;

    @PositiveOrZero(message = "El numero de banos no puede ser negativo")
    private Short numeroBanos;

    private String tipoCocina;
    private String estadoFisico;
    private String matriculaInmobiliaria;
    private String referenciaCatastral;

    @PositiveOrZero(message = "La expectativa de precio no puede ser negativa")
    private BigDecimal expectativaPrecio;

    @PositiveOrZero(message = "El precio minimo aceptado no puede ser negativo")
    private BigDecimal precioMinimoAceptado;

    @PositiveOrZero(message = "El precio sugerido no puede ser negativo")
    private BigDecimal precioSugerido;

    @PositiveOrZero(message = "El valor de administracion no puede ser negativo")
    private BigDecimal valorAdministracion;

    @PositiveOrZero(message = "El valor de arriendo no puede ser negativo")
    private BigDecimal valorArriendo;

    @PositiveOrZero(message = "El valor de impuestos no puede ser negativo")
    private BigDecimal valorImpuestos;

    @PositiveOrZero(message = "El valor adeudado no puede ser negativo")
    private BigDecimal valorAdeudado;

    private Boolean escuchaOfertas;

    @PositiveOrZero(message = "El estrato socioeconomico no puede ser negativo")
    private Short estratoSocioeconomico;

    private String usoSuelo;
    private BigDecimal latitud;
    private BigDecimal longitud;
    private String puntosClavesCercanos;
    private String situada;
    private String niveles;

    @PositiveOrZero(message = "El piso no puede ser negativo")
    private Short piso;

    private String vista;
    private String tipoAcceso;

    @PositiveOrZero(message = "La medida del frente no puede ser negativa")
    private BigDecimal medidaFrente;

    @PositiveOrZero(message = "La medida del fondo no puede ser negativa")
    private BigDecimal medidaFondo;
    private String otrasCaracteristicasLote;
    private String quienOcupaActualmente;
    private String estadoJuridico;
    private String estadoCaptacion;
    private List<String> puntosReferencia;
}