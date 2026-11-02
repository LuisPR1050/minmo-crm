export type EstadoDocumento =
  | 'BORRADOR'
  | 'EN_REVISION'
  | 'ENVIADO'
  | 'PENDIENTE_FIRMA'
  | 'FIRMADO'
  | 'VIGENTE'
  | 'VENCIDO'
  | 'RENOVADO'
  | 'CANCELADO';

export const ESTADOS_DOCUMENTO: EstadoDocumento[] = [
  'BORRADOR',
  'EN_REVISION',
  'ENVIADO',
  'PENDIENTE_FIRMA',
  'FIRMADO',
  'VIGENTE',
  'VENCIDO',
  'RENOVADO',
  'CANCELADO',
];

export const ETIQUETA_ESTADO_DOCUMENTO: Record<EstadoDocumento, string> = {
  BORRADOR: 'Borrador',
  EN_REVISION: 'En revisión',
  ENVIADO: 'Enviado',
  PENDIENTE_FIRMA: 'Pendiente de firma',
  FIRMADO: 'Firmado',
  VIGENTE: 'Vigente',
  VENCIDO: 'Vencido',
  RENOVADO: 'Renovado',
  CANCELADO: 'Cancelado',
};

export type OpcionFirma = 'PRESENCIAL' | 'ELECTRONICA' | 'DIGITAL_CERTIFICADA';

export const OPCIONES_FIRMA: OpcionFirma[] = ['PRESENCIAL', 'ELECTRONICA', 'DIGITAL_CERTIFICADA'];

export const ETIQUETA_OPCION_FIRMA: Record<OpcionFirma, string> = {
  PRESENCIAL: 'Firma presencial',
  ELECTRONICA: 'Firma electrónica',
  DIGITAL_CERTIFICADA: 'Firma digital certificada',
};

export type ConceptoPago =
  | 'RESERVA'
  | 'SEPARACION_INMUEBLE'
  | 'ARRAS'
  | 'CUOTA_INICIAL'
  | 'PAGO_PARCIAL'
  | 'PAGO_CONTRA_FIRMA_PROMESA'
  | 'PAGO_CONTRA_ESCRITURACION'
  | 'PAGO_CONTRA_DESEMBOLSO_CREDITO'
  | 'SALDO_FINAL'
  | 'COMISION_INMOBILIARIA'
  | 'OTRO';

export const CONCEPTOS_PAGO: ConceptoPago[] = [
  'RESERVA',
  'SEPARACION_INMUEBLE',
  'ARRAS',
  'CUOTA_INICIAL',
  'PAGO_PARCIAL',
  'PAGO_CONTRA_FIRMA_PROMESA',
  'PAGO_CONTRA_ESCRITURACION',
  'PAGO_CONTRA_DESEMBOLSO_CREDITO',
  'SALDO_FINAL',
  'COMISION_INMOBILIARIA',
  'OTRO',
];

export type FormaPagoRecibo =
  | 'TRANSFERENCIA_BANCARIA'
  | 'CONSIGNACION'
  | 'EFECTIVO'
  | 'CHEQUE'
  | 'ENDOSO'
  | 'CDT'
  | 'TRANSFERENCIA_INTERNACIONAL'
  | 'OTRO';

export const FORMAS_PAGO_RECIBO: FormaPagoRecibo[] = [
  'TRANSFERENCIA_BANCARIA',
  'CONSIGNACION',
  'EFECTIVO',
  'CHEQUE',
  'ENDOSO',
  'CDT',
  'TRANSFERENCIA_INTERNACIONAL',
  'OTRO',
];

export interface PromesaCompraventa {
  id: string;
  numeroContrato: string;
  negociacionId: string;
  numeroNegocio: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  fechaElaboracion?: string;
  ciudad?: string;
  opcionFirma?: OpcionFirma;
  estado: EstadoDocumento;
  textoPromesa?: string;
  fechaGeneracionTexto?: string;
  pdfUrl?: string;
  createdAt: string;
}

export interface PromesaCompraventaRequest {
  negociacionId: string;
  fechaElaboracion?: string;
  ciudad?: string;
  opcionFirma?: OpcionFirma;
}

export interface GenerarTextoIARequest {
  instruccionesAdicionales?: string;
}

export interface ActualizarPromesaCompraventaRequest {
  ciudad?: string;
  opcionFirma?: OpcionFirma;
  textoPromesa?: string;
}

export interface ActualizarOtrosiPromesaRequest {
  motivo?: MotivoOtrosi;
  otroMotivoTexto?: string;
  justificacion?: JustificacionOtrosi;
  nuevoPrecio?: number;
  nuevaFechaEscritura?: string;
  nuevaFechaEntrega?: string;
  nuevaDistribucionGastos?: string;
  nuevosBienesIncluidos?: string;
  nuevasCondicionesEspeciales?: string;
  observaciones?: string;
  textoOtrosi?: string;
}

export type PagoRealizadoPor = 'COMPRADOR' | 'APODERADO' | 'TERCERO_AUTORIZADO';

export const PAGOS_REALIZADO_POR: PagoRealizadoPor[] = ['COMPRADOR', 'APODERADO', 'TERCERO_AUTORIZADO'];

export const ETIQUETA_PAGO_REALIZADO_POR: Record<PagoRealizadoPor, string> = {
  COMPRADOR: 'Comprador',
  APODERADO: 'Apoderado',
  TERCERO_AUTORIZADO: 'Tercero autorizado',
};

export type PagoRecibidoPor = 'VENDEDOR' | 'INMOBILIARIA' | 'FIDUCIA' | 'APODERADO' | 'ASESOR_INMOBILIARIO';

export const PAGOS_RECIBIDO_POR: PagoRecibidoPor[] = ['VENDEDOR', 'INMOBILIARIA', 'FIDUCIA', 'APODERADO', 'ASESOR_INMOBILIARIO'];

export const ETIQUETA_PAGO_RECIBIDO_POR: Record<PagoRecibidoPor, string> = {
  VENDEDOR: 'Vendedor',
  INMOBILIARIA: 'Inmobiliaria',
  FIDUCIA: 'Fiducia',
  APODERADO: 'Apoderado',
  ASESOR_INMOBILIARIO: 'Asesor inmobiliario',
};

export interface ReciboPago {
  id: string;
  numeroRecibo: string;
  negociacionId: string;
  numeroNegocio: string;
  planDePagoId?: string;
  fechaElaboracion?: string;
  conceptoPago: ConceptoPago;
  numeroCuota?: number;
  valorRecibido: number;
  saldoPendiente?: number;
  moneda?: string;
  formaPago?: string;
  pagoRealizadoPor?: PagoRealizadoPor;
  pagoRecibidoPor?: PagoRecibidoPor;
  observaciones?: string;
  createdAt: string;
}

export interface ReciboPagoRequest {
  negociacionId: string;
  planDePagoId?: string;
  fechaElaboracion?: string;
  conceptoPago: ConceptoPago;
  numeroCuota?: number;
  valorRecibido: number;
  moneda?: string;
  formaPago?: FormaPagoRecibo;
  pagoRealizadoPor?: PagoRealizadoPor;
  pagoRecibidoPor?: PagoRecibidoPor;
  observaciones?: string;
}

export type DeclaracionComprador = 'CONFORME' | 'INCONFORME';

export const DECLARACIONES_COMPRADOR: DeclaracionComprador[] = ['CONFORME', 'INCONFORME'];

export const ETIQUETA_DECLARACION_COMPRADOR: Record<DeclaracionComprador, string> = {
  CONFORME: 'Conforme',
  INCONFORME: 'Inconforme',
};

export interface ActaEntrega {
  id: string;
  numeroActa: string;
  negociacionId: string;
  numeroNegocio: string;
  fechaElaboracion?: string;
  fechaHoraEntrega?: string;
  ciudad?: string;
  lecturaEnergia?: string;
  lecturaAgua?: string;
  lecturaGas?: string;
  danosExistentes?: string;
  reparacionesPendientes?: string;
  acuerdosPosteriores?: string;
  compromisosAsumidos?: string;
  declaracionComprador?: DeclaracionComprador;
  vendedorEntregaConforme: boolean;
  observaciones?: string;
  createdAt: string;
}

export interface ActaEntregaRequest {
  negociacionId: string;
  fechaElaboracion?: string;
  fechaHoraEntrega?: string;
  ciudad?: string;
  lecturaEnergia?: string;
  lecturaAgua?: string;
  lecturaGas?: string;
  danosExistentes?: string;
  reparacionesPendientes?: string;
  acuerdosPosteriores?: string;
  compromisosAsumidos?: string;
  declaracionComprador?: DeclaracionComprador;
  vendedorEntregaConforme?: boolean;
  observaciones?: string;
}

// ---- Contrato de corretaje ----

export type TipoContrato = 'EXCLUSIVO' | 'NO_EXCLUSIVO' | 'SEMI_EXCLUSIVO';

export const TIPOS_CONTRATO_CORRETAJE: TipoContrato[] = ['EXCLUSIVO', 'NO_EXCLUSIVO', 'SEMI_EXCLUSIVO'];

export const ETIQUETA_TIPO_CONTRATO: Record<TipoContrato, string> = {
  EXCLUSIVO: 'Exclusivo',
  NO_EXCLUSIVO: 'No exclusivo',
  SEMI_EXCLUSIVO: 'Semi exclusivo',
};

export type FormaPagoComision = 'CONTRA_PROMESA' | 'CONTRA_ESCRITURA' | 'CONTRA_DESEMBOLSO' | 'PROPORCIONAL' | 'OTRA';

export const FORMAS_PAGO_COMISION: FormaPagoComision[] = [
  'CONTRA_PROMESA',
  'CONTRA_ESCRITURA',
  'CONTRA_DESEMBOLSO',
  'PROPORCIONAL',
  'OTRA',
];

export const ETIQUETA_FORMA_PAGO_COMISION: Record<FormaPagoComision, string> = {
  CONTRA_PROMESA: 'Contra promesa',
  CONTRA_ESCRITURA: 'Contra escritura',
  CONTRA_DESEMBOLSO: 'Contra desembolso',
  PROPORCIONAL: 'Proporcional',
  OTRA: 'Otra',
};

export interface ContratoCorretaje {
  id: string;
  numeroContrato: string;
  inmuebleId: string;
  inmuebleCodigo: string;
  propietarioId: string;
  propietarioNombre: string;
  fechaElaboracion?: string;
  ciudad?: string;
  tipoContrato?: TipoContrato;
  precioAutorizadoVenta?: number;
  precioMinimoAutorizado?: number;
  porcentajeComision?: number;
  valorFijoComision?: number;
  sobreprecioComision?: number;
  formaPagoComision?: FormaPagoComision;
  fechaVigenciaHasta?: string;
  opcionFirma?: OpcionFirma;
  estado: EstadoDocumento;
  textoContrato?: string;
  fechaGeneracionTexto?: string;
  pdfUrl?: string;
  createdAt: string;
}

export interface ContratoCorretajeRequest {
  inmuebleId: string;
  propietarioId: string;
  fechaElaboracion?: string;
  ciudad?: string;
  tipoContrato?: TipoContrato;
  precioAutorizadoVenta?: number;
  precioMinimoAutorizado?: number;
  porcentajeComision?: number;
  valorFijoComision?: number;
  sobreprecioComision?: number;
  formaPagoComision?: FormaPagoComision;
  fechaVigenciaHasta?: string;
  opcionFirma?: OpcionFirma;
}

export interface ActualizarContratoCorretajeRequest {
  ciudad?: string;
  tipoContrato?: TipoContrato;
  precioAutorizadoVenta?: number;
  precioMinimoAutorizado?: number;
  porcentajeComision?: number;
  valorFijoComision?: number;
  sobreprecioComision?: number;
  formaPagoComision?: FormaPagoComision;
  fechaVigenciaHasta?: string;
  opcionFirma?: OpcionFirma;
  textoContrato?: string;
}

export interface ResumenContratosCorretaje {
  total: number;
  vigentes: number;
  proximosAVencer: number;
  renovados: number;
  finalizados: number;
}

export interface ClausulaCorretaje {
  id: string;
  nombre: string;
}

export interface AutorizacionPropietario {
  id: string;
  nombre: string;
}

export type TipoDocumentoContratoCorretaje =
  | 'CONTRATO_REFERENCIA'
  | 'CEDULA'
  | 'ESCRITURA'
  | 'TRADICION'
  | 'PREDIAL'
  | 'PAZ_Y_SALVOS'
  | 'OTROS';

export const TIPOS_DOCUMENTO_CONTRATO_CORRETAJE: TipoDocumentoContratoCorretaje[] = [
  'CONTRATO_REFERENCIA',
  'CEDULA',
  'ESCRITURA',
  'TRADICION',
  'PREDIAL',
  'PAZ_Y_SALVOS',
  'OTROS',
];

export const ETIQUETA_TIPO_DOCUMENTO_CONTRATO_CORRETAJE: Record<TipoDocumentoContratoCorretaje, string> = {
  CONTRATO_REFERENCIA: 'Contrato de referencia',
  CEDULA: 'Cédula',
  ESCRITURA: 'Escritura',
  TRADICION: 'Tradición',
  PREDIAL: 'Predial',
  PAZ_Y_SALVOS: 'Paz y salvos',
  OTROS: 'Otros',
};

export interface DocumentoContratoCorretaje {
  id: string;
  tipo: TipoDocumentoContratoCorretaje;
  url: string;
  createdAt: string;
}

export interface HistorialContratoCorretaje {
  id: string;
  contratoCorretajeId: string;
  usuarioId?: string;
  evento: string;
  fecha: string;
}

export type TipoDocumentoPromesaCompraventa = 'CONTRATO_REFERENCIA' | 'OTRO';

export const TIPOS_DOCUMENTO_PROMESA_COMPRAVENTA: TipoDocumentoPromesaCompraventa[] = ['CONTRATO_REFERENCIA', 'OTRO'];

export const ETIQUETA_TIPO_DOCUMENTO_PROMESA_COMPRAVENTA: Record<TipoDocumentoPromesaCompraventa, string> = {
  CONTRATO_REFERENCIA: 'Contrato de referencia',
  OTRO: 'Otro',
};

export interface DocumentoPromesaCompraventa {
  id: string;
  tipo: TipoDocumentoPromesaCompraventa;
  url: string;
  createdAt: string;
}

export interface DocumentoOtrosiPromesa {
  id: string;
  url: string;
  descripcion?: string;
  createdAt: string;
}

export type TipoDocumentoActaEntrega = 'FOTO_INMUEBLE' | 'FOTO_MEDIDORES' | 'VIDEO' | 'OTRO';

export const TIPOS_DOCUMENTO_ACTA_ENTREGA: TipoDocumentoActaEntrega[] = ['FOTO_INMUEBLE', 'FOTO_MEDIDORES', 'VIDEO', 'OTRO'];

export const ETIQUETA_TIPO_DOCUMENTO_ACTA_ENTREGA: Record<TipoDocumentoActaEntrega, string> = {
  FOTO_INMUEBLE: 'Foto del inmueble',
  FOTO_MEDIDORES: 'Foto de medidores',
  VIDEO: 'Video',
  OTRO: 'Otro',
};

export interface DocumentoActaEntrega {
  id: string;
  tipo: TipoDocumentoActaEntrega;
  url: string;
  createdAt: string;
}

export interface DocumentoReciboPago {
  id: string;
  url: string;
  createdAt: string;
}

// ---- Otrosí a la promesa de compraventa ----

export type MotivoOtrosi =
  | 'CAMBIO_PRECIO_VENTA'
  | 'CAMBIO_FORMA_PAGO'
  | 'PRORROGA_FIRMA_ESCRITURA'
  | 'PRORROGA_ENTREGA_INMUEBLE'
  | 'CAMBIO_FECHA_PROMESA'
  | 'MODIFICACION_ARRAS'
  | 'MODIFICACION_DISTRIBUCION_GASTOS'
  | 'INCLUSION_EXCLUSION_BIENES_MUEBLES'
  | 'CAMBIO_ENTIDAD_FINANCIERA'
  | 'CAMBIO_COMPRADOR'
  | 'CAMBIO_VENDEDOR'
  | 'CORRECCION_ERRORES'
  | 'CUMPLIMIENTO_REQUISITOS_LEGALES'
  | 'OTRO';

export const MOTIVOS_OTROSI: MotivoOtrosi[] = [
  'CAMBIO_PRECIO_VENTA',
  'CAMBIO_FORMA_PAGO',
  'PRORROGA_FIRMA_ESCRITURA',
  'PRORROGA_ENTREGA_INMUEBLE',
  'CAMBIO_FECHA_PROMESA',
  'MODIFICACION_ARRAS',
  'MODIFICACION_DISTRIBUCION_GASTOS',
  'INCLUSION_EXCLUSION_BIENES_MUEBLES',
  'CAMBIO_ENTIDAD_FINANCIERA',
  'CAMBIO_COMPRADOR',
  'CAMBIO_VENDEDOR',
  'CORRECCION_ERRORES',
  'CUMPLIMIENTO_REQUISITOS_LEGALES',
  'OTRO',
];

export const ETIQUETA_MOTIVO_OTROSI: Record<MotivoOtrosi, string> = {
  CAMBIO_PRECIO_VENTA: 'Cambio del precio de venta',
  CAMBIO_FORMA_PAGO: 'Cambio en la forma de pago',
  PRORROGA_FIRMA_ESCRITURA: 'Prórroga para la firma de la escritura',
  PRORROGA_ENTREGA_INMUEBLE: 'Prórroga para la entrega del inmueble',
  CAMBIO_FECHA_PROMESA: 'Cambio en la fecha de la promesa',
  MODIFICACION_ARRAS: 'Modificación de las arras',
  MODIFICACION_DISTRIBUCION_GASTOS: 'Modificación de la distribución de gastos notariales',
  INCLUSION_EXCLUSION_BIENES_MUEBLES: 'Inclusión o exclusión de bienes muebles',
  CAMBIO_ENTIDAD_FINANCIERA: 'Cambio de la entidad financiera',
  CAMBIO_COMPRADOR: 'Cambio del comprador',
  CAMBIO_VENDEDOR: 'Cambio del vendedor',
  CORRECCION_ERRORES: 'Corrección de errores',
  CUMPLIMIENTO_REQUISITOS_LEGALES: 'Cumplimiento de requisitos legales',
  OTRO: 'Otro motivo',
};

export type JustificacionOtrosi =
  | 'RETRASO_CREDITO_HIPOTECARIO'
  | 'SOLICITUD_COMPRADOR'
  | 'SOLICITUD_VENDEDOR'
  | 'PENDIENTE_CANCELACION_HIPOTECA'
  | 'PENDIENTE_LEVANTAMIENTO_PATRIMONIO_FAMILIA'
  | 'RETRASO_ESTUDIO_TITULOS'
  | 'RETRASO_AVALUO'
  | 'ACUERDO_ENTRE_PARTES';

export const JUSTIFICACIONES_OTROSI: JustificacionOtrosi[] = [
  'RETRASO_CREDITO_HIPOTECARIO',
  'SOLICITUD_COMPRADOR',
  'SOLICITUD_VENDEDOR',
  'PENDIENTE_CANCELACION_HIPOTECA',
  'PENDIENTE_LEVANTAMIENTO_PATRIMONIO_FAMILIA',
  'RETRASO_ESTUDIO_TITULOS',
  'RETRASO_AVALUO',
  'ACUERDO_ENTRE_PARTES',
];

export const ETIQUETA_JUSTIFICACION_OTROSI: Record<JustificacionOtrosi, string> = {
  RETRASO_CREDITO_HIPOTECARIO: 'Retraso en el crédito hipotecario',
  SOLICITUD_COMPRADOR: 'Solicitud del comprador',
  SOLICITUD_VENDEDOR: 'Solicitud del vendedor',
  PENDIENTE_CANCELACION_HIPOTECA: 'Pendiente cancelación de hipoteca',
  PENDIENTE_LEVANTAMIENTO_PATRIMONIO_FAMILIA: 'Pendiente levantamiento de patrimonio de familia',
  RETRASO_ESTUDIO_TITULOS: 'Retraso en el estudio de títulos',
  RETRASO_AVALUO: 'Retraso en avalúo',
  ACUERDO_ENTRE_PARTES: 'Acuerdo entre las partes',
};

export interface OtrosiPromesa {
  id: string;
  numeroOtrosi: string;
  promesaCompraventaId: string;
  numeroContratoPromesa: string;
  negociacionId: string;
  numeroNegocio: string;
  fechaElaboracion?: string;
  motivo?: MotivoOtrosi;
  otroMotivoTexto?: string;
  justificacion?: JustificacionOtrosi;
  nuevoPrecio?: number;
  nuevaFechaEscritura?: string;
  nuevaFechaEntrega?: string;
  nuevaDistribucionGastos?: string;
  nuevosBienesIncluidos?: string;
  nuevasCondicionesEspeciales?: string;
  observaciones?: string;
  aplicado: boolean;
  fechaAplicacion?: string;
  textoOtrosi?: string;
  fechaGeneracionTexto?: string;
  pdfUrl?: string;
  createdAt: string;
}

export interface OtrosiPromesaRequest {
  promesaCompraventaId: string;
  fechaElaboracion?: string;
  motivo: MotivoOtrosi;
  otroMotivoTexto?: string;
  justificacion?: JustificacionOtrosi;
  nuevoPrecio?: number;
  nuevaFechaEscritura?: string;
  nuevaFechaEntrega?: string;
  nuevaDistribucionGastos?: string;
  nuevosBienesIncluidos?: string;
  nuevasCondicionesEspeciales?: string;
  observaciones?: string;
}

// ---- Comparacion de documentos con IA ----

export type TipoCambioDiferencia = 'AGREGADO' | 'ELIMINADO' | 'MODIFICADO';
export type RelevanciaDiferencia = 'BAJA' | 'MEDIA' | 'ALTA';
export type NivelCambioComparacion = 'SIN_CAMBIOS' | 'MENOR' | 'MODERADO' | 'MAYOR';

export interface DiferenciaDocumento {
  seccion: string;
  contenidoDocumentoA: string;
  contenidoDocumentoB: string;
  tipoCambio: TipoCambioDiferencia;
  relevancia: RelevanciaDiferencia;
}

export interface ComparacionDocumentos {
  nombreDocumentoA: string;
  nombreDocumentoB: string;
  resumenGeneral: string;
  nivelCambio: NivelCambioComparacion;
  diferencias: DiferenciaDocumento[];
  fechaComparacion: string;
}
