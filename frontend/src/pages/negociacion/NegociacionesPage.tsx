import { useEffect, useMemo, useState } from 'react';
import { Button, Card, Col, DatePicker, Form, message, Modal, Row, Select, Space, Statistic, Table, Tag, Typography } from 'antd';
import InputMoneda from '../../components/InputMoneda';
import { PlusOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import dayjs from 'dayjs';
import { useAuth } from '../../context/AuthContext';
import { negociacionApi } from '../../api/negociacion';
import { inmuebleApi } from '../../api/captacion';
import { clienteApi } from '../../api/atencioncliente';
import { ESTADOS_NEGOCIACION, ETIQUETA_ESTADO_NEGOCIACION } from '../../types/negociacion';
import type { EstadoNegociacion, Negociacion, NegociacionRequest } from '../../types/negociacion';
import type { Inmueble } from '../../types/captacion';
import type { Cliente } from '../../types/atencioncliente';

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

const ESTADOS_ACTIVOS: EstadoNegociacion[] = [
  'NEGOCIACION_INICIADA',
  'OFERTA_PRESENTADA',
  'CONTRAOFERTA',
  'ACUERDO_VERBAL',
  'ESPERANDO_FINANCIACION',
  'ESPERANDO_FIRMA_PROMESA',
  'ESPERANDO_ESCRITURA',
];

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

interface CrearNegociacionForm {
  inmuebleId: string;
  clienteId: string;
  fechaInicio?: dayjs.Dayjs;
  precioInicialPublicado?: number;
  ofertaInicialComprador?: number;
}

export default function NegociacionesPage() {
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const [negociaciones, setNegociaciones] = useState<Negociacion[]>([]);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [filtroEstado, setFiltroEstado] = useState<EstadoNegociacion | undefined>();
  const [form] = Form.useForm<CrearNegociacionForm>();

  const estadisticas = useMemo(
    () => ({
      activos: negociaciones.filter((n) => ESTADOS_ACTIVOS.includes(n.estado)).length,
      cerrados: negociaciones.filter((n) => n.estado === 'NEGOCIO_CERRADO').length,
      enRiesgo: negociaciones.filter((n) => n.estado === 'NEGOCIO_EN_RIESGO').length,
      perdidos: negociaciones.filter((n) => n.estado === 'NEGOCIO_PERDIDO').length,
    }),
    [negociaciones],
  );

  const negociacionesFiltradas = filtroEstado ? negociaciones.filter((n) => n.estado === filtroEstado) : negociaciones;

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [negociacionesData, inmueblesData, clientesData] = await Promise.all([
        negociacionApi.listar(),
        inmuebleApi.listar(),
        clienteApi.listar(),
      ]);
      setNegociaciones(negociacionesData);
      setInmuebles(inmueblesData);
      setClientes(clientesData);
    } catch {
      message.error('No se pudieron cargar las negociaciones. Verifica que el backend esté corriendo.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarDatos();
  }, []);

  const abrirModal = () => {
    if (inmuebles.length === 0) {
      message.warning('Primero debes crear un inmueble en la sección "Inmuebles".');
      return;
    }
    if (clientes.length === 0) {
      message.warning('Primero debes crear un cliente en la sección "Clientes".');
      return;
    }
    setModalOpen(true);
  };

  const handleCrear = async (values: CrearNegociacionForm) => {
    const inmueble = inmuebles.find((i) => i.id === values.inmuebleId);
    if (!inmueble) {
      message.error('El inmueble seleccionado ya no existe');
      return;
    }

    const request: NegociacionRequest = {
      inmuebleId: values.inmuebleId,
      clienteId: values.clienteId,
      propietarioId: inmueble.propietarioId,
      asesorId: usuario?.usuarioId,
      fechaInicio: values.fechaInicio ? values.fechaInicio.format('YYYY-MM-DD') : undefined,
      precioInicialPublicado: values.precioInicialPublicado,
      ofertaInicialComprador: values.ofertaInicialComprador,
    };

    setGuardando(true);
    try {
      await negociacionApi.crear(request);
      message.success('Negociación creada correctamente');
      setModalOpen(false);
      form.resetFields();
      cargarDatos();
    } catch {
      message.error('No se pudo crear la negociación');
    } finally {
      setGuardando(false);
    }
  };

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Title level={3} style={{ margin: 0 }}>
          Negociaciones
        </Title>
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModal}>
          Nueva negociación
        </Button>
      </Space>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Negocios activos" value={estadisticas.activos} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Negocios cerrados" value={estadisticas.cerrados} styles={{ content: { color: '#389e0d' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Negocios en riesgo" value={estadisticas.enRiesgo} styles={{ content: { color: '#d4380d' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Negocios perdidos" value={estadisticas.perdidos} styles={{ content: { color: '#8c8c8c' } }} />
          </Card>
        </Col>
      </Row>

      <Space style={{ marginBottom: 16 }}>
        <Text type="secondary">Filtrar por estado:</Text>
        <Select
          allowClear
          placeholder="Todos los estados"
          style={{ width: 240 }}
          value={filtroEstado}
          onChange={setFiltroEstado}
          options={ESTADOS_NEGOCIACION.map((e) => ({ value: e, label: ETIQUETA_ESTADO_NEGOCIACION[e] }))}
        />
      </Space>

      <Table<Negociacion>
        rowKey="id"
        scroll={{ x: 'max-content' }}
        loading={loading}
        dataSource={negociacionesFiltradas}
        onRow={(record) => ({ onClick: () => navigate(`/negociacion/${record.id}`), style: { cursor: 'pointer' } })}
        columns={[
          { title: 'N° Negocio', dataIndex: 'numeroNegocio' },
          {
            title: 'Inmueble',
            dataIndex: 'inmuebleCodigo',
            render: (codigo: string, record: Negociacion) => (
              <Link to={`/captacion/inmuebles/${record.inmuebleId}`} onClick={(e) => e.stopPropagation()}>
                {codigo}
              </Link>
            ),
          },
          {
            title: 'Cliente',
            dataIndex: 'clienteNombre',
            render: (nombre: string, record: Negociacion) => (
              <Link to={`/atencion-cliente/${record.clienteId}`} onClick={(e) => e.stopPropagation()}>
                {nombre}
              </Link>
            ),
          },
          { title: 'Propietario', dataIndex: 'propietarioNombre' },
          {
            title: 'Precio inicial',
            dataIndex: 'precioInicialPublicado',
            render: (valor: number) => formatearMoneda(valor),
          },
          {
            title: 'Estado',
            dataIndex: 'estado',
            render: (estado: EstadoNegociacion) => <Tag color={COLOR_ESTADO[estado]}>{ETIQUETA_ESTADO_NEGOCIACION[estado]}</Tag>,
          },
        ]}
      />

      <Modal
        title="Nueva negociación"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Crear"
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleCrear}>
          <Form.Item
            name="inmuebleId"
            label="Inmueble"
            rules={[{ required: true, message: 'Selecciona un inmueble' }]}
          >
            <Select
              showSearch
              placeholder="Selecciona un inmueble"
              optionFilterProp="label"
              options={inmuebles.map((i) => ({
                value: i.id,
                label: `${i.codigo} — ${i.direccionExacta ?? i.ciudad ?? ''} (${i.propietarioNombre})`,
              }))}
            />
          </Form.Item>
          <Form.Item
            name="clienteId"
            label="Cliente (comprador)"
            rules={[{ required: true, message: 'Selecciona un cliente' }]}
          >
            <Select
              showSearch
              placeholder="Selecciona un cliente"
              optionFilterProp="label"
              options={clientes.map((c) => ({ value: c.id, label: c.nombre }))}
            />
          </Form.Item>
          <Form.Item name="fechaInicio" label="Fecha de inicio">
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="precioInicialPublicado" label="Precio inicial publicado">
                <InputMoneda />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="ofertaInicialComprador" label="Oferta inicial del comprador">
                <InputMoneda />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
}
