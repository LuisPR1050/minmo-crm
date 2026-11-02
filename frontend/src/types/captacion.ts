export type EstadoCaptacion =
  | 'PROSPECTO'
  | 'CONTACTADO'
  | 'VISITADO'
  | 'CAPTADO_CON_MATERIAL'
  | 'CAPTADO_SIN_MATERIAL'
  | 'EN_NEGOCIACION'
  | 'VENDIDO'
  | 'NEGOCIO_PERDIDO'
  | 'DESCARTADO';

// Pipeline principal según el documento de requerimientos. EN_NEGOCIACION / VENDIDO /
// NEGOCIO_PERDIDO son estados de cierre que llegan desde el módulo de Negociación.
export const ETAPAS_PIPELINE_CAPTACION: EstadoCaptacion[] = [
  'PROSPECTO',
  'CONTACTADO',
  'VISITADO',
  'CAPTADO_SIN_MATERIAL',
  'CAPTADO_CON_MATERIAL',
];

export const ESTADOS_CAPTACION: EstadoCaptacion[] = [
  'PROSPECTO',
  'CONTACTADO',
  'VISITADO',
  'CAPTADO_SIN_MATERIAL',
  'CAPTADO_CON_MATERIAL',
  'EN_NEGOCIACION',
  'VENDIDO',
  'NEGOCIO_PERDIDO',
  'DESCARTADO',
];

export const ETIQUETA_ESTADO_CAPTACION: Record<EstadoCaptacion, string> = {
  PROSPECTO: 'Prospecto',
  CONTACTADO: 'Contactado',
  VISITADO: 'Visitado',
  CAPTADO_CON_MATERIAL: 'Captado con material',
  CAPTADO_SIN_MATERIAL: 'Captado sin material',
  EN_NEGOCIACION: 'En negociación',
  VENDIDO: 'Vendido',
  NEGOCIO_PERDIDO: 'Negocio perdido',
  DESCARTADO: 'Descartado',
};

export type TipoPropiedad = 'CASA' | 'APARTAMENTO' | 'CASA_LOTE' | 'LOTE' | 'CASA_CAMPESTRE';

export const TIPOS_PROPIEDAD: TipoPropiedad[] = ['CASA', 'APARTAMENTO', 'CASA_LOTE', 'LOTE', 'CASA_CAMPESTRE'];

export const ETIQUETA_TIPO_PROPIEDAD: Record<TipoPropiedad, string> = {
  CASA: 'Casa',
  APARTAMENTO: 'Apartamento',
  CASA_LOTE: 'Casa – Lote',
  LOTE: 'Terreno',
  CASA_CAMPESTRE: 'Casa Campestre',
};

export type EstadoJuridico =
  | 'LIBRE'
  | 'HIPOTECA'
  | 'LEASING'
  | 'PATRIMONIO_DE_FAMILIA'
  | 'VIVIENDA_FAMILIAR'
  | 'EMBARGO'
  | 'PROCESO_INSOLVENCIA'
  | 'SUCESION_POR_REALIZAR'
  | 'SUCESION_EN_CURSO'
  | 'DERECHO_DE_PREFERENCIA'
  | 'PROHIBICION_DE_VENTA'
  | 'LEASING_HABITACIONAL';

export const ESTADOS_JURIDICOS: EstadoJuridico[] = [
  'LIBRE',
  'HIPOTECA',
  'LEASING',
  'PATRIMONIO_DE_FAMILIA',
  'VIVIENDA_FAMILIAR',
  'EMBARGO',
  'PROCESO_INSOLVENCIA',
  'SUCESION_POR_REALIZAR',
  'SUCESION_EN_CURSO',
  'DERECHO_DE_PREFERENCIA',
  'PROHIBICION_DE_VENTA',
  'LEASING_HABITACIONAL',
];

export const ETIQUETA_ESTADO_JURIDICO: Record<EstadoJuridico, string> = {
  LIBRE: 'Libre',
  HIPOTECA: 'Hipoteca',
  LEASING: 'Leasing',
  PATRIMONIO_DE_FAMILIA: 'Patrimonio de familia',
  VIVIENDA_FAMILIAR: 'Vivienda familiar',
  EMBARGO: 'Embargo',
  PROCESO_INSOLVENCIA: 'Proceso de insolvencia',
  SUCESION_POR_REALIZAR: 'Sucesión por realizar',
  SUCESION_EN_CURSO: 'Sucesión en curso',
  DERECHO_DE_PREFERENCIA: 'Derecho de preferencia',
  PROHIBICION_DE_VENTA: 'Prohibición de venta',
  LEASING_HABITACIONAL: 'Leasing habitacional',
};

export const TIPOS_CONTACTO_PROPIETARIO = [
  'Directo',
  'Whatsapp',
  'Facebook',
  'Instagram',
  'Portales',
  'Youtube',
  'Google',
  'TikTok',
  'Inmobiliaria aliada',
  'Asesor aliado',
  'Vigilante-Portería',
  'Tramitador de créditos',
  'Administrador de conjunto',
  'Cliente comprador',
  'Cliente vendedor',
  'Vecino de una propiedad',
];

export const TIPOS_INCENTIVO = [
  'Sin incentivo',
  'Valor fijo por referido',
  'Valor fijo por captación',
  'Valor fijo por cierre',
  'Porcentaje de comisión',
  'Porcentaje sobre valor de venta',
  'Participación en sobreprecio',
  'Bono por resultado',
  'Incentivo negociado',
  'Otro',
];

export const ZONAS_VALLEDUPAR = [
  'Norte',
  'Novalito',
  'Centro norte',
  'Centro',
  'Garupal - Las Flores',
  'Batallón la Popa',
  'Comuna 4',
  'Mayales',
  'UPC',
  'Las Americas',
  'Rural',
  'Campestre',
];

export const ENTORNOS = ['Barrio', 'Urbanización', 'Conjunto Cerrado', 'Edificio', 'Condominio', 'Reserva campestre'];

export interface Propietario {
  id: string;
  nombre: string;
  telefonoPrincipal: string;
  whatsapp?: string;
  correoElectronico?: string;
  tipoContacto: string;
  tipoIncentivo?: string;
  valorIncentivo?: number;
  referenciadorNombre?: string;
  referenciadorTelefono?: string;
  createdAt: string;
}

export interface PropietarioRequest {
  nombre: string;
  telefonoPrincipal: string;
  whatsapp?: string;
  correoElectronico?: string;
  tipoContacto: string;
  tipoIncentivo?: string;
  valorIncentivo?: number;
  referenciadorNombre?: string;
  referenciadorTelefono?: string;
}

export interface Inmueble {
  id: string;
  propietarioId: string;
  propietarioNombre: string;
  codigo: string;
  tipoPropiedad: string;
  ciudad?: string;
  direccionExacta?: string;
  zona?: string;
  entorno?: string;
  areaTotal?: number;
  areaConstruida?: number;
  areaPrivada?: number;
  numeroHabitaciones?: number;
  numeroBanos?: number;
  tipoCocina?: string;
  estadoFisico?: string;
  matriculaInmobiliaria?: string;
  referenciaCatastral?: string;
  expectativaPrecio?: number;
  precioMinimoAceptado?: number;
  precioSugerido?: number;
  valorAdministracion?: number;
  valorArriendo?: number;
  valorImpuestos?: number;
  valorAdeudado?: number;
  escuchaOfertas: boolean;
  estratoSocioeconomico?: number;
  usoSuelo?: string;
  latitud?: number;
  longitud?: number;
  puntosClavesCercanos?: string;
  situada?: string;
  niveles?: string;
  piso?: number;
  vista?: string;
  tipoAcceso?: string;
  medidaFrente?: number;
  medidaFondo?: number;
  otrasCaracteristicasLote?: string;
  quienOcupaActualmente?: string;
  estadoJuridico: EstadoJuridico;
  estadoCaptacion: EstadoCaptacion;
  puntosReferencia?: string[];
  descripcionPublica?: string;
  ganchoEmocional?: string;
  landingActiva: boolean;
  landingMostrarPrecio: boolean;
  createdAt: string;
}

export interface PuntoReferencia {
  id: string;
  nombre: string;
}

export interface PrecioSugeridoIA {
  inmuebleId: string;
  precioSugerido: number;
  rangoMinimo: number;
  rangoMaximo: number;
  justificacion: string;
  comparablesUsados: number;
}

export interface AnalisisObjecionesInmueble {
  inmuebleId: string;
  resumen: string;
  recomendaciones: string[];
  totalVisitas: number;
  visitasAnalizadas: number;
}

export interface ClienteSugerido {
  clienteId: string;
  clienteNombre: string;
  requerimientoId: string;
  motivo: string;
}

export interface InmuebleRequest {
  propietarioId?: string;
  tipoPropiedad?: string;
  ciudad?: string;
  direccionExacta?: string;
  zona?: string;
  entorno?: string;
  areaTotal?: number;
  areaConstruida?: number;
  areaPrivada?: number;
  numeroHabitaciones?: number;
  numeroBanos?: number;
  tipoCocina?: string;
  estadoFisico?: string;
  matriculaInmobiliaria?: string;
  referenciaCatastral?: string;
  expectativaPrecio?: number;
  precioMinimoAceptado?: number;
  precioSugerido?: number;
  valorAdministracion?: number;
  valorArriendo?: number;
  valorImpuestos?: number;
  valorAdeudado?: number;
  escuchaOfertas?: boolean;
  estratoSocioeconomico?: number;
  usoSuelo?: string;
  latitud?: number;
  longitud?: number;
  puntosClavesCercanos?: string;
  situada?: string;
  niveles?: string;
  piso?: number;
  vista?: string;
  tipoAcceso?: string;
  medidaFrente?: number;
  medidaFondo?: number;
  otrasCaracteristicasLote?: string;
  quienOcupaActualmente?: string;
  estadoJuridico?: EstadoJuridico;
  estadoCaptacion?: EstadoCaptacion;
  puntosReferencia?: string[];
}

export interface ArchivoAdjunto {
  id: string;
  inmuebleId: string;
  tipo: string;
  url: string;
  esPrincipal: boolean;
  createdAt: string;
}

export type OrigenVideo = 'LINK_EXTERNO' | 'ARCHIVO_SUBIDO';

export interface VideoInmueble {
  id: string;
  inmuebleId: string;
  url: string;
  origen: OrigenVideo;
  tipoContenido?: string;
  createdAt: string;
}

export interface VideoInmuebleRequest {
  url: string;
}

export interface ActualizarLandingInmuebleRequest {
  descripcionPublica?: string;
  ganchoEmocional?: string;
  landingActiva?: boolean;
  landingMostrarPrecio?: boolean;
}

export type CategoriaCaracteristica =
  | 'ZONA_COMUN'
  | 'SERVICIO'
  | 'DISTRIBUCION_INTERNA'
  | 'ADICIONAL'
  | 'AREA_SOCIAL'
  | 'AREA_COCINA_SERVICIO'
  | 'AREA_PRIVADA'
  | 'AREA_ACCESO_ALMACENAMIENTO'
  | 'AREA_EXTERIOR_CASA'
  | 'ZONA_COMUN_BASICA'
  | 'ZONA_SOCIAL_RECREATIVA'
  | 'ZONA_VEHICULAR'
  | 'OTRA_ZONA_COMUN'
  | 'ZONA_TECNICA_SERVICIO'
  | 'ELEMENTO_INCLUIDO'
  | 'SERVICIO_EDIFICIO'
  | 'SERVICIO_PUBLICO';

export const ETIQUETA_CATEGORIA_CARACTERISTICA: Record<CategoriaCaracteristica, string> = {
  ZONA_COMUN: 'Zonas comunes',
  SERVICIO: 'Servicios',
  DISTRIBUCION_INTERNA: 'Distribución interna',
  ADICIONAL: 'Características adicionales',
  AREA_SOCIAL: 'Áreas sociales de la vivienda',
  AREA_COCINA_SERVICIO: 'Áreas de cocina y servicio',
  AREA_PRIVADA: 'Áreas privadas',
  AREA_ACCESO_ALMACENAMIENTO: 'Áreas de acceso y almacenamiento',
  AREA_EXTERIOR_CASA: 'Áreas exteriores (casas)',
  ZONA_COMUN_BASICA: 'Zonas comunes básicas (conjunto/edificio)',
  ZONA_SOCIAL_RECREATIVA: 'Zonas sociales y recreativas',
  ZONA_VEHICULAR: 'Zonas vehiculares',
  OTRA_ZONA_COMUN: 'Otras zonas comunes',
  ZONA_TECNICA_SERVICIO: 'Zonas técnicas y de servicio',
  ELEMENTO_INCLUIDO: 'Elementos incluidos',
  SERVICIO_EDIFICIO: 'Servicios',
  SERVICIO_PUBLICO: 'Servicios públicos / infraestructura',
};

// Categorías de la pestaña "Distribución" (antes "Características"): zonas/áreas del inmueble.
export const CATEGORIAS_DISTRIBUCION: CategoriaCaracteristica[] = [
  'ZONA_COMUN',
  'SERVICIO',
  'DISTRIBUCION_INTERNA',
  'ADICIONAL',
  'AREA_SOCIAL',
  'AREA_COCINA_SERVICIO',
  'AREA_PRIVADA',
  'AREA_ACCESO_ALMACENAMIENTO',
  'AREA_EXTERIOR_CASA',
  'ZONA_COMUN_BASICA',
  'ZONA_SOCIAL_RECREATIVA',
  'ZONA_VEHICULAR',
  'OTRA_ZONA_COMUN',
  'ZONA_TECNICA_SERVICIO',
];

// Categorías de la nueva pestaña "Servicios y equipamiento".
export const CATEGORIAS_SERVICIOS_EQUIPAMIENTO: CategoriaCaracteristica[] = [
  'ELEMENTO_INCLUIDO',
  'SERVICIO_EDIFICIO',
  'SERVICIO_PUBLICO',
];

export interface Caracteristica {
  id: string;
  nombre: string;
  categoria: CategoriaCaracteristica;
}

export type OrigenPropiedad = 'PROPIETARIO_DIRECTO' | 'ALIADO';

export const ETIQUETA_ORIGEN_PROPIEDAD: Record<OrigenPropiedad, string> = {
  PROPIETARIO_DIRECTO: 'Propietario directo',
  ALIADO: 'Inmobiliaria o asesor aliado',
};

export type TipoAliado = 'INMOBILIARIA' | 'ASESOR';

export const TIPOS_ALIADO: TipoAliado[] = ['INMOBILIARIA', 'ASESOR'];

export const ETIQUETA_TIPO_ALIADO: Record<TipoAliado, string> = {
  INMOBILIARIA: 'Inmobiliaria',
  ASESOR: 'Asesor',
};

export interface Aliado {
  id: string;
  nombre: string;
  telefono?: string;
  tipo: TipoAliado;
  createdAt: string;
}

export interface AliadoRequest {
  nombre: string;
  telefono?: string;
  tipo: TipoAliado;
}

export interface CondicionCaptacion {
  id: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  asesorId?: string;
  origenPropiedad?: OrigenPropiedad;
  aliadoNombre?: string;
  aliadoTelefono?: string;
  aliadoId?: string;
  whatsappAsesor?: string;
  tipoComision?: string;
  valorComision?: number;
  exclusividad: boolean;
  createdAt: string;
}

export interface CondicionCaptacionRequest {
  inmuebleId: string;
  asesorId?: string;
  propietarioId?: string;
  origenPropiedad?: OrigenPropiedad;
  aliadoNombre?: string;
  aliadoTelefono?: string;
  aliadoId?: string;
  whatsappAsesor?: string;
  tipoComision?: string;
  valorComision?: number;
  exclusividad: boolean;
}

export interface ChecklistJuridico {
  id: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  tieneHipoteca: boolean;
  valorHipoteca?: number;
  tieneEmbargo: boolean;
  valorEmbargo?: number;
  tienePatrimonioFamilia: boolean;
  tieneViviendaFamiliar: boolean;
  createdAt: string;
}

export interface ChecklistJuridicoRequest {
  inmuebleId: string;
  tieneHipoteca: boolean;
  valorHipoteca?: number;
  tieneEmbargo: boolean;
  valorEmbargo?: number;
  tienePatrimonioFamilia: boolean;
  tieneViviendaFamiliar: boolean;
}

export interface HistorialCaptacion {
  id: string;
  inmuebleId: string;
  usuarioId?: string;
  evento: string;
  fecha: string;
}

export type EstadoVisitaCaptacion = 'PENDIENTE_CONFIRMAR' | 'CONFIRMADA' | 'REAGENDADA' | 'REALIZADA' | 'CANCELADA';

export const ETIQUETA_ESTADO_VISITA_CAPTACION: Record<EstadoVisitaCaptacion, string> = {
  PENDIENTE_CONFIRMAR: 'Pendiente de confirmar',
  CONFIRMADA: 'Confirmada',
  REAGENDADA: 'Reagendada',
  REALIZADA: 'Realizada',
  CANCELADA: 'Cancelada',
};

export interface VisitaCaptacion {
  id: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  asesorId?: string;
  fechaVisita?: string;
  horaInicio?: string;
  horaFin?: string;
  estado: EstadoVisitaCaptacion;
  observaciones?: string;
  createdAt: string;
}

export interface VisitaCaptacionRequest {
  inmuebleId: string;
  asesorId?: string;
  fechaVisita?: string;
  horaInicio?: string;
  horaFin?: string;
  observaciones?: string;
}
