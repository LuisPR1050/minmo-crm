package com.felipeparodi.crmbackend.captacion.service;

import com.felipeparodi.crmbackend.captacion.dto.ActualizarLandingInmuebleRequest;
import com.felipeparodi.crmbackend.captacion.dto.CambiarEstadoCaptacionRequest;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleRequest;
import com.felipeparodi.crmbackend.captacion.dto.InmuebleResponse;
import com.felipeparodi.crmbackend.captacion.entity.EstadoCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.EstadoJuridico;
import com.felipeparodi.crmbackend.captacion.entity.HistorialCaptacion;
import com.felipeparodi.crmbackend.captacion.entity.Inmueble;
import com.felipeparodi.crmbackend.captacion.entity.Propietario;
import com.felipeparodi.crmbackend.captacion.entity.PuntoReferencia;
import com.felipeparodi.crmbackend.captacion.exception.RecursoNoEncontradoException;
import com.felipeparodi.crmbackend.captacion.repository.ArchivoAdjuntoRepository;
import com.felipeparodi.crmbackend.captacion.repository.ChecklistJuridicoRepository;
import com.felipeparodi.crmbackend.captacion.repository.CondicionCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.repository.HistorialCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleCaracteristicaRepository;
import com.felipeparodi.crmbackend.captacion.repository.InmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.PropietarioRepository;
import com.felipeparodi.crmbackend.captacion.repository.PuntoReferenciaRepository;
import com.felipeparodi.crmbackend.captacion.repository.VideoInmuebleRepository;
import com.felipeparodi.crmbackend.captacion.repository.VisitaCaptacionRepository;
import com.felipeparodi.crmbackend.captacion.exception.ConfirmacionEliminacionRequeridaException;
import com.felipeparodi.crmbackend.captacion.util.GeneradorCodigoInmueble;
import com.felipeparodi.crmbackend.config.AlmacenamientoArchivosService;
import com.felipeparodi.crmbackend.atencioncliente.repository.VisitaClienteRepository;
import com.felipeparodi.crmbackend.documentacion.repository.ContratoCorretajeRepository;
import com.felipeparodi.crmbackend.marketing.repository.PublicacionInmuebleRepository;
import com.felipeparodi.crmbackend.marketing.repository.SolicitudVideoMarketingRepository;
import com.felipeparodi.crmbackend.marketing.service.PublicacionInmuebleService;
import com.felipeparodi.crmbackend.negociacion.repository.NegociacionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class InmuebleService {

    private final InmuebleRepository inmuebleRepository;
    private final PropietarioRepository propietarioRepository;
    private final GeneradorCodigoInmueble generadorCodigoInmueble;
    private final HistorialCaptacionRepository historialCaptacionRepository;
    private final NegociacionRepository negociacionRepository;
    private final ArchivoAdjuntoRepository archivoAdjuntoRepository;
    private final InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository;
    private final CondicionCaptacionRepository condicionCaptacionRepository;
    private final ChecklistJuridicoRepository checklistJuridicoRepository;
    private final VisitaCaptacionRepository visitaCaptacionRepository;
    private final AlmacenamientoArchivosService almacenamientoArchivosService;
    private final PuntoReferenciaRepository puntoReferenciaRepository;
    private final VideoInmuebleRepository videoInmuebleRepository;
    private final ContratoCorretajeRepository contratoCorretajeRepository;
    private final PublicacionInmuebleRepository publicacionInmuebleRepository;
    private final VisitaClienteRepository visitaClienteRepository;
    private final SolicitudVideoMarketingRepository solicitudVideoMarketingRepository;
    private final PublicacionInmuebleService publicacionInmuebleService;

    public InmuebleService(InmuebleRepository inmuebleRepository,
                            PropietarioRepository propietarioRepository,
                            GeneradorCodigoInmueble generadorCodigoInmueble,
                            HistorialCaptacionRepository historialCaptacionRepository,
                            NegociacionRepository negociacionRepository,
                            ArchivoAdjuntoRepository archivoAdjuntoRepository,
                            InmuebleCaracteristicaRepository inmuebleCaracteristicaRepository,
                            CondicionCaptacionRepository condicionCaptacionRepository,
                            ChecklistJuridicoRepository checklistJuridicoRepository,
                            VisitaCaptacionRepository visitaCaptacionRepository,
                            AlmacenamientoArchivosService almacenamientoArchivosService,
                            PuntoReferenciaRepository puntoReferenciaRepository,
                            VideoInmuebleRepository videoInmuebleRepository,
                            ContratoCorretajeRepository contratoCorretajeRepository,
                            PublicacionInmuebleRepository publicacionInmuebleRepository,
                            VisitaClienteRepository visitaClienteRepository,
                            SolicitudVideoMarketingRepository solicitudVideoMarketingRepository,
                            PublicacionInmuebleService publicacionInmuebleService) {
        this.inmuebleRepository = inmuebleRepository;
        this.propietarioRepository = propietarioRepository;
        this.generadorCodigoInmueble = generadorCodigoInmueble;
        this.archivoAdjuntoRepository = archivoAdjuntoRepository;
        this.inmuebleCaracteristicaRepository = inmuebleCaracteristicaRepository;
        this.condicionCaptacionRepository = condicionCaptacionRepository;
        this.checklistJuridicoRepository = checklistJuridicoRepository;
        this.visitaCaptacionRepository = visitaCaptacionRepository;
        this.almacenamientoArchivosService = almacenamientoArchivosService;
        this.historialCaptacionRepository = historialCaptacionRepository;
        this.negociacionRepository = negociacionRepository;
        this.puntoReferenciaRepository = puntoReferenciaRepository;
        this.videoInmuebleRepository = videoInmuebleRepository;
        this.contratoCorretajeRepository = contratoCorretajeRepository;
        this.publicacionInmuebleRepository = publicacionInmuebleRepository;
        this.visitaClienteRepository = visitaClienteRepository;
        this.solicitudVideoMarketingRepository = solicitudVideoMarketingRepository;
        this.publicacionInmuebleService = publicacionInmuebleService;
    }

    public InmuebleResponse crear(InmuebleRequest request) {
        Propietario propietario = propietarioRepository.findById(request.getPropietarioId())
                .orElseThrow(() -> new RecursoNoEncontradoException(
                        "Propietario no encontrado con id: " + request.getPropietarioId()));

        String codigoGenerado = generadorCodigoInmueble.generar(request.getTipoPropiedad());

        Inmueble inmueble = Inmueble.builder()
                .propietario(propietario)
                .codigo(codigoGenerado)
                .tipoPropiedad(request.getTipoPropiedad())
                .ciudad(request.getCiudad())
                .direccionExacta(request.getDireccionExacta())
                .zona(request.getZona())
                .entorno(request.getEntorno())
                .areaTotal(request.getAreaTotal())
                .areaConstruida(request.getAreaConstruida())
                .areaPrivada(request.getAreaPrivada())
                .numeroHabitaciones(request.getNumeroHabitaciones())
                .numeroBanos(request.getNumeroBanos())
                .tipoCocina(request.getTipoCocina())
                .estadoFisico(request.getEstadoFisico())
                .matriculaInmobiliaria(request.getMatriculaInmobiliaria())
                .referenciaCatastral(request.getReferenciaCatastral())
                .expectativaPrecio(request.getExpectativaPrecio())
                .precioMinimoAceptado(request.getPrecioMinimoAceptado())
                .precioSugerido(request.getPrecioSugerido())
                .valorAdministracion(request.getValorAdministracion())
                .valorArriendo(request.getValorArriendo())
                .valorImpuestos(request.getValorImpuestos())
                .valorAdeudado(request.getValorAdeudado())
                .escuchaOfertas(request.getEscuchaOfertas() != null ? request.getEscuchaOfertas() : true)
                .estratoSocioeconomico(request.getEstratoSocioeconomico())
                .usoSuelo(request.getUsoSuelo())
                .latitud(request.getLatitud())
                .longitud(request.getLongitud())
                .puntosClavesCercanos(request.getPuntosClavesCercanos())
                .situada(request.getSituada())
                .niveles(request.getNiveles())
                .piso(request.getPiso())
                .vista(request.getVista())
                .tipoAcceso(request.getTipoAcceso())
                .medidaFrente(request.getMedidaFrente())
                .medidaFondo(request.getMedidaFondo())
                .otrasCaracteristicasLote(request.getOtrasCaracteristicasLote())
                .quienOcupaActualmente(request.getQuienOcupaActualmente())
                .estadoJuridico(request.getEstadoJuridico() != null
                        ? EstadoJuridico.valueOf(request.getEstadoJuridico())
                        : EstadoJuridico.LIBRE)
                .estadoCaptacion(request.getEstadoCaptacion() != null
                        ? EstadoCaptacion.valueOf(request.getEstadoCaptacion())
                        : EstadoCaptacion.PROSPECTO)
                .puntosReferencia(resolverPuntosReferencia(request.getPuntosReferencia()))
                .build();

        Inmueble guardado = inmuebleRepository.save(inmueble);

        registrarHistorial(guardado, "Inmueble registrado en captacion");

        return mapearAResponse(guardado);
    }

    public List<InmuebleResponse> listarTodos() {
        return inmuebleRepository.findAll()
                .stream()
                .map(this::mapearAResponse)
                .toList();
    }

    public InmuebleResponse buscarPorId(UUID id) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + id));

        return mapearAResponse(inmueble);
    }

    public void eliminar(UUID id, boolean confirmar) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + id));

        if (negociacionRepository.existsByInmuebleId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el inmueble porque tiene negociaciones asociadas. "
                            + "Cierra o elimina la negociacion primero.");
        }
        if (contratoCorretajeRepository.existsByInmuebleId(id)) {
            throw new IllegalArgumentException(
                    "No se puede eliminar el inmueble porque tiene un contrato de corretaje asociado. "
                            + "Elimina el contrato primero.");
        }

        var publicaciones = publicacionInmuebleRepository.findByInmuebleId(id);
        var visitasCliente = visitaClienteRepository.findByInmuebleId(id);
        var solicitudesVideo = solicitudVideoMarketingRepository.findByInmuebleId(id);

        List<String> resumen = new ArrayList<>();
        if (!publicaciones.isEmpty()) {
            resumen.add(publicaciones.size() + " publicacion(es) de marketing (con sus fotos, videos e historial)");
        }
        if (!visitasCliente.isEmpty()) {
            resumen.add(visitasCliente.size() + " visita(s) de cliente registrada(s)");
        }
        if (!solicitudesVideo.isEmpty()) {
            resumen.add(solicitudesVideo.size() + " solicitud(es) de video de marketing");
        }

        if (!confirmar && !resumen.isEmpty()) {
            throw new ConfirmacionEliminacionRequeridaException(resumen);
        }

        publicaciones.forEach(publicacion -> publicacionInmuebleService.eliminar(publicacion.getId()));
        visitaClienteRepository.deleteAll(visitasCliente);
        solicitudVideoMarketingRepository.deleteAll(solicitudesVideo);

        var archivos = archivoAdjuntoRepository.findByInmuebleId(id);
        archivos.forEach(archivo -> almacenamientoArchivosService.eliminarPorRutaPublica(archivo.getUrl()));
        archivoAdjuntoRepository.deleteAll(archivos);
        videoInmuebleRepository.deleteAll(videoInmuebleRepository.findByInmuebleId(id));
        inmuebleCaracteristicaRepository.deleteAll(inmuebleCaracteristicaRepository.findByInmuebleId(id));
        condicionCaptacionRepository.findByInmuebleId(id).ifPresent(condicionCaptacionRepository::delete);
        checklistJuridicoRepository.findByInmuebleId(id).ifPresent(checklistJuridicoRepository::delete);
        visitaCaptacionRepository.deleteAll(visitaCaptacionRepository.findByInmuebleId(id));
        historialCaptacionRepository.deleteAll(historialCaptacionRepository.findByInmuebleIdOrderByFechaDesc(id));

        inmuebleRepository.delete(inmueble);
    }

    public InmuebleResponse actualizar(UUID id, InmuebleRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + id));

        // Reemplazo incondicional (no "if != null"): el formulario de edicion siempre envia el
        // objeto completo, y antd reporta null (no ausente) al vaciar un campo opcional. Con
        // guardas "!= null" ese null se ignoraba en silencio y el valor viejo nunca se borraba.
        inmueble.setTipoPropiedad(request.getTipoPropiedad());
        inmueble.setCiudad(request.getCiudad());
        inmueble.setDireccionExacta(request.getDireccionExacta());
        inmueble.setZona(request.getZona());
        inmueble.setEntorno(request.getEntorno());
        inmueble.setAreaTotal(request.getAreaTotal());
        inmueble.setAreaConstruida(request.getAreaConstruida());
        inmueble.setAreaPrivada(request.getAreaPrivada());
        inmueble.setNumeroHabitaciones(request.getNumeroHabitaciones());
        inmueble.setNumeroBanos(request.getNumeroBanos());
        inmueble.setTipoCocina(request.getTipoCocina());
        inmueble.setEstadoFisico(request.getEstadoFisico());
        inmueble.setMatriculaInmobiliaria(request.getMatriculaInmobiliaria());
        inmueble.setReferenciaCatastral(request.getReferenciaCatastral());
        inmueble.setExpectativaPrecio(request.getExpectativaPrecio());
        inmueble.setPrecioMinimoAceptado(request.getPrecioMinimoAceptado());
        inmueble.setPrecioSugerido(request.getPrecioSugerido());
        inmueble.setValorAdministracion(request.getValorAdministracion());
        inmueble.setValorArriendo(request.getValorArriendo());
        inmueble.setValorImpuestos(request.getValorImpuestos());
        inmueble.setValorAdeudado(request.getValorAdeudado());
        inmueble.setEscuchaOfertas(request.getEscuchaOfertas());
        inmueble.setEstratoSocioeconomico(request.getEstratoSocioeconomico());
        inmueble.setUsoSuelo(request.getUsoSuelo());
        inmueble.setLatitud(request.getLatitud());
        inmueble.setLongitud(request.getLongitud());
        inmueble.setPuntosClavesCercanos(request.getPuntosClavesCercanos());
        inmueble.setSituada(request.getSituada());
        inmueble.setNiveles(request.getNiveles());
        inmueble.setPiso(request.getPiso());
        inmueble.setVista(request.getVista());
        inmueble.setTipoAcceso(request.getTipoAcceso());
        inmueble.setMedidaFrente(request.getMedidaFrente());
        inmueble.setMedidaFondo(request.getMedidaFondo());
        inmueble.setOtrasCaracteristicasLote(request.getOtrasCaracteristicasLote());
        inmueble.setQuienOcupaActualmente(request.getQuienOcupaActualmente());
        inmueble.setEstadoJuridico(request.getEstadoJuridico() != null
                ? EstadoJuridico.valueOf(request.getEstadoJuridico()) : null);

        inmueble.getPuntosReferencia().clear();
        if (request.getPuntosReferencia() != null) {
            inmueble.getPuntosReferencia().addAll(resolverPuntosReferencia(request.getPuntosReferencia()));
        }

        Inmueble actualizado = inmuebleRepository.save(inmueble);

        return mapearAResponse(actualizado);
    }

    public InmuebleResponse cambiarEstadoCaptacion(UUID id, CambiarEstadoCaptacionRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + id));

        EstadoCaptacion estadoAnterior = inmueble.getEstadoCaptacion();
        EstadoCaptacion nuevoEstado = EstadoCaptacion.valueOf(request.getNuevoEstado());
        inmueble.setEstadoCaptacion(nuevoEstado);

        Inmueble actualizado = inmuebleRepository.save(inmueble);

        registrarHistorial(actualizado, "Estado de captacion cambiado de " + estadoAnterior.name()
                + " a " + nuevoEstado.name());

        return mapearAResponse(actualizado);
    }

    private Set<PuntoReferencia> resolverPuntosReferencia(List<String> nombres) {
        Set<PuntoReferencia> resueltos = new LinkedHashSet<>();
        if (nombres == null) {
            return resueltos;
        }

        for (String nombre : nombres) {
            if (nombre == null || nombre.isBlank()) {
                continue;
            }
            String nombreLimpio = nombre.trim();
            PuntoReferencia puntoReferencia = puntoReferenciaRepository.findByNombreIgnoreCase(nombreLimpio)
                    .orElseGet(() -> puntoReferenciaRepository.save(
                            PuntoReferencia.builder().nombre(nombreLimpio).build()));
            resueltos.add(puntoReferencia);
        }

        return resueltos;
    }

    private void registrarHistorial(Inmueble inmueble, String evento) {
        HistorialCaptacion historial = HistorialCaptacion.builder()
                .inmueble(inmueble)
                .evento(evento)
                .build();

        historialCaptacionRepository.save(historial);
    }

    private InmuebleResponse mapearAResponse(Inmueble inmueble) {
        return InmuebleResponse.builder()
                .id(inmueble.getId())
                .propietarioId(inmueble.getPropietario().getId())
                .propietarioNombre(inmueble.getPropietario().getNombre())
                .codigo(inmueble.getCodigo())
                .tipoPropiedad(inmueble.getTipoPropiedad())
                .ciudad(inmueble.getCiudad())
                .direccionExacta(inmueble.getDireccionExacta())
                .zona(inmueble.getZona())
                .entorno(inmueble.getEntorno())
                .areaTotal(inmueble.getAreaTotal())
                .areaConstruida(inmueble.getAreaConstruida())
                .areaPrivada(inmueble.getAreaPrivada())
                .numeroHabitaciones(inmueble.getNumeroHabitaciones())
                .numeroBanos(inmueble.getNumeroBanos())
                .tipoCocina(inmueble.getTipoCocina())
                .estadoFisico(inmueble.getEstadoFisico())
                .matriculaInmobiliaria(inmueble.getMatriculaInmobiliaria())
                .referenciaCatastral(inmueble.getReferenciaCatastral())
                .expectativaPrecio(inmueble.getExpectativaPrecio())
                .precioMinimoAceptado(inmueble.getPrecioMinimoAceptado())
                .precioSugerido(inmueble.getPrecioSugerido())
                .valorAdministracion(inmueble.getValorAdministracion())
                .valorArriendo(inmueble.getValorArriendo())
                .valorImpuestos(inmueble.getValorImpuestos())
                .valorAdeudado(inmueble.getValorAdeudado())
                .escuchaOfertas(inmueble.isEscuchaOfertas())
                .estratoSocioeconomico(inmueble.getEstratoSocioeconomico())
                .usoSuelo(inmueble.getUsoSuelo())
                .latitud(inmueble.getLatitud())
                .longitud(inmueble.getLongitud())
                .puntosClavesCercanos(inmueble.getPuntosClavesCercanos())
                .situada(inmueble.getSituada())
                .niveles(inmueble.getNiveles())
                .piso(inmueble.getPiso())
                .vista(inmueble.getVista())
                .tipoAcceso(inmueble.getTipoAcceso())
                .medidaFrente(inmueble.getMedidaFrente())
                .medidaFondo(inmueble.getMedidaFondo())
                .otrasCaracteristicasLote(inmueble.getOtrasCaracteristicasLote())
                .quienOcupaActualmente(inmueble.getQuienOcupaActualmente())
                .estadoJuridico(inmueble.getEstadoJuridico().name())
                .estadoCaptacion(inmueble.getEstadoCaptacion().name())
                .puntosReferencia(inmueble.getPuntosReferencia().stream()
                        .map(PuntoReferencia::getNombre)
                        .sorted(Comparator.naturalOrder())
                        .toList())
                .descripcionPublica(inmueble.getDescripcionPublica())
                .ganchoEmocional(inmueble.getGanchoEmocional())
                .landingActiva(inmueble.isLandingActiva())
                .landingMostrarPrecio(inmueble.isLandingMostrarPrecio())
                .createdAt(inmueble.getCreatedAt())
                .build();
    }

    public InmuebleResponse actualizarLanding(UUID id, ActualizarLandingInmuebleRequest request) {
        Inmueble inmueble = inmuebleRepository.findById(id)
                .orElseThrow(() -> new RecursoNoEncontradoException("Inmueble no encontrado con id: " + id));

        if (request.getDescripcionPublica() != null) {
            inmueble.setDescripcionPublica(request.getDescripcionPublica());
        }
        if (request.getGanchoEmocional() != null) {
            inmueble.setGanchoEmocional(request.getGanchoEmocional());
        }
        if (request.getLandingActiva() != null) {
            inmueble.setLandingActiva(request.getLandingActiva());
        }
        if (request.getLandingMostrarPrecio() != null) {
            inmueble.setLandingMostrarPrecio(request.getLandingMostrarPrecio());
        }

        Inmueble actualizado = inmuebleRepository.save(inmueble);

        return mapearAResponse(actualizado);
    }
}