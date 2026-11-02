import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Card, Col, Empty, Form, Input, Modal, Popconfirm, Row, Select, Skeleton, Space, Tag, Typography, message } from 'antd';
import { DeleteOutlined, EditOutlined, PlusOutlined, UserOutlined } from '@ant-design/icons';
import { parteNegociacionApi } from '../../../api/negociacion';
import { ESTADOS_CIVILES, ETIQUETA_ROL_PARTE, ROLES_PARTE_NEGOCIACION, TIPOS_DOCUMENTO_IDENTIDAD } from '../../../types/negociacion';
import type { ParteNegociacion, ParteNegociacionRequest, RolParteNegociacion } from '../../../types/negociacion';

const { Text, Title } = Typography;

interface Props {
  negociacionId: string;
}

interface FormValues {
  rol: RolParteNegociacion;
  tipoDocumento?: string;
  numeroDocumento?: string;
  estadoCivil?: string;
  direccion?: string;
  correoElectronico?: string;
}

export default function PartesTab({ negociacionId }: Props) {
  const [partes, setPartes] = useState<ParteNegociacion[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [parteEditando, setParteEditando] = useState<ParteNegociacion | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [form] = Form.useForm<FormValues>();

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await parteNegociacionApi.listarPorNegociacion(negociacionId);
      setPartes(data);
    } catch {
      message.error('No se pudieron cargar las partes de la negociación');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const compradores = partes.filter((p) => p.rol === 'COMPRADOR').sort((a, b) => (a.orden ?? 0) - (b.orden ?? 0));
  const vendedores = partes.filter((p) => p.rol === 'VENDEDOR').sort((a, b) => (a.orden ?? 0) - (b.orden ?? 0));

  const abrirModalCrear = () => {
    setParteEditando(null);
    form.resetFields();
    setModalAbierto(true);
  };

  const abrirModalEditar = (parte: ParteNegociacion) => {
    setParteEditando(parte);
    form.setFieldsValue(parte);
    setModalAbierto(true);
  };

  const handleGuardar = async (values: FormValues) => {
    setGuardando(true);
    try {
      if (parteEditando) {
        const request: ParteNegociacionRequest = {
          negociacionId,
          rol: values.rol,
          orden: parteEditando.orden,
          tipoDocumento: values.tipoDocumento,
          numeroDocumento: values.numeroDocumento,
          estadoCivil: values.estadoCivil,
          direccion: values.direccion,
          correoElectronico: values.correoElectronico,
        };
        await parteNegociacionApi.actualizar(parteEditando.id, request);
        message.success('Parte actualizada correctamente');
      } else {
        const yaExistentes = partes.filter((p) => p.rol === values.rol).length;
        const request: ParteNegociacionRequest = {
          negociacionId,
          rol: values.rol,
          orden: yaExistentes + 1,
          tipoDocumento: values.tipoDocumento,
          numeroDocumento: values.numeroDocumento,
          estadoCivil: values.estadoCivil,
          direccion: values.direccion,
          correoElectronico: values.correoElectronico,
        };
        await parteNegociacionApi.crear(request);
        message.success('Parte agregada correctamente');
      }
      setModalAbierto(false);
      form.resetFields();
      cargar();
    } catch {
      message.error(parteEditando ? 'No se pudo actualizar la parte' : 'No se pudo agregar la parte');
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await parteNegociacionApi.eliminar(id);
      message.success('Parte eliminada correctamente');
      cargar();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar la parte');
    } finally {
      setEliminandoId(null);
    }
  };

  const renderParte = (parte: ParteNegociacion, index: number) => (
    <Card key={parte.id} size="small" style={{ marginBottom: 12 }}>
      <Space align="start" style={{ width: '100%', justifyContent: 'space-between' }}>
        <Space align="start">
          <UserOutlined style={{ fontSize: 18, marginTop: 4 }} />
          <div>
            <Text strong>
              {ETIQUETA_ROL_PARTE[parte.rol]} {index + 1}
            </Text>
            <div>
              <Text type="secondary">
                {parte.tipoDocumento ?? 'Documento no registrado'} {parte.numeroDocumento ?? ''}
              </Text>
            </div>
            {parte.estadoCivil && (
              <div>
                <Text type="secondary">Estado civil: {parte.estadoCivil}</Text>
              </div>
            )}
            {parte.direccion && (
              <div>
                <Text type="secondary">Dirección: {parte.direccion}</Text>
              </div>
            )}
            {parte.correoElectronico && (
              <div>
                <Text type="secondary">{parte.correoElectronico}</Text>
              </div>
            )}
          </div>
        </Space>
        <Space>
          <Button size="small" icon={<EditOutlined />} onClick={() => abrirModalEditar(parte)} />
          <Popconfirm
            title="Eliminar parte"
            description="¿Seguro que quieres eliminar esta parte de la negociación?"
            okText="Eliminar"
            cancelText="Cancelar"
            okButtonProps={{ danger: true }}
            onConfirm={() => handleEliminar(parte.id)}
          >
            <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === parte.id} />
          </Popconfirm>
        </Space>
      </Space>
    </Card>
  );

  if (loading) return <Skeleton active />;

  return (
    <div>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text type="secondary">Registra a todos los compradores y vendedores que intervienen en el negocio.</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModalCrear}>
          Agregar parte
        </Button>
      </Space>

      <Row gutter={24}>
        <Col xs={24} sm={12}>
          <Title level={5}>
            Compradores <Tag color="blue">{compradores.length}</Tag>
          </Title>
          {compradores.length === 0 ? <Empty description="Sin compradores registrados" /> : compradores.map(renderParte)}
        </Col>
        <Col xs={24} sm={12}>
          <Title level={5}>
            Vendedores <Tag color="geekblue">{vendedores.length}</Tag>
          </Title>
          {vendedores.length === 0 ? <Empty description="Sin vendedores registrados" /> : vendedores.map(renderParte)}
        </Col>
      </Row>

      <Modal
        title={parteEditando ? 'Editar parte' : 'Agregar parte a la negociación'}
        open={modalAbierto}
        onCancel={() => {
          setModalAbierto(false);
          setParteEditando(null);
        }}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText={parteEditando ? 'Guardar' : 'Agregar'}
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleGuardar}>
          <Form.Item name="rol" label="Rol" rules={[{ required: true, message: 'Selecciona el rol' }]}>
            <Select options={ROLES_PARTE_NEGOCIACION.map((r) => ({ value: r, label: ETIQUETA_ROL_PARTE[r] }))} />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="tipoDocumento" label="Tipo de documento">
                <Select options={TIPOS_DOCUMENTO_IDENTIDAD.map((t) => ({ value: t, label: t }))} allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="numeroDocumento" label="Número de documento">
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="estadoCivil" label="Estado civil">
            <Select options={ESTADOS_CIVILES.map((e) => ({ value: e, label: e }))} allowClear />
          </Form.Item>
          <Form.Item name="direccion" label="Dirección">
            <Input />
          </Form.Item>
          <Form.Item name="correoElectronico" label="Correo electrónico">
            <Input type="email" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
