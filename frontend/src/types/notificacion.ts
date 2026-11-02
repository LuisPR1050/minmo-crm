export type TipoNotificacion =
  | 'RECORDATORIO_VISITA_24H'
  | 'RECORDATORIO_VISITA_2H'
  | 'ALERTA_SEGUIMIENTO'
  | 'VENCIMIENTO_CONTRATO_CORRETAJE'
  | 'FIRMA_PROMESA_PROXIMA'
  | 'PAGO_PENDIENTE_VENCIDO'
  | 'PUBLICACION_ANTIGUA_SIN_CONTACTOS';

export interface Notificacion {
  id: string;
  tipo: TipoNotificacion;
  mensaje: string;
  referenciaId?: string;
  leida: boolean;
  createdAt: string;
}

export const ETIQUETA_TIPO_NOTIFICACION: Record<TipoNotificacion, string> = {
  RECORDATORIO_VISITA_24H: 'Visita en 24h',
  RECORDATORIO_VISITA_2H: 'Visita en 2h',
  ALERTA_SEGUIMIENTO: 'Seguimiento pendiente',
  VENCIMIENTO_CONTRATO_CORRETAJE: 'Contrato por vencer',
  FIRMA_PROMESA_PROXIMA: 'Firma de promesa próxima',
  PAGO_PENDIENTE_VENCIDO: 'Pago vencido',
  PUBLICACION_ANTIGUA_SIN_CONTACTOS: 'Publicación sin contactos',
};

export const COLOR_TIPO_NOTIFICACION: Record<TipoNotificacion, string> = {
  RECORDATORIO_VISITA_24H: 'blue',
  RECORDATORIO_VISITA_2H: 'geekblue',
  ALERTA_SEGUIMIENTO: 'orange',
  VENCIMIENTO_CONTRATO_CORRETAJE: 'red',
  FIRMA_PROMESA_PROXIMA: 'purple',
  PAGO_PENDIENTE_VENCIDO: 'volcano',
  PUBLICACION_ANTIGUA_SIN_CONTACTOS: 'default',
};
