package com.felipeparodi.crmbackend.auth.controller;

import com.felipeparodi.crmbackend.auth.dto.CambiarPasswordRequest;
import com.felipeparodi.crmbackend.auth.dto.LoginRequest;
import com.felipeparodi.crmbackend.auth.dto.LoginResponse;
import com.felipeparodi.crmbackend.auth.security.LimitadorDeIntentosLogin;
import com.felipeparodi.crmbackend.auth.service.AuthService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import java.util.UUID;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;
    private final LimitadorDeIntentosLogin limitadorDeIntentosLogin;

    public AuthController(AuthService authService, LimitadorDeIntentosLogin limitadorDeIntentosLogin) {
        this.authService = authService;
        this.limitadorDeIntentosLogin = limitadorDeIntentosLogin;
    }

    @PostMapping("/login")
    public LoginResponse login(@Valid @RequestBody LoginRequest request, HttpServletRequest httpRequest) {
        if (!limitadorDeIntentosLogin.permitirIntento(resolverIpCliente(httpRequest))) {
            throw new ResponseStatusException(HttpStatus.TOO_MANY_REQUESTS,
                    "Demasiados intentos de inicio de sesion. Intenta de nuevo en unos minutos.");
        }
        return authService.login(request);
    }

    private String resolverIpCliente(HttpServletRequest request) {
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isBlank()) {
            return xForwardedFor.split(",")[0].trim();
        }
        return request.getRemoteAddr();
    }

    @PatchMapping("/password")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void cambiarPassword(@Valid @RequestBody CambiarPasswordRequest request, Authentication authentication) {
        UUID usuarioId = UUID.fromString(authentication.getName());
        authService.cambiarPassword(usuarioId, request);
    }
}
