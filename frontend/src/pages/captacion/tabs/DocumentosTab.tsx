import { useEffect, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Empty, List, Popconfirm, Select, Space, Tag, Typography, message } from 'antd';
import { DeleteOutlined, FileTextOutlined, UploadOutlined } from '@ant-design/icons';
import { archivoAdjuntoApi } from '../../../api/captacion';
import { abrirDocumentoProtegido } from '../../../utils/archivosProtegidos';
import type { ArchivoAdjunto } from '../../../types/captacion';

const { Text } = Typography;

const TIPOS_DOCUMENTO = [
  { value: 'ESCRITURA', label: 'Escritura' },
  { value: 'CERTIFICADO_TRADICION', label: 'Certificado de tradición y libertad' },
  { value: 'PAGO_PREDIAL', label: 'Pago del predial' },
  { value: 'OTRO', label: 'Otro' },
];

const ETIQUETA_TIPO: Record<string, string> = Object.fromEntries(TIPOS_DOCUMENTO.map((t) => [t.value, t.label]));

interface Props {
  inmuebleId: string;
}

export default function DocumentosTab({ inmuebleId }: Props) {
  const [documentos, setDocumentos] = useState<ArchivoAdjunto[]>([]);
  const [loading, setLoading] = useState(false);
  const [subiendo, setSubiendo] = useState(false);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const [tipoSeleccionado, setTipoSeleccionado] = useState<string>('ESCRITURA');
  const inputRef = useRef<HTMLInputElement>(null);

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await archivoAdjuntoApi.listarPorInmueble(inmuebleId);
      setDocumentos(data.filter((d) => d.tipo !== 'FOTO'));
    } catch {
      message.error('No se pudieron cargar los documentos');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmuebleId]);

  const handleArchivoElegido = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendo(true);
    try {
      await archivoAdjuntoApi.subirDocumento(inmuebleId, archivo, tipoSeleccionado);
      message.success('Documento subido correctamente');
      cargar();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo subir el documento. Verifica que sea PDF, JPEG, PNG o WEBP.');
    } finally {
      setSubiendo(false);
    }
  };

  const handleVer = (documento: ArchivoAdjunto) => {
    abrirDocumentoProtegido(documento.url).catch(() => message.error('No se pudo abrir el documento'));
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await archivoAdjuntoApi.eliminar(id);
      setDocumentos((prev) => prev.filter((d) => d.id !== id));
    } catch {
      message.error('No se pudo eliminar el documento');
    } finally {
      setEliminandoId(null);
    }
  };

  return (
    <div style={{ maxWidth: 560 }}>
      <input
        ref={inputRef}
        type="file"
        accept="application/pdf,image/jpeg,image/png,image/webp"
        style={{ display: 'none' }}
        onChange={handleArchivoElegido}
      />
      <Space style={{ marginBottom: 16 }}>
        <Select style={{ width: 260 }} value={tipoSeleccionado} onChange={setTipoSeleccionado} options={TIPOS_DOCUMENTO} />
        <Button icon={<UploadOutlined />} loading={subiendo} onClick={() => inputRef.current?.click()}>
          Subir documento
        </Button>
      </Space>

      {!loading && documentos.length === 0 && <Empty description="Este inmueble aún no tiene documentos" />}

      <List
        loading={loading}
        dataSource={documentos}
        renderItem={(documento) => (
          <List.Item
            actions={[
              <Button key="ver" type="link" icon={<FileTextOutlined />} onClick={() => handleVer(documento)}>
                Ver
              </Button>,
              <Popconfirm
                key="eliminar"
                title="¿Eliminar este documento?"
                okText="Eliminar"
                cancelText="Cancelar"
                onConfirm={() => handleEliminar(documento.id)}
              >
                <Button type="text" danger icon={<DeleteOutlined />} loading={eliminandoId === documento.id} />
              </Popconfirm>,
            ]}
          >
            <Space>
              <Tag>{ETIQUETA_TIPO[documento.tipo] ?? documento.tipo}</Tag>
              <Text type="secondary" style={{ fontSize: 12 }}>
                {new Date(documento.createdAt).toLocaleDateString('es-CO')}
              </Text>
            </Space>
          </List.Item>
        )}
      />
    </div>
  );
}
