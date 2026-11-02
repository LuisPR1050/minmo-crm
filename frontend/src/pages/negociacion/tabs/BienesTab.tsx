import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Form, Input, Modal, Popconfirm, Select, Skeleton, Space, Tag, Typography, message } from 'antd';
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import ItemList from '../../../components/ItemList';
import { bienAsociadoApi } from '../../../api/negociacion';
import { ETIQUETA_TIPO_BIEN_ASOCIADO, TIPOS_BIEN_ASOCIADO } from '../../../types/negociacion';
import type { BienAsociado, BienAsociadoRequest, TipoBienAsociado } from '../../../types/negociacion';

const { Text } = Typography;

interface Props {
  negociacionId: string;
}

interface FormValues {
  tipo: TipoBienAsociado;
  matriculaInmobiliaria?: string;
  referenciaCatastral?: string;
}

export default function BienesTab({ negociacionId }: Props) {
  const [bienes, setBienes] = useState<BienAsociado[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [bienEditando, setBienEditando] = useState<BienAsociado | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [form] = Form.useForm<FormValues>();

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await bienAsociadoApi.listarPorNegociacion(negociacionId);
      setBienes(data);
    } catch {
      message.error('No se pudieron cargar los bienes asociados');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const abrirModalCrear = () => {
    setBienEditando(null);
    form.resetFields();
    setModalAbierto(true);
  };

  const abrirModalEditar = (bien: BienAsociado) => {
    setBienEditando(bien);
    form.setFieldsValue(bien);
    setModalAbierto(true);
  };

  const handleGuardar = async (values: FormValues) => {
    const request: BienAsociadoRequest = {
      negociacionId,
      tipo: values.tipo,
      matriculaInmobiliaria: values.matriculaInmobiliaria,
      referenciaCatastral: values.referenciaCatastral,
    };
    setGuardando(true);
    try {
      if (bienEditando) {
        await bienAsociadoApi.actualizar(bienEditando.id, request);
        message.success('Bien asociado actualizado correctamente');
      } else {
        await bienAsociadoApi.crear(request);
        message.success('Bien asociado agregado correctamente');
      }
      setModalAbierto(false);
      form.resetFields();
      cargar();
    } catch {
      message.error(bienEditando ? 'No se pudo actualizar el bien asociado' : 'No se pudo agregar el bien asociado');
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await bienAsociadoApi.eliminar(id);
      message.success('Bien asociado eliminado correctamente');
      cargar();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar el bien asociado');
    } finally {
      setEliminandoId(null);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <div style={{ maxWidth: 700 }}>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text type="secondary">Parqueaderos, depósitos u otros inmuebles incluidos en este negocio.</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModalCrear}>
          Agregar bien
        </Button>
      </Space>

      <ItemList
        items={bienes}
        rowKey={(bien) => bien.id}
        emptyDescription="Sin bienes asociados registrados"
        renderItem={(bien) => (
          <Space style={{ width: '100%', justifyContent: 'space-between' }} align="start">
            <Space orientation="vertical" size={0}>
              <Space>
                <Tag color="blue">{ETIQUETA_TIPO_BIEN_ASOCIADO[bien.tipo]}</Tag>
              </Space>
              <Text type="secondary">
                {bien.matriculaInmobiliaria ? `Matrícula: ${bien.matriculaInmobiliaria}` : 'Sin matrícula'}
                {bien.referenciaCatastral ? ` · Ref. catastral: ${bien.referenciaCatastral}` : ''}
              </Text>
            </Space>
            <Space>
              <Button size="small" icon={<EditOutlined />} onClick={() => abrirModalEditar(bien)} />
              <Popconfirm
                title="Eliminar bien asociado"
                description="¿Seguro que quieres eliminar este bien asociado?"
                okText="Eliminar"
                cancelText="Cancelar"
                okButtonProps={{ danger: true }}
                onConfirm={() => handleEliminar(bien.id)}
              >
                <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === bien.id} />
              </Popconfirm>
            </Space>
          </Space>
        )}
      />

      <Modal
        title={bienEditando ? 'Editar bien asociado' : 'Agregar bien asociado'}
        open={modalAbierto}
        onCancel={() => {
          setModalAbierto(false);
          setBienEditando(null);
        }}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText={bienEditando ? 'Guardar' : 'Agregar'}
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleGuardar}>
          <Form.Item name="tipo" label="Tipo" rules={[{ required: true, message: 'Selecciona el tipo' }]}>
            <Select options={TIPOS_BIEN_ASOCIADO.map((t) => ({ value: t, label: ETIQUETA_TIPO_BIEN_ASOCIADO[t] }))} />
          </Form.Item>
          <Form.Item name="matriculaInmobiliaria" label="Matrícula inmobiliaria">
            <Input />
          </Form.Item>
          <Form.Item name="referenciaCatastral" label="Referencia catastral">
            <Input />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
