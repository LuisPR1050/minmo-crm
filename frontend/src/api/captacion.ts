import { apiClient } from './client';
import type {
  ActualizarLandingInmuebleRequest,
  Aliado,
  AliadoRequest,
  AnalisisObjecionesInmueble,
  ArchivoAdjunto,
  Caracteristica,
  CategoriaCaracteristica,
  ChecklistJuridico,
  ChecklistJuridicoRequest,
  ClienteSugerido,
  CondicionCaptacion,
  CondicionCaptacionRequest,
  HistorialCaptacion,
  Inmueble,
  InmuebleRequest,
  PrecioSugeridoIA,
  Propietario,
  PropietarioRequest,
  PuntoReferencia,
  VideoInmueble,
  VideoInmuebleRequest,
  VisitaCaptacion,
  VisitaCaptacionRequest,
} from '../types/captacion';

export const inmuebleApi = {
  listar: () => apiClient.get<Inmueble[]>('/api/inmuebles').then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<Inmueble>(`/api/inmuebles/${id}`).then((res) => res.data),
  crear: (request: InmuebleRequest) => apiClient.post<Inmueble>('/api/inmuebles', request).then((res) => res.data),
  actualizar: (id: string, request: InmuebleRequest) =>
    apiClient.patch<Inmueble>(`/api/inmuebles/${id}`, request).then((res) => res.data),
  eliminar: (id: string, confirmar = false) =>
    apiClient.delete<void>(`/api/inmuebles/${id}`, { params: { confirmar } }).then((res) => res.data),
  cambiarEstadoCaptacion: (id: string, nuevoEstado: string) =>
    apiClient
      .patch<Inmueble>(`/api/inmuebles/${id}/estado-captacion`, { nuevoEstado })
      .then((res) => res.data),
  buscarConIA: (consulta: string) =>
    apiClient.post<Inmueble[]>('/api/inmuebles/busqueda-ia', { consulta }).then((res) => res.data),
  sugerirPrecioConIA: (id: string) =>
    apiClient.post<PrecioSugeridoIA>(`/api/inmuebles/${id}/precio-sugerido-ia`).then((res) => res.data),
  analizarObjecionesConIA: (id: string) =>
    apiClient.post<AnalisisObjecionesInmueble>(`/api/inmuebles/${id}/analisis-objeciones-ia`).then((res) => res.data),
  sugerirClientesConIA: (id: string) =>
    apiClient.post<ClienteSugerido[]>(`/api/inmuebles/${id}/clientes-sugeridos-ia`).then((res) => res.data),
  actualizarLanding: (id: string, request: ActualizarLandingInmuebleRequest) =>
    apiClient.patch<Inmueble>(`/api/inmuebles/${id}/landing`, request).then((res) => res.data),
  generarGanchoEmocionalConIA: (id: string) =>
    apiClient.post<Inmueble>(`/api/inmuebles/${id}/gancho-emocional-ia`).then((res) => res.data),
};

export const videoInmuebleApi = {
  listar: (inmuebleId: string) =>
    apiClient.get<VideoInmueble[]>(`/api/inmuebles/${inmuebleId}/videos`).then((res) => res.data),
  agregar: (inmuebleId: string, request: VideoInmuebleRequest) =>
    apiClient.post<VideoInmueble>(`/api/inmuebles/${inmuebleId}/videos`, request).then((res) => res.data),
  subirArchivo: (inmuebleId: string, archivo: File) => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    return apiClient
      .post<VideoInmueble>(`/api/inmuebles/${inmuebleId}/videos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (inmuebleId: string, id: string) =>
    apiClient.delete<void>(`/api/inmuebles/${inmuebleId}/videos/${id}`).then((res) => res.data),
};

export const puntoReferenciaApi = {
  listar: () => apiClient.get<PuntoReferencia[]>('/api/puntos-referencia').then((res) => res.data),
};

export const propietarioApi = {
  listar: () => apiClient.get<Propietario[]>('/api/propietarios').then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<Propietario>(`/api/propietarios/${id}`).then((res) => res.data),
  crear: (request: PropietarioRequest) =>
    apiClient.post<Propietario>('/api/propietarios', request).then((res) => res.data),
  actualizar: (id: string, request: PropietarioRequest) =>
    apiClient.patch<Propietario>(`/api/propietarios/${id}`, request).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/propietarios/${id}`).then((res) => res.data),
};

export const archivoAdjuntoApi = {
  listarPorInmueble: (inmuebleId: string) =>
    apiClient.get<ArchivoAdjunto[]>('/api/archivos-adjuntos', { params: { inmuebleId } }).then((res) => res.data),
  subirFoto: (inmuebleId: string, foto: File, esPrincipal: boolean) => {
    const formData = new FormData();
    formData.append('foto', foto);
    formData.append('esPrincipal', String(esPrincipal));
    return apiClient
      .post<ArchivoAdjunto>(`/api/archivos-adjuntos/inmuebles/${inmuebleId}/fotos`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  marcarComoPrincipal: (id: string) =>
    apiClient.patch<ArchivoAdjunto>(`/api/archivos-adjuntos/${id}/marcar-principal`).then((res) => res.data),
  subirDocumento: (inmuebleId: string, documento: File, tipo: string) => {
    const formData = new FormData();
    formData.append('documento', documento);
    formData.append('tipo', tipo);
    return apiClient
      .post<ArchivoAdjunto>(`/api/archivos-adjuntos/inmuebles/${inmuebleId}/documentos`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (id: string) => apiClient.delete<void>(`/api/archivos-adjuntos/${id}`).then((res) => res.data),
};

export const caracteristicaApi = {
  listar: (categoria?: CategoriaCaracteristica) =>
    apiClient.get<Caracteristica[]>('/api/caracteristicas', { params: categoria ? { categoria } : {} }).then((res) => res.data),
};

export const inmuebleCaracteristicaApi = {
  listar: (inmuebleId: string) =>
    apiClient.get<Caracteristica[]>(`/api/inmuebles/${inmuebleId}/caracteristicas`).then((res) => res.data),
  asignar: (inmuebleId: string, caracteristicaIds: string[]) =>
    apiClient
      .post<Caracteristica[]>(`/api/inmuebles/${inmuebleId}/caracteristicas`, { caracteristicaIds })
      .then((res) => res.data),
};

export const aliadoApi = {
  listar: () => apiClient.get<Aliado[]>('/api/aliados').then((res) => res.data),
  crear: (request: AliadoRequest) => apiClient.post<Aliado>('/api/aliados', request).then((res) => res.data),
};

export const condicionCaptacionApi = {
  buscarPorInmueble: async (inmuebleId: string): Promise<CondicionCaptacion | null> => {
    try {
      const res = await apiClient.get<CondicionCaptacion>(`/api/condiciones-captacion/por-inmueble/${inmuebleId}`);
      return res.data;
    } catch {
      return null;
    }
  },
  guardar: (request: CondicionCaptacionRequest) =>
    apiClient.post<CondicionCaptacion>('/api/condiciones-captacion', request).then((res) => res.data),
};

export const checklistJuridicoApi = {
  buscarPorInmueble: async (inmuebleId: string): Promise<ChecklistJuridico | null> => {
    try {
      const res = await apiClient.get<ChecklistJuridico>(`/api/checklists-juridicos/por-inmueble/${inmuebleId}`);
      return res.data;
    } catch {
      return null;
    }
  },
  guardar: (request: ChecklistJuridicoRequest) =>
    apiClient.post<ChecklistJuridico>('/api/checklists-juridicos', request).then((res) => res.data),
};

export const historialCaptacionApi = {
  listarPorInmueble: (inmuebleId: string) =>
    apiClient.get<HistorialCaptacion[]>('/api/historial-captacion', { params: { inmuebleId } }).then((res) => res.data),
};

export const visitaCaptacionApi = {
  listarTodas: () => apiClient.get<VisitaCaptacion[]>('/api/visitas-captacion').then((res) => res.data),
  listarPorInmueble: (inmuebleId: string) =>
    apiClient.get<VisitaCaptacion[]>('/api/visitas-captacion', { params: { inmuebleId } }).then((res) => res.data),
  crear: (request: VisitaCaptacionRequest) =>
    apiClient.post<VisitaCaptacion>('/api/visitas-captacion', request).then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<VisitaCaptacion>(`/api/visitas-captacion/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/visitas-captacion/${id}`).then((res) => res.data),
};
