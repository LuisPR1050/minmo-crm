package com.felipeparodi.crmbackend.auth.controller;

import com.felipeparodi.crmbackend.auth.dto.CambiarEstadoUsuarioRequest;
import com.felipeparodi.crmbackend.auth.dto.EditarUsuarioRequest;
import com.felipeparodi.crmbackend.auth.dto.UsuarioRequest;
import com.felipeparodi.crmbackend.auth.dto.UsuarioResponse;
import com.felipeparodi.crmbackend.auth.service.UsuarioService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/usuarios")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public UsuarioResponse crear(@Valid @RequestBody UsuarioRequest request) {
        return usuarioService.crear(request);
    }

    @GetMapping
    public List<UsuarioResponse> listarTodos() {
        return usuarioService.listarTodos();
    }

    @PatchMapping("/{id}/estado")
    public UsuarioResponse cambiarEstado(@PathVariable UUID id, @RequestBody CambiarEstadoUsuarioRequest request) {
        return usuarioService.cambiarEstado(id, request);
    }

    @PatchMapping("/{id}")
    public UsuarioResponse actualizar(@PathVariable UUID id, @Valid @RequestBody EditarUsuarioRequest request) {
        return usuarioService.actualizar(id, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable UUID id, Authentication authentication) {
        UUID usuarioActualId = UUID.fromString(authentication.getName());
        usuarioService.eliminar(id, usuarioActualId);
    }
}
