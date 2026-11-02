import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Link } from 'react-router-dom';
import { Button, Checkbox, Drawer, Form, message, Modal, Popconfirm, Select, Space, Table, Tabs, Tag, Typography } from 'antd';
import { DeleteOutlined, PlusOutlined } from '@ant-design/icons';
import { publicacionInmuebleApi } from '../../api/marketing';
import { inmuebleApi } from '../../api/captacion';
import { ETIQUETA_ESTADO_PUBLICACION, TIPOS_OPERACION_PUBLICACION } from '../../types/marketing';
import type { EstadoPublicacion, PublicacionInmueble, PublicacionInmuebleRequest, TipoOperacionPublicacion } from '../../types/marketing';
import type { Inmueble } from '../../types/captacion';
import GeneralTab from './publicacion/GeneralTab';
import PortalesTab from './publicacion/PortalesTab';
import FotosTab from './publicacion/FotosTab';
import HistorialTab from './publicacion/HistorialTab';

const { Text } = Typography;

const COLOR_ESTADO: Record<EstadoPublicacion, string> = {
  BORRADOR: 'default',
  ACTIVA: 'green',
  PAUSADA: 'orange',
  RETIRADA: 'red',
  VENCIDA: 'red',
};

export default function PublicacionesTab() {
  const [publicaciones, setPublicaciones] = useState<PublicacionInmueble[]>([]);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [publicacionSeleccionada, setPublicacionSeleccionada] = useState<PublicacionInmueble | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [form] = Form.useForm<PublicacionInmuebleRequest>();

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [publicacionesData, inmueblesData] = await Promise.all([
        publicacionInmuebleApi.listar(),
        inmuebleApi.listar(),
      ]);
      setPublicaciones(publicacionesData);
      setInmuebles(inmueblesData);
    } catch {
      message.error('No se pudieron cargar las publicaciones. Verifica que el backend esté corriendo.');
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
    setModalOpen(true);
  };

  const handleCrear = async (values: PublicacionInmuebleRequest) => {
    setGuardando(true);
    try {
      await publicacionInmuebleApi.crear(values);
      message.success('Publicación creada correctamente');
      setModalOpen(false);
      form.resetFields();
      cargarDatos();
    } catch {
      message.error('No se pudo crear la publicación');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambioEnDrawer = (actualizada: PublicacionInmueble) => {
    setPublicacionSeleccionada(actualizada);
    setPublicaciones((prev) => prev.map((p) => (p.id === actualizada.id ? actualizada : p)));
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await publicacionInmuebleApi.eliminar(id);
      message.success('Publicación eliminada correctamente');
      setPublicaciones((prev) => prev.filter((p) => p.id !== id));
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar la publicación');
    } finally {
      setEliminandoId(null);
    }
  };

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text />
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModal}>
          Nueva publicación
        </Button>
      </Space>

      <Table<PublicacionInmueble>
        rowKey="id"
        scroll={{ x: 'max-content' }}
        loading={loading}
        dataSource={publicaciones}
        onRow={(record) => ({ onClick: () => setPublicacionSeleccionada(record), style: { cursor: 'pointer' } })}
        columns={[
          {
            title: 'Inmueble',
            dataIndex: 'inmuebleCodigo',
            render: (codigo: string, record: PublicacionInmueble) => (
              <Link to={`/captacion/inmuebles/${record.inmuebleId}`} onClick={(e) => e.stopPropagation()}>
                {codigo}
              </Link>
            ),
          },
          { title: 'Tipo de operación', dataIndex: 'tipoOperacion' },
          { title: 'Destacada', dataIndex: 'destacada', render: (v: boolean) => (v ? <Tag color="gold">Sí</Tag> : 'No') },
          {
            title: 'Estado',
            dataIndex: 'estado',
            render: (estado: EstadoPublicacion) => <Tag color={COLOR_ESTADO[estado]}>{ETIQUETA_ESTADO_PUBLICACION[estado]}</Tag>,
          },
          {
            title: 'Acciones',
            key: 'acciones',
            render: (_, record) => (
              <Popconfirm
                title="Eliminar publicación"
                description="¿Seguro que quieres eliminar esta publicación? Esta acción no se puede deshacer."
                okText="Eliminar"
                cancelText="Cancelar"
                okButtonProps={{ danger: true }}
                onConfirm={(e) => {
                  e?.stopPropagation();
                  handleEliminar(record.id);
                }}
              >
                <Button
                  size="small"
                  danger
                  icon={<DeleteOutlined />}
                  loading={eliminandoId === record.id}
                  onClick={(e) => e.stopPropagation()}
                />
              </Popconfirm>
            ),
          },
        ]}
      />

      <Modal
        title="Nueva publicación"
        open={modalOpen}
        onCancel={() => setModalOpen(false)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Crear"
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleCrear} initialValues={{ mostrarPrecio: true, mostrarContacto: true }}>
          <Form.Item
            name="inmuebleId"
            label="Inmueble"
            rules={[{ required: true, message: 'Selecciona un inmueble' }]}
          >
            <Select
              showSearch
              placeholder="Selecciona un inmueble"
              optionFilterProp="label"
              options={inmuebles.map((i) => ({ value: i.id, label: `${i.codigo} — ${i.ciudad ?? ''}` }))}
            />
          </Form.Item>
          <Form.Item
            name="tipoOperacion"
            label="Tipo de operación"
            rules={[{ required: true, message: 'Selecciona el tipo de operación' }]}
          >
            <Select
              options={TIPOS_OPERACION_PUBLICACION.map((t: TipoOperacionPublicacion) => ({ value: t, label: t }))}
            />
          </Form.Item>
          <Form.Item name="mostrarPrecio" valuePropName="checked">
            <Checkbox>Mostrar precio</Checkbox>
          </Form.Item>
          <Form.Item name="mostrarContacto" valuePropName="checked">
            <Checkbox>Mostrar contacto</Checkbox>
          </Form.Item>
          <Form.Item name="mostrarCorreo" valuePropName="checked">
            <Checkbox>Mostrar correo</Checkbox>
          </Form.Item>
          <Form.Item name="comisionCompartida" valuePropName="checked">
            <Checkbox>Comisión compartida</Checkbox>
          </Form.Item>
          <Form.Item name="destacada" valuePropName="checked">
            <Checkbox>Publicación destacada</Checkbox>
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title={`Publicación — ${publicacionSeleccionada?.inmuebleCodigo ?? ''}`}
        open={!!publicacionSeleccionada}
        onClose={() => setPublicacionSeleccionada(null)}
        size={640}
      >
        {publicacionSeleccionada && (
          <Tabs
            defaultActiveKey="general"
            items={[
              {
                key: 'general',
                label: 'General',
                children: <GeneralTab publicacion={publicacionSeleccionada} onCambio={handleCambioEnDrawer} />,
              },
              {
                key: 'portales',
                label: 'Portales',
                children: <PortalesTab publicacionInmuebleId={publicacionSeleccionada.id} />,
              },
              { key: 'fotos', label: 'Fotos', children: <FotosTab publicacionInmuebleId={publicacionSeleccionada.id} /> },
              { key: 'historial', label: 'Historial', children: <HistorialTab publicacionInmuebleId={publicacionSeleccionada.id} /> },
            ]}
          />
        )}
      </Drawer>
    </div>
  );
}
