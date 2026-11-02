import { useState } from 'react';
import { Button, Descriptions, Form, Select, Slider, Space, message } from 'antd';
import {
  ESTADOS_SOLICITUD_VIDEO,
  ESTILOS_CONTENIDO_VIDEO,
  ETIQUETA_ESTADO_SOLICITUD_VIDEO,
  ETIQUETA_LLAMADO_ACCION,
  ETIQUETA_OBJETIVO_VIDEO,
  LLAMADOS_ACCION_VIDEO,
  OBJETIVOS_VIDEO_MARKETING,
} from '../../../types/marketing';
import { solicitudVideoApi } from '../../../api/marketing';
import type {
  ActualizarSolicitudVideoMarketingRequest,
  EstadoSolicitudVideo,
  EstiloContenidoVideo,
  LlamadoAccionVideo,
  ObjetivoVideoMarketing,
  SolicitudVideoMarketing,
} from '../../../types/marketing';

interface Props {
  solicitud: SolicitudVideoMarketing;
  onCambio: (actualizada: SolicitudVideoMarketing) => void;
}

interface FormValues {
  objetivoPublicacion?: ObjetivoVideoMarketing;
  estiloContenido?: EstiloContenidoVideo;
  duracionMinutos?: [number, number];
  llamadoAccion?: LlamadoAccionVideo;
}

export default function GeneralTab({ solicitud, onCambio }: Props) {
  const [guardando, setGuardando] = useState(false);
  const [cambiandoEstado, setCambiandoEstado] = useState(false);
  const [form] = Form.useForm<FormValues>();

  const handleGuardar = async (values: FormValues) => {
    const { duracionMinutos, ...resto } = values;
    const request: ActualizarSolicitudVideoMarketingRequest = {
      ...resto,
      duracionMinimaMinutos: duracionMinutos?.[0],
      duracionMaximaMinutos: duracionMinutos?.[1],
    };
    setGuardando(true);
    try {
      const actualizada = await solicitudVideoApi.actualizar(solicitud.id, request);
      onCambio(actualizada);
      message.success('Solicitud actualizada');
    } catch {
      message.error('No se pudo actualizar la solicitud');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambiarEstado = async (nuevoEstado: EstadoSolicitudVideo) => {
    setCambiandoEstado(true);
    try {
      const actualizada = await solicitudVideoApi.cambiarEstado(solicitud.id, nuevoEstado);
      onCambio(actualizada);
      message.success('Estado actualizado');
    } catch {
      message.error('No se pudo cambiar el estado');
    } finally {
      setCambiandoEstado(false);
    }
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <Descriptions column={1} size="small" bordered>
        <Descriptions.Item label="Inmueble">{solicitud.inmuebleCodigo}</Descriptions.Item>
        <Descriptions.Item label="Estado">
          <Select<EstadoSolicitudVideo>
            size="small"
            value={solicitud.estado}
            loading={cambiandoEstado}
            style={{ width: 180 }}
            onChange={handleCambiarEstado}
            options={ESTADOS_SOLICITUD_VIDEO.map((e) => ({ value: e, label: ETIQUETA_ESTADO_SOLICITUD_VIDEO[e] }))}
          />
        </Descriptions.Item>
      </Descriptions>

      <Form<FormValues>
        form={form}
        layout="vertical"
        onFinish={handleGuardar}
        initialValues={{
          objetivoPublicacion: solicitud.objetivoPublicacion,
          estiloContenido: solicitud.estiloContenido,
          duracionMinutos:
            solicitud.duracionMinimaMinutos != null && solicitud.duracionMaximaMinutos != null
              ? [solicitud.duracionMinimaMinutos, solicitud.duracionMaximaMinutos]
              : [1, 2],
          llamadoAccion: solicitud.llamadoAccion,
        }}
      >
        <Form.Item name="objetivoPublicacion" label="Objetivo de la publicación">
          <Select options={OBJETIVOS_VIDEO_MARKETING.map((o) => ({ value: o, label: ETIQUETA_OBJETIVO_VIDEO[o] }))} allowClear />
        </Form.Item>
        <Form.Item name="estiloContenido" label="Estilo del contenido">
          <Select options={ESTILOS_CONTENIDO_VIDEO.map((e) => ({ value: e, label: e }))} allowClear />
        </Form.Item>
        <Form.Item name="duracionMinutos" label="Duración del video (minutos)">
          <Slider
            range
            min={0.5}
            max={10}
            step={0.5}
            marks={{ 0.5: '0.5', 1: '1', 2: '2', 5: '5', 10: '10' }}
            tooltip={{ formatter: (valor) => `${valor} min` }}
          />
        </Form.Item>
        <Form.Item name="llamadoAccion" label="Llamado a la acción">
          <Select options={LLAMADOS_ACCION_VIDEO.map((l) => ({ value: l, label: ETIQUETA_LLAMADO_ACCION[l] }))} allowClear />
        </Form.Item>
        <Button type="primary" htmlType="submit" loading={guardando}>
          Guardar
        </Button>
      </Form>
    </Space>
  );
}
