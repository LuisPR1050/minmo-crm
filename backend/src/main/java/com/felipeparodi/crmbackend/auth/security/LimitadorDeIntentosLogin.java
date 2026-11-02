package com.felipeparodi.crmbackend.auth.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Limita cuantos intentos de login puede hacer una misma IP, para mitigar fuerza bruta online
 * contra /api/auth/login. En memoria (no distribuido) porque el servicio corre como una sola
 * instancia en Railway.
 */
@Component
public class LimitadorDeIntentosLogin {

    private static final int MAX_INTENTOS = 10;
    private static final Duration VENTANA = Duration.ofMinutes(15);

    private final Map<String, Deque<Instant>> intentosPorIp = new ConcurrentHashMap<>();

    public boolean permitirIntento(String ip) {
        Instant ahora = Instant.now();
        Instant limiteVentana = ahora.minus(VENTANA);
        Deque<Instant> intentos = intentosPorIp.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        synchronized (intentos) {
            while (!intentos.isEmpty() && intentos.peekFirst().isBefore(limiteVentana)) {
                intentos.pollFirst();
            }
            if (intentos.size() >= MAX_INTENTOS) {
                return false;
            }
            intentos.addLast(ahora);
            return true;
        }
    }
}
