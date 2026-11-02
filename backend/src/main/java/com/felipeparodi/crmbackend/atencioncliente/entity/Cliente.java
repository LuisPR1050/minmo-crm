package com.felipeparodi.crmbackend.atencioncliente.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@Table(name = "cliente")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Cliente {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String nombre;

    private String telefono;

    private String whatsapp;

    private String correoElectronico;

    private String tipoContacto;

    private String subtipoContacto;

    private String numeroContacto;

    private UUID asesorId;

    @Enumerated(EnumType.STRING)
    private EstadoPipelineCliente estadoPipeline;

    private LocalDateTime createdAt;

    @PrePersist
    protected void alCrear() {
        this.createdAt = LocalDateTime.now();
        if (this.estadoPipeline == null) {
            this.estadoPipeline = EstadoPipelineCliente.CONTACTO_INICIADO;
        }
    }
}