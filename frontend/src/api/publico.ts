import { publicoClient } from './publicoClient';
import type { LandingInmueble, LandingLeadRequest } from '../types/publico';

export const landingInmuebleApi = {
  obtener: (id: string) => publicoClient.get<LandingInmueble>(`/api/public/inmuebles/${id}`).then((res) => res.data),
  enviarLead: (id: string, request: LandingLeadRequest) =>
    publicoClient.post<void>(`/api/public/inmuebles/${id}/leads`, request).then((res) => res.data),
};
