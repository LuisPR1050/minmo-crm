import axios from 'axios';
import { limpiarSesionGuardada, obtenerTokenGuardado } from '../context/AuthContext';

// Rutas relativas: en desarrollo local y a través del túnel de ngrok, Vite
// hace de proxy de /api hacia el backend (ver vite.config.ts). Así evitamos
// problemas de CORS y de URLs que cambian entre entornos.
export const apiClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
  headers: {
    'Content-Type': 'application/json',
  },
});

apiClient.interceptors.request.use((config) => {
  const token = obtenerTokenGuardado();
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

// Endpoints cuyo 401 es un error de negocio (credenciales/contraseña incorrectas), no una
// sesión inválida — no deben cerrar la sesión del usuario ni redirigirlo a /login.
const RUTAS_401_SIN_CERRAR_SESION = ['/api/auth/login', '/api/auth/password'];

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    const url: string = error.config?.url ?? '';
    const esError401DeNegocio = RUTAS_401_SIN_CERRAR_SESION.some((ruta) => url.includes(ruta));

    if (error.response?.status === 401 && !esError401DeNegocio) {
      limpiarSesionGuardada();
      if (window.location.pathname !== '/login') {
        window.location.href = '/login';
      }
    }
    return Promise.reject(error);
  },
);
