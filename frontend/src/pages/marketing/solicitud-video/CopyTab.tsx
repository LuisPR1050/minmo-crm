import { useEffect, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Checkbox, Input, Space, Typography, message } from 'antd';
import { CloseCircleOutlined, PlayCircleOutlined, RobotOutlined, UploadOutlined } from '@ant-design/icons';
import { solicitudVideoApi } from '../../../api/marketing';
import { abrirDocumentoProtegido, obtenerBlobUrl } from '../../../utils/archivosProtegidos';
import type { CopiaVideoCanal, SolicitudVideoMarketing } from '../../../types/marketing';

const { Text, Paragraph } = Typography;

interface Props {
  solicitud: SolicitudVideoMarketing;
  onCambio: (actualizada: SolicitudVideoMarketing) => void;
}

export default function CopyTab({ solicitud, onCambio }: Props) {
  const [generando, setGenerando] = useState(false);
  const [instrucciones, setInstrucciones] = useState('');
  const [copiasPorCanal, setCopiasPorCanal] = useState<CopiaVideoCanal[]>([]);
  const [generandoVideo, setGenerandoVideo] = useState(false);
  const [videoBlobUrl, setVideoBlobUrl] = useState<string | null>(null);
  const [incluirVozOff, setIncluirVozOff] = useState(false);
  const [musicaBlobUrl, setMusicaBlobUrl] = useState<string | null>(null);
  const [subiendoMusica, setSubiendoMusica] = useState(false);
  const [quitandoMusica, setQuitandoMusica] = useState(false);
  const inputMusicaRef = useRef<HTMLInputElement>(null);

  const cargarCopias = async () => {
    try {
      setCopiasPorCanal(await solicitudVideoApi.listarCopiasPorCanal(solicitud.id));
    } catch {
      setCopiasPorCanal([]);
    }
  };

  useEffect(() => {
    cargarCopias();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [solicitud.id]);

  useEffect(() => {
    if (!solicitud.videoGeneradoUrl) {
      setVideoBlobUrl(null);
      return;
    }
    if (!solicitud.videoGeneradoUrl.startsWith('/uploads/')) {
      setVideoBlobUrl(solicitud.videoGeneradoUrl);
      return;
    }
    obtenerBlobUrl(solicitud.videoGeneradoUrl)
      .then(setVideoBlobUrl)
      .catch(() => setVideoBlobUrl(null));
  }, [solicitud.videoGeneradoUrl]);

  useEffect(() => {
    if (!solicitud.musicaFondoUrl) {
      setMusicaBlobUrl(null);
      return;
    }
    if (!solicitud.musicaFondoUrl.startsWith('/uploads/')) {
      setMusicaBlobUrl(solicitud.musicaFondoUrl);
      return;
    }
    obtenerBlobUrl(solicitud.musicaFondoUrl)
      .then(setMusicaBlobUrl)
      .catch(() => setMusicaBlobUrl(null));
  }, [solicitud.musicaFondoUrl]);

  const handleSubirMusica = async (archivo: File) => {
    setSubiendoMusica(true);
    try {
      const actualizada = await solicitudVideoApi.subirMusica(solicitud.id, archivo);
      onCambio(actualizada);
      message.success('Música de fondo cargada');
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo subir la música. Verifica que sea un archivo MP3, WAV, M4A u OGG.');
    } finally {
      setSubiendoMusica(false);
      if (inputMusicaRef.current) {
        inputMusicaRef.current.value = '';
      }
    }
  };

  const handleQuitarMusica = async () => {
    setQuitandoMusica(true);
    try {
      const actualizada = await solicitudVideoApi.eliminarMusica(solicitud.id);
      onCambio(actualizada);
      message.success('Se quitó la música personalizada; se usará el tono ambiental por defecto');
    } catch {
      message.error('No se pudo quitar la música');
    } finally {
      setQuitandoMusica(false);
    }
  };

  const handleGenerarCopy = async () => {
    setGenerando(true);
    try {
      const actualizada = await solicitudVideoApi.generarCopy(solicitud.id, {
        instruccionesAdicionales: instrucciones.trim() || undefined,
      });
      onCambio(actualizada);
      cargarCopias();
      message.success('Copy generado con IA');
    } catch {
      message.warning(
        'No se pudo generar con IA. Lo más probable es que aún no esté configurada la clave de la API de Anthropic en este entorno.',
      );
    } finally {
      setGenerando(false);
    }
  };

  const handleGenerarVideo = async () => {
    setGenerandoVideo(true);
    try {
      const conCopy = await solicitudVideoApi.generarCopy(solicitud.id, {
        instruccionesAdicionales: instrucciones.trim() || undefined,
      });
      onCambio(conCopy);
      cargarCopias();
    } catch {
      message.warning(
        'No se pudo regenerar el copy con IA (revisa la clave de la API de Anthropic). Se intentará generar el video con el copy actual.',
      );
    }

    try {
      const actualizada = await solicitudVideoApi.generarVideo(solicitud.id, { incluirVozOff });
      onCambio(actualizada);
      message.success('Video generado correctamente');
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo generar el video. Verifica que la solicitud tenga al menos una foto seleccionada.');
    } finally {
      setGenerandoVideo(false);
    }
  };

  const handleAbrirVideo = (url: string) => {
    if (url.startsWith('/uploads/')) {
      abrirDocumentoProtegido(url).catch(() => message.error('No se pudo abrir el video'));
    } else {
      window.open(url, '_blank', 'noreferrer');
    }
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <div>
        <Text strong>Título</Text>
        <Paragraph type={solicitud.tituloGenerado ? undefined : 'secondary'}>{solicitud.tituloGenerado ?? 'Aún no generado'}</Paragraph>
      </div>
      <div>
        <Text strong>Subtítulo</Text>
        <Paragraph type={solicitud.subtituloGenerado ? undefined : 'secondary'}>
          {solicitud.subtituloGenerado ?? 'Aún no generado'}
        </Paragraph>
      </div>
      <div>
        <Text strong>Descripción</Text>
        <Paragraph type={solicitud.descripcionGenerada ? undefined : 'secondary'}>
          {solicitud.descripcionGenerada ?? 'Aún no generada'}
        </Paragraph>
      </div>
      <div>
        <Text strong>Llamado a la acción</Text>
        <Paragraph type={solicitud.llamadoAccionTexto ? undefined : 'secondary'}>
          {solicitud.llamadoAccionTexto ?? 'Aún no generado'}
        </Paragraph>
      </div>
      <div>
        <Text strong>Hashtags</Text>
        <Paragraph type={solicitud.hashtags ? undefined : 'secondary'}>{solicitud.hashtags ?? 'Aún no generados'}</Paragraph>
      </div>
      {copiasPorCanal.length > 0 && (
        <div>
          <Text strong>Variantes por canal</Text>
          {copiasPorCanal.map((c) => (
            <Paragraph key={c.id}>
              <Text strong>{c.canalNombre}: </Text>
              {c.texto}
            </Paragraph>
          ))}
        </div>
      )}
      <Button type="primary" icon={<RobotOutlined />} loading={generando} onClick={handleGenerarCopy}>
        {solicitud.tituloGenerado ? 'Regenerar con IA' : 'Generar con IA'}
      </Button>

      <div style={{ borderTop: '1px solid #f0f0f0', paddingTop: 16, width: '100%' }}>
        <Text strong>Video (slideshow)</Text>
        <Paragraph type="secondary" style={{ fontSize: 12 }}>
          Arma un video real con las fotos seleccionadas en la pestaña "Material" (el título va en la primera foto y
          el llamado a la acción en la última). Selecciona las fotos antes de generar.
        </Paragraph>
        {videoBlobUrl && (
          // eslint-disable-next-line jsx-a11y/media-has-caption
          <video controls src={videoBlobUrl} style={{ width: '100%', maxWidth: 420, borderRadius: 8, background: '#000', marginBottom: 12 }} />
        )}
        <Input.TextArea
          placeholder="Instrucciones adicionales para la IA antes de generar el video (opcional). Ej: enfatiza la vista al mar, usa un tono mas emocional..."
          value={instrucciones}
          onChange={(e) => setInstrucciones(e.target.value)}
          rows={2}
          style={{ maxWidth: 480, marginBottom: 12 }}
        />
        <Checkbox checked={incluirVozOff} onChange={(e) => setIncluirVozOff(e.target.checked)} style={{ display: 'block', marginBottom: 12 }}>
          Incluir voz en off (lee el título, la descripción y el llamado a la acción)
        </Checkbox>

        <div style={{ marginBottom: 12 }}>
          <Text strong style={{ display: 'block', marginBottom: 4 }}>
            Música de fondo
          </Text>
          <Paragraph type="secondary" style={{ fontSize: 12, marginBottom: 8 }}>
            Si no subes una, se usa un tono ambiental genérico por defecto.
          </Paragraph>
          {musicaBlobUrl && (
            // eslint-disable-next-line jsx-a11y/media-has-caption
            <audio controls src={musicaBlobUrl} style={{ display: 'block', marginBottom: 8, maxWidth: 420 }} />
          )}
          <input
            ref={inputMusicaRef}
            type="file"
            accept="audio/mpeg,audio/mp3,audio/wav,audio/x-wav,audio/mp4,audio/x-m4a,audio/ogg,.mp3,.wav,.m4a,.ogg"
            style={{ display: 'none' }}
            onChange={(e) => {
              const archivo = e.target.files?.[0];
              if (archivo) {
                handleSubirMusica(archivo);
              }
            }}
          />
          <Space>
            <Button
              icon={<UploadOutlined />}
              loading={subiendoMusica}
              onClick={() => inputMusicaRef.current?.click()}
            >
              {solicitud.musicaFondoUrl ? 'Cambiar música' : 'Subir música (desde este dispositivo)'}
            </Button>
            {solicitud.musicaFondoUrl && (
              <Button icon={<CloseCircleOutlined />} loading={quitandoMusica} onClick={handleQuitarMusica}>
                Quitar
              </Button>
            )}
          </Space>
        </div>

        <Space>
          <Button type="primary" icon={<PlayCircleOutlined />} loading={generandoVideo} onClick={handleGenerarVideo}>
            {solicitud.videoGeneradoUrl ? 'Regenerar video' : 'Generar video'}
          </Button>
          {solicitud.videoGeneradoUrl && (
            <Button onClick={() => handleAbrirVideo(solicitud.videoGeneradoUrl!)}>Abrir en pestaña nueva</Button>
          )}
        </Space>
      </div>
    </Space>
  );
}
