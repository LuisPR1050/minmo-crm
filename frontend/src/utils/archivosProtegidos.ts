import { apiClient } from '../api/client';

// Los archivos subidos (/uploads/**) ahora exigen sesion en el backend, por lo
// que ya no se pueden referenciar con un <img src="..."> o <a href="..."> plano
// (el navegador no adjunta el token JWT a esas peticiones). Estas funciones piden
// el archivo con axios (que si envia el header Authorization) y lo exponen como
// un blob URL temporal.

export async function abrirDocumentoProtegido(url: string): Promise<void> {
  const ventana = window.open('', '_blank');
  try {
    const respuesta = await apiClient.get<Blob>(url, { responseType: 'blob' });
    const blobUrl = URL.createObjectURL(respuesta.data);
    if (ventana) {
      ventana.location.href = blobUrl;
    } else {
      window.open(blobUrl, '_blank');
    }
    setTimeout(() => URL.revokeObjectURL(blobUrl), 60_000);
  } catch (error) {
    ventana?.close();
    throw error;
  }
}

export async function obtenerBlobUrl(url: string): Promise<string> {
  const respuesta = await apiClient.get<Blob>(url, { responseType: 'blob' });
  return URL.createObjectURL(respuesta.data);
}
