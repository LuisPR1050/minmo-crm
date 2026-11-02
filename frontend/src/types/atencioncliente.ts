import type { Inmueble } from './captacion';

export type EstadoPipelineCliente =
  | 'CONTACTO_INICIADO'
  | 'CONTACTO_CALIFICADO'
  | 'OPCIONES_ENVIADAS'
  | 'CITA_AGENDADA'
  | 'VISITA_REALIZADA'
  | 'VISITAR_OTRAS_PROPIEDADES'
  | 'EN_NEGOCIACION'
  | 'ESPERANDO_FIRMA_ESCRITURA'
  | 'EN_REGISTRO'
  | 'NEGOCIO_CERRADO_GANADO'
  | 'NEGOCIO_PERDIDO';

export const ESTADOS_PIPELINE_CLIENTE: EstadoPipelineCliente[] = [
  'CONTACTO_INICIADO',
  'CONTACTO_CALIFICADO',
  'OPCIONES_ENVIADAS',
  'CITA_AGENDADA',
  'VISITA_REALIZADA',
  'VISITAR_OTRAS_PROPIEDADES',
  'EN_NEGOCIACION',
  'ESPERANDO_FIRMA_ESCRITURA',
  'EN_REGISTRO',
  'NEGOCIO_CERRADO_GANADO',
  'NEGOCIO_PERDIDO',
];

// Pipeline principal según el documento de requerimientos. EN_NEGOCIACION / ESPERANDO_FIRMA_ESCRITURA /
// EN_REGISTRO / NEGOCIO_CERRADO_GANADO / NEGOCIO_PERDIDO son estados de cierre que llegan desde Negociación.
export const ETAPAS_PIPELINE_CLIENTE: EstadoPipelineCliente[] = [
  'CONTACTO_INICIADO',
  'CONTACTO_CALIFICADO',
  'OPCIONES_ENVIADAS',
  'CITA_AGENDADA',
  'VISITA_REALIZADA',
  'VISITAR_OTRAS_PROPIEDADES',
];

export const ETIQUETA_ESTADO_PIPELINE_CLIENTE: Record<EstadoPipelineCliente, string> = {
  CONTACTO_INICIADO: 'Contacto iniciado',
  CONTACTO_CALIFICADO: 'Contacto calificado',
  OPCIONES_ENVIADAS: 'Opciones enviadas',
  CITA_AGENDADA: 'Cita / Visita agendada',
  VISITA_REALIZADA: 'Visita realizada',
  VISITAR_OTRAS_PROPIEDADES: 'Visitar otras propiedades',
  EN_NEGOCIACION: 'En negociación',
  ESPERANDO_FIRMA_ESCRITURA: 'Esperando firma de escritura',
  EN_REGISTRO: 'En registro',
  NEGOCIO_CERRADO_GANADO: 'Negocio cerrado - Ganado',
  NEGOCIO_PERDIDO: 'Negocio perdido',
};

export const TIPOS_CONTACTO = [
  'Landing pública',
  'Directo',
  'Referido',
  'Inmobiliaria aliada',
  'Asesor aliado',
  'Vigilante - Portería',
  'Tramitador de créditos',
  'Administrador de conjunto',
  'Cliente comprador',
  'Cliente vendedor',
  'Vecino',
  'Otros',
];

export interface Cliente {
  id: string;
  nombre: string;
  telefono: string;
  whatsapp?: string;
  correoElectronico?: string;
  tipoContacto: string;
  subtipoContacto?: string;
  numeroContacto?: string;
  asesorId?: string;
  estadoPipeline: EstadoPipelineCliente;
  createdAt: string;
}

export interface ClienteRequest {
  nombre: string;
  telefono: string;
  whatsapp?: string;
  correoElectronico?: string;
  tipoContacto: string;
  subtipoContacto?: string;
  numeroContacto?: string;
  asesorId?: string;
}

// ---- Requerimientos del cliente ----

export const TIPOS_PROPIEDAD_REQUERIMIENTO = ['Casa', 'Apartamento', 'Casa o Apartamento', 'Casa Lote', 'Lote'];

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

export const FORMAS_PAGO_REQUERIMIENTO = ['Recursos propios', 'Crédito + recursos propios', 'Solo crédito'];

export const TIEMPOS_PARA_COMPRAR = ['Caliente (0-30 días)', 'Tibio (1-3 meses)', 'Frío (más adelante)'];

export interface RequerimientoCliente {
  id: string;
  clienteId: string;
  tipoPropiedad?: string;
  ciudad?: string;
  zona?: string;
  entorno?: string;
  sitiosClavesCercanos?: string;
  presupuestoMin?: number;
  presupuestoMax?: number;
  formaPago?: string;
  tiempoParaComprar?: string;
  numeroHabitaciones?: number;
  numeroBanos?: number;
  observaciones?: string;
  createdAt: string;
}

export interface RequerimientoClienteRequest {
  clienteId: string;
  tipoPropiedad?: string;
  ciudad?: string;
  zona?: string;
  entorno?: string;
  sitiosClavesCercanos?: string;
  presupuestoMin?: number;
  presupuestoMax?: number;
  formaPago?: string;
  tiempoParaComprar?: string;
  numeroHabitaciones?: number;
  numeroBanos?: number;
  observaciones?: string;
}

export interface InmuebleRecomendado {
  inmueble: Inmueble;
  motivo: string;
}

export interface AnalisisObjeciones {
  clienteId: string;
  resumen: string;
  recomendaciones: string[];
  visitasAnalizadas: number;
}

// ---- Visitas de cliente ----

export type EstadoVisitaCliente =
  | 'PENDIENTE_CONFIRMAR'
  | 'CONFIRMADA'
  | 'REAGENDADA'
  | 'REALIZADA'
  | 'CANCELADA'
  | 'NO_ASISTIO_CLIENTE'
  | 'PROPIETARIO_NO_DISPONIBLE';

export const ESTADOS_VISITA_CLIENTE: EstadoVisitaCliente[] = [
  'PENDIENTE_CONFIRMAR',
  'CONFIRMADA',
  'REAGENDADA',
  'REALIZADA',
  'CANCELADA',
  'NO_ASISTIO_CLIENTE',
  'PROPIETARIO_NO_DISPONIBLE',
];

export const ETIQUETA_ESTADO_VISITA_CLIENTE: Record<EstadoVisitaCliente, string> = {
  PENDIENTE_CONFIRMAR: 'Pendiente de confirmar',
  CONFIRMADA: 'Confirmada',
  REAGENDADA: 'Reagendada',
  REALIZADA: 'Realizada',
  CANCELADA: 'Cancelada',
  NO_ASISTIO_CLIENTE: 'No asistió el cliente',
  PROPIETARIO_NO_DISPONIBLE: 'Propietario no disponible',
};

export type TipoVisita = 'PRESENCIAL' | 'VIRTUAL' | 'RECORRIDO_VIRTUAL_GRABADO';

export const TIPOS_VISITA: TipoVisita[] = ['PRESENCIAL', 'VIRTUAL', 'RECORRIDO_VIRTUAL_GRABADO'];

export const ETIQUETA_TIPO_VISITA: Record<TipoVisita, string> = {
  PRESENCIAL: 'Presencial',
  VIRTUAL: 'Virtual',
  RECORRIDO_VIRTUAL_GRABADO: 'Recorrido virtual grabado',
};

export interface VisitaCliente {
  id: string;
  clienteId: string;
  clienteNombre: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  codigoVisita: string;
  estado: EstadoVisitaCliente;
  tipoVisita: TipoVisita;
  fechaVisita?: string;
  horaInicio?: string;
  horaFin?: string;
  confirmadaPorCliente: boolean;
  confirmadaPorPropietario: boolean;
  recomendaciones?: string;
  createdAt: string;
}

export interface VisitaClienteRequest {
  clienteId: string;
  inmuebleId: string;
  tipoVisita: TipoVisita;
  fechaVisita?: string;
  horaInicio?: string;
  horaFin?: string;
  recomendaciones?: string;
}

// ---- Resultado de la visita ----

export type NivelInteres = 'MUY_INTERESADO' | 'INTERESADO' | 'INTERES_MODERADO' | 'POCO_INTERESADO' | 'NO_INTERESADO';

export const NIVELES_INTERES: NivelInteres[] = [
  'MUY_INTERESADO',
  'INTERESADO',
  'INTERES_MODERADO',
  'POCO_INTERESADO',
  'NO_INTERESADO',
];

export const ETIQUETA_NIVEL_INTERES: Record<NivelInteres, string> = {
  MUY_INTERESADO: 'Muy interesado',
  INTERESADO: 'Interesado',
  INTERES_MODERADO: 'Interés moderado',
  POCO_INTERESADO: 'Poco interesado',
  NO_INTERESADO: 'No interesado',
};

export type IntencionCompra =
  | 'DESEA_PRESENTAR_OFERTA'
  | 'DESEA_SEGUNDA_VISITA'
  | 'DESEA_VISITAR_SIMILARES'
  | 'DESEA_ANALIZAR_INFORMACION'
  | 'NO_ESTA_INTERESADO';

export const INTENCIONES_COMPRA: IntencionCompra[] = [
  'DESEA_PRESENTAR_OFERTA',
  'DESEA_SEGUNDA_VISITA',
  'DESEA_VISITAR_SIMILARES',
  'DESEA_ANALIZAR_INFORMACION',
  'NO_ESTA_INTERESADO',
];

export const ETIQUETA_INTENCION_COMPRA: Record<IntencionCompra, string> = {
  DESEA_PRESENTAR_OFERTA: 'Desea presentar oferta',
  DESEA_SEGUNDA_VISITA: 'Desea una segunda visita',
  DESEA_VISITAR_SIMILARES: 'Desea visitar propiedades similares',
  DESEA_ANALIZAR_INFORMACION: 'Desea analizar la información',
  NO_ESTA_INTERESADO: 'No está interesado',
};

export type ProximaAccion =
  | 'LLAMAR_NUEVAMENTE'
  | 'ENVIAR_DOCUMENTACION'
  | 'ENVIAR_MAS_OPCIONES'
  | 'PROGRAMAR_NUEVA_VISITA'
  | 'INICIAR_NEGOCIACION'
  | 'SOLICITAR_ESTUDIO_CREDITO'
  | 'CERRAR_SOLICITUD';

export const PROXIMAS_ACCIONES: ProximaAccion[] = [
  'LLAMAR_NUEVAMENTE',
  'ENVIAR_DOCUMENTACION',
  'ENVIAR_MAS_OPCIONES',
  'PROGRAMAR_NUEVA_VISITA',
  'INICIAR_NEGOCIACION',
  'SOLICITAR_ESTUDIO_CREDITO',
  'CERRAR_SOLICITUD',
];

export const ETIQUETA_PROXIMA_ACCION: Record<ProximaAccion, string> = {
  LLAMAR_NUEVAMENTE: 'Llamar nuevamente',
  ENVIAR_DOCUMENTACION: 'Enviar documentación',
  ENVIAR_MAS_OPCIONES: 'Enviar más opciones',
  PROGRAMAR_NUEVA_VISITA: 'Programar nueva visita',
  INICIAR_NEGOCIACION: 'Iniciar negociación',
  SOLICITAR_ESTUDIO_CREDITO: 'Solicitar estudio de crédito',
  CERRAR_SOLICITUD: 'Cerrar solicitud',
};

export const UNIDADES_SEGUIMIENTO = ['HORAS', 'DIAS', 'SEMANAS', 'MESES'];

export const ETIQUETA_UNIDAD_SEGUIMIENTO: Record<string, string> = {
  HORAS: 'Horas',
  DIAS: 'Días',
  SEMANAS: 'Semanas',
  MESES: 'Mes(es)',
};

export interface Objecion {
  id: string;
  nombre: string;
}

export interface ResultadoVisita {
  id: string;
  visitaId: string;
  numeroVisita?: number;
  estado: EstadoVisitaCliente;
  nivelInteres?: NivelInteres;
  intencionCompra?: IntencionCompra;
  proximaAccion?: ProximaAccion;
  seguimientoNumero?: number;
  seguimientoUnidad?: string;
  observacionesAsesor?: string;
  objeciones: string[];
  createdAt: string;
  recomendacionesAlternativas?: InmuebleRecomendado[];
  resumenGeneradoIA?: string;
}

export interface ResumenVisitaIA {
  resultadoVisitaId: string;
  resumen: string;
}

export interface ResultadoVisitaRequest {
  visitaId: string;
  numeroVisita?: number;
  estado: EstadoVisitaCliente;
  nivelInteres?: NivelInteres;
  intencionCompra?: IntencionCompra;
  proximaAccion?: ProximaAccion;
  seguimientoNumero?: number;
  seguimientoUnidad?: string;
  observacionesAsesor?: string;
  objecionIds?: string[];
}
