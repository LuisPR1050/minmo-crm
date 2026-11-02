export function formatearNumeroCO(valor: number | string | undefined | null): string {
  if (valor === undefined || valor === null || valor === '') return '';
  const texto = String(valor);
  const negativo = texto.startsWith('-');
  const sinSigno = negativo ? texto.slice(1) : texto;
  const [entero, decimal] = sinSigno.split('.');
  const enteroFormateado = entero.replace(/\B(?=(\d{3})+(?!\d))/g, '.');
  const resultado = decimal !== undefined ? `${enteroFormateado},${decimal}` : enteroFormateado;
  return negativo ? `-${resultado}` : resultado;
}

export function parsearNumeroCO(valor: string | undefined): string {
  if (!valor) return '';
  return valor.replace(/\./g, '').replace(',', '.');
}
