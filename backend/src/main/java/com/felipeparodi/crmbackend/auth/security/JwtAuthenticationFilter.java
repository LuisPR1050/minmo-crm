package com.felipeparodi.crmbackend.auth.security;

import com.felipeparodi.crmbackend.auth.entity.Usuario;
import com.felipeparodi.crmbackend.auth.repository.UsuarioRepository;
import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private static final String PREFIJO_BEARER = "Bearer ";

    private final JwtService jwtService;
    private final UsuarioRepository usuarioRepository;

    public JwtAuthenticationFilter(JwtService jwtService, UsuarioRepository usuarioRepository) {
        this.jwtService = jwtService;
        this.usuarioRepository = usuarioRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                     HttpServletResponse response,
                                     FilterChain filterChain) throws ServletException, IOException {
        String encabezado = request.getHeader("Authorization");

        if (encabezado != null && encabezado.startsWith(PREFIJO_BEARER)) {
            String token = encabezado.substring(PREFIJO_BEARER.length());
            Optional<Claims> claims = jwtService.validarYExtraerClaims(token);

            if (claims.isPresent() && SecurityContextHolder.getContext().getAuthentication() == null) {
                UUID usuarioId = UUID.fromString(claims.get().getSubject());

                // Se vuelve a consultar el usuario en cada solicitud (en vez de confiar en los
                // claims del JWT) para que desactivar un usuario o cambiarle el rol tenga efecto
                // de inmediato, sin esperar a que el token expire (hasta 24h por defecto).
                usuarioRepository.findById(usuarioId)
                        .filter(Usuario::isActivo)
                        .ifPresent(usuario -> {
                            var authentication = new UsernamePasswordAuthenticationToken(
                                    usuario.getId().toString(), null,
                                    List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
                            SecurityContextHolder.getContext().setAuthentication(authentication);
                        });
            }
        }

        filterChain.doFilter(request, response);
    }
}
