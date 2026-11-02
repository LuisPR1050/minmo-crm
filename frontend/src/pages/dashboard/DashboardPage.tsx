import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Col, Empty, Row, Skeleton, Typography } from 'antd';
import {
  ArrowRightOutlined,
  FileTextOutlined,
  HomeOutlined,
  NotificationOutlined,
  SolutionOutlined,
  TeamOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import { useAuth } from '../../context/AuthContext';
import { inmuebleApi, propietarioApi, visitaCaptacionApi } from '../../api/captacion';
import { clienteApi, visitaClienteApi } from '../../api/atencioncliente';
import { negociacionApi } from '../../api/negociacion';
import { publicacionInmuebleApi } from '../../api/marketing';
import { contratoCorretajeApi } from '../../api/documentacion';
import { ETIQUETA_ESTADO_CAPTACION } from '../../types/captacion';
import { ETIQUETA_ESTADO_PIPELINE_CLIENTE } from '../../types/atencioncliente';
import type { Inmueble } from '../../types/captacion';
import type { Cliente, VisitaCliente } from '../../types/atencioncliente';
import type { Negociacion } from '../../types/negociacion';
import type { PublicacionInmueble } from '../../types/marketing';
import type { ResumenContratosCorretaje } from '../../types/documentacion';
import type { VisitaCaptacion } from '../../types/captacion';

const { Title, Text } = Typography;

const ESTADOS_INMUEBLE_CERRADOS = new Set(['VENDIDO', 'NEGOCIO_PERDIDO', 'DESCARTADO']);
const ESTADOS_NEGOCIACION_CERRADOS = new Set(['NEGOCIO_CERRADO', 'NEGOCIO_PERDIDO']);
const ESTADOS_CLIENTE_CERRADOS = new Set(['NEGOCIO_CERRADO_GANADO', 'NEGOCIO_PERDIDO']);
const ESTADOS_VISITA_CERRADOS = new Set(['REALIZADA', 'CANCELADA', 'NO_ASISTIO_CLIENTE', 'PROPIETARIO_NO_DISPONIBLE']);

const BORDE = '#e8e2d6';
const FUENTE_SERIF = "'Fraunces', Georgia, serif";
const TEXTO_TENUE = '#8c8c8c';

interface VisitaUnificada {
  id: string;
  modulo: 'Captación' | 'Cliente';
  titulo: string;
  subtitulo: string;
  fecha: string;
  hora?: string;
  link: string;
}

interface AccesoDirecto {
  titulo: string;
  descripcion: string;
  icono: React.ReactNode;
  ruta: string;
}

function Panel({
  titulo,
  accion,
  children,
}: {
  titulo: React.ReactNode;
  accion?: React.ReactNode;
  children: React.ReactNode;
}) {
  return (
    <div style={{ border: `1px solid ${BORDE}`, borderRadius: 10, padding: '18px 20px', height: '100%' }}>
      <div style={{ display: 'flex', alignItems: 'center', justifyContent: 'space-between', marginBottom: 14 }}>
        <Text strong style={{ fontSize: 14 }}>
          {titulo}
        </Text>
        {accion}
      </div>
      {children}
    </div>
  );
}

function Estadistica({
  etiqueta,
  valor,
  acento,
  onClick,
}: {
  etiqueta: string;
  valor: number;
  acento?: string;
  onClick?: () => void;
}) {
  return (
    <div
      onClick={onClick}
      style={{
        borderLeft: `3px solid ${acento ?? '#e0e0e0'}`,
        paddingLeft: 14,
        paddingTop: 2,
        paddingBottom: 2,
        cursor: onClick ? 'pointer' : undefined,
      }}
    >
      <div style={{ fontFamily: FUENTE_SERIF, fontSize: 32, fontWeight: 600, lineHeight: 1.2, color: '#1a1a1a' }}>{valor}</div>
      <Text style={{ fontSize: 13, color: TEXTO_TENUE }}>{etiqueta}</Text>
    </div>
  );
}

export default function DashboardPage() {
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const [cargando, setCargando] = useState(false);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [negociaciones, setNegociaciones] = useState<Negociacion[]>([]);
  const [publicaciones, setPublicaciones] = useState<PublicacionInmueble[]>([]);
  const [resumenContratos, setResumenContratos] = useState<ResumenContratosCorretaje | null>(null);
  const [visitasCaptacion, setVisitasCaptacion] = useState<VisitaCaptacion[]>([]);
  const [visitasCliente, setVisitasCliente] = useState<VisitaCliente[]>([]);
  const [totalPropietarios, setTotalPropietarios] = useState(0);

  useEffect(() => {
    const cargar = async () => {
      setCargando(true);
      try {
        const [
          inmueblesData,
          clientesData,
          negociacionesData,
          publicacionesData,
          resumenData,
          visitasCaptacionData,
          visitasClienteData,
          propietariosData,
        ] = await Promise.all([
          inmuebleApi.listar(),
          clienteApi.listar(),
          negociacionApi.listar(),
          publicacionInmuebleApi.listar(),
          contratoCorretajeApi.resumen(),
          visitaCaptacionApi.listarTodas(),
          visitaClienteApi.listarTodas(),
          propietarioApi.listar(),
        ]);
        setInmuebles(inmueblesData);
        setClientes(clientesData);
        setNegociaciones(negociacionesData);
        setPublicaciones(publicacionesData);
        setResumenContratos(resumenData);
        setVisitasCaptacion(visitasCaptacionData);
        setVisitasCliente(visitasClienteData);
        setTotalPropietarios(propietariosData.length);
      } finally {
        setCargando(false);
      }
    };
    cargar();
  }, []);

  const inmueblesActivos = useMemo(() => inmuebles.filter((i) => !ESTADOS_INMUEBLE_CERRADOS.has(i.estadoCaptacion)).length, [inmuebles]);
  const negociacionesActivas = useMemo(
    () => negociaciones.filter((n) => !ESTADOS_NEGOCIACION_CERRADOS.has(n.estado)).length,
    [negociaciones],
  );
  const negociacionesEnRiesgo = useMemo(() => negociaciones.filter((n) => n.estado === 'NEGOCIO_EN_RIESGO').length, [negociaciones]);
  const clientesPorAtender = useMemo(() => clientes.filter((c) => !ESTADOS_CLIENTE_CERRADOS.has(c.estadoPipeline)).length, [clientes]);
  const publicacionesActivas = useMemo(() => publicaciones.filter((p) => p.estado === 'ACTIVA').length, [publicaciones]);

  const proximasVisitas = useMemo<VisitaUnificada[]>(() => {
    const hoy = dayjs().startOf('day');

    const deCaptacion: VisitaUnificada[] = visitasCaptacion
      .filter((v) => v.fechaVisita && !ESTADOS_VISITA_CERRADOS.has(v.estado) && !dayjs(v.fechaVisita).isBefore(hoy))
      .map((v) => ({
        id: v.id,
        modulo: 'Captación',
        titulo: `Visita a ${v.inmuebleCodigo}`,
        subtitulo: ETIQUETA_ESTADO_CAPTACION[
          inmuebles.find((i) => i.id === v.inmuebleId)?.estadoCaptacion ?? 'PROSPECTO'
        ],
        fecha: v.fechaVisita as string,
        hora: v.horaInicio,
        link: `/captacion/inmuebles/${v.inmuebleId}`,
      }));

    const deCliente: VisitaUnificada[] = visitasCliente
      .filter((v) => v.fechaVisita && !ESTADOS_VISITA_CERRADOS.has(v.estado) && !dayjs(v.fechaVisita).isBefore(hoy))
      .map((v) => ({
        id: v.id,
        modulo: 'Cliente',
        titulo: `${v.clienteNombre} — ${v.inmuebleCodigo}`,
        subtitulo: ETIQUETA_ESTADO_PIPELINE_CLIENTE[
          clientes.find((c) => c.id === v.clienteId)?.estadoPipeline ?? 'CONTACTO_INICIADO'
        ],
        fecha: v.fechaVisita as string,
        hora: v.horaInicio,
        link: `/atencion-cliente/${v.clienteId}`,
      }));

    return [...deCaptacion, ...deCliente]
      .sort((a, b) => `${a.fecha}${a.hora ?? ''}`.localeCompare(`${b.fecha}${b.hora ?? ''}`))
      .slice(0, 8);
  }, [visitasCaptacion, visitasCliente, inmuebles, clientes]);

  const accesos: AccesoDirecto[] = [
    { titulo: 'Propietarios', descripcion: `${totalPropietarios} registrados`, icono: <TeamOutlined />, ruta: '/captacion/propietarios' },
    { titulo: 'Captación', descripcion: `${inmuebles.length} inmuebles`, icono: <HomeOutlined />, ruta: '/captacion/inmuebles' },
    { titulo: 'Atención al cliente', descripcion: `${clientes.length} clientes`, icono: <SolutionOutlined />, ruta: '/atencion-cliente' },
    { titulo: 'Negociación', descripcion: `${negociaciones.length} negocios`, icono: <FileTextOutlined />, ruta: '/negociacion' },
    { titulo: 'Documentación', descripcion: `${resumenContratos?.total ?? 0} contratos`, icono: <FileTextOutlined />, ruta: '/documentacion' },
    { titulo: 'Marketing', descripcion: `${publicaciones.length} publicaciones`, icono: <NotificationOutlined />, ruta: '/marketing' },
  ];

  const horaDelDia = dayjs().hour();
  const saludo = horaDelDia < 12 ? 'Buenos días' : horaDelDia < 19 ? 'Buenas tardes' : 'Buenas noches';

  return (
    <div>
      <div style={{ marginBottom: 32 }}>
        <Text style={{ fontSize: 13, color: TEXTO_TENUE, textTransform: 'capitalize' }}>
          {dayjs().format('dddd D [de] MMMM')}
        </Text>
        <Title level={2} style={{ margin: '2px 0 0', fontWeight: 600 }}>
          {saludo}, {usuario?.nombre?.split(' ')[0]}
        </Title>
      </div>

      {cargando ? (
        <Skeleton active />
      ) : (
        <>
          <Row gutter={[24, 24]} style={{ marginBottom: 32 }}>
            <Col xs={12} sm={8} md={5}>
              <Estadistica etiqueta="Inmuebles activos" valor={inmueblesActivos} acento="#0b4f6c" onClick={() => navigate('/captacion/inmuebles')} />
            </Col>
            <Col xs={12} sm={8} md={5}>
              <Estadistica etiqueta="Negociaciones activas" valor={negociacionesActivas} acento="#0b4f6c" onClick={() => navigate('/negociacion')} />
            </Col>
            <Col xs={12} sm={8} md={5}>
              <Estadistica etiqueta="Clientes por atender" valor={clientesPorAtender} acento="#0b4f6c" onClick={() => navigate('/atencion-cliente')} />
            </Col>
            <Col xs={12} sm={8} md={5}>
              <Estadistica etiqueta="Publicaciones activas" valor={publicacionesActivas} acento="#0b4f6c" onClick={() => navigate('/marketing')} />
            </Col>
            <Col xs={12} sm={8} md={4}>
              <Estadistica
                etiqueta="Negocios en riesgo"
                valor={negociacionesEnRiesgo}
                acento={negociacionesEnRiesgo > 0 ? '#cf1322' : '#e0e0e0'}
                onClick={() => navigate('/negociacion')}
              />
            </Col>
          </Row>

          <Row gutter={[24, 24]} style={{ marginBottom: 32 }}>
            <Col xs={24} lg={14}>
              <Panel titulo="Próximas visitas">
                {proximasVisitas.length === 0 ? (
                  <Empty description="Sin visitas próximas agendadas" image={Empty.PRESENTED_IMAGE_SIMPLE} />
                ) : (
                  <div>
                    {proximasVisitas.map((v, index) => (
                      <div
                        key={v.id}
                        onClick={() => navigate(v.link)}
                        style={{
                          display: 'flex',
                          justifyContent: 'space-between',
                          alignItems: 'center',
                          flexWrap: 'wrap',
                          gap: 8,
                          padding: '11px 0',
                          cursor: 'pointer',
                          borderBottom: index < proximasVisitas.length - 1 ? `1px solid ${BORDE}` : 'none',
                        }}
                      >
                        <div>
                          <div>
                            <Text strong style={{ fontSize: 13.5 }}>
                              {v.titulo}
                            </Text>
                            <Text style={{ fontSize: 11.5, color: TEXTO_TENUE, marginLeft: 8 }}>{v.modulo}</Text>
                          </div>
                          <Text style={{ fontSize: 12, color: TEXTO_TENUE }}>{v.subtitulo}</Text>
                        </div>
                        <div style={{ textAlign: 'right' }}>
                          <div>
                            <Text strong style={{ fontSize: 13.5 }}>
                              {dayjs(v.fecha).format('D MMM')}
                            </Text>
                          </div>
                          {v.hora && (
                            <Text style={{ fontSize: 12, color: TEXTO_TENUE }}>{v.hora.slice(0, 5)}</Text>
                          )}
                        </div>
                      </div>
                    ))}
                  </div>
                )}
              </Panel>
            </Col>

            <Col xs={24} lg={10}>
              <Panel titulo="Contratos de corretaje">
                <Row gutter={16} style={{ marginBottom: 14 }}>
                  <Col span={12}>
                    <div style={{ fontFamily: FUENTE_SERIF, fontSize: 26, fontWeight: 600, color: '#1a1a1a' }}>
                      {resumenContratos?.vigentes ?? 0}
                    </div>
                    <Text style={{ fontSize: 12, color: TEXTO_TENUE }}>Vigentes</Text>
                  </Col>
                  <Col span={12}>
                    <div
                      style={{
                        fontFamily: FUENTE_SERIF,
                        fontSize: 26,
                        fontWeight: 600,
                        color: (resumenContratos?.proximosAVencer ?? 0) > 0 ? '#d48806' : '#1a1a1a',
                      }}
                    >
                      {resumenContratos?.proximosAVencer ?? 0}
                    </div>
                    <Text style={{ fontSize: 12, color: TEXTO_TENUE }}>Próximos a vencer</Text>
                  </Col>
                </Row>
                <a onClick={() => navigate('/documentacion')} style={{ fontSize: 13 }}>
                  Ver documentación <ArrowRightOutlined />
                </a>
              </Panel>
            </Col>
          </Row>

          <Text strong style={{ fontSize: 14, display: 'block', marginBottom: 14 }}>
            Accesos directos
          </Text>
          <Row gutter={[16, 16]}>
            {accesos.map((a) => (
              <Col xs={12} sm={8} md={4} key={a.titulo}>
                <div
                  onClick={() => navigate(a.ruta)}
                  className="acceso-directo"
                  style={{
                    border: `1px solid ${BORDE}`,
                    borderRadius: 10,
                    padding: '18px 14px',
                    textAlign: 'center',
                    cursor: 'pointer',
                    transition: 'border-color 0.15s ease, background 0.15s ease',
                  }}
                >
                  <div style={{ fontSize: 20, color: '#0b4f6c', marginBottom: 10 }}>{a.icono}</div>
                  <div style={{ fontWeight: 500, fontSize: 13.5 }}>{a.titulo}</div>
                  <Text style={{ fontSize: 12, color: TEXTO_TENUE }}>{a.descripcion}</Text>
                </div>
              </Col>
            ))}
          </Row>
        </>
      )}
    </div>
  );
}
