import { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { isAxiosError } from 'axios';
import { Alert, Button, Card, Checkbox, Col, Empty, Form, Input, InputNumber, List, Modal, Row, Select, Skeleton, Space, Tag, Typography, message } from 'antd';
import { BulbOutlined, CopyOutlined, EditOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { clienteApi, objecionApi, resultadoVisitaApi, visitaClienteApi } from '../../../api/atencioncliente';
import {
  ESTADOS_VISITA_CLIENTE,
  ETIQUETA_ESTADO_VISITA_CLIENTE,
  ETIQUETA_INTENCION_COMPRA,
  ETIQUETA_NIVEL_INTERES,
  ETIQUETA_PROXIMA_ACCION,
  ETIQUETA_UNIDAD_SEGUIMIENTO,
  INTENCIONES_COMPRA,
  NIVELES_INTERES,
  PROXIMAS_ACCIONES,
  UNIDADES_SEGUIMIENTO,
} from '../../../types/atencioncliente';
import type {
  AnalisisObjeciones,
  EstadoVisitaCliente,
  InmuebleRecomendado,
  IntencionCompra,
  NivelInteres,
  Objecion,
  ProximaAccion,
  ResultadoVisita,
  ResultadoVisitaRequest,
  VisitaCliente,
} from '../../../types/atencioncliente';
import { ETIQUETA_TIPO_PROPIEDAD } from '../../../types/captacion';

const { Text } = Typography;

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

interface Props {
  clienteId: string;
}

interface FormValues {
  numeroVisita?: number;
  estado: EstadoVisitaCliente;
  nivelInteres?: NivelInteres;
  intencionCompra?: IntencionCompra;
  proximaAccion?: ProximaAccion;
  seguimientoNumero?: number;
  seguimientoUnidad?: string;
  observacionesAsesor?: string;
  objecionIds?: string[];
}

export default function ResultadosTab({ clienteId }: Props) {
  const [visitas, setVisitas] = useState<VisitaCliente[]>([]);
  const [resultados, setResultados] = useState<Record<string, ResultadoVisita | null>>({});
  const [objeciones, setObjeciones] = useState<Objecion[]>([]);
  const [loading, setLoading] = useState(false);
  const [visitaSeleccionada, setVisitaSeleccionada] = useState<VisitaCliente | null>(null);
  const [guardando, setGuardando] = useState(false);
  const [analizando, setAnalizando] = useState(false);
  const [analisis, setAnalisis] = useState<AnalisisObjeciones | null>(null);
  const [generandoResumenId, setGenerandoResumenId] = useState<string | null>(null);
  const [resumenVisita, setResumenVisita] = useState<string | null>(null);
  const [recomendacionesAutomaticas, setRecomendacionesAutomaticas] = useState<InmuebleRecomendado[] | null>(null);
  const [form] = Form.useForm<FormValues>();

  const cargar = async () => {
    setLoading(true);
    try {
      const [visitasData, objecionesData] = await Promise.all([
        visitaClienteApi.listarPorCliente(clienteId),
        objecionApi.listarTodas(),
      ]);
      setVisitas(visitasData);
      setObjeciones(objecionesData);

      const entradas = await Promise.all(
        visitasData.map(async (v) => [v.id, await resultadoVisitaApi.buscarPorVisita(v.id)] as const),
      );
      setResultados(Object.fromEntries(entradas));
    } catch {
      message.error('No se pudieron cargar los resultados de las visitas');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [clienteId]);

  const abrirModal = (visita: VisitaCliente) => {
    const existente = resultados[visita.id];
    setVisitaSeleccionada(visita);
    form.setFieldsValue({
      numeroVisita: existente?.numeroVisita ?? 1,
      estado: existente?.estado ?? 'REALIZADA',
      nivelInteres: existente?.nivelInteres,
      intencionCompra: existente?.intencionCompra,
      proximaAccion: existente?.proximaAccion,
      seguimientoNumero: existente?.seguimientoNumero,
      seguimientoUnidad: existente?.seguimientoUnidad,
      observacionesAsesor: existente?.observacionesAsesor,
      objecionIds: objeciones.filter((o) => existente?.objeciones.includes(o.nombre)).map((o) => o.id),
    });
  };

  const handleGuardar = async (values: FormValues) => {
    if (!visitaSeleccionada) return;
    const request: ResultadoVisitaRequest = {
      visitaId: visitaSeleccionada.id,
      numeroVisita: values.numeroVisita,
      estado: values.estado,
      nivelInteres: values.nivelInteres,
      intencionCompra: values.intencionCompra,
      proximaAccion: values.proximaAccion,
      seguimientoNumero: values.seguimientoNumero,
      seguimientoUnidad: values.seguimientoUnidad,
      observacionesAsesor: values.observacionesAsesor,
      objecionIds: values.objecionIds,
    };
    setGuardando(true);
    try {
      const guardado = await resultadoVisitaApi.guardar(request);
      message.success('Resultado de la visita guardado');
      setVisitaSeleccionada(null);
      cargar();
      if (guardado.recomendacionesAlternativas && guardado.recomendacionesAlternativas.length > 0) {
        setRecomendacionesAutomaticas(guardado.recomendacionesAlternativas);
      }
    } catch {
      message.error('No se pudo guardar el resultado de la visita');
    } finally {
      setGuardando(false);
    }
  };

  const handleGenerarResumen = async (resultadoId: string) => {
    setGenerandoResumenId(resultadoId);
    try {
      const data = await resultadoVisitaApi.generarResumenConIA(resultadoId);
      setResumenVisita(data.resumen);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo generar el resumen de la visita');
    } finally {
      setGenerandoResumenId(null);
    }
  };

  const handleCopiarResumen = () => {
    if (!resumenVisita) return;
    navigator.clipboard.writeText(resumenVisita).then(
      () => message.success('Resumen copiado al portapapeles'),
      () => message.error('No se pudo copiar el resumen'),
    );
  };

  const handleAnalizarObjeciones = async () => {
    setAnalizando(true);
    try {
      const data = await clienteApi.analizarObjecionesConIA(clienteId);
      setAnalisis(data);
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo generar el análisis de objeciones');
    } finally {
      setAnalizando(false);
    }
  };

  if (loading) return <Skeleton active />;

  if (visitas.length === 0) {
    return <Empty description="Este cliente aún no tiene visitas agendadas" />;
  }

  return (
    <div style={{ maxWidth: 780 }}>
      <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'flex-end' }}>
        <Button icon={<BulbOutlined />} loading={analizando} onClick={handleAnalizarObjeciones}>
          Analizar objeciones con IA
        </Button>
      </Space>

      <Space orientation="vertical" size={16} style={{ width: '100%' }}>
        {visitas.map((visita) => {
          const resultado = resultados[visita.id];
          return (
            <Card
              key={visita.id}
              size="small"
              title={
                <Space>
                  <Text strong>{visita.codigoVisita}</Text>
                  <Text type="secondary">{visita.fechaVisita ?? 'Sin fecha'}</Text>
                </Space>
              }
              extra={
                <Space>
                  {resultado && (
                    <Button
                      size="small"
                      icon={<ThunderboltOutlined />}
                      loading={generandoResumenId === resultado.id}
                      onClick={() => handleGenerarResumen(resultado.id)}
                    >
                      Resumen para propietario
                    </Button>
                  )}
                  <Button size="small" icon={<EditOutlined />} onClick={() => abrirModal(visita)}>
                    {resultado ? 'Editar resultado' : 'Registrar resultado'}
                  </Button>
                </Space>
              }
            >
              {resultado ? (
                <Space orientation="vertical" size={4}>
                  <Space wrap>
                    <Tag>{ETIQUETA_ESTADO_VISITA_CLIENTE[resultado.estado]}</Tag>
                    {resultado.nivelInteres && <Tag color="blue">{ETIQUETA_NIVEL_INTERES[resultado.nivelInteres]}</Tag>}
                  </Space>
                  {resultado.intencionCompra && (
                    <Text>
                      <strong>Intención de compra:</strong> {ETIQUETA_INTENCION_COMPRA[resultado.intencionCompra]}
                    </Text>
                  )}
                  {resultado.proximaAccion && (
                    <Text>
                      <strong>Próxima acción:</strong> {ETIQUETA_PROXIMA_ACCION[resultado.proximaAccion]}
                    </Text>
                  )}
                  {resultado.seguimientoNumero != null && (
                    <Text>
                      <strong>Seguimiento en:</strong> {resultado.seguimientoNumero}{' '}
                      {resultado.seguimientoUnidad ? ETIQUETA_UNIDAD_SEGUIMIENTO[resultado.seguimientoUnidad] : ''}
                    </Text>
                  )}
                  {resultado.objeciones.length > 0 && (
                    <Text>
                      <strong>Objeciones:</strong> {resultado.objeciones.join(', ')}
                    </Text>
                  )}
                  {resultado.observacionesAsesor && <Text type="secondary">{resultado.observacionesAsesor}</Text>}
                </Space>
              ) : (
                <Text type="secondary">Sin resultado registrado todavía</Text>
              )}
            </Card>
          );
        })}
      </Space>

      <Modal
        title={`Resultado de la visita ${visitaSeleccionada?.codigoVisita ?? ''}`}
        open={!!visitaSeleccionada}
        onCancel={() => setVisitaSeleccionada(null)}
        onOk={() => form.submit()}
        confirmLoading={guardando}
        okText="Guardar"
        cancelText="Cancelar"
        width={640}
      >
        <Form<FormValues> form={form} layout="vertical" onFinish={handleGuardar}>
          <Row gutter={8}>
            <Col xs={24} sm={8}>
              <Form.Item name="numeroVisita" label="N° de visita">
                <InputNumber style={{ width: '100%' }} min={1} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={16}>
              <Form.Item name="estado" label="Estado de la visita" rules={[{ required: true, message: 'Selecciona el estado' }]}>
                <Select options={ESTADOS_VISITA_CLIENTE.map((e) => ({ value: e, label: ETIQUETA_ESTADO_VISITA_CLIENTE[e] }))} />
              </Form.Item>
            </Col>
          </Row>

          <Form.Item name="nivelInteres" label="Nivel de interés del cliente">
            <Select allowClear options={NIVELES_INTERES.map((n) => ({ value: n, label: ETIQUETA_NIVEL_INTERES[n] }))} />
          </Form.Item>

          <Form.Item name="objecionIds" label="Objeciones detectadas">
            <Checkbox.Group options={objeciones.map((o) => ({ value: o.id, label: o.nombre }))} />
          </Form.Item>

          <Form.Item name="intencionCompra" label="Intención de compra">
            <Select allowClear options={INTENCIONES_COMPRA.map((i) => ({ value: i, label: ETIQUETA_INTENCION_COMPRA[i] }))} />
          </Form.Item>

          <Form.Item name="proximaAccion" label="Próxima acción">
            <Select allowClear options={PROXIMAS_ACCIONES.map((p) => ({ value: p, label: ETIQUETA_PROXIMA_ACCION[p] }))} />
          </Form.Item>

          <Row gutter={8}>
            <Col xs={24} sm={12}>
              <Form.Item name="seguimientoNumero" label="Hacer seguimiento en">
                <InputNumber style={{ width: '100%' }} min={1} />
              </Form.Item>
            </Col>
            <Col xs={24} sm={12}>
              <Form.Item name="seguimientoUnidad" label=" ">
                <Select allowClear options={UNIDADES_SEGUIMIENTO.map((u) => ({ value: u, label: ETIQUETA_UNIDAD_SEGUIMIENTO[u] }))} />
              </Form.Item>
            </Col>
          </Row>
          <Alert
            type="info"
            showIcon
            title="El asesor recibirá una alerta automática cuando se cumpla el tiempo de seguimiento."
            style={{ marginBottom: 16 }}
          />

          <Form.Item name="observacionesAsesor" label="Observaciones del asesor">
            <Input.TextArea rows={3} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Análisis de objeciones con IA"
        open={analisis !== null}
        onCancel={() => setAnalisis(null)}
        footer={<Button onClick={() => setAnalisis(null)}>Cerrar</Button>}
        width={560}
      >
        {analisis && (
          <Space orientation="vertical" size={16} style={{ width: '100%' }}>
            <Text type="secondary">Basado en {analisis.visitasAnalizadas} visita(s) con objeciones u observaciones registradas.</Text>
            <div>
              <Text strong>Resumen</Text>
              <div>{analisis.resumen}</div>
            </div>
            <div>
              <Text strong>Cómo abordarlas</Text>
              <List
                size="small"
                dataSource={analisis.recomendaciones}
                renderItem={(item) => <List.Item>{item}</List.Item>}
              />
            </div>
          </Space>
        )}
      </Modal>

      <Modal
        title="Resumen de la visita (para el propietario)"
        open={resumenVisita !== null}
        onCancel={() => setResumenVisita(null)}
        footer={[
          <Button key="copiar" icon={<CopyOutlined />} onClick={handleCopiarResumen}>
            Copiar
          </Button>,
          <Button key="cerrar" type="primary" onClick={() => setResumenVisita(null)}>
            Cerrar
          </Button>,
        ]}
        width={560}
      >
        <Text>{resumenVisita}</Text>
      </Modal>

      <Modal
        title="La IA sugiere estas propiedades alternativas"
        open={recomendacionesAutomaticas !== null}
        onCancel={() => setRecomendacionesAutomaticas(null)}
        footer={<Button onClick={() => setRecomendacionesAutomaticas(null)}>Cerrar</Button>}
        width={640}
      >
        <Text type="secondary" style={{ display: 'block', marginBottom: 12 }}>
          El nivel de interés en esta propiedad es bajo. Estas son otras opciones del inventario que podrían encajar
          con el requerimiento más reciente del cliente.
        </Text>
        <Space orientation="vertical" size={12} style={{ width: '100%' }}>
          {recomendacionesAutomaticas?.map((rec) => (
            <Card key={rec.inmueble.id} size="small">
              <Space orientation="vertical" size={4} style={{ width: '100%' }}>
                <Space style={{ width: '100%', justifyContent: 'space-between' }}>
                  <Link to={`/captacion/inmuebles/${rec.inmueble.id}`}>
                    <Text strong>{rec.inmueble.codigo}</Text>
                  </Link>
                  <Text strong style={{ color: '#0b4f6c' }}>
                    {formatearMoneda(rec.inmueble.expectativaPrecio)}
                  </Text>
                </Space>
                <Text type="secondary">
                  {ETIQUETA_TIPO_PROPIEDAD[rec.inmueble.tipoPropiedad as keyof typeof ETIQUETA_TIPO_PROPIEDAD] ??
                    rec.inmueble.tipoPropiedad}{' '}
                  · {rec.inmueble.zona ?? rec.inmueble.ciudad ?? 'Sin ubicación'}
                </Text>
                <Text>{rec.motivo}</Text>
              </Space>
            </Card>
          ))}
        </Space>
      </Modal>
    </div>
  );
}
