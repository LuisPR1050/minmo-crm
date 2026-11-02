package com.felipeparodi.crmbackend.atencioncliente.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ConfirmarVisitaRequest {

    private boolean confirmadaPorCliente;
    private boolean confirmadaPorPropietario;
}