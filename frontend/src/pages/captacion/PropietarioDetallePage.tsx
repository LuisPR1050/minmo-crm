import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Alert, Avatar, Button, Card, Col, Empty, Popconfirm, Row, Skeleton, Space, Statistic, Tag, Typography, message } from 'antd';
import {
  ArrowLeftOutlined,
  DeleteOutlined,
  FileTextOutlined,
  GiftOutlined,
  HomeOutlined,
  MailOutlined,
  PhoneOutlined,
  WhatsAppOutlined,
} from '@ant-design/icons';
import { inmuebleApi, propietarioApi } from '../../api/captacion';
import { negociacionApi } from '../../api/negociacion';
import { colorDesdeTexto, obtenerIniciales } from '../../utils/avatar';
import { ETIQUETA_ESTADO_CAPTACION } from '../../types/captacion';
import type { Inmueble, Propietario } from '../../types/captacion';
import type { Negociacion } from '../../types/negociacion';

const { Title, Text } = Typography;

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function PropietarioDetallePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [propietario, setPropietario] = useState<Propietario | null>(null);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [negociaciones, setNegociaciones] = useState<Negociacion[]>([]);
  const [loading, setLoading] = useState(false);
  const [eliminando, setEliminando] = useState(false);

  const cargar = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [data, inmueblesData, negociacionesData] = await Promise.all([
        propietarioApi.buscarPorId(id),
        inmuebleApi.listar(),
        negociacionApi.listar(),
      ]);
      setPropietario(data);
      setInmuebles(inmueblesData.filter((i) => i.propietarioId === id));
      setNegociaciones(negociacionesData.filter((n) => n.propietarioId === id));
    } catch {
      message.error('No se pudo cargar el propietario');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleEliminar = async () => {
    if (!id) return;
    setEliminando(true);
    try {
      await propietarioApi.eliminar(id);
      message.success('Propietario eliminado correctamente');
      navigate('/captacion/propietarios');
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar el propietario');
    } finally {
      setEliminando(false);
    }
  };

  if (loading && !propietario) {
    return <Skeleton active />;
  }

  if (!propietario || !id) {
    return (
      <div>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/captacion/propietarios')} style={{ marginBottom: 16 }}>
          Volver
        </Button>
        <Alert type="warning" title="Propietario no encontrado" showIcon />
      </div>
    );
  }

  const tieneInmuebles = inmuebles.length > 0;

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/captacion/propietarios')}>
          Volver a propietarios
        </Button>
        <Popconfirm
          title="Eliminar propietario"
          description={
            tieneInmuebles
              ? 'Este propietario tiene inmuebles asociados y no se puede eliminar.'
              : '¿Seguro que quieres eliminar este propietario? Esta acción no se puede deshacer.'
          }
          okText="Eliminar"
          cancelText="Cancelar"
          okButtonProps={{ danger: true, disabled: tieneInmuebles }}
          onConfirm={handleEliminar}
        >
          <Button danger icon={<DeleteOutlined />} loading={eliminando}>
            Eliminar propietario
          </Button>
        </Popconfirm>
      </Space>

      <div
        style={{
          background: 'linear-gradient(135deg, #0b4f6c 0%, #083950 100%)',
          borderRadius: 12,
          padding: '20px 24px',
          color: 'white',
          marginBottom: 20,
          display: 'flex',
          alignItems: 'center',
          gap: 16,
          flexWrap: 'wrap',
        }}
      >
        <Avatar size={56} style={{ backgroundColor: colorDesdeTexto(propietario.nombre) }}>
          {obtenerIniciales(propietario.nombre)}
        </Avatar>
        <div>
          <Title level={3} style={{ color: 'white', margin: 0 }}>
            {propietario.nombre}
          </Title>
          <div style={{ marginTop: 8, opacity: 0.85 }}>
            <Space size={16} wrap>
              <a href={`tel:${propietario.telefonoPrincipal}`} style={{ color: 'white', textDecoration: 'underline' }}>
                <PhoneOutlined /> {propietario.telefonoPrincipal}
              </a>
              {propietario.whatsapp && (
                <a
                  href={`https://wa.me/${propietario.whatsapp.replace(/\D/g, '')}`}
                  target="_blank"
                  rel="noreferrer"
                  style={{ color: 'white', textDecoration: 'underline' }}
                >
                  <WhatsAppOutlined /> {propietario.whatsapp}
                </a>
              )}
              {propietario.correoElectronico && (
                <a href={`mailto:${propietario.correoElectronico}`} style={{ color: 'white', textDecoration: 'underline' }}>
                  <MailOutlined /> {propietario.correoElectronico}
                </a>
              )}
              <Tag>{propietario.tipoContacto}</Tag>
            </Space>
          </div>
        </div>
      </div>

      <Row gutter={16} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={8}>
          <Card size="small">
            <Statistic title="Inmuebles" value={inmuebles.length} prefix={<HomeOutlined />} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card size="small">
            <Statistic title="Negociaciones" value={negociaciones.length} prefix={<FileTextOutlined />} styles={{ content: { color: '#fa8c16' } }} />
          </Card>
        </Col>
        <Col xs={24} sm={8}>
          <Card size="small">
            <Statistic
              title="Incentivo"
              value={propietario.valorIncentivo != null ? formatearMoneda(propietario.valorIncentivo) : 'Sin pactar'}
              prefix={<GiftOutlined />}
              styles={{ content: { color: '#7cb305', fontSize: 18 } }}
            />
          </Card>
        </Col>
      </Row>

      <Row gutter={16}>
        <Col xs={24} lg={12}>
          <Card title="Inmuebles asociados" size="small" style={{ marginBottom: 16 }}>
            {inmuebles.length === 0 ? (
              <Empty description="Sin inmuebles registrados" />
            ) : (
              <Space orientation="vertical" style={{ width: '100%' }} size={8}>
                {inmuebles.map((i) => (
                  <div key={i.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Link to={`/captacion/inmuebles/${i.id}`}>{i.codigo}</Link>
                    <Tag>{ETIQUETA_ESTADO_CAPTACION[i.estadoCaptacion]}</Tag>
                  </div>
                ))}
              </Space>
            )}
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card title="Negociaciones asociadas" size="small" style={{ marginBottom: 16 }}>
            {negociaciones.length === 0 ? (
              <Empty description="Sin negociaciones registradas" />
            ) : (
              <Space orientation="vertical" style={{ width: '100%' }} size={8}>
                {negociaciones.map((n) => (
                  <div key={n.id} style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                    <Link to={`/negociacion/${n.id}`}>
                      {n.numeroNegocio} — {n.inmuebleCodigo}
                    </Link>
                    <Text type="secondary">{n.clienteNombre}</Text>
                  </div>
                ))}
              </Space>
            )}
          </Card>
        </Col>
      </Row>
    </div>
  );
}
