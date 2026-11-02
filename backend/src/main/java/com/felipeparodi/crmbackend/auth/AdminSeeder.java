package com.felipeparodi.crmbackend.auth;

import com.felipeparodi.crmbackend.auth.entity.RolUsuario;
import com.felipeparodi.crmbackend.auth.entity.Usuario;
import com.felipeparodi.crmbackend.auth.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.security.SecureRandom;
import java.util.Base64;

@Component
public class AdminSeeder implements ApplicationRunner {

    private static final Logger log = LoggerFactory.getLogger(AdminSeeder.class);
    private static final String EMAIL_ADMIN_INICIAL = "admin@crm.local";

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final String passwordAdminInicial;
    private final SecureRandom random = new SecureRandom();

    public AdminSeeder(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder,
                        @Value("${admin.password-inicial:}") String passwordAdminInicial) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.passwordAdminInicial = passwordAdminInicial;
    }

    @Override
    public void run(ApplicationArguments args) {
        if (usuarioRepository.count() > 0) {
            return;
        }

        boolean passwordGenerada = passwordAdminInicial == null || passwordAdminInicial.isBlank();
        String password = passwordGenerada ? generarPasswordAleatoria() : passwordAdminInicial;

        Usuario admin = Usuario.builder()
                .nombre("Administrador")
                .email(EMAIL_ADMIN_INICIAL)
                .passwordHash(passwordEncoder.encode(password))
                .rol(RolUsuario.ADMIN)
                .activo(true)
                .build();

        usuarioRepository.save(admin);

        if (passwordGenerada) {
            log.warn("No existian usuarios: se creo un administrador inicial.\n"
                            + "  Email: {}\n"
                            + "  Password temporal (solo se muestra esta vez): {}\n"
                            + "Inicia sesion y cambiala de inmediato desde tu perfil.",
                    EMAIL_ADMIN_INICIAL, password);
        } else {
            log.warn("No existian usuarios: se creo un administrador inicial ({}) con la password "
                    + "definida en ADMIN_PASSWORD_INICIAL. Cambiala despues de iniciar sesion.",
                    EMAIL_ADMIN_INICIAL);
        }
    }

    private String generarPasswordAleatoria() {
        byte[] bytes = new byte[18];
        random.nextBytes(bytes);
        return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
    }
}
