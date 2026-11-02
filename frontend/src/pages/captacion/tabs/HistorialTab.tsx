import { useEffect, useState } from 'react';
import { Empty, Skeleton, Timeline, Typography } from 'antd';
import { historialCaptacionApi } from '../../../api/captacion';
import type { HistorialCaptacion } from '../../../types/captacion';

const { Text } = Typography;

interface Props {
  inmuebleId: string;
}

function formatearFecha(fecha: string): string {
  return new Date(fecha).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
}

export default function HistorialTab({ inmuebleId }: Props) {
  const [historial, setHistorial] = useState<HistorialCaptacion[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    historialCaptacionApi
      .listarPorInmueble(inmuebleId)
      .then(setHistorial)
      .finally(() => setLoading(false));
  }, [inmuebleId]);

  if (loading) return <Skeleton active />;

  if (historial.length === 0) {
    return <Empty description="Sin eventos registrados todavía" />;
  }

  return (
    <div style={{ maxWidth: 640, paddingTop: 8 }}>
      <Timeline
        items={historial.map((evento) => ({
          content: (
            <div key={evento.id}>
              <Text>{evento.evento}</Text>
              <div>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {formatearFecha(evento.fecha)}
                </Text>
              </div>
            </div>
          ),
        }))}
      />
    </div>
  );
}
