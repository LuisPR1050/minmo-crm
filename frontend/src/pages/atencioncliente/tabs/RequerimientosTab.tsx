import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { isAxiosError } from 'axios';
import { Button, Card, Col, Empty, Form, Input, InputNumber, Modal, Popconfirm, Row, Select, Skeleton, Space, Typography, message } from 'antd';
import InputMoneda from '../../../components/InputMoneda';
import { DeleteOutlined, PlusOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { requerimientoClienteApi } from '../../../api/atencioncliente';
import {
  ENTORNOS,
  FORMAS_PAGO_REQUERIMIENTO,
  TIEMPOS_PARA_COMPRAR,
  TIPOS_PROPIEDAD_REQUERIMIENTO,
  ZONAS_VALLEDUPAR,
} from '../../../types/atencioncliente';
import type { InmuebleRecomendado, RequerimientoCliente, RequerimientoClienteRequest } from '../../../types/atencioncliente';
import { ETIQUETA_TIPO_PROPIEDAD } from '../../../types/captacion';

const { Text } = Typography;

interface Props {
  clienteId: string;
}

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function RequerimientosTab({ clienteId }: Props) {
  const [requerimientos, setRequerimientos] = useState<RequerimientoCliente[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [recomendandoId, setRecomendandoId] = useState<string | null>(null);
  const [recomendaciones, setRecomendaciones] = useState<InmuebleRecomendado[] | null>(null);
  const [form] = Form.useForm<Omit<RequerimientoClienteRequest, 'clienteId'>>();

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await requerimientoClienteApi.listarPorCliente(clienteId);
      setRequerimientos(data.slice().reverse());
    } catch {
      message.error('No se pudieron cargar los requerimientos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [clienteId]);

  const handleCrear = async (values: Omit<RequerimientoClienteRequest, 'clienteId'>) => {
    setGuardando(true);
    try {
      await requerimientoClienteApi.crear({ clienteId, ...values });
      message.success('Requerimiento registrado');
      setModalAbierto(false);
      form.resetFields();
      cargar();
    } catch {
      message.error('No se pudo registrar el requerimiento');
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await requerimientoClienteApi.eliminar(id);
      message.success('Requerimiento eliminado correctamente');
      cargar();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar el requerimiento');
    } finally {
      setEliminandoId(null);
    }
  };

  const handleRecomendar = async (requerimientoId: string) => {
    setRecomendandoId(requerimientoId);
    try {
      const data = await requerimientoClienteApi.recomendarConIA(requerimientoId);
      setRecomendaciones(data);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudieron generar recomendaciones');
    } finally {
      setRecomendandoId(null);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <div style={{ maxWidth: 800 }}>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text type="secondary">Qué está buscando el cliente. Puedes registrar uno nuevo si sus necesidades cambian.</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalAbierto(true)}>
          Nuevo requerimiento
        </Button>
      </Space>

      {requerimientos.length === 0 ? (
        <Empty description="Sin requerimientos registrados todavía" />
      ) : (
        <Space orientation="vertical" size={16} style={{ width: '100%' }}>
          {requerimientos.map((req, index) => (
            <Card
              key={req.id}
              size="small"
              title={index === 0 ? 'Requerimiento actual' : `Requerimiento anterior — ${new Date(req.createdAt).toLocaleDateString('es-CO')}`}
              extra={
                <Space>
                  <Button
                    size="small"
                    icon={<ThunderboltOutlined />}
                    loading={recomendandoId === req.id}
                    onClick={() => handleRecomendar(req.id)}
                  >
                    Recomendar inmuebles con IA
                  </Button>
                  <Popconfirm
                    title="Eliminar requerimiento"
                    description="¿Seguro que quieres eliminar este requerimiento?"
                    okText="Eliminar"
                    cancelText="Cancelar"
                    okButtonProps={{ danger: true }}
                    onConfirm={() => handleEliminar(req.id)}
                  >
                    <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === req.id} />
                  </Popconfirm>
                </Space>
              }
            >
              <Space orientation="vertical" size={4}>
                <Text>
                  <strong>Tipo de propiedad:</strong> {req.tipoPropiedad ?? '—'}
                </Text>
                <Text>
                  <strong>Ubicación:</strong> {req.ciudad ?? '—'} {req.zona ? `· ${req.zona}` : ''} {req.entorno ? `· ${req.entorno}` : ''}
                </Text>
                {req.sitiosClavesCercanos && (
                  <Text>
                    <strong>Sitios cercanos clave:</strong> {req.sitiosClavesCercanos}
                  </Text>
                )}
                <Text>
                  <strong>Presupuesto:</strong> {formatearMoneda(req.presupuestoMin)} — {formatearMoneda(req.presupuestoMax)}
                </Text>
                <Text>
                  <strong>Forma de pago:</strong> {req.formaPago ?? '—'}
                </Text>
                <Text>
                  <strong>Tiempo para comprar:</strong> {req.tiempoParaComprar ?? '—'}
                </Text>
                <Text>
                  <strong>Habitaciones / Baños:</strong> {req.numeroHabitaciones ?? '—'} / {req.numeroBanos ?? '—'}
                </Text>
                {req.observaciones && (
                  <Text>
                    <strong>Observaciones:</strong> {req.observaciones}
                  </Text>
                )}
              </Space>
            </Card>
          ))}
        </Space>
      )}

      <Modal
        title="Nuevo requerimiento"
        open={modalAbierto}
        onCancel={() => setModalAbierto(false)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Guardar"
        cancelText="Cancelar"
        width={640}
      >
        <Form form={form} layout="vertical" onFinish={handleCrear} initialValues={{ ciudad: 'Valledupar' }}>
          <Form.Item name="tipoPropiedad" label="Tipo de propiedad">
            <Select options={TIPOS_PROPIEDAD_REQUERIMIENTO.map((t) => ({ value: t, label: t }))} allowClear />
          </Form.Item>
          <Form.Item name="ciudad" label="Ciudad">
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
          <Form.Item name="sitiosClavesCercanos" label="Sitios claves cercanos">
            <Input placeholder="Ej: colegio, trabajo, centro comercial" />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="presupuestoMin" label="Presupuesto mínimo">
                <InputMoneda />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="presupuestoMax" label="Presupuesto máximo">
                <InputMoneda />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="formaPago" label="Forma de pago">
                <Select options={FORMAS_PAGO_REQUERIMIENTO.map((f) => ({ value: f, label: f }))} allowClear />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="tiempoParaComprar" label="Tiempo para comprar">
                <Select options={TIEMPOS_PARA_COMPRAR.map((t) => ({ value: t, label: t }))} allowClear />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="numeroHabitaciones" label="Número de habitaciones">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="numeroBanos" label="Número de baños">
                <InputNumber style={{ width: '100%' }} min={0} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="observaciones" label="Observaciones sobre el requerimiento">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Inmuebles recomendados por IA"
        open={recomendaciones !== null}
        onCancel={() => setRecomendaciones(null)}
        footer={<Button onClick={() => setRecomendaciones(null)}>Cerrar</Button>}
        width={640}
      >
        {recomendaciones && recomendaciones.length === 0 ? (
          <Empty description="La IA no encontró inmuebles del inventario que encajen razonablemente con este requerimiento" />
        ) : (
          <Space orientation="vertical" size={12} style={{ width: '100%' }}>
            {recomendaciones?.map((rec) => (
              <Card key={rec.inmueble.id} size="small">
                <Space orientation="vertical" size={4} style={{ width: '100%' }}>
                  <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                    <Link to={`/captacion/inmuebles/${rec.inmueble.id}`}>
                      <Text strong>{rec.inmueble.codigo}</Text>
                    </Link>
                    <Text strong style={{ color: '#0b4f6c' }}>
                      {formatearMoneda(rec.inmueble.expectativaPrecio)}
                    </Text>
                  </Space>
                  <Text type="secondary">
                    {ETIQUETA_TIPO_PROPIEDAD[rec.inmueble.tipoPropiedad as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ??
                      rec.inmueble.tipoPropiedad}{' '}
                    · {rec.inmueble.zona ?? rec.inmueble.ciudad ?? 'Sin ubicación'} ·{' '}
                    {rec.inmueble.numeroHabitaciones ?? '—'} hab · {rec.inmueble.numeroBanos ?? '—'} baños
                  </Text>
                  <Text>{rec.motivo}</Text>
                </Space>
              </Card>
            ))}
          </Space>
        )}
      </Modal>
    </div>
  );
}
