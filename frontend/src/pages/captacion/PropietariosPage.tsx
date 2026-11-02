import { useEffect, useMemo, useState } from 'react';
import { isAxiosError } from 'axios';
import { Link } from 'react-router-dom';
import { Avatar, Button, Card, Col, Form, Input, message, Modal, Popconfirm, Row, Select, Space, Statistic, Table, Tag, Typography } from 'antd';
import InputMoneda from '../../components/InputMoneda';
import {
  DeleteOutlined,
  EditOutlined,
  GiftOutlined,
  MailOutlined,
  PhoneOutlined,
  PlusOutlined,
  SearchOutlined,
  TeamOutlined,
  WhatsAppOutlined,
} from '@ant-design/icons';
import { inmuebleApi, propietarioApi } from '../../api/captacion';
import { colorDesdeTexto, obtenerIniciales } from '../../utils/avatar';
import type { Inmueble, Propietario, PropietarioRequest } from '../../types/captacion';
import { TIPOS_CONTACTO_PROPIETARIO, TIPOS_INCENTIVO } from '../../types/captacion';

const { Title, Text } = Typography;

export default function PropietariosPage() {
  const [propietarios, setPropietarios] = useState<Propietario[]>([]);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [busqueda, setBusqueda] = useState('');
  const [propietarioEditando, setPropietarioEditando] = useState<Propietario | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [form] = Form.useForm<PropietarioRequest>();

  const cargarPropietarios = async () => {
    setLoading(true);
    try {
      const [propietariosData, inmueblesData] = await Promise.all([propietarioApi.listar(), inmuebleApi.listar()]);
      setPropietarios(propietariosData);
      setInmuebles(inmueblesData);
    } catch {
      message.error('No se pudieron cargar los propietarios. Verifica que el backend esté corriendo.');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarPropietarios();
  }, []);

  const abrirModalCrear = () => {
    setPropietarioEditando(null);
    form.resetFields();
    setModalOpen(true);
  };

  const abrirModalEditar = (propietario: Propietario) => {
    setPropietarioEditando(propietario);
    form.setFieldsValue(propietario);
    setModalOpen(true);
  };

  const handleGuardar = async (values: PropietarioRequest) => {
    setGuardando(true);
    try {
      if (propietarioEditando) {
        await propietarioApi.actualizar(propietarioEditando.id, values);
        message.success('Propietario actualizado correctamente');
      } else {
        await propietarioApi.crear(values);
        message.success('Propietario creado correctamente');
      }
      setModalOpen(false);
      form.resetFields();
      cargarPropietarios();
    } catch {
      message.error(propietarioEditando ? 'No se pudo actualizar el propietario' : 'No se pudo crear el propietario');
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await propietarioApi.eliminar(id);
      message.success('Propietario eliminado correctamente');
      cargarPropietarios();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar el propietario');
    } finally {
      setEliminandoId(null);
    }
  };

  const conIncentivo = useMemo(() => propietarios.filter((p) => p.tipoIncentivo || p.valorIncentivo != null).length, [propietarios]);

  const propietariosFiltrados = useMemo(() => {
    const termino = busqueda.trim().toLowerCase();
    if (!termino) return propietarios;
    return propietarios.filter(
      (p) =>
        p.nombre.toLowerCase().includes(termino) ||
        p.telefonoPrincipal?.toLowerCase().includes(termino) ||
        p.correoElectronico?.toLowerCase().includes(termino),
    );
  }, [propietarios, busqueda]);

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} align="start" wrap>
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Propietarios
          </Title>
          <Text type="secondary">{propietarios.length} propietarios registrados</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModalCrear}>
          Nuevo propietario
        </Button>
      </Space>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} md={8}>
          <Card size="small">
            <Statistic title="Total propietarios" value={propietarios.length} prefix={<TeamOutlined />} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card size="small">
            <Statistic title="Con incentivo pactado" value={conIncentivo} prefix={<GiftOutlined />} styles={{ content: { color: '#7cb305' } }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card size="small">
            <Statistic
              title="Con WhatsApp registrado"
              value={propietarios.filter((p) => p.whatsapp).length}
              prefix={<WhatsAppOutlined />}
              styles={{ content: { color: '#13c2c2' } }}
            />
          </Card>
        </Col>
      </Row>

      <Input
        allowClear
        placeholder="Buscar por nombre, teléfono o correo..."
        prefix={<SearchOutlined />}
        value={busqueda}
        onChange={(e) => setBusqueda(e.target.value)}
        style={{ marginBottom: 16, maxWidth: 360 }}
      />

      <Table<Propietario>
        rowKey="id"
        scroll={{ x: 'max-content' }}
        loading={loading}
        dataSource={propietariosFiltrados}
        columns={[
          {
            title: 'Propietario',
            key: 'nombre',
            render: (_, p) => (
              <Space>
                <Avatar style={{ backgroundColor: colorDesdeTexto(p.nombre) }}>{obtenerIniciales(p.nombre)}</Avatar>
                <div>
                  <div style={{ fontWeight: 500 }}>
                    <Link to={`/captacion/propietarios/${p.id}`}>{p.nombre}</Link>
                  </div>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    Desde {new Date(p.createdAt).toLocaleDateString('es-CO')}
                  </Text>
                </div>
              </Space>
            ),
          },
          {
            title: 'Contacto',
            key: 'contacto',
            render: (_, p) => (
              <Space orientation="vertical" size={2}>
                <a href={`tel:${p.telefonoPrincipal}`}>
                  <PhoneOutlined /> {p.telefonoPrincipal}
                </a>
                {p.whatsapp && (
                  <a href={`https://wa.me/${p.whatsapp.replace(/\D/g, '')}`} target="_blank" rel="noreferrer">
                    <WhatsAppOutlined style={{ color: '#25D366' }} /> {p.whatsapp}
                  </a>
                )}
                {p.correoElectronico && (
                  <a href={`mailto:${p.correoElectronico}`}>
                    <MailOutlined /> {p.correoElectronico}
                  </a>
                )}
              </Space>
            ),
          },
          {
            title: 'Inmuebles',
            key: 'inmuebles',
            render: (_, p) => {
              const delPropietario = inmuebles.filter((i) => i.propietarioId === p.id);
              if (delPropietario.length === 0) return <Text type="secondary">—</Text>;
              return (
                <Space size={4} wrap>
                  {delPropietario.map((i) => (
                    <Link key={i.id} to={`/captacion/inmuebles/${i.id}`}>
                      <Tag color="blue">{i.codigo}</Tag>
                    </Link>
                  ))}
                </Space>
              );
            },
          },
          {
            title: 'Acciones',
            key: 'acciones',
            render: (_, p) => (
              <Space>
                <Button size="small" icon={<EditOutlined />} onClick={() => abrirModalEditar(p)} />
                <Popconfirm
                  title="Eliminar propietario"
                  description="¿Seguro que quieres eliminar este propietario? Esta acción no se puede deshacer."
                  okText="Eliminar"
                  cancelText="Cancelar"
                  okButtonProps={{ danger: true }}
                  onConfirm={() => handleEliminar(p.id)}
                >
                  <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === p.id} />
                </Popconfirm>
              </Space>
            ),
          },
        ]}
      />

      <Modal
        title={propietarioEditando ? 'Editar propietario' : 'Nuevo propietario'}
        open={modalOpen}
        onCancel={() => {
          setModalOpen(false);
          setPropietarioEditando(null);
        }}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText={propietarioEditando ? 'Guardar' : 'Crear'}
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleGuardar}>
          <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'El nombre es obligatorio' }]}>
            <Input />
          </Form.Item>
          <Form.Item
            name="telefonoPrincipal"
            label="Teléfono principal"
            rules={[{ required: true, message: 'El teléfono es obligatorio' }]}
          >
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
            <Select
              showSearch
              options={TIPOS_CONTACTO_PROPIETARIO.map((t) => ({ value: t, label: t }))}
            />
          </Form.Item>
          <Form.Item name="tipoIncentivo" label="Tipo de incentivo">
            <Select allowClear showSearch options={TIPOS_INCENTIVO.map((t) => ({ value: t, label: t }))} />
          </Form.Item>
          <Form.Item name="valorIncentivo" label="Valor del incentivo">
            <InputMoneda />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item
                name="referenciadorNombre"
                label="Nombre del referenciador"
                tooltip="Quien lo refirió, para ubicarlo y entregarle el bono o incentivo cuando se cierre la venta"
              >
                <Input placeholder="Ej: Juan Pérez" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="referenciadorTelefono" label="Teléfono del referenciador">
                <Input placeholder="Ej: 3001234567" />
              </Form.Item>
            </Col>
          </Row>
        </Form>
      </Modal>
    </div>
  );
}
