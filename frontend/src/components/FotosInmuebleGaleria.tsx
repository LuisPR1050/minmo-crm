import { useEffect, useRef, useState } from 'react';
import { Button, Empty, Image, Popconfirm, Space, Tag, Typography, message } from 'antd';
import { DeleteOutlined, StarFilled, StarOutlined, UploadOutlined } from '@ant-design/icons';
import { archivoAdjuntoApi } from '../api/captacion';
import type { ArchivoAdjunto } from '../types/captacion';
import ImagenProtegida from './ImagenProtegida';

const { Text } = Typography;

const TAMANO_MAXIMO_BYTES = 8 * 1024 * 1024;
const TIPOS_PERMITIDOS = ['image/jpeg', 'image/png', 'image/webp'];

interface Props {
  inmuebleId: string;
  activo?: boolean;
}

export default function FotosInmuebleGaleria({ inmuebleId, activo = true }: Props) {
  const [fotos, setFotos] = useState<ArchivoAdjunto[]>([]);
  const [loading, setLoading] = useState(false);
  const [subiendo, setSubiendo] = useState(false);
  const [actualizandoId, setActualizandoId] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const cargarFotos = async () => {
    setLoading(true);
    try {
      const data = await archivoAdjuntoApi.listarPorInmueble(inmuebleId);
      setFotos(data.filter((f) => f.tipo === 'FOTO'));
    } catch {
      message.error('No se pudieron cargar las fotos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    if (activo) {
      cargarFotos();
    }
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [activo, inmuebleId]);

  const handleSeleccionarArchivo = () => {
    inputRef.current?.click();
  };

  const handleArchivoElegido = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivos = Array.from(event.target.files ?? []);
    event.target.value = '';
    if (archivos.length === 0) return;

    const validos = archivos.filter((archivo) => {
      if (!TIPOS_PERMITIDOS.includes(archivo.type)) {
        message.error(`"${archivo.name}" no es JPEG, PNG o WEBP`);
        return false;
      }
      if (archivo.size > TAMANO_MAXIMO_BYTES) {
        message.error(`"${archivo.name}" pesa más de 8 MB`);
        return false;
      }
      return true;
    });
    if (validos.length === 0) return;

    setSubiendo(true);
    let subidas = 0;
    let yaHayPrincipal = fotos.length > 0;
    for (const archivo of validos) {
      try {
        await archivoAdjuntoApi.subirFoto(inmuebleId, archivo, !yaHayPrincipal);
        yaHayPrincipal = true;
        subidas += 1;
      } catch {
        message.error(`No se pudo subir "${archivo.name}"`);
      }
    }
    setSubiendo(false);
    if (subidas > 0) {
      message.success(subidas === 1 ? 'Foto subida correctamente' : `${subidas} fotos subidas correctamente`);
      cargarFotos();
    }
  };

  const handleMarcarPrincipal = async (id: string) => {
    setActualizandoId(id);
    try {
      await archivoAdjuntoApi.marcarComoPrincipal(id);
      cargarFotos();
    } catch {
      message.error('No se pudo marcar la foto como principal');
    } finally {
      setActualizandoId(null);
    }
  };

  const handleEliminar = async (id: string) => {
    setActualizandoId(id);
    try {
      await archivoAdjuntoApi.eliminar(id);
      setFotos((prev) => prev.filter((f) => f.id !== id));
    } catch {
      message.error('No se pudo eliminar la foto');
    } finally {
      setActualizandoId(null);
    }
  };

  return (
    <div>
      <input
        ref={inputRef}
        type="file"
        accept="image/jpeg,image/png,image/webp"
        multiple
        style={{ display: 'none' }}
        onChange={handleArchivoElegido}
      />
      <Button icon={<UploadOutlined />} loading={subiendo} onClick={handleSeleccionarArchivo} style={{ marginBottom: 16 }}>
        Subir fotos
      </Button>

      {!loading && fotos.length === 0 && <Empty description="Este inmueble aún no tiene fotos" />}

      <Image.PreviewGroup>
        <div
          style={{
            display: 'grid',
            gridTemplateColumns: 'repeat(auto-fill, minmax(160px, 1fr))',
            gap: 16,
          }}
        >
          {fotos.map((foto) => (
            <div key={foto.id} style={{ border: '1px solid #f0f0f0', borderRadius: 8, padding: 8 }}>
              <div style={{ position: 'relative' }}>
                <ImagenProtegida url={foto.url} alt="Foto del inmueble" style={{ borderRadius: 4, objectFit: 'cover' }} height={140} width="100%" />
                {foto.esPrincipal && (
                  <Tag color="gold" style={{ position: 'absolute', top: 4, left: 4 }}>
                    Principal
                  </Tag>
                )}
              </div>
              <Space style={{ marginTop: 8, width: '100%', justifyContent: 'center' }}>
                <Button
                  size="small"
                  type="text"
                  icon={foto.esPrincipal ? <StarFilled style={{ color: '#faad14' }} /> : <StarOutlined />}
                  loading={actualizandoId === foto.id}
                  disabled={foto.esPrincipal}
                  onClick={() => handleMarcarPrincipal(foto.id)}
                  title="Marcar como principal"
                />
                <Popconfirm
                  title="¿Eliminar esta foto?"
                  okText="Eliminar"
                  cancelText="Cancelar"
                  onConfirm={() => handleEliminar(foto.id)}
                >
                  <Button size="small" type="text" danger icon={<DeleteOutlined />} loading={actualizandoId === foto.id} />
                </Popconfirm>
              </Space>
            </div>
          ))}
        </div>
      </Image.PreviewGroup>

      <Text type="secondary" style={{ display: 'block', marginTop: 16, fontSize: 12 }}>
        Formatos permitidos: JPEG, PNG o WEBP. Tamaño máximo: 8 MB.
      </Text>
    </div>
  );
}
