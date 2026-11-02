import axios from 'axios';

// Cliente aparte para la landing page publica de un inmueble: la ve el
// cliente final (sin sesion) desde un link compartido, asi que no debe
// llevar el interceptor de apiClient que adjunta el token y redirige a
// /login ante un 401 (irrelevante para un visitante sin cuenta en el CRM).
export const publicoClient = axios.create({
  baseURL: import.meta.env.VITE_API_BASE_URL ?? '',
});
