package com.felipeparodi.crmbackend.auth.security;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.time.LocalDateTime;
import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    @Value("${cors.allowed-origins:}")
    private String corsAllowedOrigins;

    public SecurityConfig(JwtAuthenticationFilter jwtAuthenticationFilter) {
        this.jwtAuthenticationFilter = jwtAuthenticationFilter;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        List<String> origenesPermitidos = new java.util.ArrayList<>(List.of(
                "http://localhost:*", "https://*.ngrok-free.dev", "https://*.ngrok-free.app",
                // Permite probar desde otros dispositivos en la misma red local (celular, etc.)
                // usando la IP LAN del equipo que corre el servidor de desarrollo.
                "http://192.168.*.*:*", "http://10.*.*.*:*", "http://172.16.*.*:*"));
        if (corsAllowedOrigins != null && !corsAllowedOrigins.isBlank()) {
            for (String origen : corsAllowedOrigins.split(",")) {
                if (!origen.isBlank()) {
                    origenesPermitidos.add(origen.trim());
                }
            }
        }

        CorsConfiguration configuracion = new CorsConfiguration();
        configuracion.setAllowedOriginPatterns(origenesPermitidos);
        configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuracion.setAllowedHeaders(List.of("*"));
        configuracion.setAllowCredentials(true);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuracion);
        return source;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable())
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.POST, "/api/auth/login").permitAll()
                        .requestMatchers("/api/webhooks/**").permitAll()
                        // Landing page publica de cada inmueble (ver captacion.publico): la
                        // consulta el cliente final desde un link compartido, sin sesion.
                        .requestMatchers("/api/public/**").permitAll()
                        .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                        // Los archivos subidos (contratos, recibos, actas, etc.) contienen
                        // datos sensibles: exigen sesion igual que el resto de /api/**.
                        // El frontend los solicita con el token via fetch/axios y los
                        // muestra como blob URL (ver frontend/src/utils/archivosProtegidos.ts).
                        .requestMatchers("/uploads/**").authenticated()
                        .requestMatchers("/api/usuarios/**").hasRole("ADMIN")
                        .requestMatchers("/api/**").authenticated()
                        // Todo lo que no sea /api/** ni /uploads/** es el shell estatico
                        // del frontend (SPA servida desde este mismo backend, ver
                        // SpaWebConfig): debe ser publico para que el navegador pueda
                        // cargarlo incluso sin sesion (login, recargar una ruta profunda).
                        .anyRequest().permitAll())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(this::manejarNoAutenticado)
                        .accessDeniedHandler(this::manejarAccesoDenegado))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    private void manejarNoAutenticado(jakarta.servlet.http.HttpServletRequest request,
                                       HttpServletResponse response,
                                       org.springframework.security.core.AuthenticationException ex) throws java.io.IOException {
        escribirError(response, HttpServletResponse.SC_UNAUTHORIZED, "No autenticado",
                "Debes iniciar sesion para acceder a este recurso");
    }

    private void manejarAccesoDenegado(jakarta.servlet.http.HttpServletRequest request,
                                        HttpServletResponse response,
                                        org.springframework.security.access.AccessDeniedException ex) throws java.io.IOException {
        escribirError(response, HttpServletResponse.SC_FORBIDDEN, "Acceso denegado",
                "No tienes permisos para acceder a este recurso");
    }

    private void escribirError(HttpServletResponse response, int status, String error, String mensaje)
            throws java.io.IOException {
        response.setStatus(status);
        response.setContentType("application/json");
        response.getWriter().write(String.format(
                "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"mensaje\":\"%s\"}",
                LocalDateTime.now(), status, error, mensaje));
    }
}
