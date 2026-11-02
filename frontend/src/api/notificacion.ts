import { apiClient } from './client';
import type { Notificacion } from '../types/notificacion';

export const notificacionApi = {
  listarNoLeidas: () => apiClient.get<Notificacion[]>('/api/notificaciones').then((res) => res.data),
  marcarComoLeida: (id: string) =>
    apiClient.patch<Notificacion>(`/api/notificaciones/${id}/marcar-leida`).then((res) => res.data),
};
