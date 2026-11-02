package com.felipeparodi.crmbackend.atencioncliente.scheduler;

import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import com.felipeparodi.crmbackend.atencioncliente.service.RecordatorioVisitaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RecordatorioVisitaScheduler {

    private static final Logger log = LoggerFactory.getLogger(RecordatorioVisitaScheduler.class);

    private final VisitaClienteRepository visitaClienteRepository;
    private final RecordatorioVisitaService recordatorioVisitaService;

    public RecordatorioVisitaScheduler(VisitaClienteRepository visitaClienteRepository,
                                        RecordatorioVisitaService recordatorioVisitaService) {
        this.visitaClienteRepository = visitaClienteRepository;
        this.recordatorioVisitaService = recordatorioVisitaService;
    }

    @Scheduled(fixedRate = 300000)
    public void revisarRecordatorios() {
        List<VisitaCliente> visitas = visitaClienteRepository.findAll();

        for (VisitaCliente visita : visitas) {
            try {
                recordatorioVisitaService.procesarRecordatoriosDeVisita(visita.getId());
            } catch (Exception ex) {
                log.error("Error al procesar recordatorio para visita {}", visita.getId(), ex);
            }
        }
    }
}
