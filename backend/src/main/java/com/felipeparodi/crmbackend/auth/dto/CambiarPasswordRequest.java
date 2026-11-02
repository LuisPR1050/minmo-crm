package com.felipeparodi.crmbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CambiarPasswordRequest {

    @NotBlank(message = "La contrasena actual es obligatoria")
    private String passwordActual;

    @NotBlank(message = "La contrasena nueva es obligatoria")
    @Size(min = 8, message = "La contrasena nueva debe tener al menos 8 caracteres")
    private String passwordNueva;
}
