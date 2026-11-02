export type RolUsuario = 'ADMIN' | 'ASESOR';

export interface LoginRequest {
  email: string;
  password: string;
}

export interface LoginResponse {
  token: string;
  usuarioId: string;
  nombre: string;
  email: string;
  rol: RolUsuario;
}

export interface UsuarioAutenticado {
  usuarioId: string;
  nombre: string;
  email: string;
  rol: RolUsuario;
}

export interface Usuario {
  id: string;
  nombre: string;
  email: string;
  telefono?: string;
  rol: RolUsuario;
  activo: boolean;
  createdAt: string;
}

export interface UsuarioRequest {
  nombre: string;
  email: string;
  telefono?: string;
  password: string;
  rol: RolUsuario;
}

export interface EditarUsuarioRequest {
  nombre: string;
  email: string;
  telefono?: string;
  rol: RolUsuario;
}

export interface CambiarPasswordRequest {
  passwordActual: string;
  passwordNueva: string;
}
