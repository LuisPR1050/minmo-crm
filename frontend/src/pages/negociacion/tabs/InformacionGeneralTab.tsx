import { useState } from 'react';
import {
  Alert,
  Button,
  Card,
  Col,
  DatePicker,
  Descriptions,
  Form,
  Input,
  Row,
  Select,
  Steps,
  Switch,
  Tag,
  Typography,
  message,
} from 'antd';
import InputMoneda from '../../../components/InputMoneda';
import { CheckCircleFilled } from '@ant-design/icons';
import dayjs from 'dayjs';
import { negociacionApi } from '../../../api/negociacion';
import {
  ESTADOS_FISICOS_ENTREGA,
  ETIQUETA_ESTADO_FISICO_ENTREGA,
  ETIQUETA_MOMENTO_ENTREGA,
  MOMENTOS_ENTREGA,
} from '../../../types/negociacion';
import type { ActualizarCondicionesRequest, Negociacion } from '../../../types/negociacion';

const { Text } = Typography;

interface Props {
  negociacion: Negociacion;
  onGuardado: () => void;
}

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

interface FormValues {
  contraofertaVendedor?: number;
  precioFinalAcordado?: number;
  fechaFirmaPcv?: dayjs.Dayjs;
  fechaEntregaProgramada?: dayjs.Dayjs;
  entidadFinanciera?: string;
  financiacionDirectaCondiciones?: string;
  permutaCondiciones?: string;
  estadoFisicoEntrega?: ActualizarCondicionesRequest['estadoFisicoEntrega'];
  momentoEntrega?: ActualizarCondicionesRequest['momentoEntrega'];
}

export default function InformacionGeneralTab({ negociacion, onGuardado }: Props) {
  const [form] = Form.useForm<FormValues>();
  const [guardando, setGuardando] = useState(false);
  const [actualizandoHito, setActualizandoHito] = useState<string | null>(null);

  const negocioCerrado = negociacion.estado === 'NEGOCIO_CERRADO';

  const handleGuardar = async (values: FormValues) => {
    const request: ActualizarCondicionesRequest = {
      contraofertaVendedor: values.contraofertaVendedor,
      precioFinalAcordado: values.precioFinalAcordado,
      fechaFirmaPcv: values.fechaFirmaPcv?.format('YYYY-MM-DD'),
      fechaEntregaProgramada: values.fechaEntregaProgramada?.format('YYYY-MM-DD'),
      entidadFinanciera: values.entidadFinanciera,
      financiacionDirectaCondiciones: values.financiacionDirectaCondiciones,
      permutaCondiciones: values.permutaCondiciones,
      estadoFisicoEntrega: values.estadoFisicoEntrega,
      momentoEntrega: values.momentoEntrega,
    };

    setGuardando(true);
    try {
      await negociacionApi.actualizarCondiciones(negociacion.id, request);
      message.success('Condiciones actualizadas');
      onGuardado();
    } catch {
      message.error('No se pudieron actualizar las condiciones');
    } finally {
      setGuardando(false);
    }
  };

  const handleHito = async (hito: 'escrituraFirmada' | 'pagoRecibido' | 'entregaRealizada', valor: boolean) => {
    setActualizandoHito(hito);
    try {
      let actualizada: Negociacion;
      if (hito === 'escrituraFirmada') actualizada = await negociacionApi.marcarEscrituraFirmada(negociacion.id, valor);
      else if (hito === 'pagoRecibido') actualizada = await negociacionApi.marcarPagoRecibido(negociacion.id, valor);
      else actualizada = await negociacionApi.marcarEntregaRealizada(negociacion.id, valor);

      if (actualizada.estado === 'NEGOCIO_CERRADO') {
        message.success('¡Los tres hitos se cumplieron! El negocio se cerró automáticamente y el inmueble quedó marcado como VENDIDO.');
      } else {
        message.success('Hito actualizado');
      }
      onGuardado();
    } catch {
      message.error('No se pudo actualizar el hito');
    } finally {
      setActualizandoHito(null);
    }
  };

  return (
    <Row gutter={16}>
      <Col xs={24} lg={14}>
        <Card title="Información general" style={{ marginBottom: 16 }} size="small">
          <Descriptions column={1} bordered size="small">
            <Descriptions.Item label="N° de negocio">{negociacion.numeroNegocio}</Descriptions.Item>
            <Descriptions.Item label="Inmueble">{negociacion.inmuebleCodigo}</Descriptions.Item>
            <Descriptions.Item label="Cliente (comprador)">{negociacion.clienteNombre}</Descriptions.Item>
            <Descriptions.Item label="Propietario (vendedor)">{negociacion.propietarioNombre}</Descriptions.Item>
            <Descriptions.Item label="Fecha de inicio">{negociacion.fechaInicio ?? '—'}</Descriptions.Item>
          </Descriptions>
        </Card>

        <Card title="Condiciones económicas" size="small">
          <Form<FormValues>
            form={form}
            layout="vertical"
            onFinish={handleGuardar}
            initialValues={{
              contraofertaVendedor: negociacion.contraofertaVendedor,
              precioFinalAcordado: negociacion.precioFinalAcordado,
              fechaFirmaPcv: negociacion.fechaFirmaPcv ? dayjs(negociacion.fechaFirmaPcv) : undefined,
              fechaEntregaProgramada: negociacion.fechaEntregaProgramada ? dayjs(negociacion.fechaEntregaProgramada) : undefined,
              entidadFinanciera: negociacion.entidadFinanciera,
              financiacionDirectaCondiciones: negociacion.financiacionDirectaCondiciones,
              permutaCondiciones: negociacion.permutaCondiciones,
              estadoFisicoEntrega: negociacion.estadoFisicoEntrega,
              momentoEntrega: negociacion.momentoEntrega,
            }}
          >
            <Descriptions column={{ xs: 1, sm: 2 }} size="small" style={{ marginBottom: 16 }}>
              <Descriptions.Item label="Precio inicial publicado">
                {formatearMoneda(negociacion.precioInicialPublicado)}
              </Descriptions.Item>
              <Descriptions.Item label="Oferta inicial del comprador">
                {formatearMoneda(negociacion.ofertaInicialComprador)}
              </Descriptions.Item>
            </Descriptions>

            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="contraofertaVendedor" label="Contraoferta del vendedor">
                  <InputMoneda />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="precioFinalAcordado" label="Precio final acordado">
                  <InputMoneda />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="fechaFirmaPcv" label="Fecha de firma de la PCV">
                  <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="fechaEntregaProgramada" label="Fecha de entrega programada">
                  <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="entidadFinanciera" label="Entidad financiera">
              <Input placeholder="Nombre del banco o entidad" />
            </Form.Item>

            <Form.Item name="financiacionDirectaCondiciones" label="Condiciones de financiación directa">
              <Input.TextArea rows={2} />
            </Form.Item>

            <Form.Item name="permutaCondiciones" label="Condiciones de permuta">
              <Input.TextArea rows={2} />
            </Form.Item>

            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="estadoFisicoEntrega" label="Estado físico de entrega">
                  <Select
                    allowClear
                    options={ESTADOS_FISICOS_ENTREGA.map((e) => ({ value: e, label: ETIQUETA_ESTADO_FISICO_ENTREGA[e] }))}
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="momentoEntrega" label="Momento de entrega">
                  <Select allowClear options={MOMENTOS_ENTREGA.map((m) => ({ value: m, label: ETIQUETA_MOMENTO_ENTREGA[m] }))} />
                </Form.Item>
              </Col>
            </Row>

            <Button type="primary" htmlType="submit" loading={guardando}>
              Guardar condiciones
            </Button>
          </Form>
        </Card>
      </Col>

      <Col xs={24} lg={10}>
        {negocioCerrado && (
          <Alert
            type="success"
            showIcon
            title="Negocio cerrado"
            description="El cierre se disparó automáticamente al cumplirse los tres hitos. El inmueble quedó marcado como VENDIDO."
            style={{ marginBottom: 16 }}
          />
        )}
        <Card title="Hitos de cierre" size="small">
          <Steps
            orientation="vertical"
            current={[negociacion.escrituraFirmada, negociacion.pagoRecibido, negociacion.entregaRealizada].filter(Boolean).length}
            items={[
              {
                title: 'Escritura firmada',
                icon: negociacion.escrituraFirmada ? <CheckCircleFilled style={{ color: '#52c41a' }} /> : undefined,
                content: (
                  <Switch
                    checked={negociacion.escrituraFirmada}
                    loading={actualizandoHito === 'escrituraFirmada'}
                    disabled={negocioCerrado}
                    onChange={(valor) => handleHito('escrituraFirmada', valor)}
                  />
                ),
              },
              {
                title: 'Pago recibido',
                icon: negociacion.pagoRecibido ? <CheckCircleFilled style={{ color: '#52c41a' }} /> : undefined,
                content: (
                  <Switch
                    checked={negociacion.pagoRecibido}
                    loading={actualizandoHito === 'pagoRecibido'}
                    disabled={negocioCerrado}
                    onChange={(valor) => handleHito('pagoRecibido', valor)}
                  />
                ),
              },
              {
                title: 'Entrega realizada',
                icon: negociacion.entregaRealizada ? <CheckCircleFilled style={{ color: '#52c41a' }} /> : undefined,
                content: (
                  <Switch
                    checked={negociacion.entregaRealizada}
                    loading={actualizandoHito === 'entregaRealizada'}
                    disabled={negocioCerrado}
                    onChange={(valor) => handleHito('entregaRealizada', valor)}
                  />
                ),
              },
            ]}
          />
          <Text type="secondary" style={{ display: 'block', marginTop: 16, fontSize: 12 }}>
            Cuando los tres hitos queden en <Tag color="green">activado</Tag>, el sistema cierra la negociación y marca el
            inmueble como <strong>VENDIDO</strong> automáticamente.
          </Text>
        </Card>
      </Col>
    </Row>
  );
}
