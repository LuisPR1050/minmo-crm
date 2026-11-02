import { useRef, useState } from 'react';
import { Button, Card, Popconfirm, Select, Space, Tag, message } from 'antd';
import { DeleteOutlined, UploadOutlined } from '@ant-design/icons';
import ItemList from './ItemList';
import { abrirDocumentoProtegido } from '../utils/archivosProtegidos';

export interface DocumentoAdjuntoItem {
  id: string;
  url: string;
  etiqueta?: string;
}

interface Props {
  title?: string;
  documentos: DocumentoAdjuntoItem[];
  onSubir: (archivo: File, tipo?: string) => Promise<void>;
  onEliminar: (id: string) => Promise<void>;
  tipoOpciones?: { value: string; label: string }[];
}

export default function DocumentosAdjuntosCard({ title = 'Documentos adjuntos', documentos, onSubir, onEliminar, tipoOpciones }: Props) {
  const [tipo, setTipo] = useState<string | undefined>(tipoOpciones?.[0]?.value);
  const [subiendo, setSubiendo] = useState(false);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const handleArchivoElegido = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendo(true);
    try {
      await onSubir(archivo, tipo);
      message.success('Documento subido correctamente');
    } catch {
      message.error('No se pudo subir el documento. Verifica que sea una imagen o un PDF de máximo 8 MB.');
    } finally {
      setSubiendo(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await onEliminar(id);
      message.success('Documento eliminado correctamente');
    } catch {
      message.error('No se pudo eliminar el documento');
    } finally {
      setEliminandoId(null);
    }
  };

  return (
    <Card title={title} size="small" style={{ marginBottom: 16 }}>
      <Space style={{ marginBottom: 12 }} wrap>
        {tipoOpciones && (
          <Select size="small" value={tipo} onChange={setTipo} style={{ width: 200 }} options={tipoOpciones} />
        )}
        <input
          ref={inputRef}
          type="file"
          accept="image/jpeg,image/png,image/webp,application/pdf"
          style={{ display: 'none' }}
          onChange={handleArchivoElegido}
        />
        <Button size="small" icon={<UploadOutlined />} loading={subiendo} onClick={() => inputRef.current?.click()}>
          Subir
        </Button>
      </Space>
      <ItemList
        size="small"
        items={documentos}
        rowKey={(d) => d.id}
        emptyDescription="Sin documentos adjuntos"
        renderItem={(d) => (
          <Space style={{ width: '100%', justifyContent: 'space-between' }}>
            <Space>
              {d.etiqueta && <Tag>{d.etiqueta}</Tag>}
              <Button
                type="link"
                size="small"
                style={{ padding: 0, height: 'auto' }}
                onClick={() => abrirDocumentoProtegido(d.url).catch(() => message.error('No se pudo abrir el documento'))}
              >
                Ver documento
              </Button>
            </Space>
            <Popconfirm
              title="Eliminar documento"
              description="¿Seguro que quieres eliminar este documento?"
              okText="Eliminar"
              cancelText="Cancelar"
              okButtonProps={{ danger: true }}
              onConfirm={() => handleEliminar(d.id)}
            >
              <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoId === d.id} />
            </Popconfirm>
          </Space>
        )}
      />
    </Card>
  );
}
