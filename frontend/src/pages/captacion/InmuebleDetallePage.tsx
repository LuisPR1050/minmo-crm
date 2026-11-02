import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { Alert, Button, Card, List, Modal, Popconfirm, Select, Skeleton, Space, Tabs, Tag, Tooltip, Typography, message } from 'antd';
import {
  ArrowLeftOutlined,
  BulbOutlined,
  DeleteOutlined,
  EnvironmentOutlined,
  FileTextOutlined,
  HistoryOutlined,
  HomeOutlined,
  IdcardOutlined,
  TeamOutlined,
  UserOutlined,
  WhatsAppOutlined,
} from '@ant-design/icons';
import { condicionCaptacionApi, inmuebleApi } from '../../api/captacion';
import { construirEnlaceWhatsapp } from '../../utils/compartir';
import { eliminarConConfirmacion } from '../../utils/eliminarConConfirmacion';
import { negociacionApi } from '../../api/negociacion';
import { ESTADOS_CAPTACION, ETIQUETA_ESTADO_CAPTACION, ETIQUETA_TIPO_PROPIEDAD } from '../../types/captacion';
import type { AnalisisObjecionesInmueble, ClienteSugerido, CondicionCaptacion, EstadoCaptacion, Inmueble } from '../../types/captacion';
import type { Negociacion } from '../../types/negociacion';
import { useAuth } from '../../context/AuthContext';
import InformacionGeneralTab from './tabs/InformacionGeneralTab';
import CaracteristicasTab from './tabs/CaracteristicasTab';
import CondicionesTab from './tabs/CondicionesTab';
import JuridicoTab from './tabs/JuridicoTab';
import HistorialTab from './tabs/HistorialTab';
import FotosTab from './tabs/FotosTab';
import LandingTab from './tabs/LandingTab';
import ServiciosEquipamientoTab from './tabs/ServiciosEquipamientoTab';
import DocumentosTab from './tabs/DocumentosTab';

const { Title, Text } = Typography;

const COLOR_ESTADO: Record<EstadoCaptacion, string> = {
  PROSPECTO: 'default',
  CONTACTADO: 'blue',
  VISITADO: 'cyan',
  CAPTADO_CON_MATERIAL: 'green',
  CAPTADO_SIN_MATERIAL: 'lime',
  EN_NEGOCIACION: 'orange',
  VENDIDO: 'purple',
  NEGOCIO_PERDIDO: 'red',
  DESCARTADO: 'default',
};

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function InmuebleDetallePage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const { usuario: usuarioActual } = useAuth();
  const [inmueble, setInmueble] = useState<Inmueble | null>(null);
  const [condicion, setCondicion] = useState<CondicionCaptacion | null>(null);
  const [negociacionRelacionada, setNegociacionRelacionada] = useState<Negociacion | null>(null);
  const [loading, setLoading] = useState(false);
  const [cambiandoEstado, setCambiandoEstado] = useState(false);
  const [eliminando, setEliminando] = useState(false);
  const [modalHistorialAbierto, setModalHistorialAbierto] = useState(false);
  const [modalFichaAbierta, setModalFichaAbierta] = useState(false);
  const [analizandoObjeciones, setAnalizandoObjeciones] = useState(false);
  const [analisisObjeciones, setAnalisisObjeciones] = useState<AnalisisObjecionesInmueble | null>(null);
  const [sugiriendoClientes, setSugiriendoClientes] = useState(false);
  const [clientesSugeridos, setClientesSugeridos] = useState<ClienteSugerido[] | null>(null);

  const cargar = async () => {
    if (!id) return;
    setLoading(true);
    try {
      const [data, negociaciones, condicionData] = await Promise.all([
        inmuebleApi.buscarPorId(id),
        negociacionApi.listar(),
        condicionCaptacionApi.buscarPorInmueble(id),
      ]);
      setInmueble(data);
      setNegociacionRelacionada(negociaciones.find((n) => n.inmuebleId === id) ?? null);
      setCondicion(condicionData);
    } catch {
      message.error('No se pudo cargar el inmueble');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [id]);

  const handleCambiarEstado = async (nuevoEstado: EstadoCaptacion) => {
    if (!id) return;
    setCambiandoEstado(true);
    try {
      const actualizado = await inmuebleApi.cambiarEstadoCaptacion(id, nuevoEstado);
      setInmueble(actualizado);
      message.success('Estado actualizado');
    } catch {
      message.error('No se pudo cambiar el estado');
    } finally {
      setCambiandoEstado(false);
    }
  };

  const handleEnviarFicha = () => {
    if (!inmueble) return;
    const enlace = `${window.location.origin}/p/${inmueble.id}`;
    const partes = [
      `${inmueble.codigo} — ${ETIQUETA_TIPO_PROPIEDAD[inmueble.tipoPropiedad as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ?? inmueble.tipoPropiedad}`,
      inmueble.ganchoEmocional || inmueble.descripcionPublica || undefined,
      inmueble.landingMostrarPrecio && inmueble.expectativaPrecio != null
        ? `Precio: ${inmueble.expectativaPrecio.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 })}`
        : undefined,
      enlace,
    ].filter(Boolean);
    window.open(construirEnlaceWhatsapp('', partes.join('\n\n')), '_blank', 'noreferrer');
  };

  const handleEliminar = async () => {
    if (!id) return;
    setEliminando(true);
    try {
      await eliminarConConfirmacion({
        eliminar: (confirmar) => inmuebleApi.eliminar(id, confirmar),
        onExito: () => navigate('/captacion/inmuebles'),
        mensajeExito: 'Inmueble eliminado correctamente',
        mensajeErrorGenerico: 'No se pudo eliminar el inmueble',
      });
    } finally {
      setEliminando(false);
    }
  };

  const handleAnalizarObjeciones = async () => {
    if (!id) return;
    setAnalizandoObjeciones(true);
    try {
      const data = await inmuebleApi.analizarObjecionesConIA(id);
      setAnalisisObjeciones(data);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo generar el análisis de objeciones');
    } finally {
      setAnalizandoObjeciones(false);
    }
  };

  const handleSugerirClientes = async () => {
    if (!id) return;
    setSugiriendoClientes(true);
    try {
      const data = await inmuebleApi.sugerirClientesConIA(id);
      setClientesSugeridos(data);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudieron sugerir clientes');
    } finally {
      setSugiriendoClientes(false);
    }
  };

  const pestanasRestringidas = usuarioActual?.rol !== 'ADMIN' && condicion?.origenPropiedad !== 'ALIADO';

  const etiquetaPestanaRestringida = (texto: string) =>
    pestanasRestringidas ? (
      <Tooltip title='Esta sección solo está disponible cuando la propiedad llega por un aliado/referenciador. Márcalo en "Condiciones de captación".'>
        {texto}
      </Tooltip>
    ) : (
      texto
    );

  if (loading && !inmueble) {
    return <Skeleton active />;
  }

  if (!inmueble || !id) {
    return (
      <div>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/captacion/inmuebles')} style={{ marginBottom: 16 }}>
          Volver
        </Button>
        <Alert type="warning" title="Inmueble no encontrado" showIcon />
      </div>
    );
  }

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/captacion/inmuebles')}>
          Volver al pipeline
        </Button>
        <Space wrap>
          <Button icon={<HistoryOutlined />} onClick={() => setModalHistorialAbierto(true)}>
            Ver historial
          </Button>
          <Button icon={<IdcardOutlined />} onClick={() => setModalFichaAbierta(true)}>
            Ficha técnica
          </Button>
          <Button icon={<BulbOutlined />} loading={analizandoObjeciones} onClick={handleAnalizarObjeciones}>
            Analizar objeciones con IA
          </Button>
          <Button icon={<TeamOutlined />} loading={sugiriendoClientes} onClick={handleSugerirClientes}>
            Sugerir clientes con IA
          </Button>
          <Popconfirm
            title="Eliminar inmueble"
            description="¿Seguro que quieres eliminar este inmueble? Esta acción no se puede deshacer."
            okText="Eliminar"
            cancelText="Cancelar"
            okButtonProps={{ danger: true }}
            onConfirm={handleEliminar}
          >
            <Button danger icon={<DeleteOutlined />} loading={eliminando}>
              Eliminar inmueble
            </Button>
          </Popconfirm>
        </Space>
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
            <HomeOutlined style={{ fontSize: 20 }} />
            <Title level={3} style={{ color: 'white', margin: 0 }}>
              {inmueble.codigo}
            </Title>
            <Tag color={COLOR_ESTADO[inmueble.estadoCaptacion]}>{ETIQUETA_ESTADO_CAPTACION[inmueble.estadoCaptacion]}</Tag>
          </Space>
          <div style={{ marginTop: 8, opacity: 0.85 }}>
            <Space size={16} wrap>
              <span>
                <EnvironmentOutlined /> {inmueble.direccionExacta ?? inmueble.zona ?? inmueble.ciudad ?? 'Sin dirección'}
              </span>
              <span>
                <UserOutlined /> {inmueble.propietarioNombre}
              </span>
              <span>
                {ETIQUETA_TIPO_PROPIEDAD[inmueble.tipoPropiedad as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ??
                  inmueble.tipoPropiedad}
              </span>
            </Space>
          </div>
        </div>
        <div style={{ textAlign: 'right' }}>
          <Text style={{ color: 'rgba(255,255,255,0.7)', fontSize: 12 }}>Expectativa de precio</Text>
          <div style={{ fontSize: 24, fontWeight: 700 }}>{formatearMoneda(inmueble.expectativaPrecio)}</div>
          <Select<EstadoCaptacion>
            value={inmueble.estadoCaptacion}
            onChange={handleCambiarEstado}
            loading={cambiandoEstado}
            style={{ width: 220, marginTop: 8 }}
            options={ESTADOS_CAPTACION.map((e) => ({ value: e, label: ETIQUETA_ESTADO_CAPTACION[e] }))}
          />
        </div>
      </div>

      {negociacionRelacionada && (
        <Alert
          type="info"
          showIcon
          icon={<FileTextOutlined />}
          style={{ marginBottom: 16 }}
          title="Este inmueble tiene una negociación asociada"
          description={
            <Link to={`/negociacion/${negociacionRelacionada.id}`}>
              Ver negociación {negociacionRelacionada.numeroNegocio} con {negociacionRelacionada.clienteNombre}
            </Link>
          }
        />
      )}

      <Tabs
        defaultActiveKey="condiciones"
        items={[
          {
            key: 'condiciones',
            label: 'Condiciones de captación',
            children: <CondicionesTab inmuebleId={id} propietarioIdActual={inmueble.propietarioId} onGuardado={cargar} />,
          },
          { key: 'general', label: 'Información general', children: <InformacionGeneralTab inmueble={inmueble} onGuardado={cargar} /> },
          {
            key: 'caracteristicas',
            label: etiquetaPestanaRestringida('Distribución'),
            disabled: pestanasRestringidas,
            children: <CaracteristicasTab inmueble={inmueble} onGuardado={cargar} />,
          },
          {
            key: 'servicios-equipamiento',
            label: etiquetaPestanaRestringida('Servicios y equipamiento'),
            disabled: pestanasRestringidas,
            children: <ServiciosEquipamientoTab inmuebleId={id} />,
          },
          {
            key: 'juridico',
            label: etiquetaPestanaRestringida('Estado jurídico'),
            disabled: pestanasRestringidas,
            children: <JuridicoTab inmueble={inmueble} onGuardado={cargar} />,
          },
          { key: 'fotos', label: 'Material gráfico', children: <FotosTab inmuebleId={id} /> },
          { key: 'documentos', label: 'Documentos', children: <DocumentosTab inmuebleId={id} /> },
        ]}
      />

      <Modal
        title="Análisis de objeciones de este inmueble (IA)"
        open={analisisObjeciones !== null}
        onCancel={() => setAnalisisObjeciones(null)}
        footer={<Button onClick={() => setAnalisisObjeciones(null)}>Cerrar</Button>}
        width={560}
      >
        {analisisObjeciones && (
          <Space orientation="vertical" size={16} style={{ width: '100%' }}>
            <Text type="secondary">
              Basado en {analisisObjeciones.visitasAnalizadas} de {analisisObjeciones.totalVisitas} visita(s) con
              objeciones u observaciones registradas.
            </Text>
            <div>
              <Text strong>Resumen</Text>
              <div>{analisisObjeciones.resumen}</div>
            </div>
            <div>
              <Text strong>Recomendaciones</Text>
              <List
                size="small"
                dataSource={analisisObjeciones.recomendaciones}
                renderItem={(item) => <List.Item>{item}</List.Item>}
              />
            </div>
          </Space>
        )}
      </Modal>

      <Modal
        title="Clientes sugeridos por IA para este inmueble"
        open={clientesSugeridos !== null}
        onCancel={() => setClientesSugeridos(null)}
        footer={<Button onClick={() => setClientesSugeridos(null)}>Cerrar</Button>}
        width={560}
      >
        {clientesSugeridos && clientesSugeridos.length === 0 ? (
          <Alert type="info" showIcon title="La IA no encontró clientes pendientes que encajen razonablemente con esta propiedad" />
        ) : (
          <Space orientation="vertical" size={12} style={{ width: '100%' }}>
            {clientesSugeridos?.map((c) => (
              <Card key={c.requerimientoId} size="small">
                <Space orientation="vertical" size={4} style={{ width: '100%' }}>
                  <Link to={`/atencion-cliente/${c.clienteId}`}>
                    <Text strong>{c.clienteNombre}</Text>
                  </Link>
                  <Text>{c.motivo}</Text>
                </Space>
              </Card>
            ))}
          </Space>
        )}
      </Modal>

      <Modal
        title="Historial del inmueble"
        open={modalHistorialAbierto}
        onCancel={() => setModalHistorialAbierto(false)}
        footer={<Button onClick={() => setModalHistorialAbierto(false)}>Cerrar</Button>}
        width={560}
      >
        <HistorialTab inmuebleId={id} />
      </Modal>

      <Modal
        title="Ficha técnica"
        open={modalFichaAbierta}
        onCancel={() => setModalFichaAbierta(false)}
        footer={
          <Space wrap style={{ justifyContent: 'flex-end', width: '100%' }}>
            <Button
              icon={<HistoryOutlined />}
              onClick={() => {
                setModalFichaAbierta(false);
                setModalHistorialAbierto(true);
              }}
            >
              Historial
            </Button>
            <Popconfirm
              title="Eliminar inmueble"
              description="¿Seguro que quieres eliminar este inmueble? Esta acción no se puede deshacer."
              okText="Eliminar"
              cancelText="Cancelar"
              okButtonProps={{ danger: true }}
              onConfirm={handleEliminar}
            >
              <Button danger icon={<DeleteOutlined />} loading={eliminando}>
                Eliminar
              </Button>
            </Popconfirm>
            <Button onClick={() => setModalFichaAbierta(false)}>Cerrar</Button>
            <Button type="primary" icon={<WhatsAppOutlined />} onClick={handleEnviarFicha}>
              Enviar por WhatsApp
            </Button>
          </Space>
        }
        width={720}
      >
        <LandingTab inmueble={inmueble} onCambio={setInmueble} />
      </Modal>
    </div>
  );
}
