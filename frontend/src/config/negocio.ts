export const NEGOCIO = {
  nombreResponsable: 'Luis Alberto Peña Ruidiaz',
  calidad: 'Persona natural — agente inmobiliario independiente',
  pais: 'Colombia',
  email: 'sentinelxcorporativo@gmail.com',
  telefonoVisible: '314 784 5192',
  whatsapp: '573147845192',
} as const;

export const construirEnlaceWhatsappNegocio = (texto: string) =>
  `https://wa.me/${NEGOCIO.whatsapp}?text=${encodeURIComponent(texto)}`;
