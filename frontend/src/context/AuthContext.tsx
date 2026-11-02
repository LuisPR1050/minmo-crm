import { createContext, useContext, useMemo, useState } from 'react';
import type { ReactNode } from 'react';
import type { UsuarioAutenticado } from '../types/auth';

const CLAVE_TOKEN = 'crm_token';
const CLAVE_USUARIO = 'crm_usuario';

interface AuthContextValue {
  usuario: UsuarioAutenticado | null;
  token: string | null;
  estaAutenticado: boolean;
  iniciarSesion: (token: string, usuario: UsuarioAutenticado) => void;
  cerrarSesion: () => void;
}

const AuthContext = createContext<AuthContextValue | undefined>(undefined);

// Algunos navegadores (Safari en modo privado, navegadores internos de apps
// como WhatsApp/Instagram) bloquean localStorage y lanzan una excepción al
// usarlo. La sesión sigue funcionando en memoria durante la pestaña actual,
// solo no persiste entre recargas.
function leerStorage(clave: string): string | null {
  try {
    return localStorage.getItem(clave);
  } catch {
    return null;
  }
}

function escribirStorage(clave: string, valor: string): void {
  try {
    localStorage.setItem(clave, valor);
  } catch {
    // Almacenamiento no disponible: la sesión queda solo en memoria.
  }
}

function borrarStorage(clave: string): void {
  try {
    localStorage.removeItem(clave);
  } catch {
    // Almacenamiento no disponible.
  }
}

function leerUsuarioGuardado(): UsuarioAutenticado | null {
  const crudo = leerStorage(CLAVE_USUARIO);
  if (!crudo) return null;
  try {
    return JSON.parse(crudo) as UsuarioAutenticado;
  } catch {
    return null;
  }
}

export function AuthProvider({ children }: { children: ReactNode }) {
  const [token, setToken] = useState<string | null>(() => leerStorage(CLAVE_TOKEN));
  const [usuario, setUsuario] = useState<UsuarioAutenticado | null>(leerUsuarioGuardado);

  const iniciarSesion = (nuevoToken: string, nuevoUsuario: UsuarioAutenticado) => {
    escribirStorage(CLAVE_TOKEN, nuevoToken);
    escribirStorage(CLAVE_USUARIO, JSON.stringify(nuevoUsuario));
    setToken(nuevoToken);
    setUsuario(nuevoUsuario);
  };

  const cerrarSesion = () => {
    borrarStorage(CLAVE_TOKEN);
    borrarStorage(CLAVE_USUARIO);
    setToken(null);
    setUsuario(null);
  };

  const value = useMemo<AuthContextValue>(
    () => ({ usuario, token, estaAutenticado: !!token, iniciarSesion, cerrarSesion }),
    [usuario, token],
  );

  return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>;
}

export function useAuth(): AuthContextValue {
  const context = useContext(AuthContext);
  if (!context) {
    throw new Error('useAuth debe usarse dentro de un AuthProvider');
  }
  return context;
}

export function obtenerTokenGuardado(): string | null {
  return leerStorage(CLAVE_TOKEN);
}

export function limpiarSesionGuardada(): void {
  borrarStorage(CLAVE_TOKEN);
  borrarStorage(CLAVE_USUARIO);
}
