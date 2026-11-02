import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Alert, Button, Popconfirm, Select, Skeleton, Space, Tabs, Tag, Tooltip, Typography, message } from 'antd';
import { ArrowLeftOutlined, DeleteOutlined, FileTextOutlined, MailOutlined, PhoneOutlined, UserOutlined, WhatsAppOutlined } from '@ant-design/icons';
import { clienteApi } from '../../api/atencioncliente';
import { negociacionApi } from '../../api/negociacion';
import { eliminarConConfirmacion } from '../../utils/eliminarConConfirmacion';
import { ESTADOS_PIPELINE_CLIENTE, ETIQUETA_ESTADO_PIPELINE_CLIENTE } from '../../types/atencioncliente';
import type { Cliente, EstadoPipelineCliente } from '../../types/atencioncliente';
import type { Negociacion } from '../../types/negociacion';
import RequerimientosTab from './tabs/RequerimientosTab';
import VisitasTab from './tabs/VisitasTab';
import ResultadosTab from './tabs/ResultadosTab';

const { Title, Text } = Typography;

const COLOR_ESTADO: Record<EstadoPipelineCliente, string> = {
  CONTACTO_INICIADO: 'default',
  CONTACTO_CALIFICADO: 'blue',
  OPCIONES_ENVIADAS: 'cyan',
  CITA_AGENDADA: 'gold',
  VISITA_REALIZADA: 'green',
  VISITAR_OTRAS_PROPIEDADES: 'lime',
  EN_NEGOCIACION: 'orange',
  ESPERANDO_FIRMA_ESCRITURA: 'purple',
  EN_REGISTRO: 'geekblue',
  NEGOCIO_CERRADO_GANADO: 'success',
  NEGOCIO_PERDIDO: 'red',
};

export default function ClienteDetallePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const [cliente, setCliente] = useState<Cliente | null>(null);
  const [negociacionRelacionada, setNegociacionRelacionada] = useState<Negociacion | null>(null);
  const [loading, setLoading] = useState(false);
  const [cambiandoEstado, setCambiandoEstado] = useState(false);
  const [eliminando, setEliminando] = useState(false);

  const cargar = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [data, negociaciones] = await Promise.all([clienteApi.buscarPorId(id), negociacionApi.listar()]);
      setCliente(data);
      setNegociacionRelacionada(negociaciones.find((n) => n.clienteId === id) ?? null);
    } catch {
      message.error('No se pudo cargar el cliente');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleCambiarEstado = async (nuevoEstado: EstadoPipelineCliente) => {
    if (!id) return;
    setCambiandoEstado(true);
    try {
      const actualizado = await clienteApi.cambiarEstadoPipeline(id, nuevoEstado);
      setCliente(actualizado);
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
      await eliminarConConfirmacion({
        eliminar: (confirmar) => clienteApi.eliminar(id, confirmar),
        onExito: () => navigate('/atencion-cliente'),
        mensajeExito: 'Cliente eliminado correctamente',
        mensajeErrorGenerico: 'No se pudo eliminar el cliente',
      });
    } finally {
      setEliminando(false);
    }
  };

  if (loading && !cliente) {
    return <Skeleton active />;
  }

  if (!cliente || !id) {
    return (
      <div>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/atencion-cliente')} style={{ marginBottom: 16 }}>
          Volver
        </Button>
        <Alert type="warning" title="Cliente no encontrado" showIcon />
      </div>
    );
  }

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/atencion-cliente')}>
          Volver al pipeline
        </Button>
        <Tooltip title={negociacionRelacionada ? 'No se puede eliminar un cliente con negociaciones asociadas' : ''}>
          <Popconfirm
            title="Eliminar cliente"
            description="¿Seguro que quieres eliminar este cliente? Esta acción no se puede deshacer."
            okText="Eliminar"
            cancelText="Cancelar"
            okButtonProps={{ danger: true }}
            disabled={!!negociacionRelacionada}
            onConfirm={handleEliminar}
          >
            <Button danger icon={<DeleteOutlined />} disabled={!!negociacionRelacionada} loading={eliminando}>
              Eliminar cliente
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
            <UserOutlined style={{ fontSize: 20 }} />
            <Title level={3} style={{ color: 'white', margin: 0 }}>
              {cliente.nombre}
            </Title>
            <Tag color={COLOR_ESTADO[cliente.estadoPipeline]}>{ETIQUETA_ESTADO_PIPELINE_CLIENTE[cliente.estadoPipeline]}</Tag>
          </Space>
          <div style={{ marginTop: 8, opacity: 0.85 }}>
            <Space size={16} wrap>
              <a href={`tel:${cliente.telefono}`} style={{ color: 'white', textDecoration: 'underline' }}>
                <PhoneOutlined /> {cliente.telefono}
              </a>
              {cliente.whatsapp && (
                <a
                  href={`https://wa.me/${cliente.whatsapp.replace(/\D/g, '')}`}
                  target="_blank"
                  rel="noreferrer"
                  style={{ color: 'white', textDecoration: 'underline' }}
                >
                  <WhatsAppOutlined /> {cliente.whatsapp}
                </a>
              )}
              {cliente.correoElectronico && (
                <a href={`mailto:${cliente.correoElectronico}`} style={{ color: 'white', textDecoration: 'underline' }}>
                  <MailOutlined /> {cliente.correoElectronico}
                </a>
              )}
              <span>{cliente.tipoContacto}</span>
            </Space>
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <Text style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>Estado en el pipeline</Text>
          <div>
            <Select<EstadoPipelineCliente>
              value={cliente.estadoPipeline}
              onChange={handleCambiarEstado}
              loading={cambiandoEstado}
              style={{ width: 260, marginTop: 8 }}
              options={ESTADOS_PIPELINE_CLIENTE.map((e) => ({ value: e, label: ETIQUETA_ESTADO_PIPELINE_CLIENTE[e] }))}
            />
          </div>
        </div>
      </div>

      {negociacionRelacionada && (
        <Alert
          type="info"
          showIcon
          icon={<FileTextOutlined />}
          style={{ marginBottom: 16 }}
          title="Este cliente tiene una negociación asociada"
          description={
            <Link to={`/negociacion/${negociacionRelacionada.id}`}>
              Ver negociación {negociacionRelacionada.numeroNegocio} — {negociacionRelacionada.inmuebleCodigo}
            </Link>
          }
        />
      )}

      <Tabs
        defaultActiveKey="requerimientos"
        items={[
          { key: 'requerimientos', label: 'Requerimientos', children: <RequerimientosTab clienteId={id} /> },
          { key: 'visitas', label: 'Visitas', children: <VisitasTab clienteId={id} /> },
          { key: 'resultados', label: 'Resultados de visitas', children: <ResultadosTab clienteId={id} /> },
        ]}
      />
    </div>
  );
}
