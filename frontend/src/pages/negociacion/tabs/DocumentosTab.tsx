import { useEffect, useRef, useState } from 'react';
import { Button, Card, Popconfirm, Select, Space, Tag, Typography, message } from 'antd';
import { DeleteOutlined, FileOutlined, UploadOutlined } from '@ant-design/icons';
import ItemList from '../../../components/ItemList';
import { documentoNegociacionApi } from '../../../api/negociacion';
import { ETIQUETA_TIPO_DOCUMENTO_NEGOCIACION, TIPOS_DOCUMENTO_NEGOCIACION } from '../../../types/negociacion';
import type { DocumentoNegociacion, TipoDocumentoNegociacion } from '../../../types/negociacion';
import DocumentosNegociacion from '../DocumentosNegociacion';
import { abrirDocumentoProtegido } from '../../../utils/archivosProtegidos';

const { Text } = Typography;

interface Props {
  negociacionId: string;
  onCambioQuePuedaCerrarNegocio?: () => void;
}

export default function DocumentosTab({ negociacionId, onCambioQuePuedaCerrarNegocio }: Props) {
  const [documentos, setDocumentos] = useState<DocumentoNegociacion[]>([]);
  const [tipoSeleccionado, setTipoSeleccionado] = useState<TipoDocumentoNegociacion>('CEDULA');
  const [subiendo, setSubiendo] = useState(false);
  const [eliminandoId, setEliminandoId] = useState<string | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);

  const cargar = async () => {
    try {
      const data = await documentoNegociacionApi.listarPorNegociacion(negociacionId);
      setDocumentos(data);
    } catch {
      message.error('No se pudieron cargar los documentos adjuntos');
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const handleSeleccionarArchivo = () => inputRef.current?.click();

  const handleArchivoElegido = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendo(true);
    try {
      await documentoNegociacionApi.subir(negociacionId, tipoSeleccionado, archivo);
      message.success('Documento subido correctamente');
      cargar();
    } catch {
      message.error('No se pudo subir el documento. Verifica que sea una imagen o un PDF de máximo 8 MB.');
    } finally {
      setSubiendo(false);
    }
  };

  const handleEliminar = async (id: string) => {
    setEliminandoId(id);
    try {
      await documentoNegociacionApi.eliminar(id);
      setDocumentos((prev) => prev.filter((d) => d.id !== id));
    } catch {
      message.error('No se pudo eliminar el documento');
    } finally {
      setEliminandoId(null);
    }
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <Card title="Documentos adjuntos" size="small">
        <Space style={{ marginBottom: 16 }}>
          <Select
            value={tipoSeleccionado}
            onChange={setTipoSeleccionado}
            style={{ width: 200 }}
            options={TIPOS_DOCUMENTO_NEGOCIACION.map((t) => ({ value: t, label: ETIQUETA_TIPO_DOCUMENTO_NEGOCIACION[t] }))}
          />
          <input
            ref={inputRef}
            type="file"
            accept="image/jpeg,image/png,image/webp,application/pdf"
            style={{ display: 'none' }}
            onChange={handleArchivoElegido}
          />
          <Button icon={<UploadOutlined />} loading={subiendo} onClick={handleSeleccionarArchivo}>
            Subir documento
          </Button>
        </Space>

        <ItemList
          items={documentos}
          rowKey={(documento) => documento.id}
          emptyDescription="Sin documentos adjuntos"
          renderItem={(documento) => (
            <Space style={{ width: '100%', justifyContent: 'space-between' }}>
              <Space>
                <FileOutlined />
                <Tag>{ETIQUETA_TIPO_DOCUMENTO_NEGOCIACION[documento.tipo]}</Tag>
                <Button
                  type="link"
                  size="small"
                  style={{ padding: 0, height: 'auto' }}
                  onClick={() => abrirDocumentoProtegido(documento.url).catch(() => message.error('No se pudo abrir el documento'))}
                >
                  Ver documento
                </Button>
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {new Date(documento.createdAt).toLocaleDateString('es-CO')}
                </Text>
              </Space>
              <Popconfirm title="¿Eliminar este documento?" okText="Eliminar" cancelText="Cancelar" onConfirm={() => handleEliminar(documento.id)}>
                <Button size="small" type="text" danger icon={<DeleteOutlined />} loading={eliminandoId === documento.id} />
              </Popconfirm>
            </Space>
          )}
        />
      </Card>

      <DocumentosNegociacion negociacionId={negociacionId} onCambioQuePuedaCerrarNegocio={onCambioQuePuedaCerrarNegocio} />
    </Space>
  );
}
