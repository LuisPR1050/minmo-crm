import { apiClient } from './client';
import type {
  ActaEntrega,
  ActaEntregaRequest,
  ActualizarContratoCorretajeRequest,
  ActualizarOtrosiPromesaRequest,
  ActualizarPromesaCompraventaRequest,
  AutorizacionPropietario,
  ClausulaCorretaje,
  ComparacionDocumentos,
  ContratoCorretaje,
  ContratoCorretajeRequest,
  DocumentoActaEntrega,
  DocumentoContratoCorretaje,
  DocumentoOtrosiPromesa,
  GenerarTextoIARequest,
  DocumentoPromesaCompraventa,
  DocumentoReciboPago,
  HistorialContratoCorretaje,
  OtrosiPromesa,
  OtrosiPromesaRequest,
  PromesaCompraventa,
  PromesaCompraventaRequest,
  ReciboPago,
  ReciboPagoRequest,
  ResumenContratosCorretaje,
  TipoDocumentoActaEntrega,
  TipoDocumentoContratoCorretaje,
  TipoDocumentoPromesaCompraventa,
} from '../types/documentacion';

export const promesaCompraventaApi = {
  listarTodos: () => apiClient.get<PromesaCompraventa[]>('/api/promesas-compraventa').then((res) => res.data),
  buscarPorNegociacion: async (negociacionId: string): Promise<PromesaCompraventa | null> => {
    const todas = await promesaCompraventaApi.listarTodos();
    return todas.find((p) => p.negociacionId === negociacionId) ?? null;
  },
  crear: (request: PromesaCompraventaRequest) =>
    apiClient.post<PromesaCompraventa>('/api/promesas-compraventa', request).then((res) => res.data),
  actualizar: (id: string, request: ActualizarPromesaCompraventaRequest) =>
    apiClient.patch<PromesaCompraventa>(`/api/promesas-compraventa/${id}`, request).then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<PromesaCompraventa>(`/api/promesas-compraventa/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  generarTexto: (id: string, request?: GenerarTextoIARequest) =>
    apiClient.post<PromesaCompraventa>(`/api/promesas-compraventa/${id}/generar-texto`, request).then((res) => res.data),
};

export const clausulaPromesaApi = {
  listarTodas: () => apiClient.get<ClausulaCorretaje[]>('/api/clausulas-promesa').then((res) => res.data),
};

export const promesaCompraventaClausulaApi = {
  listarAsignadas: (promesaCompraventaId: string) =>
    apiClient.get<ClausulaCorretaje[]>(`/api/promesas-compraventa/${promesaCompraventaId}/clausulas`).then((res) => res.data),
  asignar: (promesaCompraventaId: string, clausulaIds: string[]) =>
    apiClient
      .post<ClausulaCorretaje[]>(`/api/promesas-compraventa/${promesaCompraventaId}/clausulas`, { clausulaIds })
      .then((res) => res.data),
};

export const otrosiPromesaClausulaApi = {
  listarAsignadas: (otrosiPromesaId: string) =>
    apiClient.get<ClausulaCorretaje[]>(`/api/otrosis-promesa/${otrosiPromesaId}/clausulas`).then((res) => res.data),
  asignar: (otrosiPromesaId: string, clausulaIds: string[]) =>
    apiClient
      .post<ClausulaCorretaje[]>(`/api/otrosis-promesa/${otrosiPromesaId}/clausulas`, { clausulaIds })
      .then((res) => res.data),
};

export const condicionEntregaApi = {
  listarTodas: () => apiClient.get<ClausulaCorretaje[]>('/api/condiciones-entrega').then((res) => res.data),
};

export const accesoEntregaApi = {
  listarTodas: () => apiClient.get<ClausulaCorretaje[]>('/api/accesos-entrega').then((res) => res.data),
};

export const pazYSalvoTipoApi = {
  listarTodas: () => apiClient.get<ClausulaCorretaje[]>('/api/paz-y-salvo-tipos').then((res) => res.data),
};

export const actaEntregaCondicionApi = {
  listarAsignadas: (actaEntregaId: string) =>
    apiClient.get<ClausulaCorretaje[]>(`/api/actas-entrega/${actaEntregaId}/condiciones`).then((res) => res.data),
  asignar: (actaEntregaId: string, condicionIds: string[]) =>
    apiClient
      .post<ClausulaCorretaje[]>(`/api/actas-entrega/${actaEntregaId}/condiciones`, { condicionIds })
      .then((res) => res.data),
};

export const actaEntregaAccesoApi = {
  listarAsignadas: (actaEntregaId: string) =>
    apiClient.get<ClausulaCorretaje[]>(`/api/actas-entrega/${actaEntregaId}/accesos`).then((res) => res.data),
  asignar: (actaEntregaId: string, accesoIds: string[]) =>
    apiClient.post<ClausulaCorretaje[]>(`/api/actas-entrega/${actaEntregaId}/accesos`, { accesoIds }).then((res) => res.data),
};

export const actaEntregaPazYSalvoApi = {
  listarAsignadas: (actaEntregaId: string) =>
    apiClient.get<ClausulaCorretaje[]>(`/api/actas-entrega/${actaEntregaId}/paz-y-salvos`).then((res) => res.data),
  asignar: (actaEntregaId: string, pazYSalvoTipoIds: string[]) =>
    apiClient
      .post<ClausulaCorretaje[]>(`/api/actas-entrega/${actaEntregaId}/paz-y-salvos`, { pazYSalvoTipoIds })
      .then((res) => res.data),
};

export const reciboPagoApi = {
  listarPorNegociacion: (negociacionId: string) =>
    apiClient
      .get<ReciboPago[]>('/api/recibos-pago', { params: { negociacionId } })
      .then((res) => res.data),
  crear: (request: ReciboPagoRequest) => apiClient.post<ReciboPago>('/api/recibos-pago', request).then((res) => res.data),
};

export const actaEntregaApi = {
  buscarPorNegociacion: async (negociacionId: string): Promise<ActaEntrega | null> => {
    try {
      const res = await apiClient.get<ActaEntrega>(`/api/actas-entrega/negociacion/${negociacionId}`);
      return res.data;
    } catch {
      return null;
    }
  },
  crear: (request: ActaEntregaRequest) => apiClient.post<ActaEntrega>('/api/actas-entrega', request).then((res) => res.data),
  revisarIA: (id: string) => apiClient.post(`/api/actas-entrega/${id}/revisar-ia`).then((res) => res.data),
};

export const contratoCorretajeApi = {
  listarPorInmueble: (inmuebleId: string) =>
    apiClient.get<ContratoCorretaje[]>('/api/contratos-corretaje', { params: { inmuebleId } }).then((res) => res.data),
  buscarPorId: (id: string) => apiClient.get<ContratoCorretaje>(`/api/contratos-corretaje/${id}`).then((res) => res.data),
  crear: (request: ContratoCorretajeRequest) =>
    apiClient.post<ContratoCorretaje>('/api/contratos-corretaje', request).then((res) => res.data),
  actualizar: (id: string, request: ActualizarContratoCorretajeRequest) =>
    apiClient.patch<ContratoCorretaje>(`/api/contratos-corretaje/${id}`, request).then((res) => res.data),
  cambiarEstado: (id: string, nuevoEstado: string) =>
    apiClient.patch<ContratoCorretaje>(`/api/contratos-corretaje/${id}/estado`, { nuevoEstado }).then((res) => res.data),
  generarTexto: (id: string, request?: GenerarTextoIARequest) =>
    apiClient.post<ContratoCorretaje>(`/api/contratos-corretaje/${id}/generar-texto`, request).then((res) => res.data),
  resumen: () => apiClient.get<ResumenContratosCorretaje>('/api/contratos-corretaje/resumen').then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/contratos-corretaje/${id}`).then((res) => res.data),
};

export const clausulaCorretajeApi = {
  listarTodas: () => apiClient.get<ClausulaCorretaje[]>('/api/clausulas-corretaje').then((res) => res.data),
  listarAsignadas: (contratoCorretajeId: string) =>
    apiClient.get<ClausulaCorretaje[]>(`/api/contratos-corretaje/${contratoCorretajeId}/clausulas`).then((res) => res.data),
  asignar: (contratoCorretajeId: string, clausulaIds: string[]) =>
    apiClient
      .post<ClausulaCorretaje[]>(`/api/contratos-corretaje/${contratoCorretajeId}/clausulas`, { clausulaIds })
      .then((res) => res.data),
};

export const autorizacionPropietarioApi = {
  listarTodas: () => apiClient.get<AutorizacionPropietario[]>('/api/autorizaciones-propietario').then((res) => res.data),
  listarAsignadas: (contratoCorretajeId: string) =>
    apiClient
      .get<AutorizacionPropietario[]>(`/api/contratos-corretaje/${contratoCorretajeId}/autorizaciones`)
      .then((res) => res.data),
  asignar: (contratoCorretajeId: string, autorizacionIds: string[]) =>
    apiClient
      .post<AutorizacionPropietario[]>(`/api/contratos-corretaje/${contratoCorretajeId}/autorizaciones`, { autorizacionIds })
      .then((res) => res.data),
};

export const documentoContratoCorretajeApi = {
  listarPorContrato: (contratoCorretajeId: string) =>
    apiClient
      .get<DocumentoContratoCorretaje[]>(`/api/contratos-corretaje/${contratoCorretajeId}/documentos`)
      .then((res) => res.data),
  subir: (contratoCorretajeId: string, tipo: TipoDocumentoContratoCorretaje, archivo: File) => {
    const formData = new FormData();
    formData.append('tipo', tipo);
    formData.append('archivo', archivo);
    return apiClient
      .post<DocumentoContratoCorretaje>(`/api/contratos-corretaje/${contratoCorretajeId}/documentos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (contratoCorretajeId: string, id: string) =>
    apiClient.delete<void>(`/api/contratos-corretaje/${contratoCorretajeId}/documentos/${id}`).then((res) => res.data),
};

export const historialContratoCorretajeApi = {
  listarPorContrato: (contratoCorretajeId: string) =>
    apiClient
      .get<HistorialContratoCorretaje[]>('/api/historial-contrato-corretaje', { params: { contratoCorretajeId } })
      .then((res) => res.data),
};

export const documentoPromesaCompraventaApi = {
  listarPorPromesa: (promesaCompraventaId: string) =>
    apiClient
      .get<DocumentoPromesaCompraventa[]>(`/api/promesas-compraventa/${promesaCompraventaId}/documentos`)
      .then((res) => res.data),
  subir: (promesaCompraventaId: string, tipo: TipoDocumentoPromesaCompraventa, archivo: File) => {
    const formData = new FormData();
    formData.append('tipo', tipo);
    formData.append('archivo', archivo);
    return apiClient
      .post<DocumentoPromesaCompraventa>(`/api/promesas-compraventa/${promesaCompraventaId}/documentos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (promesaCompraventaId: string, id: string) =>
    apiClient.delete<void>(`/api/promesas-compraventa/${promesaCompraventaId}/documentos/${id}`).then((res) => res.data),
};

export const documentoOtrosiPromesaApi = {
  listarPorOtrosi: (otrosiPromesaId: string) =>
    apiClient.get<DocumentoOtrosiPromesa[]>(`/api/otrosis-promesa/${otrosiPromesaId}/documentos`).then((res) => res.data),
  subir: (otrosiPromesaId: string, descripcion: string | undefined, archivo: File) => {
    const formData = new FormData();
    if (descripcion) formData.append('descripcion', descripcion);
    formData.append('archivo', archivo);
    return apiClient
      .post<DocumentoOtrosiPromesa>(`/api/otrosis-promesa/${otrosiPromesaId}/documentos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (otrosiPromesaId: string, id: string) =>
    apiClient.delete<void>(`/api/otrosis-promesa/${otrosiPromesaId}/documentos/${id}`).then((res) => res.data),
};

export const documentoActaEntregaApi = {
  listarPorActa: (actaEntregaId: string) =>
    apiClient.get<DocumentoActaEntrega[]>(`/api/actas-entrega/${actaEntregaId}/documentos`).then((res) => res.data),
  subir: (actaEntregaId: string, tipo: TipoDocumentoActaEntrega, archivo: File) => {
    const formData = new FormData();
    formData.append('tipo', tipo);
    formData.append('archivo', archivo);
    return apiClient
      .post<DocumentoActaEntrega>(`/api/actas-entrega/${actaEntregaId}/documentos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (actaEntregaId: string, id: string) =>
    apiClient.delete<void>(`/api/actas-entrega/${actaEntregaId}/documentos/${id}`).then((res) => res.data),
};

export const documentoReciboPagoApi = {
  listarPorRecibo: (reciboPagoId: string) =>
    apiClient.get<DocumentoReciboPago[]>(`/api/recibos-pago/${reciboPagoId}/documentos`).then((res) => res.data),
  subir: (reciboPagoId: string, archivo: File) => {
    const formData = new FormData();
    formData.append('archivo', archivo);
    return apiClient
      .post<DocumentoReciboPago>(`/api/recibos-pago/${reciboPagoId}/documentos/subir`, formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
  eliminar: (reciboPagoId: string, id: string) =>
    apiClient.delete<void>(`/api/recibos-pago/${reciboPagoId}/documentos/${id}`).then((res) => res.data),
};

export const comparacionDocumentosApi = {
  compararIA: (documentoA: File, documentoB: File) => {
    const formData = new FormData();
    formData.append('documentoA', documentoA);
    formData.append('documentoB', documentoB);
    return apiClient
      .post<ComparacionDocumentos>('/api/documentos/comparar-ia', formData, {
        headers: { 'Content-Type': 'multipart/form-data' },
      })
      .then((res) => res.data);
  },
};

export const otrosiPromesaApi = {
  listarPorPromesa: (promesaCompraventaId: string) =>
    apiClient.get<OtrosiPromesa[]>('/api/otrosis-promesa', { params: { promesaCompraventaId } }).then((res) => res.data),
  crear: (request: OtrosiPromesaRequest) =>
    apiClient.post<OtrosiPromesa>('/api/otrosis-promesa', request).then((res) => res.data),
  actualizar: (id: string, request: ActualizarOtrosiPromesaRequest) =>
    apiClient.patch<OtrosiPromesa>(`/api/otrosis-promesa/${id}`, request).then((res) => res.data),
  generarTexto: (id: string, request?: GenerarTextoIARequest) =>
    apiClient.post<OtrosiPromesa>(`/api/otrosis-promesa/${id}/generar-texto`, request).then((res) => res.data),
  aplicar: (id: string) => apiClient.patch<OtrosiPromesa>(`/api/otrosis-promesa/${id}/aplicar`).then((res) => res.data),
};
