import { apiClient } from './client';
import type {
  ActualizarCondicionesRequest,
  BienAsociado,
  BienAsociadoRequest,
  CondicionEspecial,
  DistribucionGastos,
  DistribucionGastosRequest,
  DocumentoNegociacion,
  HistorialNegociacion,
  ItemEntrega,
  Negociacion,
  NegociacionRequest,
  ParteNegociacion,
  ParteNegociacionRequest,
  PlanDePago,
  PlanDePagoRequest,
  ProyeccionGastos,
  TipoDocumentoNegociacion,
} from '../types/negociacion';

export const negociacionApi = {
  listar: () => apiClient.get<Negociacion[]>('/api/negociaciones').then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<Negociacion>(`/api/negociaciones/${id}`).then((res) => res.data),
  crear: (request: NegociacionRequest) =>
    apiClient.post<Negociacion>('/api/negociaciones', request).then((res) => res.data),
  actualizarCondiciones: (id: string, request: ActualizarCondicionesRequest) =>
    apiClient.patch<Negociacion>(`/api/negociaciones/${id}/condiciones`, request).then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<Negociacion>(`/api/negociaciones/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/negociaciones/${id}`).then((res) => res.data),
  marcarEscrituraFirmada: (id: string, valor: boolean) =>
    apiClient.patch<Negociacion>(`/api/negociaciones/${id}/escritura-firmada`, { valor }).then((res) => res.data),
  marcarPagoRecibido: (id: string, valor: boolean) =>
    apiClient.patch<Negociacion>(`/api/negociaciones/${id}/pago-recibido`, { valor }).then((res) => res.data),
  marcarEntregaRealizada: (id: string, valor: boolean) =>
    apiClient.patch<Negociacion>(`/api/negociaciones/${id}/entrega-realizada`, { valor }).then((res) => res.data),
};

export const parteNegociacionApi = {
  listarPorNegociacion: (negociacionId: string) =>
    apiClient.get<ParteNegociacion[]>('/api/partes-negociacion', { params: { negociacionId } }).then((res) => res.data),
  crear: (request: ParteNegociacionRequest) =>
    apiClient.post<ParteNegociacion>('/api/partes-negociacion', request).then((res) => res.data),
  actualizar: (id: string, request: ParteNegociacionRequest) =>
    apiClient.patch<ParteNegociacion>(`/api/partes-negociacion/${id}`, request).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/partes-negociacion/${id}`).then((res) => res.data),
};

export const bienAsociadoApi = {
  listarPorNegociacion: (negociacionId: string) =>
    apiClient.get<BienAsociado[]>('/api/bienes-asociados', { params: { negociacionId } }).then((res) => res.data),
  crear: (request: BienAsociadoRequest) =>
    apiClient.post<BienAsociado>('/api/bienes-asociados', request).then((res) => res.data),
  actualizar: (id: string, request: BienAsociadoRequest) =>
    apiClient.patch<BienAsociado>(`/api/bienes-asociados/${id}`, request).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/bienes-asociados/${id}`).then((res) => res.data),
};

export const planDePagoApi = {
  listarPorNegociacion: (negociacionId: string) =>
    apiClient.get<PlanDePago[]>('/api/planes-pago', { params: { negociacionId } }).then((res) => res.data),
  crear: (request: PlanDePagoRequest) =>
    apiClient.post<PlanDePago>('/api/planes-pago', request).then((res) => res.data),
  marcarPagada: (id: string, pagada: boolean) =>
    apiClient.patch<PlanDePago>(`/api/planes-pago/${id}/pagada`, { valor: pagada }).then((res) => res.data),
  actualizar: (id: string, request: PlanDePagoRequest) =>
    apiClient.patch<PlanDePago>(`/api/planes-pago/${id}`, request).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/planes-pago/${id}`).then((res) => res.data),
};

export const itemEntregaApi = {
  listarTodos: () => apiClient.get<ItemEntrega[]>('/api/items-entrega').then((res) => res.data),
  listarIncluidos: (negociacionId: string) =>
    apiClient.get<ItemEntrega[]>(`/api/negociaciones/${negociacionId}/items-entrega`).then((res) => res.data),
  asignar: (negociacionId: string, itemEntregaId: string, incluido: boolean) =>
    apiClient
      .post(`/api/negociaciones/${negociacionId}/items-entrega`, { itemEntregaId, incluido })
      .then((res) => res.data),
};

export const condicionEspecialApi = {
  listarTodas: () => apiClient.get<CondicionEspecial[]>('/api/condiciones-especiales').then((res) => res.data),
  listarAsignadas: (negociacionId: string) =>
    apiClient.get<CondicionEspecial[]>(`/api/negociaciones/${negociacionId}/condiciones-especiales`).then((res) => res.data),
  asignar: (negociacionId: string, condicionEspecialIds: string[]) =>
    apiClient
      .post<CondicionEspecial[]>(`/api/negociaciones/${negociacionId}/condiciones-especiales`, { condicionEspecialIds })
      .then((res) => res.data),
};

export const distribucionGastosApi = {
  buscarPorNegociacion: async (negociacionId: string): Promise<DistribucionGastos | null> => {
    try {
      const res = await apiClient.get<DistribucionGastos>(`/api/distribucion-gastos/por-negociacion/${negociacionId}`);
      return res.data;
    } catch {
      return null;
    }
  },
  guardar: (request: DistribucionGastosRequest) =>
    apiClient.post<DistribucionGastos>('/api/distribucion-gastos', request).then((res) => res.data),
};

export const proyeccionGastosApi = {
  calcular: (negociacionId: string) =>
    apiClient.get<ProyeccionGastos>(`/api/negociaciones/${negociacionId}/proyeccion-gastos`).then((res) => res.data),
};

export const historialNegociacionApi = {
  listarPorNegociacion: (negociacionId: string) =>
    apiClient.get<HistorialNegociacion[]>('/api/historial-negociacion', { params: { negociacionId } }).then((res) => res.data),
};

export const documentoNegociacionApi = {
  listarPorNegociacion: (negociacionId: string) =>
    apiClient.get<DocumentoNegociacion[]>('/api/documentos-negociacion', { params: { negociacionId } }).then((res) => res.data),
  subir: (negociacionId: string, tipo: TipoDocumentoNegociacion, archivo: File) => {
    const formData = new FormData();
    formData.append('tipo', tipo);
    formData.append('archivo', archivo);
    return apiClient
      .post<DocumentoNegociacion>(`/api/documentos-negociacion/negociaciones/${negociacionId}`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (id: string) => apiClient.delete(`/api/documentos-negociacion/${id}`),
};
