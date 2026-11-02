import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Alert, Button, Popconfirm, Select, Skeleton, Space, Tabs, Tag, Tooltip, Typography, message } from 'antd';
import { ArrowLeftOutlined, DeleteOutlined, DollarOutlined, FileTextOutlined, HomeOutlined, UserOutlined } from '@ant-design/icons';
import { negociacionApi } from '../../api/negociacion';
import { ESTADOS_NEGOCIACION, ETIQUETA_ESTADO_NEGOCIACION } from '../../types/negociacion';
import type { EstadoNegociacion, Negociacion } from '../../types/negociacion';
import InformacionGeneralTab from './tabs/InformacionGeneralTab';
import PartesTab from './tabs/PartesTab';
import BienesTab from './tabs/BienesTab';
import PlanDePagoTab from './tabs/PlanDePagoTab';
import EntregaTab from './tabs/EntregaTab';
import GastosTab from './tabs/GastosTab';
import CondicionesEspecialesTab from './tabs/CondicionesEspecialesTab';
import DocumentosTab from './tabs/DocumentosTab';
import HistorialTab from './tabs/HistorialTab';

const { Title, Text } = Typography;

const COLOR_ESTADO: Record<EstadoNegociacion, string> = {
  NEGOCIACION_INICIADA: 'default',
  OFERTA_PRESENTADA: 'blue',
  CONTRAOFERTA: 'blue',
  ACUERDO_VERBAL: 'geekblue',
  ESPERANDO_FINANCIACION: 'orange',
  ESPERANDO_FIRMA_PROMESA: 'orange',
  ESPERANDO_ESCRITURA: 'orange',
  NEGOCIO_CERRADO: 'green',
  NEGOCIO_PERDIDO: 'red',
  NEGOCIO_EN_RIESGO: 'volcano',
};

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function NegociacionDetallePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [negociacion, setNegociacion] = useState<Negociacion | null>(null);
  const [loading, setLoading] = useState(false);
  const [cambiandoEstado, setCambiandoEstado] = useState(false);
  const [eliminando, setEliminando] = useState(false);

  const cargar = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const data = await negociacionApi.buscarPorId(id);
      setNegociacion(data);
    } catch {
      message.error('No se pudo cargar la negociación');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleCambiarEstado = async (nuevoEstado: EstadoNegociacion) => {
    if (!id) return;
    setCambiandoEstado(true);
    try {
      const actualizada = await negociacionApi.cambiarEstado(id, nuevoEstado);
      setNegociacion(actualizada);
      message.success('Estado actualizado');
    } catch {
      message.error('No se pudo cambiar el estado');
    } finally {
      setCambiandoEstado(false);
    }
  };

  const handleEliminar = async () => {
    if (!id) return;
    setEliminando(true);
    try {
      await negociacionApi.eliminar(id);
      message.success('Negociación eliminada correctamente');
      navigate('/negociacion');
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar la negociación');
    } finally {
      setEliminando(false);
    }
  };

  if (loading && !negociacion) {
    return <Skeleton active />;
  }

  if (!negociacion || !id) {
    return (
      <div>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/negociacion')} style={{ marginBottom: 16 }}>
          Volver
        </Button>
        <Alert type="warning" title="Negociación no encontrada" showIcon />
      </div>
    );
  }

  const negocioCerrado = negociacion.estado === 'NEGOCIO_CERRADO';

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/negociacion')}>
          Volver a negociaciones
        </Button>
        <Tooltip title={negocioCerrado ? 'No se puede eliminar un negocio ya cerrado' : ''}>
          <Popconfirm
            title="Eliminar negociación"
            description="¿Seguro que quieres eliminar esta negociación? Esta acción no se puede deshacer."
            okText="Eliminar"
            cancelText="Cancelar"
            okButtonProps={{ danger: true }}
            disabled={negocioCerrado}
            onConfirm={handleEliminar}
          >
            <Button danger icon={<DeleteOutlined />} disabled={negocioCerrado} loading={eliminando}>
              Eliminar negociación
            </Button>
          </Popconfirm>
        </Tooltip>
      </Space>

      <div
        style={{
          background: 'linear-gradient(135deg, #0b4f6c 0%, #083950 100%)',
          borderRadius: 12,
          padding: '20px 24px',
          color: 'white',
          marginBottom: 20,
          display: 'flex',
          justifyContent: 'space-between',
          alignItems: 'center',
          flexWrap: 'wrap',
          gap: 16,
        }}
      >
        <div>
          <Space align="center" size={10}>
            <FileTextOutlined style={{ fontSize: 20 }} />
            <Title level={3} style={{ color: 'white', margin: 0 }}>
              {negociacion.numeroNegocio}
            </Title>
            <Tag color={COLOR_ESTADO[negociacion.estado]}>{ETIQUETA_ESTADO_NEGOCIACION[negociacion.estado]}</Tag>
          </Space>
          <div style={{ marginTop: 8, opacity: 0.85 }}>
            <Space size={16} wrap>
              <span>
                <HomeOutlined />{' '}
                <Link to={`/captacion/inmuebles/${negociacion.inmuebleId}`} style={{ color: 'white', textDecoration: 'underline' }}>
                  {negociacion.inmuebleCodigo}
                </Link>
              </span>
              <span>
                <UserOutlined />{' '}
                <Link to={`/atencion-cliente/${negociacion.clienteId}`} style={{ color: 'white', textDecoration: 'underline' }}>
                  {negociacion.clienteNombre}
                </Link>{' '}
                (comprador)
              </span>
              <span>
                <UserOutlined /> {negociacion.propietarioNombre} (vendedor)
              </span>
            </Space>
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <Text style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>
            <DollarOutlined /> Precio final acordado
          </Text>
          <div style={{ fontSize: 24, fontWeight: 700 }}>
            {formatearMoneda(negociacion.precioFinalAcordado ?? negociacion.precioInicialPublicado)}
          </div>
          <Select<EstadoNegociacion>
            value={negociacion.estado}
            onChange={handleCambiarEstado}
            loading={cambiandoEstado}
            disabled={negocioCerrado}
            style={{ width: 240, marginTop: 8 }}
            options={ESTADOS_NEGOCIACION.filter((e) => e !== 'NEGOCIO_CERRADO').map((e) => ({
              value: e,
              label: ETIQUETA_ESTADO_NEGOCIACION[e],
            }))}
          />
        </div>
      </div>

      {negocioCerrado && (
        <Alert
          type="success"
          showIcon
          title="Negocio cerrado"
          description="El cierre se disparó automáticamente al cumplirse los tres hitos. El inmueble quedó marcado como VENDIDO."
          style={{ marginBottom: 16 }}
        />
      )}

      <Tabs
        defaultActiveKey="general"
        items={[
          { key: 'general', label: 'Información general', children: <InformacionGeneralTab negociacion={negociacion} onGuardado={cargar} /> },
          { key: 'partes', label: 'Partes', children: <PartesTab negociacionId={id} /> },
          { key: 'bienes', label: 'Bienes asociados', children: <BienesTab negociacionId={id} /> },
          { key: 'plan-pago', label: 'Plan de pago', children: <PlanDePagoTab negociacionId={id} /> },
          { key: 'entrega', label: 'Qué incluye', children: <EntregaTab negociacionId={id} /> },
          { key: 'gastos', label: 'Gastos', children: <GastosTab negociacionId={id} /> },
          { key: 'condiciones-especiales', label: 'Condiciones especiales', children: <CondicionesEspecialesTab negociacionId={id} /> },
          { key: 'documentos', label: 'Documentos', children: <DocumentosTab negociacionId={id} onCambioQuePuedaCerrarNegocio={cargar} /> },
          { key: 'historial', label: 'Historial', children: <HistorialTab negociacionId={id} /> },
        ]}
      />
    </div>
  );
}
