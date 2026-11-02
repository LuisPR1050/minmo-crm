import { isAxiosError } from 'axios';
import { Modal, message } from 'antd';
import { ExclamationCircleFilled } from '@ant-design/icons';

interface RespuestaConfirmacionRequerida {
  requiereConfirmacion?: boolean;
  resumen?: string[];
  mensaje?: string;
}

interface OpcionesEliminarConConfirmacion {
  eliminar: (confirmar: boolean) => Promise<void>;
  onExito: () => void;
  mensajeExito?: string;
  mensajeErrorGenerico?: string;
}

function extraerDatos(error: unknown): RespuestaConfirmacionRequerida | undefined {
  if (isAxiosError(error)) {
    return error.response?.data as RespuestaConfirmacionRequerida | undefined;
  }
  return undefined;
}

/**
 * Intenta eliminar un recurso. Si el backend responde que hace falta confirmar porque se
 * perderia informacion relacionada (409 con requiereConfirmacion: true), muestra un modal con
 * el detalle de lo que se perderia y, si el usuario confirma, reintenta con confirmar=true.
 * Si el backend bloquea la eliminacion por una razon de negocio (ej. negociacion activa),
 * simplemente muestra ese mensaje de error, sin ofrecer la opcion de forzar.
 */
export async function eliminarConConfirmacion({
  eliminar,
  onExito,
  mensajeExito = 'Eliminado correctamente',
  mensajeErrorGenerico = 'No se pudo eliminar',
}: OpcionesEliminarConConfirmacion): Promise<void> {
  try {
    await eliminar(false);
    message.success(mensajeExito);
    onExito();
  } catch (error) {
    const datos = extraerDatos(error);

    if (datos?.requiereConfirmacion && datos.resumen) {
      Modal.confirm({
        title: 'Esta acción también eliminará información relacionada',
        icon: <ExclamationCircleFilled style={{ color: '#faad14' }} />,
        content: (
          <div>
            <ul style={{ paddingLeft: 20, margin: '8px 0' }}>
              {datos.resumen.map((linea) => (
                <li key={linea}>{linea}</li>
              ))}
            </ul>
            <p style={{ marginTop: 8 }}>Esta acción no se puede deshacer.</p>
          </div>
        ),
        okText: 'Eliminar de todas formas',
        okButtonProps: { danger: true },
        cancelText: 'Cancelar',
        onOk: async () => {
          try {
            await eliminar(true);
            message.success(mensajeExito);
            onExito();
          } catch (segundoError) {
            const detalle = extraerDatos(segundoError)?.mensaje;
            message.error(detalle ?? mensajeErrorGenerico);
          }
        },
      });
      return;
    }

    message.error(datos?.mensaje ?? mensajeErrorGenerico);
  }
}
