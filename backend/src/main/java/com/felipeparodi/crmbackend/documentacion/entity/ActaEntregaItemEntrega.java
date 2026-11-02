package com.felipeparodi.crmbackend.documentacion.entity;

import com.felipeparodi.crmbackend.negociacion.entity.ItemEntrega;
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
@Table(name = "acta_entrega_item_entrega")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ActaEntregaItemEntrega {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "acta_entrega_id", nullable = false)
    private ActaEntrega actaEntrega;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_entrega_id", nullable = false)
    private ItemEntrega itemEntrega;

    private boolean entregado;
}
