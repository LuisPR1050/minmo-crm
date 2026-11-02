import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Avatar,
  Badge,
  Button,
  Card,
  Col,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
  Segmented,
  Select,
  Space,
  Statistic,
  Table,
  Tag,
  Typography,
} from 'antd';
import { HomeOutlined, PlusOutlined, TableOutlined, UserOutlined } from '@ant-design/icons';
import { aliadoApi, condicionCaptacionApi, inmuebleApi, propietarioApi, puntoReferenciaApi } from '../../api/captacion';
import InputMoneda from '../../components/InputMoneda';
import {
  ENTORNOS,
  ESTADOS_CAPTACION,
  ETIQUETA_ESTADO_CAPTACION,
  ETIQUETA_ORIGEN_PROPIEDAD,
  ETIQUETA_TIPO_ALIADO,
  ETIQUETA_TIPO_PROPIEDAD,
  TIPOS_ALIADO,
  TIPOS_CONTACTO_PROPIETARIO,
  TIPOS_PROPIEDAD,
  ZONAS_VALLEDUPAR,
} from '../../types/captacion';
import type {
  Aliado,
  AliadoRequest,
  EstadoCaptacion,
  Inmueble,
  InmuebleRequest,
  OrigenPropiedad,
  Propietario,
  PropietarioRequest,
  TipoAliado,
} from '../../types/captacion';

const ORIGENES_PROPIEDAD: OrigenPropiedad[] = ['PROPIETARIO_DIRECTO', 'ALIADO'];

type FormValuesCreacion = InmuebleRequest & { origenPropiedad?: OrigenPropiedad; aliadoId?: string };

interface NuevoAliadoFormValues {
  nombre: string;
  telefono?: string;
  tipo: TipoAliado;
}

interface NuevoPropietarioFormValues {
  nombre: string;
  telefonoPrincipal: string;
  tipoContacto: string;
}

const { Title, Text } = Typography;

const COLOR_ESTADO: Record<EstadoCaptacion, string> = {
  PROSPECTO: '#8c8c8c',
  CONTACTADO: '#1677ff',
  VISITADO: '#13c2c2',
  CAPTADO_CON_MATERIAL: '#52c41a',
  CAPTADO_SIN_MATERIAL: '#7cb305',
  EN_NEGOCIACION: '#fa8c16',
  VENDIDO: '#722ed1',
  NEGOCIO_PERDIDO: '#cf1322',
  DESCARTADO: '#bfbfbf',
};

const COLUMNAS: EstadoCaptacion[] = [
  'PROSPECTO',
  'CONTACTADO',
  'VISITADO',
  'CAPTADO_SIN_MATERIAL',
  'CAPTADO_CON_MATERIAL',
  'EN_NEGOCIACION',
  'VENDIDO',
  'NEGOCIO_PERDIDO',
  'DESCARTADO',
];

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function PipelineCaptacionPage() {
  const navigate = useNavigate();
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [propietarios, setPropietarios] = useState<Propietario[]>([]);
  const [loading, setLoading] = useState(false);
  const [vista, setVista] = useState<'pipeline' | 'lista'>('pipeline');
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [columnaSobrevolada, setColumnaSobrevolada] = useState<EstadoCaptacion | null>(null);
  const [catalogoPuntosReferencia, setCatalogoPuntosReferencia] = useState<string[]>([]);
  const [busqueda, setBusqueda] = useState('');
  const [buscando, setBuscando] = useState(false);
  const [resultadosBusqueda, setResultadosBusqueda] = useState<Inmueble[] | null>(null);
  const [aliados, setAliados] = useState<Aliado[]>([]);
  const [modalAliadoAbierto, setModalAliadoAbierto] = useState(false);
  const [creandoAliado, setCreandoAliado] = useState(false);
  const [modalPropietarioAbierto, setModalPropietarioAbierto] = useState(false);
  const [creandoPropietario, setCreandoPropietario] = useState(false);
  const [form] = Form.useForm<FormValuesCreacion>();
  const [formAliado] = Form.useForm<NuevoAliadoFormValues>();
  const [formPropietario] = Form.useForm<NuevoPropietarioFormValues>();

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [inmueblesData, propietariosData] = await Promise.all([inmuebleApi.listar(), propietarioApi.listar()]);
      setInmuebles(inmueblesData);
      setPropietarios(propietariosData);
    } catch {
      message.error('No se pudieron cargar los inmuebles. Verifica que el backend esté corriendo.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarDatos();
    puntoReferenciaApi
      .listar()
      .then((puntos) => setCatalogoPuntosReferencia(puntos.map((p) => p.nombre)))
      .catch(() => undefined);
    aliadoApi
      .listar()
      .then(setAliados)
      .catch(() => undefined);
  }, []);

  const inmueblesMostrados = resultadosBusqueda ?? inmuebles;

  const handleBuscarIA = async (consulta: string) => {
    if (!consulta.trim()) {
      setResultadosBusqueda(null);
      return;
    }
    setBuscando(true);
    try {
      const resultados = await inmuebleApi.buscarConIA(consulta);
      setResultadosBusqueda(resultados);
    } catch {
      message.error('No se pudo interpretar la búsqueda. Intenta reformularla.');
    } finally {
      setBuscando(false);
    }
  };

  const handleLimpiarBusqueda = () => {
    setBusqueda('');
    setResultadosBusqueda(null);
  };

  const estadisticas = useMemo(
    () => ({
      activos: inmuebles.filter((i) => !['VENDIDO', 'NEGOCIO_PERDIDO', 'DESCARTADO'].includes(i.estadoCaptacion)).length,
      enNegociacion: inmuebles.filter((i) => i.estadoCaptacion === 'EN_NEGOCIACION').length,
      vendidos: inmuebles.filter((i) => i.estadoCaptacion === 'VENDIDO').length,
      descartados: inmuebles.filter((i) => ['NEGOCIO_PERDIDO', 'DESCARTADO'].includes(i.estadoCaptacion)).length,
    }),
    [inmuebles],
  );

  const inmueblesPorEstado = useMemo(() => {
    const mapa = new Map<EstadoCaptacion, Inmueble[]>();
    COLUMNAS.forEach((estado) => mapa.set(estado, []));
    inmueblesMostrados.forEach((inmueble) => {
      mapa.get(inmueble.estadoCaptacion)?.push(inmueble);
    });
    return mapa;
  }, [inmueblesMostrados]);

  const abrirModal = () => {
    setModalOpen(true);
  };

  const handleCrear = async (values: FormValuesCreacion) => {
    const { origenPropiedad, aliadoId, ...inmuebleValues } = values;
    setGuardando(true);
    try {
      const creado = await inmuebleApi.crear(inmuebleValues);
      if (origenPropiedad) {
        try {
          await condicionCaptacionApi.guardar({
            inmuebleId: creado.id,
            origenPropiedad,
            aliadoId: origenPropiedad === 'ALIADO' ? aliadoId : undefined,
            exclusividad: false,
          });
        } catch {
          message.warning('El inmueble se creó, pero no se pudo guardar el origen de la propiedad. Puedes completarlo en "Condiciones de captación".');
        }
      }
      message.success('Inmueble creado correctamente');
      setModalOpen(false);
      form.resetFields();
      cargarDatos();
    } catch {
      message.error('No se pudo crear el inmueble');
    } finally {
      setGuardando(false);
    }
  };

  const handleCrearAliado = async (values: NuevoAliadoFormValues) => {
    setCreandoAliado(true);
    try {
      const request: AliadoRequest = { nombre: values.nombre, telefono: values.telefono, tipo: values.tipo };
      const creado = await aliadoApi.crear(request);
      setAliados((prev) => [...prev, creado]);
      form.setFieldValue('aliadoId', creado.id);
      message.success('Aliado creado');
      setModalAliadoAbierto(false);
      formAliado.resetFields();
    } catch {
      message.error('No se pudo crear el aliado');
    } finally {
      setCreandoAliado(false);
    }
  };

  const handleCrearPropietario = async (values: NuevoPropietarioFormValues) => {
    setCreandoPropietario(true);
    try {
      const request: PropietarioRequest = {
        nombre: values.nombre,
        telefonoPrincipal: values.telefonoPrincipal,
        tipoContacto: values.tipoContacto,
      };
      const creado = await propietarioApi.crear(request);
      setPropietarios((prev) => [...prev, creado]);
      form.setFieldValue('propietarioId', creado.id);
      message.success('Propietario creado');
      setModalPropietarioAbierto(false);
      formPropietario.resetFields();
    } catch {
      message.error('No se pudo crear el propietario');
    } finally {
      setCreandoPropietario(false);
    }
  };

  const handleDrop = async (nuevoEstado: EstadoCaptacion, inmuebleId: string) => {
    setColumnaSobrevolada(null);
    const inmueble = inmuebles.find((i) => i.id === inmuebleId);
    if (!inmueble || inmueble.estadoCaptacion === nuevoEstado) return;

    setInmuebles((prev) => prev.map((i) => (i.id === inmuebleId ? { ...i, estadoCaptacion: nuevoEstado } : i)));

    try {
      await inmuebleApi.cambiarEstadoCaptacion(inmuebleId, nuevoEstado);
      message.success(`${inmueble.codigo} movido a "${ETIQUETA_ESTADO_CAPTACION[nuevoEstado]}"`);
    } catch {
      message.error('No se pudo actualizar el estado');
      cargarDatos();
    }
  };

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Captación de inmuebles
          </Title>
          <Text type="secondary">{inmuebles.length} inmuebles en el pipeline</Text>
        </div>
        <Space>
          <Segmented
            value={vista}
            onChange={(v) => setVista(v as 'pipeline' | 'lista')}
            options={[
              { label: 'Pipeline', value: 'pipeline', icon: <HomeOutlined /> },
              { label: 'Lista', value: 'lista', icon: <TableOutlined /> },
            ]}
          />
          <Button type="primary" icon={<PlusOutlined />} onClick={abrirModal}>
            Nuevo inmueble
          </Button>
        </Space>
      </Space>

      <Space style={{ marginBottom: 16, width: '100%' }} wrap>
        <Input.Search
          value={busqueda}
          onChange={(e) => setBusqueda(e.target.value)}
          onSearch={handleBuscarIA}
          loading={buscando}
          allowClear
          placeholder='Busca con tu voz o escribe, ej: "apartamentos cerca de Mayales con 3 habitaciones y precio hasta 400 millones"'
          style={{ width: 480, maxWidth: '100%' }}
        />
        {resultadosBusqueda !== null && (
          <Space>
            <Text type="secondary">{resultadosBusqueda.length} resultado(s) para "{busqueda}"</Text>
            <Button size="small" onClick={handleLimpiarBusqueda}>
              Limpiar búsqueda
            </Button>
          </Space>
        )}
      </Space>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Inmuebles activos" value={estadisticas.activos} prefix={<HomeOutlined />} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="En negociación" value={estadisticas.enNegociacion} styles={{ content: { color: '#fa8c16' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Vendidos" value={estadisticas.vendidos} styles={{ content: { color: '#389e0d' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Perdidos / descartados" value={estadisticas.descartados} styles={{ content: { color: '#8c8c8c' } }} />
          </Card>
        </Col>
      </Row>

      {vista === 'pipeline' ? (
        <div style={{ display: 'flex', gap: 16, overflowX: 'auto', paddingBottom: 16 }}>
          {COLUMNAS.map((estado) => {
            const items = inmueblesPorEstado.get(estado) ?? [];
            const esZonaDeSuelta = columnaSobrevolada === estado;
            return (
              <div
                key={estado}
                onDragOver={(e) => {
                  e.preventDefault();
                  setColumnaSobrevolada(estado);
                }}
                onDragLeave={() => setColumnaSobrevolada((prev) => (prev === estado ? null : prev))}
                onDrop={(e) => {
                  e.preventDefault();
                  const inmuebleId = e.dataTransfer.getData('text/inmueble-id');
                  handleDrop(estado, inmuebleId);
                }}
                style={{
                  minWidth: 272,
                  maxWidth: 272,
                  flexShrink: 0,
                  background: esZonaDeSuelta ? '#f0f5ff' : '#fafafa',
                  border: esZonaDeSuelta ? '2px dashed #0b4f6c' : '1px solid #f0f0f0',
                  borderRadius: 10,
                  display: 'flex',
                  flexDirection: 'column',
                  maxHeight: 'calc(100vh - 260px)',
                }}
              >
                <div
                  style={{
                    padding: '12px 14px',
                    borderBottom: '1px solid #eee',
                    display: 'flex',
                    alignItems: 'center',
                    gap: 8,
                  }}
                >
                  <span
                    style={{
                      width: 8,
                      height: 8,
                      borderRadius: '50%',
                      background: COLOR_ESTADO[estado],
                      flexShrink: 0,
                    }}
                  />
                  <Text strong style={{ fontSize: 13, flex: 1 }}>
                    {ETIQUETA_ESTADO_CAPTACION[estado]}
                  </Text>
                  <Badge count={items.length} showZero color={COLOR_ESTADO[estado]} />
                </div>
                <div style={{ padding: 10, overflowY: 'auto', flex: 1 }}>
                  {items.map((inmueble) => (
                    <div
                      key={inmueble.id}
                      draggable
                      onDragStart={(e) => e.dataTransfer.setData('text/inmueble-id', inmueble.id)}
                      onClick={() => navigate(`/captacion/inmuebles/${inmueble.id}`)}
                      style={{
                        background: '#fff',
                        border: '1px solid #eee',
                        borderLeft: `3px solid ${COLOR_ESTADO[estado]}`,
                        borderRadius: 8,
                        padding: 10,
                        marginBottom: 8,
                        cursor: 'grab',
                        boxShadow: '0 1px 2px rgba(0,0,0,0.04)',
                      }}
                    >
                      <Text strong style={{ fontSize: 13 }}>
                        {inmueble.codigo}{' '}
                        {ETIQUETA_TIPO_PROPIEDAD[inmueble.tipoPropiedad as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ?? inmueble.tipoPropiedad}
                      </Text>
                      <div style={{ fontSize: 12, color: '#888', marginTop: 2 }}>
                        {[
                          inmueble.zona ? `Zona ${inmueble.zona}` : inmueble.ciudad,
                          inmueble.direccionExacta,
                        ]
                          .filter(Boolean)
                          .join(' ')}
                      </div>
                      <div style={{ fontSize: 13, fontWeight: 600, color: '#0b4f6c', marginTop: 4 }}>
                        {formatearMoneda(inmueble.expectativaPrecio)}
                      </div>
                      <Space size={4} style={{ marginTop: 6 }}>
                        <Avatar size={16} icon={<UserOutlined />} />
                        <Text style={{ fontSize: 11 }} type="secondary">
                          {inmueble.propietarioNombre}
                        </Text>
                      </Space>
                    </div>
                  ))}
                  {items.length === 0 && (
                    <div style={{ textAlign: 'center', color: '#bbb', fontSize: 12, padding: '20px 0' }}>
                      Sin inmuebles
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <Table<Inmueble>
          rowKey="id"
          scroll={{ x: 'max-content' }}
          loading={loading}
          dataSource={inmueblesMostrados}
          onRow={(record) => ({
            onClick: () => navigate(`/captacion/inmuebles/${record.id}`),
            style: { cursor: 'pointer' },
          })}
          columns={[
            { title: 'Código', dataIndex: 'codigo' },
            {
              title: 'Tipo',
              dataIndex: 'tipoPropiedad',
              render: (v: string) => ETIQUETA_TIPO_PROPIEDAD[v as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ?? v,
            },
            { title: 'Ciudad', dataIndex: 'ciudad' },
            { title: 'Zona', dataIndex: 'zona' },
            { title: 'Dirección', dataIndex: 'direccionExacta' },
            { title: 'Propietario', dataIndex: 'propietarioNombre' },
            {
              title: 'Expectativa de precio',
              dataIndex: 'expectativaPrecio',
              render: (valor: number) => formatearMoneda(valor),
            },
            {
              title: 'Estado',
              dataIndex: 'estadoCaptacion',
              render: (estado: EstadoCaptacion) => (
                <Tag color={COLOR_ESTADO[estado]}>{ETIQUETA_ESTADO_CAPTACION[estado]}</Tag>
              ),
            },
          ]}
        />
      )}

      <Modal
        title="Nuevo inmueble"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Crear"
        cancelText="Cancelar"
        width={640}
      >
        <Form form={form} layout="vertical" onFinish={handleCrear} initialValues={{ estadoCaptacion: 'PROSPECTO' }}>
          <Form.Item
            name="origenPropiedad"
            label="Origen de la propiedad"
            tooltip="¿Llegó por contacto directo con el propietario o por medio de un aliado?"
            rules={[{ required: true, message: 'Selecciona el origen de la propiedad' }]}
          >
            <Select
              placeholder="Selecciona el origen"
              options={ORIGENES_PROPIEDAD.map((o) => ({ value: o, label: ETIQUETA_ORIGEN_PROPIEDAD[o] }))}
            />
          </Form.Item>
          <Form.Item noStyle shouldUpdate={(prev, next) => prev.origenPropiedad !== next.origenPropiedad}>
            {({ getFieldValue }) =>
              getFieldValue('origenPropiedad') === 'ALIADO' && (
                <Form.Item
                  name="aliadoId"
                  label="Inmobiliaria o asesor aliado"
                  rules={[{ required: true, message: 'Selecciona o crea un aliado' }]}
                >
                  <Select
                    showSearch
                    placeholder="Selecciona un aliado"
                    optionFilterProp="label"
                    options={aliados.map((a) => ({ value: a.id, label: `${a.nombre} (${ETIQUETA_TIPO_ALIADO[a.tipo]})` }))}
                    popupRender={(menu) => (
                      <>
                        {menu}
                        <div style={{ padding: 8, borderTop: '1px solid #f0f0f0' }}>
                          <Button type="link" icon={<PlusOutlined />} style={{ padding: 0 }} onClick={() => setModalAliadoAbierto(true)}>
                            Crear nuevo aliado
                          </Button>
                        </div>
                      </>
                    )}
                  />
                </Form.Item>
              )
            }
          </Form.Item>
          <Form.Item
            name="propietarioId"
            label="Propietario"
            rules={[{ required: true, message: 'Selecciona o crea un propietario' }]}
          >
            <Select
              showSearch
              placeholder="Selecciona un propietario"
              optionFilterProp="label"
              options={propietarios.map((p) => ({ value: p.id, label: p.nombre }))}
              popupRender={(menu) => (
                <>
                  {menu}
                  <div style={{ padding: 8, borderTop: '1px solid #f0f0f0' }}>
                    <Button type="link" icon={<PlusOutlined />} style={{ padding: 0 }} onClick={() => setModalPropietarioAbierto(true)}>
                      Crear nuevo propietario
                    </Button>
                  </div>
                </>
              )}
            />
          </Form.Item>
          <Form.Item
            name="tipoPropiedad"
            label="Tipo de propiedad"
            rules={[{ required: true, message: 'Selecciona el tipo de propiedad' }]}
          >
            <Select options={TIPOS_PROPIEDAD.map((t) => ({ value: t, label: ETIQUETA_TIPO_PROPIEDAD[t] }))} />
          </Form.Item>
          <Form.Item name="ciudad" label="Ciudad" initialValue="Valledupar">
            <Input />
          </Form.Item>
          <Form.Item name="direccionExacta" label="Dirección exacta">
            <Input />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="zona" label="Zona">
                <Select showSearch allowClear options={ZONAS_VALLEDUPAR.map((z) => ({ value: z, label: z }))} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="entorno" label="Entorno">
                <Select allowClear options={ENTORNOS.map((e) => ({ value: e, label: e }))} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="puntosReferencia"
            label="Puntos de referencia (para búsqueda)"
            tooltip="Lugares cercanos que un cliente podría mencionar al buscar, ej: La Popa, C.C. Mayales"
          >
            <Select
              mode="tags"
              allowClear
              placeholder="Escribe y presiona Enter para agregar"
              options={catalogoPuntosReferencia.map((nombre) => ({ value: nombre, label: nombre }))}
            />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="areaTotal" label="Área total (m²)">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="areaConstruida" label="Área construida (m²)">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="numeroHabitaciones" label="Habitaciones">
                <InputNumber style={{ width: '100%' }} min={0} max={20} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="numeroBanos" label="Baños">
                <InputNumber style={{ width: '100%' }} min={0} max={20} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="estadoFisico" label="Estado físico">
            <Input placeholder="Ej: Excelente, Bueno, Requiere remodelación" />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="expectativaPrecio" label="Expectativa de precio">
                <InputMoneda />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="precioMinimoAceptado" label="Precio mínimo aceptado">
                <InputMoneda />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="estadoCaptacion" label="Estado de captación">
            <Select options={ESTADOS_CAPTACION.map((e) => ({ value: e, label: ETIQUETA_ESTADO_CAPTACION[e] }))} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal title="Nuevo aliado" open={modalAliadoAbierto} onCancel={() => setModalAliadoAbierto(false)} footer={null} destroyOnHidden>
        <Form form={formAliado} layout="vertical" onFinish={handleCrearAliado} initialValues={{ tipo: 'INMOBILIARIA' }}>
          <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'Escribe el nombre' }]}>
            <Input placeholder="Ej: Inmobiliaria del Valle" />
          </Form.Item>
          <Form.Item name="telefono" label="Teléfono">
            <Input placeholder="Ej: 3001234567" />
          </Form.Item>
          <Form.Item name="tipo" label="Tipo">
            <Select options={TIPOS_ALIADO.map((t) => ({ value: t, label: ETIQUETA_TIPO_ALIADO[t] }))} />
          </Form.Item>
          <Button type="primary" htmlType="submit" loading={creandoAliado}>
            Crear aliado
          </Button>
        </Form>
      </Modal>

      <Modal title="Nuevo propietario" open={modalPropietarioAbierto} onCancel={() => setModalPropietarioAbierto(false)} footer={null} destroyOnHidden>
        <Form form={formPropietario} layout="vertical" onFinish={handleCrearPropietario}>
          <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'Escribe el nombre' }]}>
            <Input placeholder="Ej: Carlos Pérez" />
          </Form.Item>
          <Form.Item name="telefonoPrincipal" label="Teléfono principal" rules={[{ required: true, message: 'Escribe el teléfono' }]}>
            <Input placeholder="Ej: 3001234567" />
          </Form.Item>
          <Form.Item name="tipoContacto" label="Tipo de contacto" rules={[{ required: true, message: 'Selecciona el tipo de contacto' }]}>
            <Select
              showSearch
              options={TIPOS_CONTACTO_PROPIETARIO.map((t) => ({ value: t, label: t }))}
            />
          </Form.Item>
          <Button type="primary" htmlType="submit" loading={creandoPropietario}>
            Crear propietario
          </Button>
        </Form>
      </Modal>
    </div>
  );
}
