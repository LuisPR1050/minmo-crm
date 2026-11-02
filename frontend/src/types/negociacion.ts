export type EstadoNegociacion =
  | 'NEGOCIACION_INICIADA'
  | 'OFERTA_PRESENTADA'
  | 'CONTRAOFERTA'
  | 'ACUERDO_VERBAL'
  | 'ESPERANDO_FINANCIACION'
  | 'ESPERANDO_FIRMA_PROMESA'
  | 'ESPERANDO_ESCRITURA'
  | 'NEGOCIO_CERRADO'
  | 'NEGOCIO_PERDIDO'
  | 'NEGOCIO_EN_RIESGO';

export const ESTADOS_NEGOCIACION: EstadoNegociacion[] = [
  'NEGOCIACION_INICIADA',
  'OFERTA_PRESENTADA',
  'CONTRAOFERTA',
  'ACUERDO_VERBAL',
  'ESPERANDO_FINANCIACION',
  'ESPERANDO_FIRMA_PROMESA',
  'ESPERANDO_ESCRITURA',
  'NEGOCIO_CERRADO',
  'NEGOCIO_PERDIDO',
  'NEGOCIO_EN_RIESGO',
];

// Pipeline principal segun el documento de requerimientos. Los estados de cierre
// (cerrado / perdido / en riesgo) se alcanzan desde acciones puntuales, no arrastrando tarjetas.
export const ETAPAS_PIPELINE_NEGOCIACION: EstadoNegociacion[] = [
  'NEGOCIACION_INICIADA',
  'OFERTA_PRESENTADA',
  'CONTRAOFERTA',
  'ACUERDO_VERBAL',
  'ESPERANDO_FINANCIACION',
  'ESPERANDO_FIRMA_PROMESA',
  'ESPERANDO_ESCRITURA',
];

export const ETIQUETA_ESTADO_NEGOCIACION: Record<EstadoNegociacion, string> = {
  NEGOCIACION_INICIADA: 'Negociación iniciada',
  OFERTA_PRESENTADA: 'Oferta presentada',
  CONTRAOFERTA: 'Contraoferta',
  ACUERDO_VERBAL: 'Acuerdo verbal',
  ESPERANDO_FINANCIACION: 'Esperando financiación',
  ESPERANDO_FIRMA_PROMESA: 'Esperando firma de promesa',
  ESPERANDO_ESCRITURA: 'Esperando escritura',
  NEGOCIO_CERRADO: 'Negocio cerrado',
  NEGOCIO_PERDIDO: 'Negocio perdido',
  NEGOCIO_EN_RIESGO: 'Negocio en riesgo',
};

export type EstadoFisicoEntrega = 'REMODELADA' | 'COMO_SE_ENCUENTRA' | 'PINTADA' | 'DESOCUPADA';

export const ESTADOS_FISICOS_ENTREGA: EstadoFisicoEntrega[] = [
  'REMODELADA',
  'COMO_SE_ENCUENTRA',
  'PINTADA',
  'DESOCUPADA',
];

export const ETIQUETA_ESTADO_FISICO_ENTREGA: Record<EstadoFisicoEntrega, string> = {
  REMODELADA: 'Remodelada',
  COMO_SE_ENCUENTRA: 'Como se encuentra',
  PINTADA: 'Pintada',
  DESOCUPADA: 'Desocupada',
};

export type MomentoEntrega =
  | 'FIRMA_PROMESA_COMPRAVENTA'
  | 'FIRMA_ESCRITURA'
  | 'UN_DIA_DESPUES_DESEMBOLSO'
  | 'AL_EFECTUAR_TODOS_LOS_PAGOS'
  | 'FECHA_ESPECIFICA';

export const MOMENTOS_ENTREGA: MomentoEntrega[] = [
  'FIRMA_PROMESA_COMPRAVENTA',
  'FIRMA_ESCRITURA',
  'UN_DIA_DESPUES_DESEMBOLSO',
  'AL_EFECTUAR_TODOS_LOS_PAGOS',
  'FECHA_ESPECIFICA',
];

export const ETIQUETA_MOMENTO_ENTREGA: Record<MomentoEntrega, string> = {
  FIRMA_PROMESA_COMPRAVENTA: 'Firma de la promesa de compraventa',
  FIRMA_ESCRITURA: 'Firma de escritura',
  UN_DIA_DESPUES_DESEMBOLSO: 'Un día después del desembolso',
  AL_EFECTUAR_TODOS_LOS_PAGOS: 'Al efectuar todos los pagos',
  FECHA_ESPECIFICA: 'Fecha específica',
};

export interface Negociacion {
  id: string;
  numeroNegocio: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  clienteId: string;
  clienteNombre: string;
  propietarioId: string;
  propietarioNombre: string;
  asesorId?: string;
  fechaInicio?: string;
  fechaFirmaPcv?: string;
  fechaEntregaProgramada?: string;
  precioInicialPublicado?: number;
  ofertaInicialComprador?: number;
  contraofertaVendedor?: number;
  precioFinalAcordado?: number;
  entidadFinanciera?: string;
  financiacionDirectaCondiciones?: string;
  permutaCondiciones?: string;
  estadoFisicoEntrega?: EstadoFisicoEntrega;
  momentoEntrega?: MomentoEntrega;
  escrituraFirmada: boolean;
  pagoRecibido: boolean;
  entregaRealizada: boolean;
  estado: EstadoNegociacion;
  createdAt: string;
}

export interface NegociacionRequest {
  inmuebleId: string;
  clienteId: string;
  propietarioId: string;
  asesorId?: string;
  fechaInicio?: string;
  precioInicialPublicado?: number;
  ofertaInicialComprador?: number;
}

export interface ActualizarCondicionesRequest {
  contraofertaVendedor?: number;
  precioFinalAcordado?: number;
  fechaFirmaPcv?: string;
  fechaEntregaProgramada?: string;
  entidadFinanciera?: string;
  financiacionDirectaCondiciones?: string;
  permutaCondiciones?: string;
  estadoFisicoEntrega?: EstadoFisicoEntrega;
  momentoEntrega?: MomentoEntrega;
}

// ---- Partes de la negociacion (compradores / vendedores) ----

export type RolParteNegociacion = 'COMPRADOR' | 'VENDEDOR';

export const ROLES_PARTE_NEGOCIACION: RolParteNegociacion[] = ['COMPRADOR', 'VENDEDOR'];

export const ETIQUETA_ROL_PARTE: Record<RolParteNegociacion, string> = {
  COMPRADOR: 'Comprador',
  VENDEDOR: 'Vendedor',
};

export const TIPOS_DOCUMENTO_IDENTIDAD = [
  'Cédula de ciudadanía',
  'Cédula de extranjería',
  'Pasaporte',
  'NIT',
];

export const ESTADOS_CIVILES = ['Soltero(a)', 'Casado(a)', 'Unión libre', 'Divorciado(a)', 'Viudo(a)'];

export interface ParteNegociacion {
  id: string;
  negociacionId: string;
  rol: RolParteNegociacion;
  orden?: number;
  tipoDocumento?: string;
  numeroDocumento?: string;
  estadoCivil?: string;
  direccion?: string;
  correoElectronico?: string;
}

export interface ParteNegociacionRequest {
  negociacionId: string;
  rol: RolParteNegociacion;
  orden?: number;
  tipoDocumento?: string;
  numeroDocumento?: string;
  estadoCivil?: string;
  direccion?: string;
  correoElectronico?: string;
}

// ---- Bienes asociados (parqueadero, deposito, inmueble adicional) ----

export type TipoBienAsociado = 'PARQUEADERO' | 'DEPOSITO' | 'INMUEBLE_ASOCIADO';

export const TIPOS_BIEN_ASOCIADO: TipoBienAsociado[] = ['PARQUEADERO', 'DEPOSITO', 'INMUEBLE_ASOCIADO'];

export const ETIQUETA_TIPO_BIEN_ASOCIADO: Record<TipoBienAsociado, string> = {
  PARQUEADERO: 'Parqueadero',
  DEPOSITO: 'Depósito',
  INMUEBLE_ASOCIADO: 'Inmueble asociado',
};

export interface BienAsociado {
  id: string;
  negociacionId: string;
  tipo: TipoBienAsociado;
  matriculaInmobiliaria?: string;
  referenciaCatastral?: string;
}

export interface BienAsociadoRequest {
  negociacionId: string;
  tipo: TipoBienAsociado;
  matriculaInmobiliaria?: string;
  referenciaCatastral?: string;
}

// ---- Plan de pago ----

export type TipoRecursoPago = 'RECURSOS_PROPIOS' | 'CREDITO_LEASING';

export const TIPOS_RECURSO_PAGO: TipoRecursoPago[] = ['RECURSOS_PROPIOS', 'CREDITO_LEASING'];

export const ETIQUETA_TIPO_RECURSO_PAGO: Record<TipoRecursoPago, string> = {
  RECURSOS_PROPIOS: 'Recursos propios',
  CREDITO_LEASING: 'Crédito / Leasing',
};

export type MomentoPago = 'FIRMA_PCV' | 'FECHA_FIJA' | 'FIRMA_ESCRITURA_PUBLICA' | 'ENTREGA_PROPIEDAD' | 'DESEMBOLSO_BANCO';

export const MOMENTOS_PAGO: MomentoPago[] = [
  'FIRMA_PCV',
  'FECHA_FIJA',
  'FIRMA_ESCRITURA_PUBLICA',
  'ENTREGA_PROPIEDAD',
  'DESEMBOLSO_BANCO',
];

export const ETIQUETA_MOMENTO_PAGO: Record<MomentoPago, string> = {
  FIRMA_PCV: 'A la firma de la PCV',
  FECHA_FIJA: 'Fecha fija',
  FIRMA_ESCRITURA_PUBLICA: 'A la firma de escritura pública',
  ENTREGA_PROPIEDAD: 'Con la entrega de la propiedad',
  DESEMBOLSO_BANCO: 'Al desembolso del banco',
};

export type MetodoPago = 'DE_CONTADO' | 'TRANSFERENCIA_BANCARIA' | 'CHEQUE_GERENCIA';

export const METODOS_PAGO: MetodoPago[] = ['DE_CONTADO', 'TRANSFERENCIA_BANCARIA', 'CHEQUE_GERENCIA'];

export const ETIQUETA_METODO_PAGO: Record<MetodoPago, string> = {
  DE_CONTADO: 'De contado',
  TRANSFERENCIA_BANCARIA: 'Transferencia bancaria',
  CHEQUE_GERENCIA: 'Cheque de gerencia',
};

export interface PlanDePago {
  id: string;
  negociacionId: string;
  tipoRecurso: TipoRecursoPago;
  numeroCuota?: number;
  porcentaje?: number;
  valor?: number;
  momentoPago?: MomentoPago;
  metodoPago?: MetodoPago;
  fechaEsperada?: string;
  pagada: boolean;
}

export interface PlanDePagoRequest {
  negociacionId: string;
  tipoRecurso: TipoRecursoPago;
  numeroCuota?: number;
  porcentaje?: number;
  valor?: number;
  momentoPago?: MomentoPago;
  metodoPago?: MetodoPago;
  fechaEsperada?: string;
}

// ---- Que incluye / no incluye la entrega ----

export interface ItemEntrega {
  id: string;
  nombre: string;
}

// ---- Condiciones especiales ----

export interface CondicionEspecial {
  id: string;
  nombre: string;
}

// ---- Distribucion de gastos ----

export type QuienPaga = 'COMPRADOR' | 'VENDEDOR' | 'COMPARTIDO';

export const QUIENES_PAGAN: QuienPaga[] = ['COMPRADOR', 'VENDEDOR', 'COMPARTIDO'];

export const ETIQUETA_QUIEN_PAGA: Record<QuienPaga, string> = {
  COMPRADOR: 'Comprador',
  VENDEDOR: 'Vendedor',
  COMPARTIDO: '50% comprador y 50% vendedor',
};

export interface DistribucionGastos {
  id: string;
  negociacionId: string;
  gastosEscrituracion: QuienPaga;
  retencionFuente: QuienPaga;
  gastosRegistro: QuienPaga;
  gastosCancelacionGravamen: QuienPaga;
  gastosConstitucionGravamen: QuienPaga;
}

export interface DistribucionGastosRequest {
  negociacionId: string;
  gastosEscrituracion: QuienPaga;
  retencionFuente: QuienPaga;
  gastosRegistro: QuienPaga;
  gastosCancelacionGravamen: QuienPaga;
  gastosConstitucionGravamen: QuienPaga;
}

// ---- Proyeccion de gastos de escrituracion y registro ----

export interface ProyeccionGastos {
  negociacionId: string;
  valorVenta: number;
  gastosNotariales: number;
  derechosRegistro: number;
  retencionFuente: number;
  totalEstimado: number;
}

// ---- Historial ----

export interface HistorialNegociacion {
  id: string;
  negociacionId: string;
  usuarioId?: string;
  evento: string;
  fecha: string;
}

// ---- Documentos adjuntos (cedula, escritura, tradicion, predial, paz y salvos...) ----

export type TipoDocumentoNegociacion = 'CEDULA' | 'ESCRITURA' | 'TRADICION' | 'PREDIAL' | 'PAZ_Y_SALVOS' | 'OTROS';

export const TIPOS_DOCUMENTO_NEGOCIACION: TipoDocumentoNegociacion[] = [
  'CEDULA',
  'ESCRITURA',
  'TRADICION',
  'PREDIAL',
  'PAZ_Y_SALVOS',
  'OTROS',
];

export const ETIQUETA_TIPO_DOCUMENTO_NEGOCIACION: Record<TipoDocumentoNegociacion, string> = {
  CEDULA: 'Cédula',
  ESCRITURA: 'Escritura',
  TRADICION: 'Tradición',
  PREDIAL: 'Predial',
  PAZ_Y_SALVOS: 'Paz y salvos',
  OTROS: 'Otros',
};

export interface DocumentoNegociacion {
  id: string;
  negociacionId: string;
  tipo: TipoDocumentoNegociacion;
  url: string;
  createdAt: string;
}
