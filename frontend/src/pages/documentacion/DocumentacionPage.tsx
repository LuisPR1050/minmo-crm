import { useEffect, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import { Alert, Button, Card, List, Modal, Select, Space, Tag, Typography, message } from 'antd';
import { DiffOutlined, UploadOutlined } from '@ant-design/icons';
import { comparacionDocumentosApi } from '../../api/documentacion';
import { inmuebleApi } from '../../api/captacion';
import { negociacionApi } from '../../api/negociacion';
import ContratoCorretajeTab from '../captacion/tabs/ContratoCorretajeTab';
import NegociacionDocumentosTab from '../negociacion/tabs/DocumentosTab';
import type { ComparacionDocumentos, NivelCambioComparacion, RelevanciaDiferencia, TipoCambioDiferencia } from '../../types/documentacion';
import type { Inmueble } from '../../types/captacion';
import type { Negociacion } from '../../types/negociacion';

const { Title, Paragraph, Text } = Typography;

const COLOR_NIVEL_CAMBIO: Record<NivelCambioComparacion, 'success' | 'info' | 'warning' | 'error'> = {
  SIN_CAMBIOS: 'success',
  MENOR: 'info',
  MODERADO: 'warning',
  MAYOR: 'error',
};

const TEXTO_NIVEL_CAMBIO: Record<NivelCambioComparacion, string> = {
  SIN_CAMBIOS: 'Sin cambios sustanciales',
  MENOR: 'Cambios menores',
  MODERADO: 'Cambios moderados',
  MAYOR: 'Cambios mayores',
};

const COLOR_TIPO_CAMBIO: Record<TipoCambioDiferencia, string> = {
  AGREGADO: 'green',
  ELIMINADO: 'red',
  MODIFICADO: 'blue',
};

const COLOR_RELEVANCIA: Record<RelevanciaDiferencia, string> = {
  ALTA: 'red',
  MEDIA: 'orange',
  BAJA: 'default',
};

export default function DocumentacionPage() {
  const inputARef = useRef<HTMLInputElement>(null);
  const inputBRef = useRef<HTMLInputElement>(null);

  const [documentoA, setDocumentoA] = useState<File | null>(null);
  const [documentoB, setDocumentoB] = useState<File | null>(null);
  const [comparando, setComparando] = useState(false);
  const [resultado, setResultado] = useState<ComparacionDocumentos | null>(null);

  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [inmuebleSeleccionadoId, setInmuebleSeleccionadoId] = useState<string | null>(null);

  const [negociaciones, setNegociaciones] = useState<Negociacion[]>([]);
  const [negociacionSeleccionadaId, setNegociacionSeleccionadaId] = useState<string | null>(null);

  useEffect(() => {
    inmuebleApi
      .listar()
      .then(setInmuebles)
      .catch(() => undefined);
    negociacionApi
      .listar()
      .then(setNegociaciones)
      .catch(() => undefined);
  }, []);

  const inmuebleSeleccionado = inmuebles.find((i) => i.id === inmuebleSeleccionadoId) ?? null;
  const negociacionSeleccionada = negociaciones.find((n) => n.id === negociacionSeleccionadaId) ?? null;

  const handleComparar = async () => {
    if (!documentoA || !documentoB) return;

    setComparando(true);
    try {
      const data = await comparacionDocumentosApi.compararIA(documentoA, documentoB);
      setResultado(data);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo comparar los documentos. Verifica que sean PDF o imagen y vuelve a intentar.');
    } finally {
      setComparando(false);
    }
  };

  return (
    <div>
      <Title level={3}>Documentación</Title>

      <Card title="Promesa de compraventa, recibos y acta de entrega" style={{ marginBottom: 16 }}>
        <Paragraph type="secondary">
          Busca una negociación para generar y gestionar su promesa de compraventa, recibos de pago y acta de
          entrega (incluye redacción, revisión y resúmenes con IA). Estos documentos dependen de los datos de esa
          negociación específica: comprador, vendedor, inmueble y condiciones acordadas.
        </Paragraph>
        <Select
          showSearch
          allowClear
          style={{ width: '100%', maxWidth: 480, marginBottom: 16 }}
          placeholder="Busca por número de negocio, cliente o inmueble..."
          optionFilterProp="label"
          value={negociacionSeleccionadaId}
          onChange={setNegociacionSeleccionadaId}
          options={negociaciones.map((n) => ({
            value: n.id,
            label: `${n.numeroNegocio} — ${n.clienteNombre} — ${n.inmuebleCodigo}`,
          }))}
        />
        {negociacionSeleccionada && <NegociacionDocumentosTab negociacionId={negociacionSeleccionada.id} />}
      </Card>

      <Card title="Contrato de corretaje" style={{ marginBottom: 16 }}>
        <Paragraph type="secondary">
          Busca un inmueble para ver, crear o editar su contrato de corretaje (incluye redacción con IA y PDF).
        </Paragraph>
        <Select
          showSearch
          allowClear
          style={{ width: '100%', maxWidth: 480, marginBottom: 16 }}
          placeholder="Busca por código o propietario..."
          optionFilterProp="label"
          value={inmuebleSeleccionadoId}
          onChange={setInmuebleSeleccionadoId}
          options={inmuebles.map((i) => ({ value: i.id, label: `${i.codigo} — ${i.propietarioNombre}` }))}
        />
        {inmuebleSeleccionado && (
          <ContratoCorretajeTab inmuebleId={inmuebleSeleccionado.id} propietarioId={inmuebleSeleccionado.propietarioId} />
        )}
      </Card>

      <Card title="Comparar documentos con IA">
        <Paragraph type="secondary">
          Sube dos versiones de un documento o contrato (por ejemplo, dos versiones de una promesa de compraventa) y
          la IA te dirá qué cambió entre ellas: precio, plazos, partes, cláusulas agregadas o eliminadas, etc.
          Acepta archivos PDF, JPEG, PNG o WEBP de hasta 8MB cada uno.
        </Paragraph>

        <input
          ref={inputARef}
          type="file"
          accept="application/pdf,image/jpeg,image/png,image/webp"
          style={{ display: 'none' }}
          onChange={(e) => setDocumentoA(e.target.files?.[0] ?? null)}
        />
        <input
          ref={inputBRef}
          type="file"
          accept="application/pdf,image/jpeg,image/png,image/webp"
          style={{ display: 'none' }}
          onChange={(e) => setDocumentoB(e.target.files?.[0] ?? null)}
        />

        <Space wrap size={16} style={{ marginBottom: 16 }}>
          <Space direction="vertical" size={4}>
            <Text strong>Documento A</Text>
            <Button icon={<UploadOutlined />} onClick={() => inputARef.current?.click()}>
              {documentoA ? documentoA.name : 'Seleccionar archivo'}
            </Button>
          </Space>
          <Space direction="vertical" size={4}>
            <Text strong>Documento B</Text>
            <Button icon={<UploadOutlined />} onClick={() => inputBRef.current?.click()}>
              {documentoB ? documentoB.name : 'Seleccionar archivo'}
            </Button>
          </Space>
        </Space>

        <div>
          <Button
            type="primary"
            icon={<DiffOutlined />}
            loading={comparando}
            disabled={!documentoA || !documentoB}
            onClick={handleComparar}
          >
            Comparar con IA
          </Button>
        </div>
      </Card>

      <Modal
        title="Comparación de documentos"
        open={resultado !== null}
        onCancel={() => setResultado(null)}
        footer={<Button onClick={() => setResultado(null)}>Cerrar</Button>}
        width={720}
      >
        {resultado && (
          <Space orientation="vertical" size={16} style={{ width: '100%' }}>
            <Text type="secondary">
              {resultado.nombreDocumentoA} <strong>vs</strong> {resultado.nombreDocumentoB}
            </Text>

            <Alert
              type={COLOR_NIVEL_CAMBIO[resultado.nivelCambio]}
              showIcon
              title={TEXTO_NIVEL_CAMBIO[resultado.nivelCambio]}
              description={resultado.resumenGeneral}
            />

            {resultado.diferencias.length === 0 ? (
              <Text>No se encontraron diferencias relevantes entre los dos documentos.</Text>
            ) : (
              <List
                dataSource={resultado.diferencias}
                renderItem={(diferencia) => (
                  <List.Item>
                    <Space direction="vertical" size={4} style={{ width: '100%' }}>
                      <Space wrap>
                        <Text strong>{diferencia.seccion}</Text>
                        <Tag color={COLOR_TIPO_CAMBIO[diferencia.tipoCambio]}>{diferencia.tipoCambio}</Tag>
                        <Tag color={COLOR_RELEVANCIA[diferencia.relevancia]}>Relevancia {diferencia.relevancia}</Tag>
                      </Space>
                      <Text>
                        <Text type="secondary">Documento A: </Text>
                        {diferencia.contenidoDocumentoA}
                      </Text>
                      <Text>
                        <Text type="secondary">Documento B: </Text>
                        {diferencia.contenidoDocumentoB}
                      </Text>
                    </Space>
                  </List.Item>
                )}
              />
            )}
          </Space>
        )}
      </Modal>
    </div>
  );
}
