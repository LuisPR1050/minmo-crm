import { apiClient } from './client';
import type { GenerarTextoIARequest } from '../types/documentacion';
import type {
  ActualizarPublicacionInmuebleRequest,
  ActualizarSolicitudVideoMarketingRequest,
  AnalisisFotos,
  CanalPublicacionVideo,
  CopiaVideoCanal,
  DestinoVideo,
  EstadisticaPortal,
  EstadisticaPortalRequest,
  FotoPublicacion,
  FotoSolicitudVideo,
  HistorialPublicacion,
  PortalInmobiliario,
  PublicacionInmueble,
  PublicacionInmueblePortal,
  PublicacionInmuebleRequest,
  ResumenEstadisticasPublicacion,
  SolicitudVideoMarketing,
  SolicitudVideoMarketingRequest,
  TipoPublicoVideo,
  VideoFuenteSolicitud,
  VideoFuenteSolicitudRequest,
  VideoPublicacion,
  VideoPublicacionRequest,
} from '../types/marketing';

export const publicacionInmuebleApi = {
  listar: () => apiClient.get<PublicacionInmueble[]>('/api/publicaciones-inmuebles').then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<PublicacionInmueble>(`/api/publicaciones-inmuebles/${id}`).then((res) => res.data),
  crear: (request: PublicacionInmuebleRequest) =>
    apiClient.post<PublicacionInmueble>('/api/publicaciones-inmuebles', request).then((res) => res.data),
  actualizar: (id: string, request: ActualizarPublicacionInmuebleRequest) =>
    apiClient.patch<PublicacionInmueble>(`/api/publicaciones-inmuebles/${id}`, request).then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<PublicacionInmueble>(`/api/publicaciones-inmuebles/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  generarDescripciones: (id: string) =>
    apiClient.post<PublicacionInmueble>(`/api/publicaciones-inmuebles/${id}/generar-descripciones`).then((res) => res.data),
  analizarFotosConIA: (id: string) =>
    apiClient.post<AnalisisFotos>(`/api/publicaciones-inmuebles/${id}/analisis-fotos-ia`).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/publicaciones-inmuebles/${id}`).then((res) => res.data),
};

export const portalInmobiliarioApi = {
  listarTodos: () => apiClient.get<PortalInmobiliario[]>('/api/portales-inmobiliarios').then((res) => res.data),
};

export const publicacionInmueblePortalApi = {
  listarPorPublicacion: (publicacionInmuebleId: string) =>
    apiClient
      .get<PublicacionInmueblePortal[]>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/portales`)
      .then((res) => res.data),
  asignar: (publicacionInmuebleId: string, portalIds: string[]) =>
    apiClient
      .post<PublicacionInmueblePortal[]>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/portales`, { portalIds })
      .then((res) => res.data),
  cambiarEstado: (vinculoId: string, nuevoEstado: string) =>
    apiClient.patch<PublicacionInmueblePortal>(`/api/publicacion-portal/${vinculoId}/estado`, { nuevoEstado }).then((res) => res.data),
};

export const fotoPublicacionApi = {
  listarPorPublicacion: (publicacionInmuebleId: string) =>
    apiClient.get<FotoPublicacion[]>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/fotos`).then((res) => res.data),
  subir: (publicacionInmuebleId: string, foto: File, esPrincipal: boolean) => {
    const formData = new FormData();
    formData.append('foto', foto);
    formData.append('esPrincipal', String(esPrincipal));
    return apiClient
      .post<FotoPublicacion>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/fotos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
};

export const estadisticaPortalApi = {
  listarPorVinculo: (vinculoId: string) =>
    apiClient.get<EstadisticaPortal[]>(`/api/publicacion-portal/${vinculoId}/estadisticas`).then((res) => res.data),
  registrar: (vinculoId: string, request: EstadisticaPortalRequest) =>
    apiClient.post<EstadisticaPortal>(`/api/publicacion-portal/${vinculoId}/estadisticas`, request).then((res) => res.data),
  resumenPorPublicacion: (publicacionInmuebleId: string) =>
    apiClient
      .get<ResumenEstadisticasPublicacion>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/estadisticas/resumen`)
      .then((res) => res.data),
};

export const historialPublicacionApi = {
  listarPorPublicacion: (publicacionInmuebleId: string) =>
    apiClient
      .get<HistorialPublicacion[]>('/api/historial-publicacion', { params: { publicacionInmuebleId } })
      .then((res) => res.data),
};

export const videoPublicacionApi = {
  listarPorPublicacion: (publicacionInmuebleId: string) =>
    apiClient.get<VideoPublicacion[]>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/videos`).then((res) => res.data),
  agregar: (publicacionInmuebleId: string, request: VideoPublicacionRequest) =>
    apiClient.post<VideoPublicacion>(`/api/publicaciones-inmuebles/${publicacionInmuebleId}/videos`, request).then((res) => res.data),
};

export const solicitudVideoApi = {
  listar: () => apiClient.get<SolicitudVideoMarketing[]>('/api/solicitudes-video').then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}`).then((res) => res.data),
  crear: (request: SolicitudVideoMarketingRequest) =>
    apiClient.post<SolicitudVideoMarketing>('/api/solicitudes-video', request).then((res) => res.data),
  actualizar: (id: string, request: ActualizarSolicitudVideoMarketingRequest) =>
    apiClient.patch<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}`, request).then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  subirLogo: (id: string, logo: File) => {
    const formData = new FormData();
    formData.append('logo', logo);
    return apiClient
      .post<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}/logo`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  subirMusica: (id: string, musica: File) => {
    const formData = new FormData();
    formData.append('musica', musica);
    return apiClient
      .post<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}/musica`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminarMusica: (id: string) =>
    apiClient.delete<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}/musica`).then((res) => res.data),
  generarCopy: (id: string, request?: GenerarTextoIARequest) =>
    apiClient.post<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}/generar-copy`, request).then((res) => res.data),
  generarVideo: (id: string, request?: { incluirVozOff?: boolean }) =>
    apiClient.post<SolicitudVideoMarketing>(`/api/solicitudes-video/${id}/generar-video`, request).then((res) => res.data),
  listarCopiasPorCanal: (id: string) =>
    apiClient.get<CopiaVideoCanal[]>(`/api/solicitudes-video/${id}/copias-por-canal`).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/solicitudes-video/${id}`).then((res) => res.data),
};

export const canalPublicacionVideoApi = {
  listarTodos: () => apiClient.get<CanalPublicacionVideo[]>('/api/canales-publicacion-video').then((res) => res.data),
};

export const solicitudVideoCanalApi = {
  listarPorSolicitud: (solicitudVideoId: string) =>
    apiClient.get<CanalPublicacionVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/canales`).then((res) => res.data),
  asignar: (solicitudVideoId: string, canalIds: string[]) =>
    apiClient
      .post<CanalPublicacionVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/canales`, { canalIds })
      .then((res) => res.data),
};

export const destinoVideoApi = {
  listarTodos: () => apiClient.get<DestinoVideo[]>('/api/destinos-video').then((res) => res.data),
};

export const solicitudVideoDestinoApi = {
  listarPorSolicitud: (solicitudVideoId: string) =>
    apiClient.get<DestinoVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/destinos`).then((res) => res.data),
  asignar: (solicitudVideoId: string, destinoIds: string[]) =>
    apiClient.post<DestinoVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/destinos`, { destinoIds }).then((res) => res.data),
};

export const tipoPublicoVideoApi = {
  listarTodos: () => apiClient.get<TipoPublicoVideo[]>('/api/tipos-publico-video').then((res) => res.data),
};

export const solicitudVideoTipoPublicoApi = {
  listarPorSolicitud: (solicitudVideoId: string) =>
    apiClient.get<TipoPublicoVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/tipos-publico`).then((res) => res.data),
  asignar: (solicitudVideoId: string, tipoPublicoIds: string[]) =>
    apiClient
      .post<TipoPublicoVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/tipos-publico`, { tipoPublicoIds })
      .then((res) => res.data),
};

export const fotoSolicitudVideoApi = {
  listarPorSolicitud: (solicitudVideoId: string) =>
    apiClient.get<FotoSolicitudVideo[]>(`/api/solicitudes-video/${solicitudVideoId}/fotos`).then((res) => res.data),
  agregar: (solicitudVideoId: string, request: { url: string }) =>
    apiClient.post<FotoSolicitudVideo>(`/api/solicitudes-video/${solicitudVideoId}/fotos`, request).then((res) => res.data),
  subir: (solicitudVideoId: string, foto: File) => {
    const formData = new FormData();
    formData.append('foto', foto);
    return apiClient
      .post<FotoSolicitudVideo>(`/api/solicitudes-video/${solicitudVideoId}/fotos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
};

export const videoFuenteSolicitudApi = {
  listarPorSolicitud: (solicitudVideoId: string) =>
    apiClient.get<VideoFuenteSolicitud[]>(`/api/solicitudes-video/${solicitudVideoId}/videos-fuente`).then((res) => res.data),
  agregar: (solicitudVideoId: string, request: VideoFuenteSolicitudRequest) =>
    apiClient
      .post<VideoFuenteSolicitud>(`/api/solicitudes-video/${solicitudVideoId}/videos-fuente`, request)
      .then((res) => res.data),
};
