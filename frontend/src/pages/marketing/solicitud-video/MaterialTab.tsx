import { useEffect, useRef, useState } from 'react';
import { Avatar, Button, Card, Empty, Form, Image, Input, Space, Tag, Typography, message } from 'antd';
import { CheckCircleFilled, LinkOutlined, PictureOutlined, UploadOutlined, VideoCameraOutlined } from '@ant-design/icons';
import ItemList from '../../../components/ItemList';
import ImagenProtegida from '../../../components/ImagenProtegida';
import { fotoSolicitudVideoApi, solicitudVideoApi, videoFuenteSolicitudApi } from '../../../api/marketing';
import { archivoAdjuntoApi, videoInmuebleApi } from '../../../api/captacion';
import type { FotoSolicitudVideo, SolicitudVideoMarketing, VideoFuenteSolicitud, VideoFuenteSolicitudRequest } from '../../../types/marketing';
import type { ArchivoAdjunto, VideoInmueble } from '../../../types/captacion';
import { obtenerBlobUrl } from '../../../utils/archivosProtegidos';

const { Text } = Typography;

interface Props {
  solicitud: SolicitudVideoMarketing;
  onCambio: (actualizada: SolicitudVideoMarketing) => void;
}

export default function MaterialTab({ solicitud, onCambio }: Props) {
  const [fotos, setFotos] = useState<FotoSolicitudVideo[]>([]);
  const [videosFuente, setVideosFuente] = useState<VideoFuenteSolicitud[]>([]);
  const [fotosCaptacion, setFotosCaptacion] = useState<ArchivoAdjunto[]>([]);
  const [videosCaptacion, setVideosCaptacion] = useState<VideoInmueble[]>([]);
  const [subiendoFoto, setSubiendoFoto] = useState(false);
  const [subiendoLogo, setSubiendoLogo] = useState(false);
  const [agregandoVideo, setAgregandoVideo] = useState(false);
  const [importandoFotoId, setImportandoFotoId] = useState<string | null>(null);
  const [importandoVideoId, setImportandoVideoId] = useState<string | null>(null);
  const inputFotoRef = useRef<HTMLInputElement>(null);
  const inputLogoRef = useRef<HTMLInputElement>(null);
  const [form] = Form.useForm<{ url: string }>();
  const [logoBlobUrl, setLogoBlobUrl] = useState<string | undefined>(undefined);

  const cargar = async () => {
    try {
      const [fotosData, videosData, fotosCaptacionData, videosCaptacionData] = await Promise.all([
        fotoSolicitudVideoApi.listarPorSolicitud(solicitud.id),
        videoFuenteSolicitudApi.listarPorSolicitud(solicitud.id),
        archivoAdjuntoApi.listarPorInmueble(solicitud.inmuebleId),
        videoInmuebleApi.listar(solicitud.inmuebleId),
      ]);
      setFotos(fotosData);
      setVideosFuente(videosData);
      setFotosCaptacion(fotosCaptacionData);
      setVideosCaptacion(videosCaptacionData);
    } catch {
      message.error('No se pudo cargar el material visual');
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [solicitud.id]);

  useEffect(() => {
    if (!solicitud.logoUrl) {
      setLogoBlobUrl(undefined);
      return;
    }
    let cancelado = false;
    let objectUrl: string | undefined;
    obtenerBlobUrl(solicitud.logoUrl)
      .then((creado) => {
        if (cancelado) {
          URL.revokeObjectURL(creado);
          return;
        }
        objectUrl = creado;
        setLogoBlobUrl(creado);
      })
      .catch(() => {
        if (!cancelado) setLogoBlobUrl(undefined);
      });
    return () => {
      cancelado = true;
      if (objectUrl) URL.revokeObjectURL(objectUrl);
    };
  }, [solicitud.logoUrl]);

  const handleSubirFoto = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendoFoto(true);
    try {
      await fotoSolicitudVideoApi.subir(solicitud.id, archivo);
      message.success('Foto subida correctamente');
      cargar();
    } catch {
      message.error('No se pudo subir la foto');
    } finally {
      setSubiendoFoto(false);
    }
  };

  const handleSubirLogo = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendoLogo(true);
    try {
      const actualizada = await solicitudVideoApi.subirLogo(solicitud.id, archivo);
      onCambio(actualizada);
      message.success('Logo subido correctamente');
    } catch {
      message.error('No se pudo subir el logo');
    } finally {
      setSubiendoLogo(false);
    }
  };

  const handleAgregarVideo = async (values: { url: string }) => {
    const request: VideoFuenteSolicitudRequest = { url: values.url, tipoOrigen: 'ENLACE' };
    setAgregandoVideo(true);
    try {
      const creado = await videoFuenteSolicitudApi.agregar(solicitud.id, request);
      setVideosFuente((prev) => [...prev, creado]);
      form.resetFields();
      message.success('Enlace de video agregado');
    } catch {
      message.error('No se pudo agregar el enlace de video');
    } finally {
      setAgregandoVideo(false);
    }
  };

  const handleImportarFoto = async (foto: ArchivoAdjunto) => {
    setImportandoFotoId(foto.id);
    try {
      await fotoSolicitudVideoApi.agregar(solicitud.id, { url: foto.url });
      message.success('Foto agregada desde captación');
      cargar();
    } catch {
      message.error('No se pudo agregar la foto');
    } finally {
      setImportandoFotoId(null);
    }
  };

  const handleImportarVideo = async (video: VideoInmueble) => {
    setImportandoVideoId(video.id);
    try {
      const request: VideoFuenteSolicitudRequest = {
        url: video.url,
        tipoOrigen: video.origen === 'ARCHIVO_SUBIDO' ? 'SUBIDO' : 'ENLACE',
      };
      await videoFuenteSolicitudApi.agregar(solicitud.id, request);
      message.success('Video agregado desde captación');
      cargar();
    } catch {
      message.error('No se pudo agregar el video');
    } finally {
      setImportandoVideoId(null);
    }
  };

  const urlsFotosAgregadas = new Set(fotos.map((f) => f.url));
  const urlsVideosAgregados = new Set(videosFuente.map((v) => v.url));

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <Card title="Logo" size="small">
        <Space align="center">
          {solicitud.logoUrl ? (
            <Avatar shape="square" size={64} src={logoBlobUrl} icon={!logoBlobUrl ? <PictureOutlined /> : undefined} />
          ) : (
            <Avatar shape="square" size={64} icon={<PictureOutlined />} />
          )}
          <input ref={inputLogoRef} type="file" accept="image/jpeg,image/png,image/webp" style={{ display: 'none' }} onChange={handleSubirLogo} />
          <Button icon={<UploadOutlined />} loading={subiendoLogo} onClick={() => inputLogoRef.current?.click()}>
            {solicitud.logoUrl ? 'Cambiar logo' : 'Subir logo'}
          </Button>
        </Space>
      </Card>

      <Card title="Fotos" size="small">
        <Text strong style={{ display: 'block', marginBottom: 8 }}>
          Fotos ya captadas de este inmueble
        </Text>
        {fotosCaptacion.length === 0 ? (
          <Text type="secondary" style={{ display: 'block', marginBottom: 12, fontSize: 12 }}>
            Este inmueble no tiene fotos en captación todavía.
          </Text>
        ) : (
          <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(100px, 1fr))', gap: 8, marginBottom: 16 }}>
            {fotosCaptacion.map((foto) => {
              const yaAgregada = urlsFotosAgregadas.has(foto.url);
              return (
                <div key={foto.id} style={{ position: 'relative' }}>
                  <ImagenProtegida url={foto.url} alt="Foto" style={{ borderRadius: 4, objectFit: 'cover', opacity: yaAgregada ? 0.5 : 1 }} height={80} width="100%" />
                  {yaAgregada ? (
                    <CheckCircleFilled style={{ position: 'absolute', top: 4, right: 4, color: '#52c41a', fontSize: 18, background: 'white', borderRadius: '50%' }} />
                  ) : (
                    <Button
                      size="small"
                      style={{ position: 'absolute', bottom: 4, left: 4, right: 4 }}
                      loading={importandoFotoId === foto.id}
                      onClick={() => handleImportarFoto(foto)}
                    >
                      Agregar
                    </Button>
                  )}
                </div>
              );
            })}
          </div>
        )}

        <Text strong style={{ display: 'block', marginBottom: 8 }}>
          Fotos agregadas al video
        </Text>
        <input ref={inputFotoRef} type="file" accept="image/jpeg,image/png,image/webp" style={{ display: 'none' }} onChange={handleSubirFoto} />
        <Button icon={<UploadOutlined />} loading={subiendoFoto} onClick={() => inputFotoRef.current?.click()} style={{ marginBottom: 12 }}>
          Subir otra foto
        </Button>
        {fotos.length === 0 ? (
          <Empty description="Sin fotos" />
        ) : (
          <Image.PreviewGroup>
            <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(120px, 1fr))', gap: 10 }}>
              {fotos.map((foto) => (
                <ImagenProtegida key={foto.id} url={foto.url} alt="Foto" style={{ borderRadius: 4, objectFit: 'cover' }} height={90} width="100%" />
              ))}
            </div>
          </Image.PreviewGroup>
        )}
      </Card>

      <Card title="Video" size="small">
        <Text strong style={{ display: 'block', marginBottom: 8 }}>
          Videos ya captados de este inmueble
        </Text>
        {videosCaptacion.length === 0 ? (
          <Text type="secondary" style={{ display: 'block', marginBottom: 12, fontSize: 12 }}>
            Este inmueble no tiene videos en captación todavía.
          </Text>
        ) : (
          <ItemList
            size="small"
            items={videosCaptacion}
            rowKey={(v) => v.id}
            emptyDescription="Sin videos captados"
            renderItem={(v) => {
              const yaAgregado = urlsVideosAgregados.has(v.url);
              return (
                <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                  <Space>
                    <VideoCameraOutlined />
                    <Tag color={v.origen === 'ARCHIVO_SUBIDO' ? 'blue' : 'default'}>
                      {v.origen === 'ARCHIVO_SUBIDO' ? 'Subido' : 'Link externo'}
                    </Tag>
                  </Space>
                  {yaAgregado ? (
                    <Tag icon={<CheckCircleFilled />} color="success">
                      Agregado
                    </Tag>
                  ) : (
                    <Button size="small" loading={importandoVideoId === v.id} onClick={() => handleImportarVideo(v)}>
                      Agregar
                    </Button>
                  )}
                </Space>
              );
            }}
          />
        )}

        <Text strong style={{ display: 'block', margin: '16px 0 8px' }}>
          Pegar otro enlace de video (YouTube, Vimeo, Drive, etc.)
        </Text>
        <Form form={form} layout="inline" onFinish={handleAgregarVideo} style={{ marginBottom: 12 }}>
          <Form.Item name="url" rules={[{ required: true, message: 'Pega un enlace' }]} style={{ flex: 1 }}>
            <Input prefix={<LinkOutlined />} placeholder="https://..." style={{ width: 320 }} />
          </Form.Item>
          <Form.Item>
            <Button type="primary" htmlType="submit" loading={agregandoVideo}>
              Agregar
            </Button>
          </Form.Item>
        </Form>
        <Text strong style={{ display: 'block', marginBottom: 8 }}>
          Videos agregados al video
        </Text>
        <ItemList
          size="small"
          items={videosFuente}
          rowKey={(v) => v.id}
          emptyDescription="Sin videos agregados"
          renderItem={(v) => (
            <Space>
              <VideoCameraOutlined />
              <a href={v.url} target="_blank" rel="noreferrer">
                {v.url}
              </a>
            </Space>
          )}
        />
      </Card>
    </Space>
  );
}
