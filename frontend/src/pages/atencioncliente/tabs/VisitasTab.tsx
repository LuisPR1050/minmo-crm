import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Card, Col, DatePicker, Empty, Form, Input, Modal, Popconfirm, Row, Select, Skeleton, Space, Switch, Tag, TimePicker, Typography, message } from 'antd';
import { DeleteOutlined, EnvironmentOutlined, PlusOutlined } from '@ant-design/icons';
import { Link } from 'react-router-dom';
import dayjs from 'dayjs';
import { visitaClienteApi } from '../../../api/atencioncliente';
import { inmuebleApi } from '../../../api/captacion';
import { ESTADOS_VISITA_CLIENTE, ETIQUETA_ESTADO_VISITA_CLIENTE, ETIQUETA_TIPO_VISITA, TIPOS_VISITA } from '../../../types/atencioncliente';
import type { EstadoVisitaCliente, TipoVisita, VisitaCliente, VisitaClienteRequest } from '../../../types/atencioncliente';
import type { Inmueble } from '../../../types/captacion';

const { Text } = Typography;

interface Props {
  clienteId: string;
}

interface FormValues {
  inmuebleId: string;
  tipoVisita: TipoVisita;
  fechaVisita?: dayjs.Dayjs;
  horaInicio?: dayjs.Dayjs;
  horaFin?: dayjs.Dayjs;
  recomendaciones?: string;
}

const COLOR_ESTADO: Record<EstadoVisitaCliente, string> = {
  PENDIENTE_CONFIRMAR: 'default',
  CONFIRMADA: 'blue',
  REAGENDADA: 'orange',
  REALIZADA: 'green',
  CANCELADA: 'red',
  NO_ASISTIO_CLIENTE: 'volcano',
  PROPIETARIO_NO_DISPONIBLE: 'volcano',
};

export default function VisitasTab({ clienteId }: Props) {
  const [visitas, setVisitas] = useState<VisitaCliente[]>([]);
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [actualizandoId, setActualizandoId] = useState<string | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [form] = Form.useForm<FormValues>();

  const cargar = async () => {
    setLoading(true);
    try {
      const [visitasData, inmueblesData] = await Promise.all([
        visitaClienteApi.listarPorCliente(clienteId),
        inmuebleApi.listar(),
      ]);
      setVisitas(visitasData);
      setInmuebles(inmueblesData);
    } catch {
      message.error('No se pudieron cargar las visitas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [clienteId]);

  const handleCrear = async (values: FormValues) => {
    const request: VisitaClienteRequest = {
      clienteId,
      inmuebleId: values.inmuebleId,
      tipoVisita: values.tipoVisita,
      fechaVisita: values.fechaVisita?.format('YYYY-MM-DD'),
      horaInicio: values.horaInicio?.format('HH:mm:ss'),
      horaFin: values.horaFin?.format('HH:mm:ss'),
      recomendaciones: values.recomendaciones,
    };
    setGuardando(true);
    try {
      await visitaClienteApi.crear(request);
      message.success('Visita agendada correctamente');
      setModalAbierto(false);
      form.resetFields();
      cargar();
    } catch {
      message.error('No se pudo agendar la visita');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambiarEstado = async (id: string, nuevoEstado: EstadoVisitaCliente) => {
    setActualizandoId(id);
    try {
      const actualizada = await visitaClienteApi.cambiarEstado(id, nuevoEstado);
      setVisitas((prev) => prev.map((v) => (v.id === id ? actualizada : v)));
    } catch {
      message.error('No se pudo cambiar el estado de la visita');
    } finally {
      setActualizandoId(null);
    }
  };

  const handleConfirmar = async (visita: VisitaCliente, campo: 'confirmadaPorCliente' | 'confirmadaPorPropietario', valor: boolean) => {
    setActualizandoId(visita.id);
    try {
      const actualizada = await visitaClienteApi.confirmar(
        visita.id,
        campo === 'confirmadaPorCliente' ? valor : visita.confirmadaPorCliente,
        campo === 'confirmadaPorPropietario' ? valor : visita.confirmadaPorPropietario,
      );
      setVisitas((prev) => prev.map((v) => (v.id === visita.id ? actualizada : v)));
    } catch {
      message.error('No se pudo actualizar la confirmación');
    } finally {
      setActualizandoId(null);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await visitaClienteApi.eliminar(id);
      message.success('Visita eliminada correctamente');
      cargar();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar la visita');
    } finally {
      setEliminandoId(null);
    }
  };

  const abrirEnGoogleMaps = (inmueble?: Inmueble) => {
    const direccion = inmueble?.direccionExacta ?? inmueble?.ciudad;
    if (!direccion) return;
    window.open(`https://www.google.com/maps/search/?api=1&query=${encodeURIComponent(direccion)}`, '_blank');
  };

  if (loading) return <Skeleton active />;

  return (
    <div style={{ maxWidth: 780 }}>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text type="secondary">Visitas presenciales, virtuales o recorridos grabados agendados con este cliente.</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={() => setModalAbierto(true)}>
          Agendar visita
        </Button>
      </Space>

      {visitas.length === 0 ? (
        <Empty description="Sin visitas agendadas" />
      ) : (
        <>
          {visitas.map((visita) => {
            const inmueble = inmuebles.find((i) => i.id === visita.inmuebleId);
            return (
              <Card key={visita.id} size="small" style={{ marginBottom: 12 }}>
                <Space orientation="vertical" style={{ width: '100%' }} size={8}>
                  <Space style={{ width: '100%', justifyContent: 'space-between' }} wrap>
                    <Space>
                      <Text strong>{visita.codigoVisita}</Text>
                      <Link to={`/captacion/inmuebles/${visita.inmuebleId}`}>{inmueble?.codigo ?? visita.inmuebleCodigo}</Link>
                      <Tag>{ETIQUETA_TIPO_VISITA[visita.tipoVisita]}</Tag>
                      <Tag color={COLOR_ESTADO[visita.estado]}>{ETIQUETA_ESTADO_VISITA_CLIENTE[visita.estado]}</Tag>
                    </Space>
                    <Space>
                      <Select<EstadoVisitaCliente>
                        size="small"
                        value={visita.estado}
                        loading={actualizandoId === visita.id}
                        style={{ width: 200 }}
                        onChange={(v) => handleCambiarEstado(visita.id, v)}
                        options={ESTADOS_VISITA_CLIENTE.map((e) => ({ value: e, label: ETIQUETA_ESTADO_VISITA_CLIENTE[e] }))}
                      />
                      <Popconfirm
                        title="Eliminar visita"
                        description="¿Seguro que quieres eliminar esta visita?"
                        okText="Eliminar"
                        cancelText="Cancelar"
                        okButtonProps={{ danger: true }}
                        onConfirm={() => handleEliminar(visita.id)}
                      >
                        <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === visita.id} />
                      </Popconfirm>
                    </Space>
                  </Space>
                  <Text type="secondary">
                    {visita.fechaVisita ?? 'Sin fecha'} {visita.horaInicio ? `· ${visita.horaInicio.slice(0, 5)}` : ''}
                    {visita.horaFin ? ` - ${visita.horaFin.slice(0, 5)}` : ''}
                  </Text>
                  {inmueble && (
                    <Button size="small" icon={<EnvironmentOutlined />} onClick={() => abrirEnGoogleMaps(inmueble)}>
                      Ver ubicación en Google Maps
                    </Button>
                  )}
                  {visita.recomendaciones && <Text type="secondary">Recomendaciones: {visita.recomendaciones}</Text>}
                  <Space size={16}>
                    <Space size={4}>
                      <Text type="secondary" style={{ fontSize: 12 }}>
                        Confirmada por cliente
                      </Text>
                      <Switch
                        size="small"
                        checked={visita.confirmadaPorCliente}
                        loading={actualizandoId === visita.id}
                        onChange={(v) => handleConfirmar(visita, 'confirmadaPorCliente', v)}
                      />
                    </Space>
                    <Space size={4}>
                      <Text type="secondary" style={{ fontSize: 12 }}>
                        Confirmada por propietario
                      </Text>
                      <Switch
                        size="small"
                        checked={visita.confirmadaPorPropietario}
                        loading={actualizandoId === visita.id}
                        onChange={(v) => handleConfirmar(visita, 'confirmadaPorPropietario', v)}
                      />
                    </Space>
                  </Space>
                </Space>
              </Card>
            );
          })}
        </>
      )}

      <Modal
        title="Agendar visita"
        open={modalAbierto}
        onCancel={() => setModalAbierto(false)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Agendar"
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleCrear}>
          <Form.Item name="inmuebleId" label="Inmueble" rules={[{ required: true, message: 'Selecciona un inmueble' }]}>
            <Select
              showSearch
              placeholder="Selecciona un inmueble"
              optionFilterProp="label"
              options={inmuebles.map((i) => ({ value: i.id, label: `${i.codigo} — ${i.direccionExacta ?? i.ciudad ?? ''}` }))}
            />
          </Form.Item>
          <Form.Item name="tipoVisita" label="Tipo de visita" rules={[{ required: true, message: 'Selecciona el tipo de visita' }]}>
            <Select options={TIPOS_VISITA.map((t) => ({ value: t, label: ETIQUETA_TIPO_VISITA[t] }))} />
          </Form.Item>
          <Form.Item name="fechaVisita" label="Fecha de la visita">
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="horaInicio" label="Hora de inicio">
                <TimePicker style={{ width: '100%' }} format="HH:mm" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="horaFin" label="Hora de finalización">
                <TimePicker style={{ width: '100%' }} format="HH:mm" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="recomendaciones" label="Recomendaciones para la visita">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
