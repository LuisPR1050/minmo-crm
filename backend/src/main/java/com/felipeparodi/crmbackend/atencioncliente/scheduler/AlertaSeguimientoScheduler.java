package com.felipeparodi.crmbackend.atencioncliente.scheduler;

import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.ResultadoVisita;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoNotificacion;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.ResultadoVisitaRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

@Component
public class AlertaSeguimientoScheduler {

    private static final Logger log = LoggerFactory.getLogger(AlertaSeguimientoScheduler.class);

    private final ResultadoVisitaRepository resultadoVisitaRepository;
    private final NotificacionRepository notificacionRepository;

    public AlertaSeguimientoScheduler(ResultadoVisitaRepository resultadoVisitaRepository,
                                       NotificacionRepository notificacionRepository) {
        this.resultadoVisitaRepository = resultadoVisitaRepository;
        this.notificacionRepository = notificacionRepository;
    }

    @Scheduled(fixedRate = 300000)
    @Transactional
    public void revisarSeguimientos() {
        LocalDateTime ahora = LocalDateTime.now();
        List<ResultadoVisita> resultados = resultadoVisitaRepository.findAll();

        for (ResultadoVisita resultado : resultados) {
            try {
                if (resultado.isSeguimientoAlertaEnviada()
                        || resultado.getSeguimientoNumero() == null
                        || resultado.getSeguimientoUnidad() == null) {
                    continue;
                }

                LocalDateTime fechaLimite = calcularFechaLimite(resultado);

                if (!ahora.isBefore(fechaLimite)) {
                    String mensaje = "Seguimiento pendiente para el cliente "
                            + resultado.getVisita().getCliente().getNombre()
                            + " (visita " + resultado.getVisita().getCodigoVisita() + ")";

                    Notificacion notificacion = Notificacion.builder()
                            .tipo(TipoNotificacion.ALERTA_SEGUIMIENTO)
                            .mensaje(mensaje)
                            .referenciaId(resultado.getId())
                            .build();

                    notificacionRepository.save(notificacion);

                    resultado.setSeguimientoAlertaEnviada(true);
                    resultadoVisitaRepository.save(resultado);
                }
            } catch (Exception ex) {
                log.error("Error al procesar alerta de seguimiento para resultado de visita {}", resultado.getId(), ex);
            }
        }
    }

    private LocalDateTime calcularFechaLimite(ResultadoVisita resultado) {
        LocalDateTime base = resultado.getCreatedAt();
        int cantidad = resultado.getSeguimientoNumero();

        return switch (resultado.getSeguimientoUnidad().toUpperCase()) {
            case "HORAS" -> base.plusHours(cantidad);
            case "DIAS" -> base.plusDays(cantidad);
            case "SEMANAS" -> base.plusWeeks(cantidad);
            case "MESES" -> base.plusMonths(cantidad);
            default -> base.plusDays(cantidad);
        };
    }
}