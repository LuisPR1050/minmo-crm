package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.entity.EstadoVisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.entity.Notificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.TipoNotificacion;
import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.NotificacionRepository;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.UUID;

@Service
public class RecordatorioVisitaService {

    private final VisitaClienteRepository visitaClienteRepository;
    private final NotificacionRepository notificacionRepository;
    private final VisitaSmsNotificador visitaSmsNotificador;

    public RecordatorioVisitaService(VisitaClienteRepository visitaClienteRepository,
                                      NotificacionRepository notificacionRepository,
                                      VisitaSmsNotificador visitaSmsNotificador) {
        this.visitaClienteRepository = visitaClienteRepository;
        this.notificacionRepository = notificacionRepository;
        this.visitaSmsNotificador = visitaSmsNotificador;
    }

    /**
     * Cada visita se procesa en su propia transaccion: si el proceso se cae a mitad del lote,
     * las banderas de recordatorio ya confirmadas en visitas anteriores no se pierden, y no se
     * reenvian los SMS de esas visitas en el siguiente tick del scheduler.
     */
    @Transactional
    public void procesarRecordatoriosDeVisita(UUID visitaId) {
        VisitaCliente visita = visitaClienteRepository.findById(visitaId).orElse(null);
        if (visita == null) {
            return;
        }

        if (visita.getEstado() == EstadoVisitaCliente.CANCELADA
                || visita.getEstado() == EstadoVisitaCliente.REALIZADA
                || visita.getHoraInicio() == null) {
            return;
        }

        LocalDateTime ahora = LocalDateTime.now();
        LocalDateTime momentoVisita = LocalDateTime.of(visita.getFechaVisita(), visita.getHoraInicio());
        long horasFaltantes = Duration.between(ahora, momentoVisita).toHours();

        if (!visita.isRecordatorio24hEnviado() && horasFaltantes <= 24 && horasFaltantes > 2) {
            crearNotificacion(visita, TipoNotificacion.RECORDATORIO_VISITA_24H);
            visita.setRecordatorio24hEnviado(true);
            visitaClienteRepository.save(visita);
        }

        if (!visita.isRecordatorio2hEnviado() && horasFaltantes <= 2 && horasFaltantes >= 0) {
            crearNotificacion(visita, TipoNotificacion.RECORDATORIO_VISITA_2H);
            visita.setRecordatorio2hEnviado(true);
            visitaClienteRepository.save(visita);
        }

        long minutosFaltantes = Duration.between(ahora, momentoVisita).toMinutes();
        if (!visita.isRecordatorioSms1hEnviado() && minutosFaltantes <= 60 && minutosFaltantes >= 0) {
            visitaSmsNotificador.notificar(visita);
            visita.setRecordatorioSms1hEnviado(true);
            visitaClienteRepository.save(visita);
        }
    }

    private void crearNotificacion(VisitaCliente visita, TipoNotificacion tipo) {
        String mensaje = "Visita " + visita.getCodigoVisita() + " con " + visita.getCliente().getNombre()
                + " programada para " + visita.getFechaVisita() + " a las " + visita.getHoraInicio();

        Notificacion notificacion = Notificacion.builder()
                .tipo(tipo)
                .mensaje(mensaje)
                .referenciaId(visita.getId())
                .build();

        notificacionRepository.save(notificacion);
    }
}
