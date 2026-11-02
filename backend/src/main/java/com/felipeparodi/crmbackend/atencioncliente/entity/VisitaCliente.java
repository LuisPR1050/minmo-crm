package com.felipeparodi.crmbackend.atencioncliente.entity;

import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.UUID;

@Entity
@Table(name = "visita_cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VisitaCliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id", nullable = false)
    private Cliente cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "inmueble_id", nullable = false)
    private Inmueble inmueble;

    private String codigoVisita;

    @Enumerated(EnumType.STRING)
    private EstadoVisitaCliente estado;

    @Enumerated(EnumType.STRING)
    private TipoVisita tipoVisita;

    private LocalDate fechaVisita;

    private LocalTime horaInicio;

    private LocalTime horaFin;

    private boolean confirmadaPorCliente;

    private boolean confirmadaPorPropietario;

    private String recomendaciones;

    @Column(name = "recordatorio_24h_enviado")
    private boolean recordatorio24hEnviado;

    @Column(name = "recordatorio_2h_enviado")
    private boolean recordatorio2hEnviado;

    @Column(name = "recordatorio_sms_1h_enviado")
    private boolean recordatorioSms1hEnviado;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estado == null) {
            this.estado = EstadoVisitaCliente.PENDIENTE_CONFIRMAR;
        }
    }
}