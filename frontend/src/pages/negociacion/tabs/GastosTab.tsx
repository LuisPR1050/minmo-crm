import { useEffect, useState } from 'react';
import { Alert, Button, Card, Col, Descriptions, Form, Row, Select, Skeleton, Space, Typography, message } from 'antd';
import { MailOutlined, WhatsAppOutlined } from '@ant-design/icons';
import { negociacionApi, distribucionGastosApi, proyeccionGastosApi } from '../../../api/negociacion';
import { clienteApi } from '../../../api/atencioncliente';
import { propietarioApi } from '../../../api/captacion';
import { ETIQUETA_QUIEN_PAGA, QUIENES_PAGAN } from '../../../types/negociacion';
import type { DistribucionGastosRequest, ProyeccionGastos, QuienPaga } from '../../../types/negociacion';
import { construirEnlaceCorreo, construirEnlaceWhatsapp } from '../../../utils/compartir';

const { Text } = Typography;

interface Props {
  negociacionId: string;
}

interface FormValues {
  gastosEscrituracion: QuienPaga;
  retencionFuente: QuienPaga;
  gastosRegistro: QuienPaga;
  gastosCancelacionGravamen: QuienPaga;
  gastosConstitucionGravamen: QuienPaga;
}

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

export default function GastosTab({ negociacionId }: Props) {
  const [form] = Form.useForm<FormValues>();
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [proyeccion, setProyeccion] = useState<ProyeccionGastos | null>(null);
  const [compradorWhatsapp, setCompradorWhatsapp] = useState<string | undefined>();
  const [compradorCorreo, setCompradorCorreo] = useState<string | undefined>();
  const [vendedorWhatsapp, setVendedorWhatsapp] = useState<string | undefined>();
  const [vendedorCorreo, setVendedorCorreo] = useState<string | undefined>();

  const cargar = async () => {
    setLoading(true);
    try {
      const negociacion = await negociacionApi.buscarPorId(negociacionId);

      const [distribucion, cliente, propietario] = await Promise.all([
        distribucionGastosApi.buscarPorNegociacion(negociacionId),
        clienteApi.buscarPorId(negociacion.clienteId),
        propietarioApi.buscarPorId(negociacion.propietarioId),
      ]);

      if (distribucion) {
        form.setFieldsValue(distribucion);
      }
      setCompradorWhatsapp(cliente.whatsapp);
      setCompradorCorreo(cliente.correoElectronico);
      setVendedorWhatsapp(propietario.whatsapp);
      setVendedorCorreo(propietario.correoElectronico);

      try {
        const proyeccionCalculada = await proyeccionGastosApi.calcular(negociacionId);
        setProyeccion(proyeccionCalculada);
      } catch {
        setProyeccion(null);
      }
    } catch {
      message.error('No se pudo cargar la información de gastos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const handleGuardar = async (values: FormValues) => {
    const request: DistribucionGastosRequest = { negociacionId, ...values };
    setGuardando(true);
    try {
      await distribucionGastosApi.guardar(request);
      message.success('Distribución de gastos guardada');
    } catch {
      message.error('No se pudo guardar la distribución de gastos');
    } finally {
      setGuardando(false);
    }
  };

  const textoResumenProyeccion = proyeccion
    ? `Proyección de gastos de escrituración y registro:\n` +
      `Valor de venta: ${formatearMoneda(proyeccion.valorVenta)}\n` +
      `Gastos notariales: ${formatearMoneda(proyeccion.gastosNotariales)}\n` +
      `Derechos de registro: ${formatearMoneda(proyeccion.derechosRegistro)}\n` +
      `Retención en la fuente: ${formatearMoneda(proyeccion.retencionFuente)}\n` +
      `Total estimado: ${formatearMoneda(proyeccion.totalEstimado)}`
    : '';

  if (loading) return <Skeleton active />;

  return (
    <Row gutter={16}>
      <Col xs={24} sm={12}>
        <Card title="Distribución de gastos" size="small">
          <Form<FormValues> form={form} layout="vertical" onFinish={handleGuardar}>
            <Form.Item name="gastosEscrituracion" label="Gastos de escrituración" rules={[{ required: true, message: 'Selecciona quién paga' }]}>
              <Select options={QUIENES_PAGAN.map((q) => ({ value: q, label: ETIQUETA_QUIEN_PAGA[q] }))} />
            </Form.Item>
            <Form.Item name="retencionFuente" label="Retención en la fuente" rules={[{ required: true, message: 'Selecciona quién paga' }]}>
              <Select options={QUIENES_PAGAN.map((q) => ({ value: q, label: ETIQUETA_QUIEN_PAGA[q] }))} />
            </Form.Item>
            <Form.Item name="gastosRegistro" label="Gastos de registro" rules={[{ required: true, message: 'Selecciona quién paga' }]}>
              <Select options={QUIENES_PAGAN.map((q) => ({ value: q, label: ETIQUETA_QUIEN_PAGA[q] }))} />
            </Form.Item>
            <Form.Item
              name="gastosCancelacionGravamen"
              label="Gastos de cancelación de afectaciones y/o gravamen"
              rules={[{ required: true, message: 'Selecciona quién paga' }]}
            >
              <Select options={QUIENES_PAGAN.map((q) => ({ value: q, label: ETIQUETA_QUIEN_PAGA[q] }))} />
            </Form.Item>
            <Form.Item
              name="gastosConstitucionGravamen"
              label="Gastos de constitución de afectaciones y/o gravamen"
              rules={[{ required: true, message: 'Selecciona quién paga' }]}
            >
              <Select options={QUIENES_PAGAN.map((q) => ({ value: q, label: ETIQUETA_QUIEN_PAGA[q] }))} />
            </Form.Item>
            <Button type="primary" htmlType="submit" loading={guardando}>
              Guardar distribución
            </Button>
          </Form>
        </Card>
      </Col>

      <Col xs={24} sm={12}>
        <Card title="Proyección de gastos de escrituración y registro" size="small">
          {proyeccion ? (
            <>
              <Descriptions column={1} bordered size="small" style={{ marginBottom: 16 }}>
                <Descriptions.Item label="Valor de venta">{formatearMoneda(proyeccion.valorVenta)}</Descriptions.Item>
                <Descriptions.Item label="Gastos notariales (0.3%)">{formatearMoneda(proyeccion.gastosNotariales)}</Descriptions.Item>
                <Descriptions.Item label="Derechos de registro (1%)">{formatearMoneda(proyeccion.derechosRegistro)}</Descriptions.Item>
                <Descriptions.Item label="Retención en la fuente (1%)">{formatearMoneda(proyeccion.retencionFuente)}</Descriptions.Item>
                <Descriptions.Item label="Total estimado">
                  <Text strong>{formatearMoneda(proyeccion.totalEstimado)}</Text>
                </Descriptions.Item>
              </Descriptions>

              <Text type="secondary" style={{ display: 'block', marginBottom: 8 }}>
                Enviar esta proyección a:
              </Text>
              <Space orientation="vertical" style={{ width: '100%' }}>
                <Space wrap>
                  <Text>Comprador:</Text>
                  <Button
                    size="small"
                    icon={<WhatsAppOutlined />}
                    disabled={!compradorWhatsapp}
                    href={compradorWhatsapp ? construirEnlaceWhatsapp(compradorWhatsapp, textoResumenProyeccion) : undefined}
                    target="_blank"
                  >
                    WhatsApp
                  </Button>
                  <Button
                    size="small"
                    icon={<MailOutlined />}
                    disabled={!compradorCorreo}
                    href={compradorCorreo ? construirEnlaceCorreo(compradorCorreo, 'Proyección de gastos', textoResumenProyeccion) : undefined}
                  >
                    Correo
                  </Button>
                </Space>
                <Space wrap>
                  <Text>Vendedor:</Text>
                  <Button
                    size="small"
                    icon={<WhatsAppOutlined />}
                    disabled={!vendedorWhatsapp}
                    href={vendedorWhatsapp ? construirEnlaceWhatsapp(vendedorWhatsapp, textoResumenProyeccion) : undefined}
                    target="_blank"
                  >
                    WhatsApp
                  </Button>
                  <Button
                    size="small"
                    icon={<MailOutlined />}
                    disabled={!vendedorCorreo}
                    href={vendedorCorreo ? construirEnlaceCorreo(vendedorCorreo, 'Proyección de gastos', textoResumenProyeccion) : undefined}
                  >
                    Correo
                  </Button>
                </Space>
              </Space>
            </>
          ) : (
            <Alert
              type="info"
              showIcon
              title="Sin proyección disponible"
              description="Registra un precio inicial publicado o un precio final acordado para calcular la proyección de gastos."
            />
          )}
        </Card>
      </Col>
    </Row>
  );
}
