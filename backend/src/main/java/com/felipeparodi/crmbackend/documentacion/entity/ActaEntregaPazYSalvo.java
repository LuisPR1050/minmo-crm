package com.felipeparodi.crmbackend.documentacion.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "acta_entrega_paz_y_salvo")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActaEntregaPazYSalvo {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acta_entrega_id", nullable = false)
    private ActaEntrega actaEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "paz_y_salvo_tipo_id", nullable = false)
    private PazYSalvoTipo pazYSalvoTipo;

    private boolean alDia;
}
