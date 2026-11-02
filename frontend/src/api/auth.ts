import { apiClient } from './client';
import type { CambiarPasswordRequest, EditarUsuarioRequest, LoginRequest, LoginResponse, Usuario, UsuarioRequest } from '../types/auth';

export const authApi = {
  login: (request: LoginRequest) => apiClient.post<LoginResponse>('/api/auth/login', request).then((res) => res.data),
  cambiarPassword: (request: CambiarPasswordRequest) =>
    apiClient.patch<void>('/api/auth/password', request).then((res) => res.data),
};

export const usuarioApi = {
  listar: () => apiClient.get<Usuario[]>('/api/usuarios').then((res) => res.data),
  crear: (request: UsuarioRequest) => apiClient.post<Usuario>('/api/usuarios', request).then((res) => res.data),
  cambiarEstado: (id: string, activo: boolean) =>
    apiClient.patch<Usuario>(`/api/usuarios/${id}/estado`, { activo }).then((res) => res.data),
  actualizar: (id: string, request: EditarUsuarioRequest) =>
    apiClient.patch<Usuario>(`/api/usuarios/${id}`, request).then((res) => res.data),
  eliminar: (id: string) => apiClient.delete<void>(`/api/usuarios/${id}`).then((res) => res.data),
};
