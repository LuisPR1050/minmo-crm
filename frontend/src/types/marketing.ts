export type TipoOperacionPublicacion = 'VENTA' | 'ARRIENDO';

export const TIPOS_OPERACION_PUBLICACION: TipoOperacionPublicacion[] = ['VENTA', 'ARRIENDO'];

export type EstadoPublicacion = 'BORRADOR' | 'ACTIVA' | 'PAUSADA' | 'RETIRADA' | 'VENCIDA';

export const ESTADOS_PUBLICACION: EstadoPublicacion[] = ['BORRADOR', 'ACTIVA', 'PAUSADA', 'RETIRADA', 'VENCIDA'];

export const ETIQUETA_ESTADO_PUBLICACION: Record<EstadoPublicacion, string> = {
  BORRADOR: 'Borrador',
  ACTIVA: 'Activa',
  PAUSADA: 'Pausada',
  RETIRADA: 'Retirada',
  VENCIDA: 'Vencida',
};

export type ObjetivoVideoMarketing =
  | 'VENDER'
  | 'LANZAMIENTO_PROPIEDAD'
  | 'BAJO_PRECIO'
  | 'NUEVA_CAPTACION'
  | 'TESTIMONIO_CLIENTE'
  | 'MARCA_PERSONAL'
  | 'CONTENIDO_EDUCATIVO';

export const OBJETIVOS_VIDEO_MARKETING: ObjetivoVideoMarketing[] = [
  'VENDER',
  'LANZAMIENTO_PROPIEDAD',
  'BAJO_PRECIO',
  'NUEVA_CAPTACION',
  'TESTIMONIO_CLIENTE',
  'MARCA_PERSONAL',
  'CONTENIDO_EDUCATIVO',
];

export const ETIQUETA_OBJETIVO_VIDEO: Record<ObjetivoVideoMarketing, string> = {
  VENDER: 'Publicación para vender',
  LANZAMIENTO_PROPIEDAD: 'Lanzamiento de nueva propiedad',
  BAJO_PRECIO: 'Bajó de precio',
  NUEVA_CAPTACION: 'Nueva captación',
  TESTIMONIO_CLIENTE: 'Testimonio de cliente',
  MARCA_PERSONAL: 'Marca personal',
  CONTENIDO_EDUCATIVO: 'Contenido educativo',
};

export type EstiloContenidoVideo =
  | 'ELEGANTE'
  | 'EXCLUSIVO'
  | 'CERCANO'
  | 'PROFESIONAL'
  | 'EMOCIONAL'
  | 'COMERCIAL'
  | 'MINIMALISTA'
  | 'CORPORATIVO'
  | 'PREMIUM'
  | 'JUVENIL';

export const ESTILOS_CONTENIDO_VIDEO: EstiloContenidoVideo[] = [
  'ELEGANTE',
  'EXCLUSIVO',
  'CERCANO',
  'PROFESIONAL',
  'EMOCIONAL',
  'COMERCIAL',
  'MINIMALISTA',
  'CORPORATIVO',
  'PREMIUM',
  'JUVENIL',
];

export type LlamadoAccionVideo =
  | 'AGENDA_VISITA'
  | 'ESCRIBEME_WHATSAPP'
  | 'SOLICITA_INFORMACION'
  | 'CONOCE_PROPIEDAD'
  | 'OTRO';

export const LLAMADOS_ACCION_VIDEO: LlamadoAccionVideo[] = [
  'AGENDA_VISITA',
  'ESCRIBEME_WHATSAPP',
  'SOLICITA_INFORMACION',
  'CONOCE_PROPIEDAD',
  'OTRO',
];

export const ETIQUETA_LLAMADO_ACCION: Record<LlamadoAccionVideo, string> = {
  AGENDA_VISITA: 'Agenda tu visita',
  ESCRIBEME_WHATSAPP: 'Escríbeme por WhatsApp',
  SOLICITA_INFORMACION: 'Solicita más información',
  CONOCE_PROPIEDAD: 'Conoce esta propiedad',
  OTRO: 'Otro',
};

export type EstadoSolicitudVideo = 'BORRADOR' | 'EN_PROCESO' | 'GENERADO' | 'FALLIDO' | 'PUBLICADO' | 'DESCARTADO';

export const ESTADOS_SOLICITUD_VIDEO: EstadoSolicitudVideo[] = [
  'BORRADOR',
  'EN_PROCESO',
  'GENERADO',
  'FALLIDO',
  'PUBLICADO',
  'DESCARTADO',
];

export const ETIQUETA_ESTADO_SOLICITUD_VIDEO: Record<EstadoSolicitudVideo, string> = {
  BORRADOR: 'Borrador',
  EN_PROCESO: 'En proceso',
  GENERADO: 'Generado',
  FALLIDO: 'Fallo la generacion',
  PUBLICADO: 'Publicado',
  DESCARTADO: 'Descartado',
};

export interface PublicacionInmueble {
  id: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  tipoOperacion: TipoOperacionPublicacion;
  mostrarPrecio: boolean;
  mostrarContacto: boolean;
  mostrarCorreo: boolean;
  comisionCompartida: boolean;
  destacada: boolean;
  descripcionCorta?: string;
  descripcionLarga?: string;
  descripcionOptimizada?: string;
  estado: EstadoPublicacion;
  fechaPublicacion?: string;
  createdAt: string;
}

export interface PublicacionInmuebleRequest {
  inmuebleId: string;
  tipoOperacion: TipoOperacionPublicacion;
  mostrarPrecio?: boolean;
  mostrarContacto?: boolean;
  mostrarCorreo?: boolean;
  comisionCompartida?: boolean;
  destacada?: boolean;
}

export interface ActualizarPublicacionInmuebleRequest {
  tipoOperacion?: TipoOperacionPublicacion;
  mostrarPrecio?: boolean;
  mostrarContacto?: boolean;
  mostrarCorreo?: boolean;
  comisionCompartida?: boolean;
  destacada?: boolean;
  descripcionCorta?: string;
  descripcionLarga?: string;
  descripcionOptimizada?: string;
}

export interface AnalisisFotos {
  publicacionId: string;
  puntuacionGeneral: number;
  fortalezas: string[];
  problemas: string[];
  recomendaciones: string[];
  fotosFaltantes: string[];
  fotosAnalizadas: number;
}

// ---- Portales inmobiliarios ----

export interface PortalInmobiliario {
  id: string;
  nombre: string;
}

export type EstadoPublicacionPortal = 'ACTIVA' | 'PAUSADA' | 'RETIRADA';

export const ESTADOS_PUBLICACION_PORTAL: EstadoPublicacionPortal[] = ['ACTIVA', 'PAUSADA', 'RETIRADA'];

export const ETIQUETA_ESTADO_PUBLICACION_PORTAL: Record<EstadoPublicacionPortal, string> = {
  ACTIVA: 'Activa',
  PAUSADA: 'Pausada',
  RETIRADA: 'Retirada',
};

export interface PublicacionInmueblePortal {
  id: string;
  portalId: string;
  portalNombre: string;
  estado: EstadoPublicacionPortal;
}

// ---- Fotos de publicación ----

export interface FotoPublicacion {
  id: string;
  url: string;
  esPrincipal: boolean;
  createdAt: string;
}

// ---- Estadísticas por portal ----

export interface EstadisticaPortal {
  id: string;
  vistas?: number;
  contactos?: number;
  fechaCorte?: string;
}

export interface EstadisticaPortalRequest {
  vistas?: number;
  contactos?: number;
  fechaCorte?: string;
}

export interface ResumenEstadisticasPublicacion {
  publicacionInmuebleId: string;
  totalVistas: number;
  totalContactos: number;
}

// ---- Historial de publicación ----

export interface HistorialPublicacion {
  id: string;
  publicacionInmuebleId: string;
  evento: string;
  fecha: string;
}

// ---- Solicitud de video marketing ----

export interface SolicitudVideoMarketing {
  id: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  objetivoPublicacion?: ObjetivoVideoMarketing;
  estiloContenido?: EstiloContenidoVideo;
  duracionMinimaMinutos?: number;
  duracionMaximaMinutos?: number;
  llamadoAccion?: LlamadoAccionVideo;
  logoUrl?: string;
  musicaFondoUrl?: string;
  estado: EstadoSolicitudVideo;
  videoGeneradoUrl?: string;
  tituloGenerado?: string;
  subtituloGenerado?: string;
  descripcionGenerada?: string;
  llamadoAccionTexto?: string;
  hashtags?: string;
  fechaGeneracionCopy?: string;
  createdAt: string;
}

export interface SolicitudVideoMarketingRequest {
  inmuebleId: string;
  objetivoPublicacion?: ObjetivoVideoMarketing;
  estiloContenido?: EstiloContenidoVideo;
  duracionMinimaMinutos?: number;
  duracionMaximaMinutos?: number;
  llamadoAccion?: LlamadoAccionVideo;
}

export interface ActualizarSolicitudVideoMarketingRequest {
  objetivoPublicacion?: ObjetivoVideoMarketing;
  estiloContenido?: EstiloContenidoVideo;
  duracionMinimaMinutos?: number;
  duracionMaximaMinutos?: number;
  llamadoAccion?: LlamadoAccionVideo;
  logoUrl?: string;
}

export interface CopiaVideoCanal {
  id: string;
  canalNombre: string;
  texto: string;
}

// ---- Catálogos de canal / destino / público del video ----

export interface CanalPublicacionVideo {
  id: string;
  nombre: string;
}

export interface DestinoVideo {
  id: string;
  nombre: string;
}

export interface TipoPublicoVideo {
  id: string;
  nombre: string;
}

// ---- Fotos y video fuente de la solicitud ----

export interface FotoSolicitudVideo {
  id: string;
  url: string;
  createdAt: string;
}

export type TipoOrigenArchivo = 'SUBIDO' | 'ENLACE';

export const TIPOS_ORIGEN_ARCHIVO: TipoOrigenArchivo[] = ['SUBIDO', 'ENLACE'];

export interface VideoFuenteSolicitud {
  id: string;
  url: string;
  tipoOrigen: TipoOrigenArchivo;
  createdAt: string;
}

export interface VideoFuenteSolicitudRequest {
  url: string;
  tipoOrigen: TipoOrigenArchivo;
}

export interface VideoPublicacion {
  id: string;
  url: string;
  tipoOrigen: TipoOrigenArchivo;
  createdAt: string;
}

export interface VideoPublicacionRequest {
  url: string;
  tipoOrigen: TipoOrigenArchivo;
}
