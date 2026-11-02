package com.felipeparodi.crmbackend.atencioncliente.service;

import com.felipeparodi.crmbackend.atencioncliente.entity.VisitaCliente;
import com.felipeparodi.crmbackend.auth.entity.Usuario;
import com.felipeparodi.crmbackend.auth.repository.UsuarioRepository;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Locale;

@Component
public class VisitaSmsNotificador {

    private static final Locale ESPANOL_CO = Locale.of("es", "CO");
    private static final DateTimeFormatter FORMATO_FECHA = DateTimeFormatter.ofPattern("EEEE d 'de' MMMM", ESPANOL_CO);
    private static final DateTimeFormatter FORMATO_HORA = DateTimeFormatter.ofPattern("h:mm a", ESPANOL_CO);

    private final SmsService smsService;
    private final UsuarioRepository usuarioRepository;

    public VisitaSmsNotificador(SmsService smsService, UsuarioRepository usuarioRepository) {
        this.smsService = smsService;
        this.usuarioRepository = usuarioRepository;
    }

    public void notificar(VisitaCliente visita) {
        Inmueble inmueble = visita.getInmueble();
        Propietario propietario = inmueble.getPropietario();
        Usuario asesor = buscarAsesor(visita);

        String fecha = visita.getFechaVisita() != null ? visita.getFechaVisita().format(FORMATO_FECHA) : "por confirmar";
        String hora = visita.getHoraInicio() != null ? visita.getHoraInicio().format(FORMATO_HORA) : "por confirmar";
        String nombreCliente = visita.getCliente().getNombre();
        String nombreAsesor = asesor != null && asesor.getNombre() != null ? asesor.getNombre() : "tu asesor";
        String codigoInmueble = inmueble.getCodigo() != null ? inmueble.getCodigo() : "";

        if (propietario != null && tieneTexto(propietario.getTelefonoPrincipal())) {
            String mensaje = "Tenemos programada una visita con un cliente: 📅 Fecha: " + fecha
                    + " ⏰ Hora: " + hora
                    + " 👤 Cliente: " + nombreCliente
                    + " 💼 Asesor: " + nombreAsesor
                    + ". Por favor confírmame si todo está correcto o si necesita ajustar algo";
            smsService.enviar(propietario.getTelefonoPrincipal(), mensaje);
        }

        if (tieneTexto(visita.getCliente().getTelefono())) {
            String mensaje = "Listo👍 ya tenemos todo coordinado para la visita. 📅 Fecha: " + fecha
                    + " ⏰ Hora: " + hora
                    + " 🏠 Propiedad: " + codigoInmueble
                    + " 💼 Asesor: " + nombreAsesor
                    + ". La dirección exacta te la estaré enviando antes de la visita.";
            smsService.enviar(visita.getCliente().getTelefono(), mensaje);
        }

        if (asesor != null && tieneTexto(asesor.getTelefono())) {
            String direccion = inmueble.getDireccionExacta() != null ? inmueble.getDireccionExacta() : "no disponible";
            String nombrePropietario = propietario != null && propietario.getNombre() != null
                    ? propietario.getNombre() : "no disponible";
            String ubicacionMaps = (inmueble.getLatitud() != null && inmueble.getLongitud() != null)
                    ? "https://maps.google.com/?q=" + inmueble.getLatitud() + "," + inmueble.getLongitud()
                    : "no disponible";
            String mensaje = "Te confirmo visita programada con los siguientes datos: 🏡 Propiedad: " + codigoInmueble
                    + " 📍 Dirección: " + direccion
                    + " 👤 Persona que atiende: " + nombrePropietario
                    + " 🙋‍♂️ Cliente: " + nombreCliente
                    + " 🕒 Hora: " + hora
                    + " 📍 Ubicación (Google Maps): " + ubicacionMaps
                    + ". Confírmame si todo está bien";
            smsService.enviar(asesor.getTelefono(), mensaje);
        }
    }

    private Usuario buscarAsesor(VisitaCliente visita) {
        if (visita.getCliente().getAsesorId() == null) {
            return null;
        }
        return usuarioRepository.findById(visita.getCliente().getAsesorId()).orElse(null);
    }

    private boolean tieneTexto(String texto) {
        return texto != null && !texto.isBlank();
    }
}
