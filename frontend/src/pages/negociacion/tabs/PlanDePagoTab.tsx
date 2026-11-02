import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Card, Col, DatePicker, Form, InputNumber, Modal, Popconfirm, Row, Select, Skeleton, Space, Switch, Tag, Typography, message } from 'antd';
import { DeleteOutlined, EditOutlined, PlusOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import InputMoneda from '../../../components/InputMoneda';
import ItemList from '../../../components/ItemList';
import { planDePagoApi } from '../../../api/negociacion';
import {
  ETIQUETA_METODO_PAGO,
  ETIQUETA_MOMENTO_PAGO,
  ETIQUETA_TIPO_RECURSO_PAGO,
  METODOS_PAGO,
  MOMENTOS_PAGO,
  TIPOS_RECURSO_PAGO,
} from '../../../types/negociacion';
import type { MetodoPago, MomentoPago, PlanDePago, PlanDePagoRequest, TipoRecursoPago } from '../../../types/negociacion';

const { Text } = Typography;

interface Props {
  negociacionId: string;
}

interface FormValues {
  tipoRecurso: TipoRecursoPago;
  numeroCuota?: number;
  porcentaje?: number;
  valor?: number;
  momentoPago?: MomentoPago;
  metodoPago?: MetodoPago;
  fechaEsperada?: dayjs.Dayjs;
}

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function PlanDePagoTab({ negociacionId }: Props) {
  const [plan, setPlan] = useState<PlanDePago[]>([]);
  const [loading, setLoading] = useState(false);
  const [modalAbierto, setModalAbierto] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [actualizandoId, setActualizandoId] = useState<string | null>(null);
  const [cuotaEditando, setCuotaEditando] = useState<PlanDePago | null>(null);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [form] = Form.useForm<FormValues>();

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await planDePagoApi.listarPorNegociacion(negociacionId);
      setPlan(data);
    } catch {
      message.error('No se pudo cargar el plan de pago');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const recursosPropios = plan.filter((p) => p.tipoRecurso === 'RECURSOS_PROPIOS').sort((a, b) => (a.numeroCuota ?? 0) - (b.numeroCuota ?? 0));
  const creditoLeasing = plan.filter((p) => p.tipoRecurso === 'CREDITO_LEASING').sort((a, b) => (a.numeroCuota ?? 0) - (b.numeroCuota ?? 0));

  const abrirModalCrear = () => {
    setCuotaEditando(null);
    form.resetFields();
    setModalAbierto(true);
  };

  const abrirModalEditar = (cuota: PlanDePago) => {
    setCuotaEditando(cuota);
    form.setFieldsValue({
      tipoRecurso: cuota.tipoRecurso,
      numeroCuota: cuota.numeroCuota,
      porcentaje: cuota.porcentaje,
      valor: cuota.valor,
      momentoPago: cuota.momentoPago,
      metodoPago: cuota.metodoPago,
      fechaEsperada: cuota.fechaEsperada ? dayjs(cuota.fechaEsperada) : undefined,
    });
    setModalAbierto(true);
  };

  const handleGuardar = async (values: FormValues) => {
    const request: PlanDePagoRequest = {
      negociacionId,
      tipoRecurso: values.tipoRecurso,
      numeroCuota: values.numeroCuota,
      porcentaje: values.porcentaje,
      valor: values.valor,
      momentoPago: values.momentoPago,
      metodoPago: values.metodoPago,
      fechaEsperada: values.fechaEsperada?.format('YYYY-MM-DD'),
    };
    setGuardando(true);
    try {
      if (cuotaEditando) {
        await planDePagoApi.actualizar(cuotaEditando.id, request);
        message.success('Cuota actualizada correctamente');
      } else {
        await planDePagoApi.crear(request);
        message.success('Cuota agregada al plan de pago');
      }
      setModalAbierto(false);
      form.resetFields();
      cargar();
    } catch {
      message.error(cuotaEditando ? 'No se pudo actualizar la cuota' : 'No se pudo agregar la cuota');
    } finally {
      setGuardando(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await planDePagoApi.eliminar(id);
      message.success('Cuota eliminada correctamente');
      cargar();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar la cuota');
    } finally {
      setEliminandoId(null);
    }
  };

  const handleMarcarPagada = async (id: string, pagada: boolean) => {
    setActualizandoId(id);
    try {
      const actualizado = await planDePagoApi.marcarPagada(id, pagada);
      setPlan((prev) => prev.map((p) => (p.id === id ? actualizado : p)));
    } catch {
      message.error('No se pudo actualizar la cuota');
    } finally {
      setActualizandoId(null);
    }
  };

  const renderLista = (items: PlanDePago[]) => (
    <ItemList
      items={items}
      rowKey={(cuota) => cuota.id}
      emptyDescription="Sin cuotas registradas"
      renderItem={(cuota) => (
        <Space style={{ width: '100%', justifyContent: 'space-between' }} align="start">
          <Space orientation="vertical" size={0}>
            <Space>
              <Text strong>Cuota {cuota.numeroCuota ?? '—'}</Text>
              {cuota.pagada && <Tag color="green">Pagada</Tag>}
            </Space>
            <Text type="secondary">
              {formatearMoneda(cuota.valor)}
              {cuota.porcentaje != null ? ` · ${cuota.porcentaje}%` : ''}
            </Text>
            <Text type="secondary">
              {cuota.momentoPago ? ETIQUETA_MOMENTO_PAGO[cuota.momentoPago] : 'Sin momento definido'}
              {cuota.metodoPago ? ` · ${ETIQUETA_METODO_PAGO[cuota.metodoPago]}` : ''}
            </Text>
            {cuota.fechaEsperada && <Text type="secondary">Fecha esperada: {cuota.fechaEsperada}</Text>}
          </Space>
          <Space>
            <Text type="secondary">Pagada</Text>
            <Switch
              checked={cuota.pagada}
              loading={actualizandoId === cuota.id}
              onChange={(valor) => handleMarcarPagada(cuota.id, valor)}
            />
            <Button size="small" icon={<EditOutlined />} onClick={() => abrirModalEditar(cuota)} />
            <Popconfirm
              title="Eliminar cuota"
              description="¿Seguro que quieres eliminar esta cuota del plan de pago?"
              okText="Eliminar"
              cancelText="Cancelar"
              okButtonProps={{ danger: true }}
              onConfirm={() => handleEliminar(cuota.id)}
            >
              <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === cuota.id} />
            </Popconfirm>
          </Space>
        </Space>
      )}
    />
  );

  if (loading) return <Skeleton active />;

  return (
    <div style={{ maxWidth: 780 }}>
      <Space style={{ marginBottom: 16, justifyContent: 'space-between', width: '100%' }} wrap>
        <Text type="secondary">Cuotas de recursos propios y de crédito/leasing acordadas para el negocio.</Text>
        <Button type="primary" icon={<PlusOutlined />} onClick={abrirModalCrear}>
          Agregar cuota
        </Button>
      </Space>

      <Card title={ETIQUETA_TIPO_RECURSO_PAGO.RECURSOS_PROPIOS} size="small" style={{ marginBottom: 16 }}>
        {renderLista(recursosPropios)}
      </Card>
      <Card title={ETIQUETA_TIPO_RECURSO_PAGO.CREDITO_LEASING} size="small">
        {renderLista(creditoLeasing)}
      </Card>

      <Modal
        title={cuotaEditando ? 'Editar cuota' : 'Agregar cuota al plan de pago'}
        open={modalAbierto}
        onCancel={() => {
          setModalAbierto(false);
          setCuotaEditando(null);
        }}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText={cuotaEditando ? 'Guardar' : 'Agregar'}
        cancelText="Cancelar"
      >
        <Form form={form} layout="vertical" onFinish={handleGuardar}>
          <Form.Item name="tipoRecurso" label="Tipo de recurso" rules={[{ required: true, message: 'Selecciona el tipo de recurso' }]}>
            <Select options={TIPOS_RECURSO_PAGO.map((t) => ({ value: t, label: ETIQUETA_TIPO_RECURSO_PAGO[t] }))} />
          </Form.Item>
          <Row gutter={8}>
            <Col xs={24} sm={8}>
              <Form.Item name="numeroCuota" label="N° de cuota">
                <InputNumber style={{ width: '100%' }} min={1} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="porcentaje" label="Porcentaje">
                <InputNumber style={{ width: '100%' }} min={0} max={100} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="valor" label="Valor">
                <InputMoneda />
              </Form.Item>
            </Col>
          </Row>
          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="momentoPago" label="Momento de pago">
                <Select allowClear options={MOMENTOS_PAGO.map((m) => ({ value: m, label: ETIQUETA_MOMENTO_PAGO[m] }))} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="metodoPago" label="Método de pago">
                <Select allowClear options={METODOS_PAGO.map((m) => ({ value: m, label: ETIQUETA_METODO_PAGO[m] }))} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="fechaEsperada" label="Fecha esperada">
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
        </Form>
      </Modal>
    </div>
  );
}
