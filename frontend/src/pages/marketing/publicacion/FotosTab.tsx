import { useEffect, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import { Alert, Button, Empty, Image, List, Modal, Space, Tag, Typography, message } from 'antd';
import { BulbOutlined, StarFilled, StarOutlined, UploadOutlined } from '@ant-design/icons';
import { publicacionInmuebleApi, fotoPublicacionApi } from '../../../api/marketing';
import type { AnalisisFotos, FotoPublicacion } from '../../../types/marketing';
import ImagenProtegida from '../../../components/ImagenProtegida';

const { Text } = Typography;

interface Props {
  publicacionInmuebleId: string;
}

export default function FotosTab({ publicacionInmuebleId }: Props) {
  const [fotos, setFotos] = useState<FotoPublicacion[]>([]);
  const [loading, setLoading] = useState(false);
  const [subiendo, setSubiendo] = useState(false);
  const [analizando, setAnalizando] = useState(false);
  const [analisis, setAnalisis] = useState<AnalisisFotos | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      setFotos(await fotoPublicacionApi.listarPorPublicacion(publicacionInmuebleId));
    } catch {
      message.error('No se pudieron cargar las fotos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [publicacionInmuebleId]);

  const handleSeleccionarArchivo = () => inputRef.current?.click();

  const handleArchivoElegido = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendo(true);
    try {
      await fotoPublicacionApi.subir(publicacionInmuebleId, archivo, fotos.length === 0);
      message.success('Foto subida correctamente');
      cargar();
    } catch {
      message.error('No se pudo subir la foto');
    } finally {
      setSubiendo(false);
    }
  };

  const handleAnalizar = async () => {
    setAnalizando(true);
    try {
      const data = await publicacionInmuebleApi.analizarFotosConIA(publicacionInmuebleId);
      setAnalisis(data);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo analizar las fotos');
    } finally {
      setAnalizando(false);
    }
  };

  return (
    <div>
      <input
        ref={inputRef}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        style={{ display: 'none' }}
        onChange={handleArchivoElegido}
      />
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<UploadOutlined />} loading={subiendo} onClick={handleSeleccionarArchivo}>
          Subir foto
        </Button>
        <Button icon={<BulbOutlined />} loading={analizando} onClick={handleAnalizar} disabled={fotos.length === 0}>
          Analizar fotos con IA
        </Button>
      </Space>

      {!loading && fotos.length === 0 && <Empty description="Sin fotos para esta publicación" />}

      <Image.PreviewGroup>
        <div style={{ display: 'grid', gridTemplateColumns: 'repeat(auto-fill, minmax(140px, 1fr))', gap: 12 }}>
          {fotos.map((foto) => (
            <div key={foto.id} style={{ border: '1px solid #f0f0f0', borderRadius: 8, padding: 6 }}>
              <div style={{ position: 'relative' }}>
                <ImagenProtegida url={foto.url} alt="Foto de publicación" style={{ borderRadius: 4, objectFit: 'cover' }} height={110} width="100%" />
                {foto.esPrincipal && (
                  <Tag color="gold" style={{ position: 'absolute', top: 4, left: 4 }}>
                    <StarFilled /> Principal
                  </Tag>
                )}
              </div>
            </div>
          ))}
        </div>
      </Image.PreviewGroup>

      <Text type="secondary" style={{ display: 'block', marginTop: 16, fontSize: 12 }}>
        <StarOutlined /> La primera foto subida se marca automáticamente como principal.
      </Text>

      <Modal
        title="Análisis de fotos con IA"
        open={analisis !== null}
        onCancel={() => setAnalisis(null)}
        footer={<Button onClick={() => setAnalisis(null)}>Cerrar</Button>}
        width={560}
      >
        {analisis && (
          <Space orientation="vertical" size={16} style={{ width: '100%' }}>
            <Alert
              type={analisis.puntuacionGeneral >= 7 ? 'success' : analisis.puntuacionGeneral >= 4 ? 'warning' : 'error'}
              showIcon
              title={`Puntuación: ${analisis.puntuacionGeneral} / 10 (basado en ${analisis.fotosAnalizadas} foto(s))`}
            />
            {analisis.fortalezas.length > 0 && (
              <div>
                <Text strong>Fortalezas</Text>
                <List size="small" dataSource={analisis.fortalezas} renderItem={(item) => <List.Item>{item}</List.Item>} />
              </div>
            )}
            {analisis.problemas.length > 0 && (
              <div>
                <Text strong>Problemas detectados</Text>
                <List size="small" dataSource={analisis.problemas} renderItem={(item) => <List.Item>{item}</List.Item>} />
              </div>
            )}
            {analisis.recomendaciones.length > 0 && (
              <div>
                <Text strong>Recomendaciones</Text>
                <List size="small" dataSource={analisis.recomendaciones} renderItem={(item) => <List.Item>{item}</List.Item>} />
              </div>
            )}
            {analisis.fotosFaltantes.length > 0 && (
              <div>
                <Text strong>Fotos que podrían faltar</Text>
                <List size="small" dataSource={analisis.fotosFaltantes} renderItem={(item) => <List.Item>{item}</List.Item>} />
              </div>
            )}
          </Space>
        )}
      </Modal>
    </div>
  );
}
