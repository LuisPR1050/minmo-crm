import { useEffect, useRef, useState } from 'react';
import { Badge, Button, Empty, Popover, Tag, Typography, message } from 'antd';
import { BellOutlined, CheckOutlined } from '@ant-design/icons';
import ItemList from './ItemList';
import { notificacionApi } from '../api/notificacion';
import { COLOR_TIPO_NOTIFICACION, ETIQUETA_TIPO_NOTIFICACION } from '../types/notificacion';
import type { Notificacion } from '../types/notificacion';

const { Text } = Typography;

const INTERVALO_ACTUALIZACION_MS = 60_000;

function formatearFecha(fecha: string): string {
  return new Date(fecha).toLocaleString('es-CO', { dateStyle: 'medium', timeStyle: 'short' });
}

export default function NotificacionesBell() {
  const [notificaciones, setNotificaciones] = useState<Notificacion[]>([]);
  const [loading, setLoading] = useState(false);
  const [marcandoId, setMarcandoId] = useState<string | null>(null);
  const [abierto, setAbierto] = useState(false);
  const primerCarga = useRef(true);

  const cargar = async () => {
    if (primerCarga.current) setLoading(true);
    try {
      const data = await notificacionApi.listarNoLeidas();
      setNotificaciones(data);
    } catch {
      // Silencioso: no interrumpir con un error visible por un fallo de polling
    } finally {
      setLoading(false);
      primerCarga.current = false;
    }
  };

  useEffect(() => {
    cargar();
    const intervalo = setInterval(cargar, INTERVALO_ACTUALIZACION_MS);
    return () => clearInterval(intervalo);
  }, []);

  const handleMarcarLeida = async (id: string) => {
    setMarcandoId(id);
    try {
      await notificacionApi.marcarComoLeida(id);
      setNotificaciones((prev) => prev.filter((n) => n.id !== id));
    } catch {
      message.error('No se pudo marcar la notificación como leída');
    } finally {
      setMarcandoId(null);
    }
  };

  const contenido = (
    <div style={{ width: 360, maxHeight: 420, overflowY: 'auto' }}>
      <ItemList<Notificacion>
        loading={loading}
        items={notificaciones}
        rowKey={(n) => n.id}
        emptyDescription="Sin notificaciones pendientes"
        emptyImage={Empty.PRESENTED_IMAGE_SIMPLE}
        renderItem={(n) => (
          <div style={{ display: 'flex', justifyContent: 'space-between', gap: 8 }}>
            <div>
              <Tag color={COLOR_TIPO_NOTIFICACION[n.tipo]}>{ETIQUETA_TIPO_NOTIFICACION[n.tipo]}</Tag>
              <div style={{ marginTop: 4 }}>{n.mensaje}</div>
              <Text type="secondary" style={{ fontSize: 12 }}>
                {formatearFecha(n.createdAt)}
              </Text>
            </div>
            <Button
              type="text"
              size="small"
              icon={<CheckOutlined />}
              loading={marcandoId === n.id}
              onClick={() => handleMarcarLeida(n.id)}
              title="Marcar como leída"
            />
          </div>
        )}
      />
    </div>
  );

  return (
    <Popover
      content={contenido}
      title="Notificaciones"
      trigger="click"
      placement="bottomRight"
      open={abierto}
      onOpenChange={setAbierto}
    >
      <Badge count={notificaciones.length} size="small" offset={[-2, 2]} color="#ff7a00">
        <Button type="text" icon={<BellOutlined style={{ fontSize: 18 }} />} shape="circle" />
      </Badge>
    </Popover>
  );
}
