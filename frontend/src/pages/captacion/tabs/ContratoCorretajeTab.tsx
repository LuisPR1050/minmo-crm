import { useEffect, useRef, useState } from 'react';
import { isAxiosError } from 'axios';
import InputMoneda from '../../../components/InputMoneda';
import {
  Alert,
  Button,
  Card,
  Checkbox,
  DatePicker,
  Empty,
  Form,
  Input,
  InputNumber,
  Modal,
  Popconfirm,
  Row,
  Col,
  Select,
  Skeleton,
  Space,
  Tag,
  Timeline,
  Typography,
  message,
} from 'antd';
import {
  DeleteOutlined,
  FileAddOutlined,
  FilePdfOutlined,
  FileTextOutlined,
  MailOutlined,
  RobotOutlined,
  UploadOutlined,
  WhatsAppOutlined,
} from '@ant-design/icons';
import dayjs from 'dayjs';
import ItemList from '../../../components/ItemList';
import {
  autorizacionPropietarioApi,
  clausulaCorretajeApi,
  contratoCorretajeApi,
  documentoContratoCorretajeApi,
  historialContratoCorretajeApi,
} from '../../../api/documentacion';
import { propietarioApi } from '../../../api/captacion';
import { construirEnlaceCorreo, construirEnlaceWhatsapp } from '../../../utils/compartir';
import { abrirDocumentoProtegido } from '../../../utils/archivosProtegidos';
import {
  ESTADOS_DOCUMENTO,
  ETIQUETA_ESTADO_DOCUMENTO,
  ETIQUETA_FORMA_PAGO_COMISION,
  ETIQUETA_OPCION_FIRMA,
  ETIQUETA_TIPO_CONTRATO,
  ETIQUETA_TIPO_DOCUMENTO_CONTRATO_CORRETAJE,
  FORMAS_PAGO_COMISION,
  OPCIONES_FIRMA,
  TIPOS_CONTRATO_CORRETAJE,
  TIPOS_DOCUMENTO_CONTRATO_CORRETAJE,
} from '../../../types/documentacion';
import type {
  ActualizarContratoCorretajeRequest,
  AutorizacionPropietario,
  ClausulaCorretaje,
  ContratoCorretaje,
  ContratoCorretajeRequest,
  DocumentoContratoCorretaje,
  EstadoDocumento,
  HistorialContratoCorretaje,
  TipoDocumentoContratoCorretaje,
} from '../../../types/documentacion';

const { Text } = Typography;

interface Props {
  inmuebleId: string;
  propietarioId: string;
}

const COLOR_ESTADO: Record<EstadoDocumento, string> = {
  BORRADOR: 'default',
  EN_REVISION: 'blue',
  ENVIADO: 'cyan',
  PENDIENTE_FIRMA: 'gold',
  FIRMADO: 'green',
  VIGENTE: 'success',
  VENCIDO: 'red',
  RENOVADO: 'purple',
  CANCELADO: 'default',
};

interface CondicionesFormValues {
  tipoContrato?: string;
  ciudad?: string;
  precioAutorizadoVenta?: number;
  precioMinimoAutorizado?: number;
  porcentajeComision?: number;
  valorFijoComision?: number;
  sobreprecioComision?: number;
  formaPagoComision?: string;
  fechaVigenciaHasta?: dayjs.Dayjs;
  opcionFirma?: string;
}

export default function ContratoCorretajeTab({ inmuebleId, propietarioId }: Props) {
  const [contratos, setContratos] = useState<ContratoCorretaje[]>([]);
  const [loading, setLoading] = useState(false);
  const [creando, setCreando] = useState(false);
  const [formCrear] = Form.useForm<{ ciudad?: string; fechaElaboracion?: dayjs.Dayjs }>();

  const cargar = async () => {
    setLoading(true);
    try {
      const data = await contratoCorretajeApi.listarPorInmueble(inmuebleId);
      setContratos(data);
    } catch {
      message.error('No se pudo cargar el contrato de corretaje');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmuebleId]);

  const handleCrear = async (values: { ciudad?: string; fechaElaboracion?: dayjs.Dayjs }) => {
    const request: ContratoCorretajeRequest = {
      inmuebleId,
      propietarioId,
      ciudad: values.ciudad,
      fechaElaboracion: values.fechaElaboracion?.format('YYYY-MM-DD'),
    };
    setCreando(true);
    try {
      await contratoCorretajeApi.crear(request);
      message.success('Contrato de corretaje creado');
      formCrear.resetFields();
      cargar();
    } catch {
      message.error('No se pudo crear el contrato de corretaje');
    } finally {
      setCreando(false);
    }
  };

  if (loading) return <Skeleton active />;

  if (contratos.length === 0) {
    return (
      <div style={{ maxWidth: 520 }}>
        <Alert
          type="info"
          showIcon
          title="Este inmueble aún no tiene contrato de corretaje"
          style={{ marginBottom: 16 }}
        />
        <Card title="Nuevo contrato de corretaje" size="small">
          <Form form={formCrear} layout="vertical" onFinish={handleCrear} initialValues={{ ciudad: 'Valledupar' }}>
            <Form.Item name="ciudad" label="Ciudad">
              <Input />
            </Form.Item>
            <Form.Item name="fechaElaboracion" label="Fecha de elaboración">
              <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
            </Form.Item>
            <Button type="primary" icon={<FileAddOutlined />} htmlType="submit" loading={creando}>
              Crear contrato
            </Button>
          </Form>
        </Card>
      </div>
    );
  }

  const [activo, ...anteriores] = contratos;

  return (
    <div>
      <ContratoActivo key={activo.id} contrato={activo} onCambio={cargar} />

      {anteriores.length > 0 && (
        <Card title="Contratos anteriores" size="small" style={{ marginTop: 16 }}>
          <ItemList
            items={anteriores}
            rowKey={(c) => c.id}
            renderItem={(c) => (
              <Space>
                <Text strong>{c.numeroContrato}</Text>
                <Tag color={COLOR_ESTADO[c.estado]}>{ETIQUETA_ESTADO_DOCUMENTO[c.estado]}</Tag>
                <Text type="secondary">{new Date(c.createdAt).toLocaleDateString('es-CO')}</Text>
              </Space>
            )}
          />
        </Card>
      )}
    </div>
  );
}

interface ContratoActivoProps {
  contrato: ContratoCorretaje;
  onCambio: () => void;
}

function ContratoActivo({ contrato, onCambio }: ContratoActivoProps) {
  const [form] = Form.useForm<CondicionesFormValues>();
  const [guardando, setGuardando] = useState(false);
  const [cambiandoEstado, setCambiandoEstado] = useState(false);
  const [generandoTexto, setGenerandoTexto] = useState(false);
  const [instruccionesIA, setInstruccionesIA] = useState('');
  const [modalTextoAbierto, setModalTextoAbierto] = useState(false);
  const [textoEditado, setTextoEditado] = useState('');
  const [guardandoTexto, setGuardandoTexto] = useState(false);
  const [propietarioWhatsapp, setPropietarioWhatsapp] = useState<string | undefined>();
  const [propietarioCorreo, setPropietarioCorreo] = useState<string | undefined>();

  const [catalogoClausulas, setCatalogoClausulas] = useState<ClausulaCorretaje[]>([]);
  const [clausulasAsignadas, setClausulasAsignadas] = useState<Set<string>>(new Set());
  const [guardandoClausulas, setGuardandoClausulas] = useState(false);

  const [catalogoAutorizaciones, setCatalogoAutorizaciones] = useState<AutorizacionPropietario[]>([]);
  const [autorizacionesAsignadas, setAutorizacionesAsignadas] = useState<Set<string>>(new Set());
  const [guardandoAutorizaciones, setGuardandoAutorizaciones] = useState(false);

  const [documentos, setDocumentos] = useState<DocumentoContratoCorretaje[]>([]);
  const [tipoDocumento, setTipoDocumento] = useState<TipoDocumentoContratoCorretaje>('CEDULA');
  const [subiendo, setSubiendo] = useState(false);
  const [eliminandoDocId, setEliminandoDocId] = useState<string | null>(null);
  const [eliminandoContrato, setEliminandoContrato] = useState(false);
  const inputRef = useRef<HTMLInputElement>(null);

  const [historial, setHistorial] = useState<HistorialContratoCorretaje[]>([]);

  const cargarDetalle = async () => {
    form.setFieldsValue({
      tipoContrato: contrato.tipoContrato,
      ciudad: contrato.ciudad,
      precioAutorizadoVenta: contrato.precioAutorizadoVenta,
      precioMinimoAutorizado: contrato.precioMinimoAutorizado,
      porcentajeComision: contrato.porcentajeComision,
      valorFijoComision: contrato.valorFijoComision,
      sobreprecioComision: contrato.sobreprecioComision,
      formaPagoComision: contrato.formaPagoComision,
      fechaVigenciaHasta: contrato.fechaVigenciaHasta ? dayjs(contrato.fechaVigenciaHasta) : undefined,
      opcionFirma: contrato.opcionFirma,
    });

    try {
      const [todasClausulas, asignadasClausulas, todasAutorizaciones, asignadasAutorizaciones, docs, hist, propietario] =
        await Promise.all([
          clausulaCorretajeApi.listarTodas(),
          clausulaCorretajeApi.listarAsignadas(contrato.id),
          autorizacionPropietarioApi.listarTodas(),
          autorizacionPropietarioApi.listarAsignadas(contrato.id),
          documentoContratoCorretajeApi.listarPorContrato(contrato.id),
          historialContratoCorretajeApi.listarPorContrato(contrato.id),
          propietarioApi.buscarPorId(contrato.propietarioId),
        ]);
      setCatalogoClausulas(todasClausulas);
      setClausulasAsignadas(new Set(asignadasClausulas.map((c) => c.id)));
      setCatalogoAutorizaciones(todasAutorizaciones);
      setAutorizacionesAsignadas(new Set(asignadasAutorizaciones.map((a) => a.id)));
      setDocumentos(docs);
      setHistorial(hist);
      setPropietarioWhatsapp(propietario.whatsapp);
      setPropietarioCorreo(propietario.correoElectronico);
    } catch {
      message.error('No se pudo cargar el detalle del contrato');
    }
  };

  useEffect(() => {
    cargarDetalle();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [contrato.id]);

  const handleGuardarCondiciones = async (values: CondicionesFormValues) => {
    const request: ActualizarContratoCorretajeRequest = {
      tipoContrato: values.tipoContrato as ActualizarContratoCorretajeRequest['tipoContrato'],
      ciudad: values.ciudad,
      precioAutorizadoVenta: values.precioAutorizadoVenta,
      precioMinimoAutorizado: values.precioMinimoAutorizado,
      porcentajeComision: values.porcentajeComision,
      valorFijoComision: values.valorFijoComision,
      sobreprecioComision: values.sobreprecioComision,
      formaPagoComision: values.formaPagoComision as ActualizarContratoCorretajeRequest['formaPagoComision'],
      fechaVigenciaHasta: values.fechaVigenciaHasta?.format('YYYY-MM-DD'),
      opcionFirma: values.opcionFirma as ActualizarContratoCorretajeRequest['opcionFirma'],
    };
    setGuardando(true);
    try {
      await contratoCorretajeApi.actualizar(contrato.id, request);
      message.success('Condiciones actualizadas');
      onCambio();
    } catch {
      message.error('No se pudieron actualizar las condiciones');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambiarEstado = async (nuevoEstado: EstadoDocumento) => {
    setCambiandoEstado(true);
    try {
      await contratoCorretajeApi.cambiarEstado(contrato.id, nuevoEstado);
      message.success('Estado actualizado');
      onCambio();
    } catch (error) {
      const detalle = (error as { response?: { data?: { mensaje?: string } } })?.response?.data?.mensaje;
      message.error(detalle ?? 'No se pudo cambiar el estado');
    } finally {
      setCambiandoEstado(false);
    }
  };

  const handleGenerarTexto = async () => {
    setGenerandoTexto(true);
    try {
      await contratoCorretajeApi.generarTexto(contrato.id, {
        instruccionesAdicionales: instruccionesIA.trim() || undefined,
      });
      message.success('Texto generado con IA');
      onCambio();
      cargarDetalle();
    } catch {
      message.warning(
        'No se pudo generar con IA. Lo más probable es que aún no esté configurada la clave de la API de Anthropic en este entorno.',
      );
    } finally {
      setGenerandoTexto(false);
    }
  };

  const abrirModalTexto = () => {
    setTextoEditado(contrato.textoContrato ?? '');
    setModalTextoAbierto(true);
  };

  const handleGuardarTexto = async () => {
    setGuardandoTexto(true);
    try {
      await contratoCorretajeApi.actualizar(contrato.id, { textoContrato: textoEditado });
      message.success('Texto del contrato actualizado. El PDF se regeneró automáticamente.');
      setModalTextoAbierto(false);
      onCambio();
      cargarDetalle();
    } catch {
      message.error('No se pudo actualizar el texto del contrato');
    } finally {
      setGuardandoTexto(false);
    }
  };

  const handleVerPdf = () => {
    if (!contrato.pdfUrl) return;
    abrirDocumentoProtegido(contrato.pdfUrl).catch(() => message.error('No se pudo abrir el PDF'));
  };

  const toggleClausula = async (id: string, checked: boolean) => {
    if (!checked) return; // el backend solo soporta asignar, no desasignar
    setGuardandoClausulas(true);
    try {
      const actualizadas = await clausulaCorretajeApi.asignar(contrato.id, [id]);
      setClausulasAsignadas(new Set(actualizadas.map((c) => c.id)));
    } catch {
      message.error('No se pudo asignar la cláusula');
    } finally {
      setGuardandoClausulas(false);
    }
  };

  const toggleAutorizacion = async (id: string, checked: boolean) => {
    if (!checked) return;
    setGuardandoAutorizaciones(true);
    try {
      const actualizadas = await autorizacionPropietarioApi.asignar(contrato.id, [id]);
      setAutorizacionesAsignadas(new Set(actualizadas.map((a) => a.id)));
    } catch {
      message.error('No se pudo asignar la autorización');
    } finally {
      setGuardandoAutorizaciones(false);
    }
  };

  const handleSeleccionarArchivo = () => inputRef.current?.click();

  const handleArchivoElegido = async (event: React.ChangeEvent<HTMLInputElement>) => {
    const archivo = event.target.files?.[0];
    event.target.value = '';
    if (!archivo) return;

    setSubiendo(true);
    try {
      await documentoContratoCorretajeApi.subir(contrato.id, tipoDocumento, archivo);
      message.success('Documento subido correctamente');
      cargarDetalle();
    } catch {
      message.error('No se pudo subir el documento. Verifica que sea una imagen o un PDF de máximo 8 MB.');
    } finally {
      setSubiendo(false);
    }
  };

  const handleEliminarDocumento = async (id: string) => {
    setEliminandoDocId(id);
    try {
      await documentoContratoCorretajeApi.eliminar(contrato.id, id);
      message.success('Documento eliminado correctamente');
      cargarDetalle();
    } catch {
      message.error('No se pudo eliminar el documento');
    } finally {
      setEliminandoDocId(null);
    }
  };

  const handleEliminarContrato = async () => {
    setEliminandoContrato(true);
    try {
      await contratoCorretajeApi.eliminar(contrato.id);
      message.success('Contrato eliminado correctamente');
      onCambio();
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo eliminar el contrato');
    } finally {
      setEliminandoContrato(false);
    }
  };

  const textoParaCompartir = contrato.textoContrato
    ? `Contrato de corretaje ${contrato.numeroContrato}\n\n${contrato.textoContrato}`
    : '';

  return (
    <Row gutter={16}>
      <Col xs={24} lg={14}>
        <Card
          title={
            <Space>
              <Text strong>{contrato.numeroContrato}</Text>
              <Tag color={COLOR_ESTADO[contrato.estado]}>{ETIQUETA_ESTADO_DOCUMENTO[contrato.estado]}</Tag>
            </Space>
          }
          extra={
            <Space>
              <Select<EstadoDocumento>
                size="small"
                value={contrato.estado}
                loading={cambiandoEstado}
                style={{ width: 190 }}
                onChange={handleCambiarEstado}
                options={ESTADOS_DOCUMENTO.map((e) => ({ value: e, label: ETIQUETA_ESTADO_DOCUMENTO[e] }))}
              />
              <Popconfirm
                title="Eliminar contrato"
                description="¿Seguro que quieres eliminar este contrato de corretaje? Esta acción no se puede deshacer."
                okText="Eliminar"
                cancelText="Cancelar"
                okButtonProps={{ danger: true }}
                onConfirm={handleEliminarContrato}
              >
                <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoContrato} />
              </Popconfirm>
            </Space>
          }
          size="small"
          style={{ marginBottom: 16 }}
        >
          <Form<CondicionesFormValues> form={form} layout="vertical" onFinish={handleGuardarCondiciones}>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="tipoContrato" label="Tipo de contrato">
                  <Select allowClear options={TIPOS_CONTRATO_CORRETAJE.map((t) => ({ value: t, label: ETIQUETA_TIPO_CONTRATO[t] }))} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="ciudad" label="Ciudad">
                  <Input />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="precioAutorizadoVenta" label="Precio autorizado de venta">
                  <InputMoneda />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="precioMinimoAutorizado" label="Precio mínimo autorizado">
                  <InputMoneda />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={8}>
              <Col xs={24} sm={8}>
                <Form.Item name="porcentajeComision" label="Comisión (%)">
                  <InputNumber style={{ width: '100%' }} min={0} max={100} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={8}>
                <Form.Item name="valorFijoComision" label="Comisión (valor fijo)">
                  <InputMoneda />
                </Form.Item>
              </Col>
              <Col xs={24} sm={8}>
                <Form.Item name="sobreprecioComision" label="Comisión (sobreprecio)">
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
            </Row>

            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="formaPagoComision" label="Forma de pago de la comisión">
                  <Select allowClear options={FORMAS_PAGO_COMISION.map((f) => ({ value: f, label: ETIQUETA_FORMA_PAGO_COMISION[f] }))} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="fechaVigenciaHasta" label="Vigencia hasta">
                  <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
                </Form.Item>
              </Col>
            </Row>

            <Form.Item name="opcionFirma" label="Opción de firma">
              <Select allowClear options={OPCIONES_FIRMA.map((o) => ({ value: o, label: ETIQUETA_OPCION_FIRMA[o] }))} />
            </Form.Item>

            <Button type="primary" htmlType="submit" loading={guardando}>
              Guardar condiciones
            </Button>
          </Form>
        </Card>

        <Card title="Cláusulas a incluir" size="small" style={{ marginBottom: 16 }} loading={guardandoClausulas}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {catalogoClausulas.map((c) => (
              <Checkbox key={c.id} checked={clausulasAsignadas.has(c.id)} onChange={(e) => toggleClausula(c.id, e.target.checked)}>
                {c.nombre}
              </Checkbox>
            ))}
          </div>
        </Card>

        <Card title="Autorizaciones del propietario" size="small" loading={guardandoAutorizaciones}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {catalogoAutorizaciones.map((a) => (
              <Checkbox
                key={a.id}
                checked={autorizacionesAsignadas.has(a.id)}
                onChange={(e) => toggleAutorizacion(a.id, e.target.checked)}
              >
                {a.nombre}
              </Checkbox>
            ))}
          </div>
        </Card>
      </Col>

      <Col xs={24} lg={10}>
        <Card title="Texto del contrato (IA)" size="small" style={{ marginBottom: 16 }}>
          <Input.TextArea
            placeholder="Instrucciones adicionales para la IA (opcional)"
            value={instruccionesIA}
            onChange={(e) => setInstruccionesIA(e.target.value)}
            rows={2}
            style={{ marginBottom: 8 }}
          />
          {contrato.textoContrato ? (
            <>
              <Text type="secondary" style={{ fontSize: 12 }}>
                Generado el {contrato.fechaGeneracionTexto ? new Date(contrato.fechaGeneracionTexto).toLocaleString('es-CO') : ''}
              </Text>
              <div
                style={{
                  maxHeight: 240,
                  overflowY: 'auto',
                  whiteSpace: 'pre-wrap',
                  fontSize: 12,
                  background: '#fafafa',
                  padding: 10,
                  borderRadius: 6,
                  margin: '8px 0',
                }}
              >
                {contrato.textoContrato}
              </div>
              <Space wrap>
                <Button size="small" icon={<RobotOutlined />} loading={generandoTexto} onClick={handleGenerarTexto}>
                  Regenerar con IA
                </Button>
                <Button size="small" icon={<FileTextOutlined />} onClick={abrirModalTexto}>
                  Editar texto
                </Button>
                {contrato.pdfUrl && (
                  <Button size="small" icon={<FilePdfOutlined />} onClick={handleVerPdf}>
                    Descargar PDF
                  </Button>
                )}
                <Button
                  size="small"
                  icon={<WhatsAppOutlined />}
                  disabled={!propietarioWhatsapp}
                  href={propietarioWhatsapp ? construirEnlaceWhatsapp(propietarioWhatsapp, textoParaCompartir) : undefined}
                  target="_blank"
                >
                  Enviar por WhatsApp
                </Button>
                <Button
                  size="small"
                  icon={<MailOutlined />}
                  disabled={!propietarioCorreo}
                  href={
                    propietarioCorreo
                      ? construirEnlaceCorreo(propietarioCorreo, `Contrato de corretaje ${contrato.numeroContrato}`, textoParaCompartir)
                      : undefined
                  }
                >
                  Enviar por correo
                </Button>
              </Space>
            </>
          ) : (
            <Button icon={<RobotOutlined />} loading={generandoTexto} onClick={handleGenerarTexto}>
              Generar texto con IA
            </Button>
          )}
        </Card>

        <Card title="Documentos adjuntos" size="small" style={{ marginBottom: 16 }}>
          <Space style={{ marginBottom: 12 }}>
            <Select
              size="small"
              value={tipoDocumento}
              onChange={setTipoDocumento}
              style={{ width: 180 }}
              options={TIPOS_DOCUMENTO_CONTRATO_CORRETAJE.map((t) => ({ value: t, label: ETIQUETA_TIPO_DOCUMENTO_CONTRATO_CORRETAJE[t] }))}
            />
            <input
              ref={inputRef}
              type="file"
              accept="image/jpeg,image/png,image/webp,application/pdf"
              style={{ display: 'none' }}
              onChange={handleArchivoElegido}
            />
            <Button size="small" icon={<UploadOutlined />} loading={subiendo} onClick={handleSeleccionarArchivo}>
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
                  <Tag>{ETIQUETA_TIPO_DOCUMENTO_CONTRATO_CORRETAJE[d.tipo]}</Tag>
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
                  onConfirm={() => handleEliminarDocumento(d.id)}
                >
                  <Button size="small" danger icon={<DeleteOutlined />} loading={eliminandoDocId === d.id} />
                </Popconfirm>
              </Space>
            )}
          />
        </Card>

        <Card title="Historial" size="small">
          {historial.length === 0 ? (
            <Empty description="Sin eventos registrados" />
          ) : (
            <Timeline
              items={historial.map((h) => ({
                content: (
                  <div key={h.id}>
                    <Text style={{ fontSize: 13 }}>{h.evento}</Text>
                    <div>
                      <Text type="secondary" style={{ fontSize: 11 }}>
                        {new Date(h.fecha).toLocaleString('es-CO')}
                      </Text>
                    </div>
                  </div>
                ),
              }))}
            />
          )}
        </Card>
      </Col>

      <Modal
        title="Editar texto del contrato"
        open={modalTextoAbierto}
        onCancel={() => setModalTextoAbierto(false)}
        onOk={handleGuardarTexto}
        confirmLoading={guardandoTexto}
        okText="Guardar"
        cancelText="Cancelar"
        width={760}
      >
        <Input.TextArea
          value={textoEditado}
          onChange={(e) => setTextoEditado(e.target.value)}
          rows={20}
          style={{ fontFamily: 'monospace', fontSize: 13 }}
        />
      </Modal>
    </Row>
  );
}

