export interface LandingFoto {
  id: string;
  url: string;
  esPrincipal: boolean;
}

export interface LandingVideo {
  id: string;
  url: string;
  origen: 'LINK_EXTERNO' | 'ARCHIVO_SUBIDO';
}

export interface LandingInmueble {
  id: string;
  codigo: string;
  tipoPropiedad: string;
  ciudad?: string;
  zona?: string;
  entorno?: string;
  areaTotal?: number;
  areaConstruida?: number;
  areaPrivada?: number;
  numeroHabitaciones?: number;
  numeroBanos?: number;
  piso?: number;
  niveles?: string;
  vista?: string;
  estratoSocioeconomico?: number;
  precio?: number;
  descripcionPublica?: string;
  ganchoEmocional?: string;
  fotos: LandingFoto[];
  videos: LandingVideo[];
  caracteristicas: string[];
}

export interface LandingLeadRequest {
  nombre: string;
  telefono: string;
  correoElectronico?: string;
  mensaje?: string;
}
