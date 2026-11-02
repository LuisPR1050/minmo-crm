import { useEffect, useState } from 'react';
import { Alert, Button, Card, Input, Space, Switch, Typography, message } from 'antd';
import { CopyOutlined, LinkOutlined, RobotOutlined } from '@ant-design/icons';
import { inmuebleApi } from '../../../api/captacion';
import type { Inmueble } from '../../../types/captacion';

const { Text, Paragraph } = Typography;
const { TextArea } = Input;

interface Props {
  inmueble: Inmueble;
  onCambio: (actualizado: Inmueble) => void;
}

export default function LandingTab({ inmueble, onCambio }: Props) {
  const [descripcion, setDescripcion] = useState(inmueble.descripcionPublica ?? '');
  const [guardandoDescripcion, setGuardandoDescripcion] = useState(false);
  const [ganchoEmocional, setGanchoEmocional] = useState(inmueble.ganchoEmocional ?? '');
  const [guardandoGancho, setGuardandoGancho] = useState(false);
  const [generandoGancho, setGenerandoGancho] = useState(false);
  const [cambiandoActiva, setCambiandoActiva] = useState(false);
  const [cambiandoPrecio, setCambiandoPrecio] = useState(false);

  const enlacePublico = `${window.location.origin}/p/${inmueble.id}`;

  useEffect(() => {
    setDescripcion(inmueble.descripcionPublica ?? '');
  }, [inmueble.descripcionPublica]);

  useEffect(() => {
    setGanchoEmocional(inmueble.ganchoEmocional ?? '');
  }, [inmueble.ganchoEmocional]);

  const handleCambiarActiva = async (activa: boolean) => {
    setCambiandoActiva(true);
    try {
      const actualizado = await inmuebleApi.actualizarLanding(inmueble.id, { landingActiva: activa });
      onCambio(actualizado);
      message.success(activa ? 'Landing page publicada' : 'Landing page ocultada');
    } catch {
      message.error('No se pudo actualizar la landing page');
    } finally {
      setCambiandoActiva(false);
    }
  };

  const handleCambiarMostrarPrecio = async (mostrar: boolean) => {
    setCambiandoPrecio(true);
    try {
      const actualizado = await inmuebleApi.actualizarLanding(inmueble.id, { landingMostrarPrecio: mostrar });
      onCambio(actualizado);
    } catch {
      message.error('No se pudo actualizar la landing page');
    } finally {
      setCambiandoPrecio(false);
    }
  };

  const handleGuardarDescripcion = async () => {
    setGuardandoDescripcion(true);
    try {
      const actualizado = await inmuebleApi.actualizarLanding(inmueble.id, { descripcionPublica: descripcion });
      onCambio(actualizado);
      message.success('Descripción guardada');
    } catch {
      message.error('No se pudo guardar la descripción');
    } finally {
      setGuardandoDescripcion(false);
    }
  };

  const handleGuardarGancho = async () => {
    setGuardandoGancho(true);
    try {
      const actualizado = await inmuebleApi.actualizarLanding(inmueble.id, { ganchoEmocional });
      onCambio(actualizado);
      message.success('Gancho emocional guardado');
    } catch {
      message.error('No se pudo guardar el gancho emocional');
    } finally {
      setGuardandoGancho(false);
    }
  };

  const handleGenerarGanchoConIA = async () => {
    setGenerandoGancho(true);
    try {
      const actualizado = await inmuebleApi.generarGanchoEmocionalConIA(inmueble.id);
      onCambio(actualizado);
      message.success('Gancho emocional generado con IA');
    } catch {
      message.warning(
        'No se pudo generar con IA. Lo más probable es que aún no esté configurada la clave de la API de Anthropic en este entorno.',
      );
    } finally {
      setGenerandoGancho(false);
    }
  };

  const handleCopiarEnlace = async () => {
    try {
      await navigator.clipboard.writeText(enlacePublico);
      message.success('Link copiado al portapapeles');
    } catch {
      message.error('No se pudo copiar el link. Cópialo manualmente.');
    }
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%', maxWidth: 780 }}>
      <Alert
        type="info"
        showIcon
        title="Landing page pública"
        description="Este link muestra la descripción, fotos y videos de este inmueble a cualquier persona que lo abra, sin necesidad de iniciar sesión. Compártelo con tus clientes por WhatsApp, correo, etc."
      />

      <Card size="small">
        <Space orientation="vertical" size={12} style={{ width: '100%' }}>
          <Space align="center">
            <Switch checked={inmueble.landingActiva} loading={cambiandoActiva} onChange={handleCambiarActiva} />
            <Text strong>{inmueble.landingActiva ? 'Landing publicada' : 'Landing oculta'}</Text>
          </Space>
          <Space align="center">
            <Switch checked={inmueble.landingMostrarPrecio} loading={cambiandoPrecio} onChange={handleCambiarMostrarPrecio} />
            <Text>Mostrar precio en la landing</Text>
          </Space>

          <Space.Compact style={{ width: '100%' }}>
            <Input prefix={<LinkOutlined />} value={enlacePublico} readOnly />
            <Button icon={<CopyOutlined />} onClick={handleCopiarEnlace}>
              Copiar
            </Button>
            <Button type="primary" href={enlacePublico} target="_blank" rel="noreferrer" disabled={!inmueble.landingActiva}>
              Ver landing
            </Button>
          </Space.Compact>
          {!inmueble.landingActiva && (
            <Text type="secondary" style={{ fontSize: 12 }}>
              La landing está oculta: actívala para que el link funcione públicamente.
            </Text>
          )}
        </Space>
      </Card>

      <Card title="Gancho emocional" size="small">
        <Paragraph type="secondary" style={{ fontSize: 12 }}>
          Una frase corta y destacada que aparece justo debajo del título en la landing, pensada para generarle al
          cliente la necesidad de preguntar por más. No reemplaza la descripción de abajo.
        </Paragraph>
        <TextArea
          rows={2}
          value={ganchoEmocional}
          onChange={(e) => setGanchoEmocional(e.target.value)}
          maxLength={400}
          showCount
          placeholder="Ej: El amanecer que siempre soñaste ver desde tu propia sala."
        />
        <Space style={{ marginTop: 8 }}>
          <Button type="primary" loading={guardandoGancho} onClick={handleGuardarGancho}>
            Guardar
          </Button>
          <Button icon={<RobotOutlined />} loading={generandoGancho} onClick={handleGenerarGanchoConIA}>
            {inmueble.ganchoEmocional ? 'Regenerar con IA' : 'Generar con IA'}
          </Button>
        </Space>
      </Card>

      <Card title="Descripción pública" size="small">
        <Paragraph type="secondary" style={{ fontSize: 12 }}>
          Este texto es el que ve el cliente en la landing. Las fotos se administran en la pestaña "Fotos".
        </Paragraph>
        <TextArea rows={5} value={descripcion} onChange={(e) => setDescripcion(e.target.value)} maxLength={2000} showCount />
        <Button type="primary" style={{ marginTop: 8 }} loading={guardandoDescripcion} onClick={handleGuardarDescripcion}>
          Guardar descripción
        </Button>
      </Card>
    </Space>
  );
}
