import { useEffect, useState } from 'react';
import { Alert, Button, Card, Col, Form, Row, Select, Skeleton, Switch, message } from 'antd';
import InputMoneda from '../../../components/InputMoneda';
import { SaveOutlined } from '@ant-design/icons';
import { checklistJuridicoApi, inmuebleApi } from '../../../api/captacion';
import { ESTADOS_JURIDICOS, ETIQUETA_ESTADO_JURIDICO } from '../../../types/captacion';
import type { ChecklistJuridicoRequest, Inmueble, InmuebleRequest } from '../../../types/captacion';

interface Props {
  inmueble: Inmueble;
  onGuardado: () => void;
}

interface FormValues {
  tieneHipoteca: boolean;
  valorHipoteca?: number;
  tieneEmbargo: boolean;
  valorEmbargo?: number;
  tienePatrimonioFamilia: boolean;
  tieneViviendaFamiliar: boolean;
}

interface EstadoFormValues {
  estadoJuridico: Inmueble['estadoJuridico'];
  valorAdeudado?: number;
}

export default function JuridicoTab({ inmueble, onGuardado }: Props) {
  const inmuebleId = inmueble.id;
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [guardandoEstado, setGuardandoEstado] = useState(false);
  const [form] = Form.useForm<FormValues>();
  const [formEstado] = Form.useForm<EstadoFormValues>();
  const hipoteca = Form.useWatch('tieneHipoteca', form);
  const embargo = Form.useWatch('tieneEmbargo', form);

  const cargar = async () => {
    setLoading(true);
    try {
      const checklist = await checklistJuridicoApi.buscarPorInmueble(inmuebleId);
      if (checklist) {
        form.setFieldsValue({
          tieneHipoteca: checklist.tieneHipoteca,
          valorHipoteca: checklist.valorHipoteca,
          tieneEmbargo: checklist.tieneEmbargo,
          valorEmbargo: checklist.valorEmbargo,
          tienePatrimonioFamilia: checklist.tienePatrimonioFamilia,
          tieneViviendaFamiliar: checklist.tieneViviendaFamiliar,
        });
      }
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmuebleId]);

  const handleGuardar = async (values: FormValues) => {
    setGuardando(true);
    try {
      const request: ChecklistJuridicoRequest = {
        inmuebleId,
        tieneHipoteca: values.tieneHipoteca ?? false,
        valorHipoteca: values.valorHipoteca,
        tieneEmbargo: values.tieneEmbargo ?? false,
        valorEmbargo: values.valorEmbargo,
        tienePatrimonioFamilia: values.tienePatrimonioFamilia ?? false,
        tieneViviendaFamiliar: values.tieneViviendaFamiliar ?? false,
      };
      await checklistJuridicoApi.guardar(request);
      message.success('Checklist jurídico guardado');
    } catch {
      message.error('No se pudo guardar el checklist jurídico');
    } finally {
      setGuardando(false);
    }
  };

  const handleGuardarEstado = async (values: EstadoFormValues) => {
    setGuardandoEstado(true);
    try {
      const payload: InmuebleRequest = {
        estadoJuridico: values.estadoJuridico,
        valorAdeudado: values.valorAdeudado,
      };
      await inmuebleApi.actualizar(inmuebleId, payload);
      message.success('Estado jurídico actualizado');
      onGuardado();
    } catch {
      message.error('No se pudo guardar el estado jurídico');
    } finally {
      setGuardandoEstado(false);
    }
  };

  if (loading) return <Skeleton active />;

  const hayRiesgoJuridico = hipoteca || embargo;

  return (
    <Card size="small" style={{ maxWidth: 560 }}>
      {hayRiesgoJuridico && (
        <Alert
          type="warning"
          showIcon
          title="Este inmueble tiene condiciones jurídicas que revisar antes de negociar"
          style={{ marginBottom: 16 }}
        />
      )}
      <Form
        form={formEstado}
        layout="vertical"
        onFinish={handleGuardarEstado}
        initialValues={{
          estadoJuridico: inmueble.estadoJuridico,
          valorAdeudado: inmueble.valorAdeudado,
        }}
        style={{ marginBottom: 24 }}
      >
        <Form.Item name="estadoJuridico" label="Estado jurídico general">
          <Select options={ESTADOS_JURIDICOS.map((e) => ({ value: e, label: ETIQUETA_ESTADO_JURIDICO[e] }))} />
        </Form.Item>
        <Form.Item name="valorAdeudado" label="Saldo Pendiente">
          <InputMoneda />
        </Form.Item>
        <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={guardandoEstado}>
          Guardar estado jurídico
        </Button>
      </Form>
      <Form
        form={form}
        layout="vertical"
        onFinish={handleGuardar}
        initialValues={{
          tieneHipoteca: false,
          tieneEmbargo: false,
          tienePatrimonioFamilia: false,
          tieneViviendaFamiliar: false,
        }}
      >
        <Row gutter={8} align="middle">
          <Col xs={24} sm={12}>
            <Form.Item name="tieneHipoteca" label="¿Tiene hipoteca?" valuePropName="checked">
              <Switch />
            </Form.Item>
          </Col>
          {hipoteca && (
            <Col xs={24} sm={12}>
              <Form.Item name="valorHipoteca" label="Valor de la hipoteca">
                <InputMoneda />
              </Form.Item>
            </Col>
          )}
        </Row>
        <Row gutter={8} align="middle">
          <Col xs={24} sm={12}>
            <Form.Item name="tieneEmbargo" label="¿Tiene embargo?" valuePropName="checked">
              <Switch />
            </Form.Item>
          </Col>
          {embargo && (
            <Col xs={24} sm={12}>
              <Form.Item name="valorEmbargo" label="Valor del embargo">
                <InputMoneda />
              </Form.Item>
            </Col>
          )}
        </Row>
        <Form.Item name="tienePatrimonioFamilia" label="¿Tiene patrimonio de familia?" valuePropName="checked">
          <Switch />
        </Form.Item>
        <Form.Item name="tieneViviendaFamiliar" label="¿Es afectación a vivienda familiar?" valuePropName="checked">
          <Switch />
        </Form.Item>
        <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={guardando}>
          Guardar checklist jurídico
        </Button>
      </Form>
    </Card>
  );
}
