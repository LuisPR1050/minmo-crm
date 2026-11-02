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
@Table(name = "otrosi_promesa_clausula")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtrosiPromesaClausula {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "otrosi_promesa_id", nullable = false)
    private OtrosiPromesa otrosiPromesa;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "clausula_corretaje_id", nullable = false)
    private ClausulaCorretaje clausulaCorretaje;

    private boolean incluida;
}
