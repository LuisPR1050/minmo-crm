package com.felipeparodi.crmbackend.captacion.publico.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LandingLeadRequest {

    @NotBlank(message = "El nombre es obligatorio")
    @Size(max = 150, message = "El nombre es demasiado largo")
    private String nombre;

    @NotBlank(message = "El telefono es obligatorio")
    @Size(max = 20, message = "El telefono es demasiado largo")
    private String telefono;

    @Email(message = "El correo no tiene un formato valido")
    @Size(max = 150, message = "El correo es demasiado largo")
    private String correoElectronico;

    @Size(max = 500, message = "El mensaje es demasiado largo")
    private String mensaje;
}
