const COLORES = ['#0b4f6c', '#7cb305', '#13c2c2', '#722ed1', '#fa8c16', '#2f54eb', '#eb2f96', '#389e0d'];

export function obtenerIniciales(nombre: string): string {
  const partes = nombre.trim().split(/\s+/);
  const iniciales = partes
    .slice(0, 2)
    .map((p) => p[0]?.toUpperCase() ?? '')
    .join('');
  return iniciales || '?';
}

export function colorDesdeTexto(texto: string): string {
  let hash = 0;
  for (let i = 0; i < texto.length; i++) {
    hash = texto.charCodeAt(i) + ((hash << 5) - hash);
  }
  return COLORES[Math.abs(hash) % COLORES.length];
}
