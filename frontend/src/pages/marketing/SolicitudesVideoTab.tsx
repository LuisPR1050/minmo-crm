import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Link } from 'react-router-dom';
import { Button, Drawer, Dropdown, Form, message, Modal, Popconfirm, Select, Space, Table, Tabs, Tag, Typography } from 'antd';
import type { MenuProps } from 'antd';
import { DeleteOutlined, DownOutlined, PictureOutlined, PlayCircleOutlined, PlusOutlined, VideoCameraAddOutlined } from '@ant-design/icons';
import { solicitudVideoApi } from '../../api/marketing';
import { inmuebleApi } from '../../api/captacion';
import { abrirDocumentoProtegido } from '../../utils/archivosProtegidos';
import { ETIQUETA_ESTADO_SOLICITUD_VIDEO, OBJETIVOS_VIDEO_MARKETING } from '../../types/marketing';
import type { EstadoSolicitudVideo, SolicitudVideoMarketing, SolicitudVideoMarketingRequest } from '../../types/marketing';
import type { Inmueble } from '../../types/captacion';
import GeneralTab from './solicitud-video/GeneralTab';
import SegmentacionTab from './solicitud-video/SegmentacionTab';
import MaterialTab from './solicitud-video/MaterialTab';
import CopyTab from './solicitud-video/CopyTab';

const { Text } = Typography;

const COLOR_ESTADO: Record<EstadoSolicitudVideo, string> = {
  BORRADOR: 'default',
  EN_PROCESO: 'blue',
  GENERADO: 'green',
  FALLIDO: 'red',
  PUBLICADO: 'purple',
  DESCARTADO: 'red',
};

export default function SolicitudesVideoTab() {
  const [solicitudes, setSolicitudes] = useState<SolicitudVideoMarketing[]>([]);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalOpen, setModalOpen] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [seleccionada, setSeleccionada] = useState<SolicitudVideoMarketing | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [generandoVideoId, setGenerandoVideoId] = useState<string | null>(null);
  const [tabActivoDrawer, setTabActivoDrawer] = useState('general');
  const [form] = Form.useForm<{ inmuebleId: string; objetivoPublicacion: SolicitudVideoMarketingRequest['objetivoPublicacion'] }>();

  const cargarDatos = async () => {
    setLoading(true);
    try {
      const [solicitudesData, inmueblesData] = await Promise.all([solicitudVideoApi.listar(), inmuebleApi.listar()]);
      setSolicitudes(solicitudesData);
      setInmuebles(inmueblesData);
    } catch {
      message.error('No se pudieron cargar las solicitudes de video. Verifica que el backend esté corriendo.');
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

  const handleCrear = async (values: SolicitudVideoMarketingRequest) => {
    setGuardando(true);
    try {
      await solicitudVideoApi.crear(values);
      message.success('Solicitud de video creada correctamente');
      setModalOpen(false);
      form.resetFields();
      cargarDatos();
    } catch {
      message.error('No se pudo crear la solicitud de video');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambioEnDrawer = (actualizada: SolicitudVideoMarketing) => {
    setSeleccionada(actualizada);
    setSolicitudes((prev) => prev.map((s) => (s.id === actualizada.id ? actualizada : s)));
  };

  const handleGenerarVideo = async (id: string) => {
    setGenerandoVideoId(id);
    try {
      const actualizada = await solicitudVideoApi.generarVideo(id);
      setSolicitudes((prev) => prev.map((s) => (s.id === id ? actualizada : s)));
      if (seleccionada?.id === id) setSeleccionada(actualizada);
      message.success('Video generado correctamente');
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo generar el video. Verifica que la solicitud tenga fotos o videos seleccionados.');
    } finally {
      setGenerandoVideoId(null);
    }
  };

  const abrirConMaterial = (record: SolicitudVideoMarketing) => {
    setTabActivoDrawer('material');
    setSeleccionada(record);
  };

  const handleVerVideoGenerado = (record: SolicitudVideoMarketing) => {
    if (!record.videoGeneradoUrl) return;
    if (record.videoGeneradoUrl.startsWith('/uploads/')) {
      abrirDocumentoProtegido(record.videoGeneradoUrl).catch(() => message.error('No se pudo abrir el video'));
    } else {
      window.open(record.videoGeneradoUrl, '_blank', 'noreferrer');
    }
  };

  const menuGenerarVideo = (record: SolicitudVideoMarketing): MenuProps['items'] => [
    {
      key: 'generar',
      icon: <VideoCameraAddOutlined />,
      label: record.videoGeneradoUrl ? 'Regenerar ahora' : 'Generar ahora',
      onClick: () => handleGenerarVideo(record.id),
    },
    {
      key: 'material',
      icon: <PictureOutlined />,
      label: 'Elegir material primero',
      onClick: () => abrirConMaterial(record),
    },
    ...(record.videoGeneradoUrl
      ? [
          {
            key: 'ver',
            icon: <PlayCircleOutlined />,
            label: 'Ver video generado',
            onClick: () => handleVerVideoGenerado(record),
          },
        ]
      : []),
  ];

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await solicitudVideoApi.eliminar(id);
      message.success('Solicitud de video eliminada correctamente');
      setSolicitudes((prev) => prev.filter((s) => s.id !== id));
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar la solicitud de video');
    } finally {
      setEliminandoId(null);
    }
  };

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text />
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModal}>
          Nueva solicitud de video
        </Button>
      </Space>

      <Table<SolicitudVideoMarketing>
        rowKey="id"
        scroll={{ x: 'max-content' }}
        loading={loading}
        dataSource={solicitudes}
        onRow={(record) => ({
          onClick: () => {
            setTabActivoDrawer('general');
            setSeleccionada(record);
          },
          style: { cursor: 'pointer' },
        })}
        columns={[
          {
            title: 'Inmueble',
            dataIndex: 'inmuebleCodigo',
            render: (codigo: string, record: SolicitudVideoMarketing) => (
              <Link to={`/captacion/inmuebles/${record.inmuebleId}`} onClick={(e) => e.stopPropagation()}>
                {codigo}
              </Link>
            ),
          },
          { title: 'Objetivo', dataIndex: 'objetivoPublicacion' },
          { title: 'Estilo', dataIndex: 'estiloContenido' },
          {
            title: 'Estado',
            dataIndex: 'estado',
            render: (estado: EstadoSolicitudVideo) => <Tag color={COLOR_ESTADO[estado]}>{ETIQUETA_ESTADO_SOLICITUD_VIDEO[estado]}</Tag>,
          },
          {
            title: 'Video',
            key: 'video',
            render: (_, record) =>
              record.videoGeneradoUrl ? <Tag color="green">Generado</Tag> : <Tag>Sin generar</Tag>,
          },
          {
            title: 'Acciones',
            key: 'acciones',
            render: (_, record) => (
              <Space>
                <Dropdown menu={{ items: menuGenerarVideo(record) }} trigger={['click']}>
                  <Button
                    size="small"
                    type="primary"
                    icon={<VideoCameraAddOutlined />}
                    loading={generandoVideoId === record.id}
                    onClick={(e) => e.stopPropagation()}
                  >
                    {record.videoGeneradoUrl ? 'Regenerar video' : 'Generar video'} <DownOutlined />
                  </Button>
                </Dropdown>
                <Popconfirm
                  title="Eliminar solicitud de video"
                  description="¿Seguro que quieres eliminar esta solicitud de video? Esta acción no se puede deshacer."
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
              </Space>
            ),
          },
        ]}
      />

      <Modal
        title="Nueva solicitud de video"
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
              options={inmuebles.map((i) => ({ value: i.id, label: `${i.codigo} — ${i.ciudad ?? ''}` }))}
            />
          </Form.Item>
          <Form.Item
            name="objetivoPublicacion"
            label="Objetivo de la publicación"
            rules={[{ required: true, message: 'Selecciona el objetivo' }]}
          >
            <Select options={OBJETIVOS_VIDEO_MARKETING.map((o) => ({ value: o, label: o }))} />
          </Form.Item>
        </Form>
      </Modal>

      <Drawer
        title={`Solicitud de video — ${seleccionada?.inmuebleCodigo ?? ''}`}
        open={!!seleccionada}
        onClose={() => {
          setSeleccionada(null);
          setTabActivoDrawer('general');
        }}
        size={640}
      >
        {seleccionada && (
          <Tabs
            activeKey={tabActivoDrawer}
            onChange={setTabActivoDrawer}
            items={[
              { key: 'general', label: 'General', children: <GeneralTab solicitud={seleccionada} onCambio={handleCambioEnDrawer} /> },
              { key: 'segmentacion', label: 'Segmentación', children: <SegmentacionTab solicitudVideoId={seleccionada.id} /> },
              { key: 'material', label: 'Material visual', children: <MaterialTab solicitud={seleccionada} onCambio={handleCambioEnDrawer} /> },
              { key: 'copy', label: 'Copy (IA)', children: <CopyTab solicitud={seleccionada} onCambio={handleCambioEnDrawer} /> },
            ]}
          />
        )}
      </Drawer>
    </div>
  );
}
