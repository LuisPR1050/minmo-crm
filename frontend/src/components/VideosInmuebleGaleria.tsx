import { useEffect, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Card, Input, Popconfirm, Space, Tag, Typography, message } from 'antd';
import { DeleteOutlined, PlusOutlined, UploadOutlined } from '@ant-design/icons';
import { videoInmuebleApi } from '../api/captacion';
import type { VideoInmueble } from '../types/captacion';
import { abrirDocumentoProtegido } from '../utils/archivosProtegidos';

const { Text } = Typography;

interface Props {
  inmuebleId: string;
}

export default function VideosInmuebleGaleria({ inmuebleId }: Props) {
  const [videos, setVideos] = useState<VideoInmueble[]>([]);
  const [cargando, setCargando] = useState(false);
  const [nuevaUrl, setNuevaUrl] = useState('');
  const [agregando, setAgregando] = useState(false);
  const [subiendo, setSubiendo] = useState(false);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const inputArchivoRef = useRef<HTMLInputElement>(null);

  const cargarVideos = async () => {
    setCargando(true);
    try {
      setVideos(await videoInmuebleApi.listar(inmuebleId));
    } catch {
      message.error('No se pudieron cargar los videos');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarVideos();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmuebleId]);

  const handleAgregarVideo = async () => {
    if (!nuevaUrl.trim()) return;
    setAgregando(true);
    try {
      const creado = await videoInmuebleApi.agregar(inmuebleId, { url: nuevaUrl.trim() });
      setVideos((prev) => [...prev, creado]);
      setNuevaUrl('');
      message.success('Video agregado');
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo agregar el video. Verifica que el enlace empiece con http:// o https://');
    } finally {
      setAgregando(false);
    }
  };

  const handleSubirArchivos = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivos = Array.from(event.target.files ?? []);
    event.target.value = '';
    if (archivos.length === 0) return;

    setSubiendo(true);
    let subidos = 0;
    for (const archivo of archivos) {
      try {
        const creado = await videoInmuebleApi.subirArchivo(inmuebleId, archivo);
        setVideos((prev) => [...prev, creado]);
        subidos += 1;
      } catch (error) {
        const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
        message.error(detalle ?? `No se pudo subir el video "${archivo.name}". Verifica que sea MP4, MOV, WEBM o MKV de máximo 100MB.`);
      }
    }
    setSubiendo(false);
    if (subidos > 0) {
      message.success(subidos === 1 ? 'Video subido correctamente' : `${subidos} videos subidos correctamente`);
    }
  };

  const handleVerVideoSubido = (video: VideoInmueble) => {
    abrirDocumentoProtegido(video.url).catch(() => message.error('No se pudo abrir el video'));
  };

  const handleEliminarVideo = async (id: string) => {
    setEliminandoId(id);
    try {
      await videoInmuebleApi.eliminar(inmuebleId, id);
      setVideos((prev) => prev.filter((v) => v.id !== id));
    } catch {
      message.error('No se pudo eliminar el video');
    } finally {
      setEliminandoId(null);
    }
  };

  return (
    <Card title="Videos" size="small" loading={cargando}>
      <Space orientation="vertical" size={12} style={{ width: '100%' }}>
        <Space.Compact style={{ width: '100%' }}>
          <Input
            placeholder="Pega un enlace de YouTube, Vimeo o video (https://...)"
            value={nuevaUrl}
            onChange={(e) => setNuevaUrl(e.target.value)}
            onPressEnter={handleAgregarVideo}
          />
          <Button type="primary" icon={<PlusOutlined />} loading={agregando} onClick={handleAgregarVideo}>
            Agregar link
          </Button>
        </Space.Compact>

        <input
          ref={inputArchivoRef}
          type="file"
          accept="video/mp4,video/quicktime,video/webm,video/x-matroska"
          multiple
          style={{ display: 'none' }}
          onChange={handleSubirArchivos}
        />
        <Button icon={<UploadOutlined />} loading={subiendo} onClick={() => inputArchivoRef.current?.click()}>
          Subir video(s) (máx. 100MB cada uno)
        </Button>
      </Space>

      {videos.length === 0 ? (
        <Text type="secondary">Sin videos agregados</Text>
      ) : (
        <Space orientation="vertical" size={8} style={{ width: '100%', marginTop: 12 }}>
          {videos.map((video) => (
            <Space key={video.id} style={{ width: '100%', justifyContent: 'space-between' }}>
              <Space>
                <Tag color={video.origen === 'ARCHIVO_SUBIDO' ? 'blue' : 'default'}>
                  {video.origen === 'ARCHIVO_SUBIDO' ? 'Subido' : 'Link externo'}
                </Tag>
                {video.origen === 'ARCHIVO_SUBIDO' ? (
                  <Button type="link" size="small" style={{ padding: 0, height: 'auto' }} onClick={() => handleVerVideoSubido(video)}>
                    Ver video
                  </Button>
                ) : (
                  <a href={video.url} target="_blank" rel="noreferrer" style={{ wordBreak: 'break-all' }}>
                    {video.url}
                  </a>
                )}
              </Space>
              <Popconfirm
                title="Eliminar video"
                description="¿Seguro que quieres eliminar este video?"
                okText="Eliminar"
                cancelText="Cancelar"
                okButtonProps={{ danger: true }}
                onConfirm={() => handleEliminarVideo(video.id)}
              >
                <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === video.id} />
              </Popconfirm>
            </Space>
          ))}
        </Space>
      )}
    </Card>
  );
}
