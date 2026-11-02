import { useEffect, useMemo, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { Badge, Button, Card, Col, Form, Input, message, Modal, Row, Segmented, Select, Space, Statistic, Table, Tag, Typography } from 'antd';
import { PlusOutlined, SolutionOutlined, TableOutlined, TeamOutlined } from '@ant-design/icons';
import { useAuth } from '../../context/AuthContext';
import { clienteApi } from '../../api/atencioncliente';
import { ESTADOS_PIPELINE_CLIENTE, ETIQUETA_ESTADO_PIPELINE_CLIENTE, TIPOS_CONTACTO } from '../../types/atencioncliente';
import type { Cliente, ClienteRequest, EstadoPipelineCliente } from '../../types/atencioncliente';

const { Title, Text } = Typography;

const COLOR_ESTADO: Record<EstadoPipelineCliente, string> = {
  CONTACTO_INICIADO: '#8c8c8c',
  CONTACTO_CALIFICADO: '#1677ff',
  OPCIONES_ENVIADAS: '#13c2c2',
  CITA_AGENDADA: '#faad14',
  VISITA_REALIZADA: '#52c41a',
  VISITAR_OTRAS_PROPIEDADES: '#7cb305',
  EN_NEGOCIACION: '#fa8c16',
  ESPERANDO_FIRMA_ESCRITURA: '#722ed1',
  EN_REGISTRO: '#2f54eb',
  NEGOCIO_CERRADO_GANADO: '#389e0d',
  NEGOCIO_PERDIDO: '#cf1322',
};

export default function PipelineClientesPage() {
  const navigate = useNavigate();
  const { usuario } = useAuth();
  const [clientes, setClientes] = useState<Cliente[]>([]);
  const [loading, setLoading] = useState(false);
  const [vista, setVista] = useState<'pipeline' | 'lista'>('pipeline');
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [columnaSobrevolada, setColumnaSobrevolada] = useState<EstadoPipelineCliente | null>(null);
  const [form] = Form.useForm<ClienteRequest>();

  const cargarClientes = async () => {
    setLoading(true);
    try {
      const data = await clienteApi.listar();
      setClientes(data);
    } catch {
      message.error('No se pudieron cargar los clientes. Verifica que el backend esté corriendo.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarClientes();
  }, []);

  const estadisticas = useMemo(
    () => ({
      porAtender: clientes.filter((c) => !['NEGOCIO_CERRADO_GANADO', 'NEGOCIO_PERDIDO'].includes(c.estadoPipeline)).length,
      enNegociacion: clientes.filter((c) => c.estadoPipeline === 'EN_NEGOCIACION').length,
      ganados: clientes.filter((c) => c.estadoPipeline === 'NEGOCIO_CERRADO_GANADO').length,
      perdidos: clientes.filter((c) => c.estadoPipeline === 'NEGOCIO_PERDIDO').length,
    }),
    [clientes],
  );

  const clientesPorEstado = useMemo(() => {
    const mapa = new Map<EstadoPipelineCliente, Cliente[]>();
    ESTADOS_PIPELINE_CLIENTE.forEach((estado) => mapa.set(estado, []));
    clientes.forEach((cliente) => {
      mapa.get(cliente.estadoPipeline)?.push(cliente);
    });
    return mapa;
  }, [clientes]);

  const handleCrear = async (values: ClienteRequest) => {
    setGuardando(true);
    try {
      await clienteApi.crear({ ...values, asesorId: usuario?.usuarioId });
      message.success('Cliente creado correctamente');
      setModalOpen(false);
      form.resetFields();
      cargarClientes();
    } catch {
      message.error('No se pudo crear el cliente');
    } finally {
      setGuardando(false);
    }
  };

  const handleDrop = async (nuevoEstado: EstadoPipelineCliente, clienteId: string) => {
    setColumnaSobrevolada(null);
    const cliente = clientes.find((c) => c.id === clienteId);
    if (!cliente || cliente.estadoPipeline === nuevoEstado) return;

    setClientes((prev) => prev.map((c) => (c.id === clienteId ? { ...c, estadoPipeline: nuevoEstado } : c)));

    try {
      await clienteApi.cambiarEstadoPipeline(clienteId, nuevoEstado);
      message.success(`${cliente.nombre} movido a "${ETIQUETA_ESTADO_PIPELINE_CLIENTE[nuevoEstado]}"`);
    } catch {
      message.error('No se pudo actualizar el estado');
      cargarClientes();
    }
  };

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Atención al cliente
          </Title>
          <Text type="secondary">{clientes.length} clientes en el pipeline</Text>
        </div>
        <Space>
          <Segmented
            value={vista}
            onChange={(v) => setVista(v as 'pipeline' | 'lista')}
            options={[
              { label: 'Pipeline', value: 'pipeline', icon: <TeamOutlined /> },
              { label: 'Lista', value: 'lista', icon: <TableOutlined /> },
            ]}
          />
          <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalOpen(true)}>
            Nuevo cliente
          </Button>
        </Space>
      </Space>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Clientes por atender" value={estadisticas.porAtender} prefix={<SolutionOutlined />} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="En negociación" value={estadisticas.enNegociacion} styles={{ content: { color: '#fa8c16' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Negocios ganados" value={estadisticas.ganados} styles={{ content: { color: '#389e0d' } }} />
          </Card>
        </Col>
        <Col xs={12} sm={12} md={6}>
          <Card size="small">
            <Statistic title="Negocios perdidos" value={estadisticas.perdidos} styles={{ content: { color: '#8c8c8c' } }} />
          </Card>
        </Col>
      </Row>

      {vista === 'pipeline' ? (
        <div style={{ display: 'flex', gap: 16, overflowX: 'auto', paddingBottom: 16 }}>
          {ESTADOS_PIPELINE_CLIENTE.map((estado) => {
            const items = clientesPorEstado.get(estado) ?? [];
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
                  const clienteId = e.dataTransfer.getData('text/cliente-id');
                  handleDrop(estado, clienteId);
                }}
                style={{
                  minWidth: 260,
                  maxWidth: 260,
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
                    {ETIQUETA_ESTADO_PIPELINE_CLIENTE[estado]}
                  </Text>
                  <Badge count={items.length} showZero color={COLOR_ESTADO[estado]} />
                </div>
                <div style={{ padding: 10, overflowY: 'auto', flex: 1 }}>
                  {items.map((cliente) => (
                    <div
                      key={cliente.id}
                      draggable
                      onDragStart={(e) => e.dataTransfer.setData('text/cliente-id', cliente.id)}
                      onClick={() => navigate(`/atencion-cliente/${cliente.id}`)}
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
                        {cliente.nombre}
                      </Text>
                      <div style={{ fontSize: 12, color: '#888', marginTop: 2 }}>{cliente.tipoContacto}</div>
                      <div style={{ fontSize: 12, color: '#888' }}>{cliente.whatsapp ?? cliente.telefono}</div>
                    </div>
                  ))}
                  {items.length === 0 && (
                    <div style={{ textAlign: 'center', color: '#bbb', fontSize: 12, padding: '20px 0' }}>
                      Sin clientes
                    </div>
                  )}
                </div>
              </div>
            );
          })}
        </div>
      ) : (
        <Table<Cliente>
          rowKey="id"
          scroll={{ x: 'max-content' }}
          loading={loading}
          dataSource={clientes}
          onRow={(record) => ({ onClick: () => navigate(`/atencion-cliente/${record.id}`), style: { cursor: 'pointer' } })}
          columns={[
            { title: 'Nombre', dataIndex: 'nombre' },
            {
              title: 'Teléfono',
              dataIndex: 'telefono',
              render: (telefono: string) => (
                <a href={`tel:${telefono}`} onClick={(e) => e.stopPropagation()}>
                  {telefono}
                </a>
              ),
            },
            { title: 'Correo', dataIndex: 'correoElectronico' },
            { title: 'Tipo de contacto', dataIndex: 'tipoContacto' },
            {
              title: 'Estado en el pipeline',
              dataIndex: 'estadoPipeline',
              render: (estado: EstadoPipelineCliente) => (
                <Tag color={COLOR_ESTADO[estado]}>{ETIQUETA_ESTADO_PIPELINE_CLIENTE[estado]}</Tag>
              ),
            },
          ]}
        />
      )}

      <Modal
        title="Nuevo cliente"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Crear"
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleCrear}>
          <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'El nombre es obligatorio' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="telefono" label="Teléfono" rules={[{ required: true, message: 'El teléfono es obligatorio' }]}>
            <Input />
          </Form.Item>
          <Form.Item name="whatsapp" label="WhatsApp">
            <Input />
          </Form.Item>
          <Form.Item name="correoElectronico" label="Correo electrónico">
            <Input type="email" />
          </Form.Item>
          <Form.Item
            name="tipoContacto"
            label="Tipo de contacto"
            rules={[{ required: true, message: 'El tipo de contacto es obligatorio' }]}
          >
            <Select options={TIPOS_CONTACTO.map((t) => ({ value: t, label: t }))} />
          </Form.Item>
          <Form.Item name="subtipoContacto" label="Subtipo de contacto">
            <Input placeholder="Ej: nombre de la inmobiliaria aliada, del vecino, etc." />
          </Form.Item>
          <Form.Item name="numeroContacto" label="Número de contacto">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
