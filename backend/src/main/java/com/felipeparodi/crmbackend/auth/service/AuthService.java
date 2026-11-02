package com.felipeparodi.crmbackend.auth.service;

import com.felipeparodi.crmbackend.auth.dto.CambiarPasswordRequest;
import com.felipeparodi.crmbackend.auth.dto.LoginRequest;
import com.felipeparodi.crmbackend.auth.dto.LoginResponse;
import com.felipeparodi.crmbackend.auth.entity.Usuario;
import com.felipeparodi.crmbackend.auth.exception.CredencialesInvalidasException;
import com.felipeparodi.crmbackend.auth.repository.UsuarioRepository;
import com.felipeparodi.crmbackend.auth.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
@Transactional
public class AuthService {

    private static final String MENSAJE_CREDENCIALES_INVALIDAS = "Correo o contrasena incorrectos";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public AuthService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                        JwtService jwtService) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    public LoginResponse login(LoginRequest request) {
        Usuario usuario = usuarioRepository.findByEmailIgnoreCase(request.getEmail())
                .orElseThrow(() -> new CredencialesInvalidasException(MENSAJE_CREDENCIALES_INVALIDAS));

        if (!usuario.isActivo() || !passwordEncoder.matches(request.getPassword(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException(MENSAJE_CREDENCIALES_INVALIDAS);
        }

        String token = jwtService.generarToken(usuario);

        return LoginResponse.builder()
                .token(token)
                .usuarioId(usuario.getId())
                .nombre(usuario.getNombre())
                .email(usuario.getEmail())
                .rol(usuario.getRol().name())
                .build();
    }

    public void cambiarPassword(UUID usuarioId, CambiarPasswordRequest request) {
        Usuario usuario = usuarioRepository.findById(usuarioId)
                .orElseThrow(() -> new CredencialesInvalidasException(MENSAJE_CREDENCIALES_INVALIDAS));

        if (!passwordEncoder.matches(request.getPasswordActual(), usuario.getPasswordHash())) {
            throw new CredencialesInvalidasException("La contrasena actual no es correcta");
        }

        usuario.setPasswordHash(passwordEncoder.encode(request.getPasswordNueva()));
        usuarioRepository.save(usuario);
    }
}
