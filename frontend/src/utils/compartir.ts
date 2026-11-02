export const construirEnlaceWhatsapp = (whatsapp: string, texto: string) =>
  `https://wa.me/${whatsapp.replace(/\D/g, '')}?text=${encodeURIComponent(texto)}`;

export const construirEnlaceCorreo = (correo: string, asunto: string, texto: string) =>
  `mailto:${correo}?subject=${encodeURIComponent(asunto)}&body=${encodeURIComponent(texto)}`;
