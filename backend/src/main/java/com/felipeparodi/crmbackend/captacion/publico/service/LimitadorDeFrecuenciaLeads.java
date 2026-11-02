package com.felipeparodi.crmbackend.captacion.publico.service;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Deque;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedDeque;

/**
 * Limita cuantos leads puede enviar una misma IP desde la landing publica (sin autenticacion),
 * para evitar que alguien llene la base de datos de leads falsos con un script. Es en memoria
 * (no distribuido) porque el servicio corre como una sola instancia en Railway.
 */
@Component
public class LimitadorDeFrecuenciaLeads {

    private static final int MAX_SOLICITUDES = 5;
    private static final Duration VENTANA = Duration.ofMinutes(10);

    private final Map<String, Deque<Instant>> solicitudesPorIp = new ConcurrentHashMap<>();

    public boolean permitir(String ip) {
        Instant ahora = Instant.now();
        Instant limiteVentana = ahora.minus(VENTANA);
        Deque<Instant> solicitudes = solicitudesPorIp.computeIfAbsent(ip, k -> new ConcurrentLinkedDeque<>());

        synchronized (solicitudes) {
            while (!solicitudes.isEmpty() && solicitudes.peekFirst().isBefore(limiteVentana)) {
                solicitudes.pollFirst();
            }
            if (solicitudes.size() >= MAX_SOLICITUDES) {
                return false;
            }
            solicitudes.addLast(ahora);
            return true;
        }
    }
}
