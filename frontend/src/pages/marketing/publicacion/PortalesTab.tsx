import { useEffect, useState } from 'react';
import { Button, Card, Checkbox, Col, DatePicker, Empty, Form, InputNumber, Modal, Row, Select, Skeleton, Space, Statistic, Tag, Typography, message } from 'antd';
import { BarChartOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import { estadisticaPortalApi, portalInmobiliarioApi, publicacionInmueblePortalApi } from '../../../api/marketing';
import { ESTADOS_PUBLICACION_PORTAL, ETIQUETA_ESTADO_PUBLICACION_PORTAL } from '../../../types/marketing';
import type {
  EstadisticaPortal,
  EstadisticaPortalRequest,
  EstadoPublicacionPortal,
  PortalInmobiliario,
  PublicacionInmueblePortal,
  ResumenEstadisticasPublicacion,
} from '../../../types/marketing';

const { Text } = Typography;

interface Props {
  publicacionInmuebleId: string;
}

export default function PortalesTab({ publicacionInmuebleId }: Props) {
  const [catalogo, setCatalogo] = useState<PortalInmobiliario[]>([]);
  const [asignados, setAsignados] = useState<PublicacionInmueblePortal[]>([]);
  const [resumen, setResumen] = useState<ResumenEstadisticasPublicacion | null>(null);
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [actualizandoId, setActualizandoId] = useState<string | null>(null);
  const [vinculoEstadistica, setVinculoEstadistica] = useState<PublicacionInmueblePortal | null>(null);
  const [historialEstadisticas, setHistorialEstadisticas] = useState<EstadisticaPortal[]>([]);
  const [guardandoEstadistica, setGuardandoEstadistica] = useState(false);
  const [formEstadistica] = Form.useForm<{ vistas?: number; contactos?: number; fechaCorte?: dayjs.Dayjs }>();

  const cargar = async () => {
    setLoading(true);
    try {
      const [todos, asignadosData, resumenData] = await Promise.all([
        portalInmobiliarioApi.listarTodos(),
        publicacionInmueblePortalApi.listarPorPublicacion(publicacionInmuebleId),
        estadisticaPortalApi.resumenPorPublicacion(publicacionInmuebleId),
      ]);
      setCatalogo(todos);
      setAsignados(asignadosData);
      setResumen(resumenData);
    } catch {
      message.error('No se pudieron cargar los portales');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [publicacionInmuebleId]);

  const idsAsignados = new Set(asignados.map((a) => a.portalId));

  const handleAsignar = async (portalId: string, checked: boolean) => {
    if (!checked) return; // el backend solo soporta asignar, no desasignar
    setGuardando(true);
    try {
      await publicacionInmueblePortalApi.asignar(publicacionInmuebleId, [portalId]);
      message.success('Portal asignado');
      cargar();
    } catch {
      message.error('No se pudo asignar el portal');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambiarEstado = async (vinculoId: string, nuevoEstado: EstadoPublicacionPortal) => {
    setActualizandoId(vinculoId);
    try {
      const actualizado = await publicacionInmueblePortalApi.cambiarEstado(vinculoId, nuevoEstado);
      setAsignados((prev) => prev.map((a) => (a.id === vinculoId ? actualizado : a)));
      message.success('Estado del portal actualizado');
    } catch {
      message.error('No se pudo cambiar el estado del portal');
    } finally {
      setActualizandoId(null);
    }
  };

  const abrirEstadisticas = async (vinculo: PublicacionInmueblePortal) => {
    setVinculoEstadistica(vinculo);
    try {
      setHistorialEstadisticas(await estadisticaPortalApi.listarPorVinculo(vinculo.id));
    } catch {
      setHistorialEstadisticas([]);
    }
  };

  const handleRegistrarEstadistica = async (values: { vistas?: number; contactos?: number; fechaCorte?: dayjs.Dayjs }) => {
    if (!vinculoEstadistica) return;
    const request: EstadisticaPortalRequest = {
      vistas: values.vistas,
      contactos: values.contactos,
      fechaCorte: values.fechaCorte?.format('YYYY-MM-DD'),
    };
    setGuardandoEstadistica(true);
    try {
      const creada = await estadisticaPortalApi.registrar(vinculoEstadistica.id, request);
      setHistorialEstadisticas((prev) => [creada, ...prev]);
      formEstadistica.resetFields();
      message.success('Estadística registrada');
      cargar();
    } catch {
      message.error('No se pudo registrar la estadística');
    } finally {
      setGuardandoEstadistica(false);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <Space size={32}>
        <Statistic title="Vistas totales" value={resumen?.totalVistas ?? 0} />
        <Statistic title="Contactos totales" value={resumen?.totalContactos ?? 0} />
      </Space>

      <Card title="Portales donde publicar" size="small" loading={guardando}>
        <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
          {catalogo.map((portal) => (
            <Checkbox
              key={portal.id}
              checked={idsAsignados.has(portal.id)}
              onChange={(e) => handleAsignar(portal.id, e.target.checked)}
            >
              {portal.nombre}
            </Checkbox>
          ))}
        </div>
      </Card>

      {asignados.length === 0 ? (
        <Empty description="Sin portales asignados todavía" />
      ) : (
        <Card title="Estado por portal" size="small">
          <Space orientation="vertical" style={{ width: '100%' }}>
            {asignados.map((a) => (
              <Space key={a.id} style={{ width: '100%', justifyContent: 'space-between' }}>
                <Space>
                  <Text strong>{a.portalNombre}</Text>
                  <Tag>{ETIQUETA_ESTADO_PUBLICACION_PORTAL[a.estado]}</Tag>
                </Space>
                <Space>
                  <Select<EstadoPublicacionPortal>
                    size="small"
                    value={a.estado}
                    loading={actualizandoId === a.id}
                    style={{ width: 150 }}
                    onChange={(v) => handleCambiarEstado(a.id, v)}
                    options={ESTADOS_PUBLICACION_PORTAL.map((e) => ({ value: e, label: ETIQUETA_ESTADO_PUBLICACION_PORTAL[e] }))}
                  />
                  <Button size="small" icon={<BarChartOutlined />} onClick={() => abrirEstadisticas(a)}>
                    Estadísticas
                  </Button>
                </Space>
              </Space>
            ))}
          </Space>
        </Card>
      )}

      <Modal
        title={`Estadísticas — ${vinculoEstadistica?.portalNombre ?? ''}`}
        open={!!vinculoEstadistica}
        onCancel={() => setVinculoEstadistica(null)}
        footer={null}
      >
        <Form form={formEstadistica} layout="vertical" onFinish={handleRegistrarEstadistica}>
          <Row gutter={8}>
            <Col xs={24} sm={8}>
              <Form.Item name="vistas" label="Vistas">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="contactos" label="Contactos">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="fechaCorte" label="Fecha de corte">
                <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
          </Row>
          <Button type="primary" htmlType="submit" loading={guardandoEstadistica}>
            Registrar
          </Button>
        </Form>

        {historialEstadisticas.length === 0 ? (
          <Empty description="Sin estadísticas registradas" style={{ marginTop: 16 }} />
        ) : (
          <Space orientation="vertical" style={{ width: '100%', marginTop: 16 }}>
            {historialEstadisticas.map((e) => (
              <Space key={e.id} style={{ width: '100%', justifyContent: 'space-between' }}>
                <Text type="secondary">{e.fechaCorte ?? 'Sin fecha'}</Text>
                <Text>
                  {e.vistas ?? 0} vistas · {e.contactos ?? 0} contactos
                </Text>
              </Space>
            ))}
          </Space>
        )}
      </Modal>
    </Space>
  );
}
