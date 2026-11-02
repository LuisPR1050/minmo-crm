import { useState } from 'react';
import { Button, Checkbox, Descriptions, Form, Select, Space, Typography, message } from 'antd';
import { RobotOutlined } from '@ant-design/icons';
import { publicacionInmuebleApi } from '../../../api/marketing';
import { ESTADOS_PUBLICACION, ETIQUETA_ESTADO_PUBLICACION, TIPOS_OPERACION_PUBLICACION } from '../../../types/marketing';
import type {
  ActualizarPublicacionInmuebleRequest,
  EstadoPublicacion,
  PublicacionInmueble,
  TipoOperacionPublicacion,
} from '../../../types/marketing';

const { Text, Paragraph } = Typography;

interface Props {
  publicacion: PublicacionInmueble;
  onCambio: (actualizada: PublicacionInmueble) => void;
}

interface FormValues {
  tipoOperacion: TipoOperacionPublicacion;
  mostrarPrecio: boolean;
  mostrarContacto: boolean;
  mostrarCorreo: boolean;
  comisionCompartida: boolean;
  destacada: boolean;
}

export default function GeneralTab({ publicacion, onCambio }: Props) {
  const [guardando, setGuardando] = useState(false);
  const [cambiandoEstado, setCambiandoEstado] = useState(false);
  const [generandoTexto, setGenerandoTexto] = useState(false);
  const [form] = Form.useForm<FormValues>();

  const handleGuardar = async (values: FormValues) => {
    const request: ActualizarPublicacionInmuebleRequest = { ...values };
    setGuardando(true);
    try {
      const actualizada = await publicacionInmuebleApi.actualizar(publicacion.id, request);
      onCambio(actualizada);
      message.success('Publicación actualizada');
    } catch {
      message.error('No se pudo actualizar la publicación');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambiarEstado = async (nuevoEstado: EstadoPublicacion) => {
    setCambiandoEstado(true);
    try {
      const actualizada = await publicacionInmuebleApi.cambiarEstado(publicacion.id, nuevoEstado);
      onCambio(actualizada);
      message.success('Estado actualizado');
    } catch {
      message.error('No se pudo cambiar el estado');
    } finally {
      setCambiandoEstado(false);
    }
  };

  const handleGenerarDescripciones = async () => {
    setGenerandoTexto(true);
    try {
      const actualizada = await publicacionInmuebleApi.generarDescripciones(publicacion.id);
      onCambio(actualizada);
      message.success('Descripciones generadas con IA');
    } catch {
      message.warning(
        'No se pudo generar con IA. Lo más probable es que aún no esté configurada la clave de la API de Anthropic en este entorno.',
      );
    } finally {
      setGenerandoTexto(false);
    }
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <Descriptions column={1} size="small" bordered>
        <Descriptions.Item label="Inmueble">{publicacion.inmuebleCodigo}</Descriptions.Item>
        <Descriptions.Item label="Estado">
          <Select<EstadoPublicacion>
            size="small"
            value={publicacion.estado}
            loading={cambiandoEstado}
            style={{ width: 180 }}
            onChange={handleCambiarEstado}
            options={ESTADOS_PUBLICACION.map((e) => ({ value: e, label: ETIQUETA_ESTADO_PUBLICACION[e] }))}
          />
        </Descriptions.Item>
        <Descriptions.Item label="Fecha de publicación">{publicacion.fechaPublicacion ?? '—'}</Descriptions.Item>
      </Descriptions>

      <Form<FormValues>
        form={form}
        layout="vertical"
        onFinish={handleGuardar}
        initialValues={{
          tipoOperacion: publicacion.tipoOperacion,
          mostrarPrecio: publicacion.mostrarPrecio,
          mostrarContacto: publicacion.mostrarContacto,
          mostrarCorreo: publicacion.mostrarCorreo,
          comisionCompartida: publicacion.comisionCompartida,
          destacada: publicacion.destacada,
        }}
      >
        <Form.Item name="tipoOperacion" label="Estado (venta/arriendo)">
          <Select options={TIPOS_OPERACION_PUBLICACION.map((t) => ({ value: t, label: t }))} />
        </Form.Item>
        <Space orientation="vertical">
          <Form.Item name="mostrarPrecio" valuePropName="checked" noStyle>
            <Checkbox>Mostrar precio</Checkbox>
          </Form.Item>
          <Form.Item name="mostrarContacto" valuePropName="checked" noStyle>
            <Checkbox>Mostrar contacto</Checkbox>
          </Form.Item>
          <Form.Item name="mostrarCorreo" valuePropName="checked" noStyle>
            <Checkbox>Mostrar correo</Checkbox>
          </Form.Item>
          <Form.Item name="comisionCompartida" valuePropName="checked" noStyle>
            <Checkbox>Comisión compartida</Checkbox>
          </Form.Item>
          <Form.Item name="destacada" valuePropName="checked" noStyle>
            <Checkbox>Destacar publicación</Checkbox>
          </Form.Item>
        </Space>
        <div style={{ marginTop: 16 }}>
          <Button type="primary" htmlType="submit" loading={guardando}>
            Guardar
          </Button>
        </div>
      </Form>

      <div>
        <Text strong>Descripción corta</Text>
        <Paragraph type={publicacion.descripcionCorta ? undefined : 'secondary'}>
          {publicacion.descripcionCorta ?? 'Aún no generada'}
        </Paragraph>
        <Text strong>Descripción larga</Text>
        <Paragraph type={publicacion.descripcionLarga ? undefined : 'secondary'}>
          {publicacion.descripcionLarga ?? 'Aún no generada'}
        </Paragraph>
        <Text strong>Descripción optimizada para SEO</Text>
        <Paragraph type={publicacion.descripcionOptimizada ? undefined : 'secondary'}>
          {publicacion.descripcionOptimizada ?? 'Aún no generada'}
        </Paragraph>
        <Button icon={<RobotOutlined />} loading={generandoTexto} onClick={handleGenerarDescripciones}>
          {publicacion.descripcionCorta ? 'Regenerar con IA' : 'Generar con IA'}
        </Button>
      </div>
    </Space>
  );
}
