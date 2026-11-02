import { useEffect, useMemo, useState } from 'react';
import { isAxiosError } from 'axios';
import {
  Avatar,
  Button,
  Card,
  Col,
  Form,
  Input,
  message,
  Modal,
  Popconfirm,
  Row,
  Select,
  Space,
  Statistic,
  Switch,
  Table,
  Tag,
  Tooltip,
  Typography,
} from 'antd';
import { CrownOutlined, DeleteOutlined, EditOutlined, PlusOutlined, SearchOutlined, TeamOutlined, UserOutlined } from '@ant-design/icons';
import { usuarioApi } from '../../api/auth';
import { useAuth } from '../../context/AuthContext';
import { colorDesdeTexto, obtenerIniciales } from '../../utils/avatar';
import type { EditarUsuarioRequest, Usuario, UsuarioRequest } from '../../types/auth';

const { Title, Text } = Typography;

export default function UsuariosPage() {
  const { usuario: usuarioActual } = useAuth();
  const [usuarios, setUsuarios] = useState<Usuario[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [actualizandoId, setActualizandoId] = useState<string | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [busqueda, setBusqueda] = useState('');
  const [usuarioEditando, setUsuarioEditando] = useState<Usuario | null>(null);
  const [form] = Form.useForm<UsuarioRequest>();

  const cargarUsuarios = async () => {
    setLoading(true);
    try {
      const data = await usuarioApi.listar();
      setUsuarios(data);
    } catch {
      message.error('No se pudieron cargar los usuarios');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarUsuarios();
  }, []);

  const abrirModalCrear = () => {
    setUsuarioEditando(null);
    form.resetFields();
    setModalOpen(true);
  };

  const abrirModalEditar = (usuarioFila: Usuario) => {
    setUsuarioEditando(usuarioFila);
    form.setFieldsValue(usuarioFila);
    setModalOpen(true);
  };

  const handleGuardar = async (values: UsuarioRequest) => {
    setGuardando(true);
    try {
      if (usuarioEditando) {
        const request: EditarUsuarioRequest = { nombre: values.nombre, email: values.email, telefono: values.telefono, rol: values.rol };
        const actualizado = await usuarioApi.actualizar(usuarioEditando.id, request);
        setUsuarios((prev) => prev.map((u) => (u.id === actualizado.id ? actualizado : u)));
        message.success('Usuario actualizado correctamente');
      } else {
        await usuarioApi.crear(values);
        message.success('Usuario creado correctamente');
        cargarUsuarios();
      }
      setModalOpen(false);
      form.resetFields();
    } catch {
      message.error(
        usuarioEditando
          ? 'No se pudo actualizar el usuario. Verifica que el correo no esté ya registrado.'
          : 'No se pudo crear el usuario. Verifica que el correo no esté ya registrado.',
      );
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await usuarioApi.eliminar(id);
      message.success('Usuario eliminado correctamente');
      setUsuarios((prev) => prev.filter((u) => u.id !== id));
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar el usuario');
    } finally {
      setEliminandoId(null);
    }
  };

  const handleCambiarEstado = async (id: string, activo: boolean) => {
    setActualizandoId(id);
    try {
      const actualizado = await usuarioApi.cambiarEstado(id, activo);
      setUsuarios((prev) => prev.map((u) => (u.id === id ? actualizado : u)));
      message.success(activo ? 'Usuario activado' : 'Usuario desactivado');
    } catch {
      message.error('No se pudo cambiar el estado del usuario');
    } finally {
      setActualizandoId(null);
    }
  };

  const admins = useMemo(() => usuarios.filter((u) => u.rol === 'ADMIN').length, [usuarios]);
  const activos = useMemo(() => usuarios.filter((u) => u.activo).length, [usuarios]);

  const usuariosFiltrados = useMemo(() => {
    const termino = busqueda.trim().toLowerCase();
    if (!termino) return usuarios;
    return usuarios.filter((u) => u.nombre.toLowerCase().includes(termino) || u.email.toLowerCase().includes(termino));
  }, [usuarios, busqueda]);

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} align="start" wrap>
        <div>
          <Title level={3} style={{ margin: 0 }}>
            Usuarios
          </Title>
          <Text type="secondary">{usuarios.length} usuarios registrados</Text>
        </div>
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModalCrear}>
          Nuevo usuario
        </Button>
      </Space>

      <Row gutter={[16, 16]} style={{ marginBottom: 16 }}>
        <Col xs={24} sm={12} md={8}>
          <Card size="small">
            <Statistic title="Total usuarios" value={usuarios.length} prefix={<TeamOutlined />} styles={{ content: { color: '#0b4f6c' } }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card size="small">
            <Statistic title="Administradores" value={admins} prefix={<CrownOutlined />} styles={{ content: { color: '#722ed1' } }} />
          </Card>
        </Col>
        <Col xs={24} sm={12} md={8}>
          <Card size="small">
            <Statistic title="Activos" value={activos} styles={{ content: { color: '#389e0d' } }} />
          </Card>
        </Col>
      </Row>

      <Input
        allowClear
        placeholder="Buscar por nombre o correo..."
        prefix={<SearchOutlined />}
        value={busqueda}
        onChange={(e) => setBusqueda(e.target.value)}
        style={{ marginBottom: 16, maxWidth: 360 }}
      />

      <Table<Usuario>
        rowKey="id"
        scroll={{ x: 'max-content' }}
        loading={loading}
        dataSource={usuariosFiltrados}
        columns={[
          {
            title: 'Usuario',
            key: 'nombre',
            render: (_, u) => (
              <Space>
                <Avatar style={{ backgroundColor: colorDesdeTexto(u.nombre) }}>{obtenerIniciales(u.nombre)}</Avatar>
                <div>
                  <div style={{ fontWeight: 500 }}>{u.nombre}</div>
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    {u.email}
                  </Text>
                </div>
              </Space>
            ),
          },
          {
            title: 'Rol',
            dataIndex: 'rol',
            render: (rol: string) => (
              <Tag icon={rol === 'ADMIN' ? <CrownOutlined /> : <UserOutlined />} color={rol === 'ADMIN' ? 'purple' : 'blue'}>
                {rol === 'ADMIN' ? 'Administrador' : 'Asesor'}
              </Tag>
            ),
          },
          {
            title: 'Miembro desde',
            dataIndex: 'createdAt',
            render: (v: string) => new Date(v).toLocaleDateString('es-CO'),
          },
          {
            title: 'Activo',
            key: 'activo',
            render: (_, u) => {
              const esUsuarioActual = u.id === usuarioActual?.usuarioId;
              const switchNode = (
                <Switch
                  checked={u.activo}
                  loading={actualizandoId === u.id}
                  disabled={esUsuarioActual}
                  onChange={(valor) => handleCambiarEstado(u.id, valor)}
                />
              );
              return esUsuarioActual ? (
                <Tooltip title="No puedes desactivar tu propia cuenta">{switchNode}</Tooltip>
              ) : (
                switchNode
              );
            },
          },
          {
            title: 'Acciones',
            key: 'acciones',
            render: (_, u) => {
              const esUsuarioActual = u.id === usuarioActual?.usuarioId;
              return (
                <Space>
                  <Button size="small" icon={<EditOutlined />} onClick={() => abrirModalEditar(u)} />
                  <Tooltip title={esUsuarioActual ? 'No puedes eliminar tu propio usuario' : ''}>
                    <Popconfirm
                      title="Eliminar usuario"
                      description="¿Seguro que quieres eliminar este usuario? Esta acción no se puede deshacer."
                      okText="Eliminar"
                      cancelText="Cancelar"
                      okButtonProps={{ danger: true }}
                      disabled={esUsuarioActual}
                      onConfirm={() => handleEliminar(u.id)}
                    >
                      <Button size="small" danger icon={<DeleteOutlined />} disabled={esUsuarioActual} loading={eliminandoId === u.id} />
                    </Popconfirm>
                  </Tooltip>
                </Space>
              );
            },
          },
        ]}
      />

      <Modal
        title={usuarioEditando ? 'Editar usuario' : 'Nuevo usuario'}
        open={modalOpen}
        onCancel={() => {
          setModalOpen(false);
          setUsuarioEditando(null);
        }}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText={usuarioEditando ? 'Guardar' : 'Crear'}
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleGuardar} initialValues={{ rol: 'ASESOR' }}>
          <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'El nombre es obligatorio' }]}>
            <Input />
          </Form.Item>
          <Form.Item
            name="email"
            label="Correo"
            rules={[{ required: true, message: 'El correo es obligatorio' }, { type: 'email', message: 'Correo no valido' }]}
          >
            <Input />
          </Form.Item>
          <Form.Item
            name="telefono"
            label="Teléfono"
            tooltip="Se usa para enviarle notificaciones de visitas por SMS"
            rules={[{ pattern: /^\+?[0-9]{7,15}$/, message: 'Ingresa un teléfono válido (solo números)' }]}
          >
            <Input placeholder="3001234567" />
          </Form.Item>
          {!usuarioEditando && (
            <Form.Item
              name="password"
              label="Contraseña"
              rules={[{ required: true, message: 'La contraseña es obligatoria' }]}
            >
              <Input.Password />
            </Form.Item>
          )}
          <Form.Item name="rol" label="Rol" rules={[{ required: true, message: 'Selecciona un rol' }]}>
            <Select
              options={[
                { value: 'ASESOR', label: 'Asesor' },
                { value: 'ADMIN', label: 'Administrador' },
              ]}
            />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
