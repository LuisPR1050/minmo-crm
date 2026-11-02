import { useEffect, useState } from 'react';
import { Empty, Skeleton, Timeline, Typography } from 'antd';
import { historialPublicacionApi } from '../../../api/marketing';
import type { HistorialPublicacion } from '../../../types/marketing';

const { Text } = Typography;

interface Props {
  publicacionInmuebleId: string;
}

export default function HistorialTab({ publicacionInmuebleId }: Props) {
  const [historial, setHistorial] = useState<HistorialPublicacion[]>([]);
  const [loading, setLoading] = useState(false);

  useEffect(() => {
    setLoading(true);
    historialPublicacionApi
      .listarPorPublicacion(publicacionInmuebleId)
      .then(setHistorial)
      .finally(() => setLoading(false));
  }, [publicacionInmuebleId]);

  if (loading) return <Skeleton active />;

  if (historial.length === 0) {
    return <Empty description="Sin eventos registrados todavía" />;
  }

  return (
    <div style={{ maxWidth: 560, paddingTop: 8 }}>
      <Timeline
        items={historial.map((evento) => ({
          content: (
            <div key={evento.id}>
              <Text>{evento.evento}</Text>
              <div>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {new Date(evento.fecha).toLocaleString('es-CO')}
                </Text>
              </div>
            </div>
          ),
        }))}
      />
    </div>
  );
}
