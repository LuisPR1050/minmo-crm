package com.felipeparodi.crmbackend.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class UsuarioRequest {

    @NotBlank(message = "El nombre es obligatorio")
    private String nombre;

    @NotBlank(message = "El correo es obligatorio")
    private String email;

    private String telefono;

    @NotBlank(message = "La contrasena es obligatoria")
    private String password;

    @NotBlank(message = "El rol es obligatorio")
    private String rol;
}
