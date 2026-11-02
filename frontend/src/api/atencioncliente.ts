import { apiClient } from './client';
import type {
  AnalisisObjeciones,
  Cliente,
  ClienteRequest,
  InmuebleRecomendado,
  Objecion,
  RequerimientoCliente,
  RequerimientoClienteRequest,
  ResultadoVisita,
  ResultadoVisitaRequest,
  ResumenVisitaIA,
  VisitaCliente,
  VisitaClienteRequest,
} from '../types/atencioncliente';

export const clienteApi = {
  listar: () => apiClient.get<Cliente[]>('/api/clientes').then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<Cliente>(`/api/clientes/${id}`).then((res) => res.data),
  crear: (request: ClienteRequest) => apiClient.post<Cliente>('/api/clientes', request).then((res) => res.data),
  cambiarEstadoPipeline: (id: string, nuevoEstado: string) =>
    apiClient.patch<Cliente>(`/api/clientes/${id}/estado-pipeline`, { nuevoEstado }).then((res) => res.data),
  eliminar: (id: string, confirmar = false) =>
    apiClient.delete<void>(`/api/clientes/${id}`, { params: { confirmar } }).then((res) => res.data),
  analizarObjecionesConIA: (id: string) =>
    apiClient.post<AnalisisObjeciones>(`/api/clientes/${id}/analisis-objeciones-ia`).then((res) => res.data),
};

export const requerimientoClienteApi = {
  listarPorCliente: (clienteId: string) =>
    apiClient.get<RequerimientoCliente[]>('/api/requerimientos-cliente', { params: { clienteId } }).then((res) => res.data),
  crear: (request: RequerimientoClienteRequest) =>
    apiClient.post<RequerimientoCliente>('/api/requerimientos-cliente', request).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/requerimientos-cliente/${id}`).then((res) => res.data),
  recomendarConIA: (id: string) =>
    apiClient.post<InmuebleRecomendado[]>(`/api/requerimientos-cliente/${id}/recomendaciones-ia`).then((res) => res.data),
};

export const visitaClienteApi = {
  listarTodas: () => apiClient.get<VisitaCliente[]>('/api/visitas-cliente').then((res) => res.data),
  listarPorCliente: (clienteId: string) =>
    apiClient.get<VisitaCliente[]>('/api/visitas-cliente', { params: { clienteId } }).then((res) => res.data),
  crear: (request: VisitaClienteRequest) =>
    apiClient.post<VisitaCliente>('/api/visitas-cliente', request).then((res) => res.data),
  confirmar: (id: string, confirmadaPorCliente: boolean, confirmadaPorPropietario: boolean) =>
    apiClient
      .patch<VisitaCliente>(`/api/visitas-cliente/${id}/confirmar`, { confirmadaPorCliente, confirmadaPorPropietario })
      .then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<VisitaCliente>(`/api/visitas-cliente/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/visitas-cliente/${id}`).then((res) => res.data),
};

export const objecionApi = {
  listarTodas: () => apiClient.get<Objecion[]>('/api/objeciones').then((res) => res.data),
};

export const resultadoVisitaApi = {
  buscarPorVisita: async (visitaId: string): Promise<ResultadoVisita | null> => {
    try {
      const res = await apiClient.get<ResultadoVisita>(`/api/resultados-visita/por-visita/${visitaId}`);
      return res.data;
    } catch {
      return null;
    }
  },
  guardar: (request: ResultadoVisitaRequest) =>
    apiClient.post<ResultadoVisita>('/api/resultados-visita', request).then((res) => res.data),
  generarResumenConIA: (id: string) =>
    apiClient.post<ResumenVisitaIA>(`/api/resultados-visita/${id}/resumen-ia`).then((res) => res.data),
};
