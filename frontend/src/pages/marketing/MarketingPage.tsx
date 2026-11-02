import { useEffect, useMemo, useState } from 'react';
import { Card, Col, Row, Statistic, Tabs, Typography } from 'antd';
import { NotificationOutlined, StarOutlined, VideoCameraOutlined } from '@ant-design/icons';
import { publicacionInmuebleApi, solicitudVideoApi } from '../../api/marketing';
import type { PublicacionInmueble, SolicitudVideoMarketing } from '../../types/marketing';
import PublicacionesTab from './PublicacionesTab';
import SolicitudesVideoTab from './SolicitudesVideoTab';
import LandingPromocionTab from './LandingPromocionTab';

const { Title, Text } = Typography;

export default function MarketingPage() {
  const [publicaciones, setPublicaciones] = useState<PublicacionInmueble[]>([]);
  const [solicitudesVideo, setSolicitudesVideo] = useState<SolicitudVideoMarketing[]>([]);

  useEffect(() => {
    const cargar = async () => {
      try {
        const [publicacionesData, solicitudesData] = await Promise.all([
          publicacionInmuebleApi.listar(),
          solicitudVideoApi.listar(),
        ]);
        setPublicaciones(publicacionesData);
        setSolicitudesVideo(solicitudesData);
      } catch {
        // Las pestañas internas ya notifican el error de carga.
      }
    };
    cargar();
  }, []);

  const estadisticas = useMemo(
    () => ({
      activas: publicaciones.filter((p) => p.estado === 'ACTIVA').length,
      destacadas: publicaciones.filter((p) => p.destacada).length,
      videosEnProceso: solicitudesVideo.filter((s) => s.estado === 'EN_PROCESO').length,
      videosGenerados: solicitudesVideo.filter((s) => s.estado === 'GENERADO' || s.estado === 'PUBLICADO').length,
    }),
    [publicaciones, solicitudesVideo],
  );

  return (
    <div>
      <Title level={3} style={{ margin: 0 }}>
        Marketing
      </Title>
      <Text type="secondary">{publicaciones.length} publicaciones · {solicitudesVideo.length} solicitudes de video</Text>

      <Row gutter={[16, 16]} style={{ marginTop: 16, marginBottom: 16 }}>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Publicaciones activas" value={estadisticas.activas} prefix={<NotificationOutlined />} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Destacadas" value={estadisticas.destacadas} prefix={<StarOutlined />} styles={{ content: { color: '#faad14' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Videos en proceso" value={estadisticas.videosEnProceso} prefix={<VideoCameraOutlined />} styles={{ content: { color: '#fa8c16' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Videos generados" value={estadisticas.videosGenerados} prefix={<VideoCameraOutlined />} styles={{ content: { color: '#389e0d' } }} />
          </Card>
        </Col>
      </Row>

      <Tabs
        defaultActiveKey="publicaciones"
        items={[
          { key: 'publicaciones', label: 'Publicaciones', children: <PublicacionesTab /> },
          { key: 'videos', label: 'Solicitudes de video', children: <SolicitudesVideoTab /> },
          { key: 'landing', label: 'Landing / Promoción', children: <LandingPromocionTab /> },
        ]}
      />
    </div>
  );
}
