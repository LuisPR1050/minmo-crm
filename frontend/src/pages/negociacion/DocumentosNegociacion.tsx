import { useEffect, useState } from 'react';
import {
  Button,
  Card,
  Checkbox,
  Col,
  DatePicker,
  Form,
  Input,
  InputNumber,
  message,
  Modal,
  Row,
  Select,
  Space,
  Switch,
  Tag,
  Typography,
} from 'antd';
import InputMoneda from '../../components/InputMoneda';
import { FileAddOutlined, FilePdfOutlined, FileTextOutlined, RobotOutlined } from '@ant-design/icons';
import dayjs from 'dayjs';
import ItemList from '../../components/ItemList';
import DocumentosAdjuntosCard from '../../components/DocumentosAdjuntosCard';
import { abrirDocumentoProtegido } from '../../utils/archivosProtegidos';
import {
  accesoEntregaApi,
  actaEntregaAccesoApi,
  actaEntregaApi,
  actaEntregaCondicionApi,
  actaEntregaPazYSalvoApi,
  clausulaCorretajeApi,
  clausulaPromesaApi,
  condicionEntregaApi,
  documentoActaEntregaApi,
  documentoOtrosiPromesaApi,
  documentoPromesaCompraventaApi,
  documentoReciboPagoApi,
  otrosiPromesaApi,
  otrosiPromesaClausulaApi,
  pazYSalvoTipoApi,
  promesaCompraventaApi,
  promesaCompraventaClausulaApi,
  reciboPagoApi,
} from '../../api/documentacion';
import { planDePagoApi } from '../../api/negociacion';
import type { PlanDePago } from '../../types/negociacion';
import {
  CONCEPTOS_PAGO,
  DECLARACIONES_COMPRADOR,
  ESTADOS_DOCUMENTO,
  ETIQUETA_DECLARACION_COMPRADOR,
  ETIQUETA_ESTADO_DOCUMENTO,
  ETIQUETA_JUSTIFICACION_OTROSI,
  ETIQUETA_MOTIVO_OTROSI,
  ETIQUETA_PAGO_REALIZADO_POR,
  ETIQUETA_PAGO_RECIBIDO_POR,
  ETIQUETA_TIPO_DOCUMENTO_ACTA_ENTREGA,
  ETIQUETA_TIPO_DOCUMENTO_PROMESA_COMPRAVENTA,
  FORMAS_PAGO_RECIBO,
  JUSTIFICACIONES_OTROSI,
  MOTIVOS_OTROSI,
  OPCIONES_FIRMA,
  PAGOS_REALIZADO_POR,
  PAGOS_RECIBIDO_POR,
  TIPOS_DOCUMENTO_ACTA_ENTREGA,
  TIPOS_DOCUMENTO_PROMESA_COMPRAVENTA,
} from '../../types/documentacion';
import type {
  ActaEntrega,
  ActaEntregaRequest,
  ActualizarOtrosiPromesaRequest,
  ActualizarPromesaCompraventaRequest,
  ClausulaCorretaje,
  ConceptoPago,
  DeclaracionComprador,
  DocumentoActaEntrega,
  DocumentoOtrosiPromesa,
  DocumentoPromesaCompraventa,
  DocumentoReciboPago,
  EstadoDocumento,
  FormaPagoRecibo,
  JustificacionOtrosi,
  MotivoOtrosi,
  OpcionFirma,
  OtrosiPromesa,
  OtrosiPromesaRequest,
  PagoRealizadoPor,
  PagoRecibidoPor,
  PromesaCompraventa,
  PromesaCompraventaRequest,
  ReciboPago,
  ReciboPagoRequest,
  TipoDocumentoActaEntrega,
  TipoDocumentoPromesaCompraventa,
} from '../../types/documentacion';

const { Text } = Typography;

const formatearMoneda = (valor?: number) =>
  valor != null ? valor.toLocaleString('es-CO', { style: 'currency', currency: 'COP', maximumFractionDigits: 0 }) : '—';

const explicarErrorIA = () =>
  message.warning(
    'No se pudo generar con IA. Lo más probable es que aún no esté configurada la clave de la API de Anthropic en este entorno.',
  );

interface Props {
  negociacionId: string;
  onCambioQuePuedaCerrarNegocio?: () => void;
}

export default function DocumentosNegociacion({ negociacionId, onCambioQuePuedaCerrarNegocio }: Props) {
  const [promesa, setPromesa] = useState<PromesaCompraventa | null>(null);
  const [recibos, setRecibos] = useState<ReciboPago[]>([]);
  const [acta, setActa] = useState<ActaEntrega | null>(null);
  const [otrosis, setOtrosis] = useState<OtrosiPromesa[]>([]);
  const [cargando, setCargando] = useState(false);

  const [documentosPromesa, setDocumentosPromesa] = useState<DocumentoPromesaCompraventa[]>([]);
  const [documentosActa, setDocumentosActa] = useState<DocumentoActaEntrega[]>([]);
  const [documentosOtrosi, setDocumentosOtrosi] = useState<Record<string, DocumentoOtrosiPromesa[]>>({});
  const [documentosRecibo, setDocumentosRecibo] = useState<Record<string, DocumentoReciboPago[]>>({});

  const [modalPromesaAbierto, setModalPromesaAbierto] = useState(false);
  const [modalReciboAbierto, setModalReciboAbierto] = useState(false);
  const [modalActaAbierto, setModalActaAbierto] = useState(false);
  const [modalOtrosiAbierto, setModalOtrosiAbierto] = useState(false);
  const [generandoTexto, setGenerandoTexto] = useState(false);
  const [instruccionesPromesa, setInstruccionesPromesa] = useState('');
  const [instruccionesOtrosi, setInstruccionesOtrosi] = useState<Record<string, string>>({});
  const [revisandoActa, setRevisandoActa] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [generandoTextoOtrosiId, setGenerandoTextoOtrosiId] = useState<string | null>(null);
  const [aplicandoOtrosiId, setAplicandoOtrosiId] = useState<string | null>(null);

  const [modalTextoPromesaAbierto, setModalTextoPromesaAbierto] = useState(false);
  const [textoPromesaEditado, setTextoPromesaEditado] = useState('');
  const [guardandoTextoPromesa, setGuardandoTextoPromesa] = useState(false);

  const [otrosiTextoEditando, setOtrosiTextoEditando] = useState<OtrosiPromesa | null>(null);
  const [textoOtrosiEditado, setTextoOtrosiEditado] = useState('');
  const [guardandoTextoOtrosi, setGuardandoTextoOtrosi] = useState(false);

  const [modalEditarPromesaAbierto, setModalEditarPromesaAbierto] = useState(false);
  const [cambiandoEstadoPromesa, setCambiandoEstadoPromesa] = useState(false);
  const [catalogoClausulasPromesa, setCatalogoClausulasPromesa] = useState<ClausulaCorretaje[]>([]);
  const [clausulasAsignadasPromesa, setClausulasAsignadasPromesa] = useState<Set<string>>(new Set());
  const [guardandoClausulasPromesa, setGuardandoClausulasPromesa] = useState(false);

  const [otrosiEditando, setOtrosiEditando] = useState<OtrosiPromesa | null>(null);
  const [catalogoClausulasOtrosi, setCatalogoClausulasOtrosi] = useState<ClausulaCorretaje[]>([]);
  const [clausulasAsignadasOtrosi, setClausulasAsignadasOtrosi] = useState<Record<string, Set<string>>>({});
  const [guardandoClausulasOtrosiId, setGuardandoClausulasOtrosiId] = useState<string | null>(null);

  const [catalogoCondicionesEntrega, setCatalogoCondicionesEntrega] = useState<ClausulaCorretaje[]>([]);
  const [condicionesAsignadas, setCondicionesAsignadas] = useState<Set<string>>(new Set());
  const [guardandoCondiciones, setGuardandoCondiciones] = useState(false);
  const [catalogoAccesosEntrega, setCatalogoAccesosEntrega] = useState<ClausulaCorretaje[]>([]);
  const [accesosAsignados, setAccesosAsignados] = useState<Set<string>>(new Set());
  const [guardandoAccesos, setGuardandoAccesos] = useState(false);
  const [catalogoPazYSalvoTipos, setCatalogoPazYSalvoTipos] = useState<ClausulaCorretaje[]>([]);
  const [pazYSalvosAsignados, setPazYSalvosAsignados] = useState<Set<string>>(new Set());
  const [guardandoPazYSalvos, setGuardandoPazYSalvos] = useState(false);

  const [formPromesa] = Form.useForm<{ fechaElaboracion?: dayjs.Dayjs; ciudad?: string; opcionFirma?: OpcionFirma }>();
  const [formEditarPromesa] = Form.useForm<{ ciudad?: string; opcionFirma?: OpcionFirma }>();
  const [planDePago, setPlanDePago] = useState<PlanDePago[]>([]);
  const [formRecibo] = Form.useForm<{
    planDePagoId?: string;
    fechaElaboracion?: dayjs.Dayjs;
    conceptoPago: ConceptoPago;
    numeroCuota?: number;
    valorRecibido: number;
    moneda?: string;
    formaPago?: FormaPagoRecibo;
    pagoRealizadoPor?: PagoRealizadoPor;
    pagoRecibidoPor?: PagoRecibidoPor;
    observaciones?: string;
  }>();
  const [formActa] = Form.useForm<{
    fechaElaboracion?: dayjs.Dayjs;
    fechaHoraEntrega?: dayjs.Dayjs;
    ciudad?: string;
    lecturaEnergia?: string;
    lecturaAgua?: string;
    lecturaGas?: string;
    danosExistentes?: string;
    reparacionesPendientes?: string;
    acuerdosPosteriores?: string;
    compromisosAsumidos?: string;
    declaracionComprador?: DeclaracionComprador;
    observaciones?: string;
    vendedorEntregaConforme: boolean;
  }>();
  const [formOtrosi] = Form.useForm<{
    fechaElaboracion?: dayjs.Dayjs;
    motivo: MotivoOtrosi;
    otroMotivoTexto?: string;
    justificacion?: JustificacionOtrosi;
    nuevoPrecio?: number;
    nuevaFechaEscritura?: dayjs.Dayjs;
    nuevaFechaEntrega?: dayjs.Dayjs;
    nuevaDistribucionGastos?: string;
    nuevosBienesIncluidos?: string;
    nuevasCondicionesEspeciales?: string;
    observaciones?: string;
  }>();
  const motivoOtrosiSeleccionado = Form.useWatch('motivo', formOtrosi);

  const cargarTodo = async () => {
    setCargando(true);
    try {
      const [promesaData, recibosData, actaData, planDePagoData] = await Promise.all([
        promesaCompraventaApi.buscarPorNegociacion(negociacionId),
        reciboPagoApi.listarPorNegociacion(negociacionId),
        actaEntregaApi.buscarPorNegociacion(negociacionId),
        planDePagoApi.listarPorNegociacion(negociacionId),
      ]);
      setPromesa(promesaData);
      setRecibos(recibosData);
      setActa(actaData);
      setPlanDePago(planDePagoData);

      const [docsPromesa, docsActa] = await Promise.all([
        promesaData ? documentoPromesaCompraventaApi.listarPorPromesa(promesaData.id) : Promise.resolve([]),
        actaData ? documentoActaEntregaApi.listarPorActa(actaData.id) : Promise.resolve([]),
      ]);
      setDocumentosPromesa(docsPromesa);
      setDocumentosActa(docsActa);

      const otrosisData = promesaData ? await otrosiPromesaApi.listarPorPromesa(promesaData.id) : [];
      setOtrosis(otrosisData);
      const entradasDocsOtrosi = await Promise.all(
        otrosisData.map(async (o) => [o.id, await documentoOtrosiPromesaApi.listarPorOtrosi(o.id)] as const),
      );
      setDocumentosOtrosi(Object.fromEntries(entradasDocsOtrosi));

      const entradasDocsRecibo = await Promise.all(
        recibosData.map(async (r) => [r.id, await documentoReciboPagoApi.listarPorRecibo(r.id)] as const),
      );
      setDocumentosRecibo(Object.fromEntries(entradasDocsRecibo));

      if (promesaData) {
        const [catalogoPromesa, asignadasPromesa] = await Promise.all([
          clausulaPromesaApi.listarTodas(),
          promesaCompraventaClausulaApi.listarAsignadas(promesaData.id),
        ]);
        setCatalogoClausulasPromesa(catalogoPromesa);
        setClausulasAsignadasPromesa(new Set(asignadasPromesa.map((c) => c.id)));
      } else {
        setCatalogoClausulasPromesa([]);
        setClausulasAsignadasPromesa(new Set());
      }

      if (otrosisData.length > 0) {
        setCatalogoClausulasOtrosi(await clausulaCorretajeApi.listarTodas());
        const entradasClausulasOtrosi = await Promise.all(
          otrosisData.map(async (o) => [o.id, new Set((await otrosiPromesaClausulaApi.listarAsignadas(o.id)).map((c) => c.id))] as const),
        );
        setClausulasAsignadasOtrosi(Object.fromEntries(entradasClausulasOtrosi));
      } else {
        setCatalogoClausulasOtrosi([]);
        setClausulasAsignadasOtrosi({});
      }

      if (actaData) {
        const [
          catalogoCondiciones,
          asignadasCondiciones,
          catalogoAccesos,
          asignadosAccesos,
          catalogoPazYSalvo,
          asignadosPazYSalvo,
        ] = await Promise.all([
          condicionEntregaApi.listarTodas(),
          actaEntregaCondicionApi.listarAsignadas(actaData.id),
          accesoEntregaApi.listarTodas(),
          actaEntregaAccesoApi.listarAsignadas(actaData.id),
          pazYSalvoTipoApi.listarTodas(),
          actaEntregaPazYSalvoApi.listarAsignadas(actaData.id),
        ]);
        setCatalogoCondicionesEntrega(catalogoCondiciones);
        setCondicionesAsignadas(new Set(asignadasCondiciones.map((c) => c.id)));
        setCatalogoAccesosEntrega(catalogoAccesos);
        setAccesosAsignados(new Set(asignadosAccesos.map((c) => c.id)));
        setCatalogoPazYSalvoTipos(catalogoPazYSalvo);
        setPazYSalvosAsignados(new Set(asignadosPazYSalvo.map((c) => c.id)));
      } else {
        setCatalogoCondicionesEntrega([]);
        setCondicionesAsignadas(new Set());
        setCatalogoAccesosEntrega([]);
        setAccesosAsignados(new Set());
        setCatalogoPazYSalvoTipos([]);
        setPazYSalvosAsignados(new Set());
      }
    } catch {
      message.error('No se pudieron cargar los documentos de esta negociación');
    } finally {
      setCargando(false);
    }
  };

  useEffect(() => {
    cargarTodo();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const handleCrearPromesa = async (values: {
    fechaElaboracion?: dayjs.Dayjs;
    ciudad?: string;
    opcionFirma?: OpcionFirma;
  }) => {
    const request: PromesaCompraventaRequest = {
      negociacionId,
      fechaElaboracion: values.fechaElaboracion?.format('YYYY-MM-DD'),
      ciudad: values.ciudad,
      opcionFirma: values.opcionFirma,
    };
    setGuardando(true);
    try {
      const creada = await promesaCompraventaApi.crear(request);
      setPromesa(creada);
      setModalPromesaAbierto(false);
      formPromesa.resetFields();
      message.success('Promesa de compraventa creada');
    } catch {
      message.error('No se pudo crear la promesa de compraventa');
    } finally {
      setGuardando(false);
    }
  };

  const handleGenerarTextoPromesa = async () => {
    if (!promesa) return;
    setGenerandoTexto(true);
    try {
      const actualizada = await promesaCompraventaApi.generarTexto(promesa.id, {
        instruccionesAdicionales: instruccionesPromesa.trim() || undefined,
      });
      setPromesa(actualizada);
      message.success('Texto generado con IA');
    } catch {
      explicarErrorIA();
    } finally {
      setGenerandoTexto(false);
    }
  };

  const abrirModalTextoPromesa = () => {
    if (!promesa) return;
    setTextoPromesaEditado(promesa.textoPromesa ?? '');
    setModalTextoPromesaAbierto(true);
  };

  const handleGuardarTextoPromesa = async () => {
    if (!promesa) return;
    setGuardandoTextoPromesa(true);
    try {
      const actualizada = await promesaCompraventaApi.actualizar(promesa.id, { textoPromesa: textoPromesaEditado });
      setPromesa(actualizada);
      setModalTextoPromesaAbierto(false);
      message.success('Texto de la promesa actualizado. El PDF se regeneró automáticamente.');
    } catch {
      message.error('No se pudo actualizar el texto de la promesa');
    } finally {
      setGuardandoTextoPromesa(false);
    }
  };

  const handleVerPdfPromesa = () => {
    if (!promesa?.pdfUrl) return;
    abrirDocumentoProtegido(promesa.pdfUrl).catch(() => message.error('No se pudo abrir el PDF'));
  };

  const abrirModalTextoOtrosi = (otrosi: OtrosiPromesa) => {
    setOtrosiTextoEditando(otrosi);
    setTextoOtrosiEditado(otrosi.textoOtrosi ?? '');
  };

  const handleGuardarTextoOtrosi = async () => {
    if (!otrosiTextoEditando) return;
    setGuardandoTextoOtrosi(true);
    try {
      const request: ActualizarOtrosiPromesaRequest = { textoOtrosi: textoOtrosiEditado };
      const actualizado = await otrosiPromesaApi.actualizar(otrosiTextoEditando.id, request);
      setOtrosis((prev) => prev.map((o) => (o.id === actualizado.id ? actualizado : o)));
      setOtrosiTextoEditando(null);
      message.success('Texto del otrosí actualizado. El PDF se regeneró automáticamente.');
    } catch {
      message.error('No se pudo actualizar el texto del otrosí');
    } finally {
      setGuardandoTextoOtrosi(false);
    }
  };

  const handleVerPdfOtrosi = (otrosi: OtrosiPromesa) => {
    if (!otrosi.pdfUrl) return;
    abrirDocumentoProtegido(otrosi.pdfUrl).catch(() => message.error('No se pudo abrir el PDF'));
  };

  const abrirModalCrearOtrosi = () => {
    setOtrosiEditando(null);
    formOtrosi.resetFields();
    setModalOtrosiAbierto(true);
  };

  const abrirModalEditarOtrosi = (otrosi: OtrosiPromesa) => {
    setOtrosiEditando(otrosi);
    formOtrosi.setFieldsValue({
      motivo: otrosi.motivo,
      otroMotivoTexto: otrosi.otroMotivoTexto,
      justificacion: otrosi.justificacion,
      nuevoPrecio: otrosi.nuevoPrecio,
      nuevaFechaEscritura: otrosi.nuevaFechaEscritura ? dayjs(otrosi.nuevaFechaEscritura) : undefined,
      nuevaFechaEntrega: otrosi.nuevaFechaEntrega ? dayjs(otrosi.nuevaFechaEntrega) : undefined,
      nuevaDistribucionGastos: otrosi.nuevaDistribucionGastos,
      nuevosBienesIncluidos: otrosi.nuevosBienesIncluidos,
      nuevasCondicionesEspeciales: otrosi.nuevasCondicionesEspeciales,
      observaciones: otrosi.observaciones,
    });
    setModalOtrosiAbierto(true);
  };

  const handleGuardarOtrosi = async (values: {
    fechaElaboracion?: dayjs.Dayjs;
    motivo: MotivoOtrosi;
    otroMotivoTexto?: string;
    justificacion?: JustificacionOtrosi;
    nuevoPrecio?: number;
    nuevaFechaEscritura?: dayjs.Dayjs;
    nuevaFechaEntrega?: dayjs.Dayjs;
    nuevaDistribucionGastos?: string;
    nuevosBienesIncluidos?: string;
    nuevasCondicionesEspeciales?: string;
    observaciones?: string;
  }) => {
    if (!promesa) return;
    setGuardando(true);
    try {
      if (otrosiEditando) {
        const request: ActualizarOtrosiPromesaRequest = {
          motivo: values.motivo,
          otroMotivoTexto: values.otroMotivoTexto,
          justificacion: values.justificacion,
          nuevoPrecio: values.nuevoPrecio,
          nuevaFechaEscritura: values.nuevaFechaEscritura?.format('YYYY-MM-DD'),
          nuevaFechaEntrega: values.nuevaFechaEntrega?.format('YYYY-MM-DD'),
          nuevaDistribucionGastos: values.nuevaDistribucionGastos,
          nuevosBienesIncluidos: values.nuevosBienesIncluidos,
          nuevasCondicionesEspeciales: values.nuevasCondicionesEspeciales,
          observaciones: values.observaciones,
        };
        const actualizado = await otrosiPromesaApi.actualizar(otrosiEditando.id, request);
        setOtrosis((prev) => prev.map((o) => (o.id === actualizado.id ? actualizado : o)));
        message.success('Otrosí actualizado correctamente');
      } else {
        const request: OtrosiPromesaRequest = {
          promesaCompraventaId: promesa.id,
          fechaElaboracion: values.fechaElaboracion?.format('YYYY-MM-DD'),
          motivo: values.motivo,
          otroMotivoTexto: values.otroMotivoTexto,
          justificacion: values.justificacion,
          nuevoPrecio: values.nuevoPrecio,
          nuevaFechaEscritura: values.nuevaFechaEscritura?.format('YYYY-MM-DD'),
          nuevaFechaEntrega: values.nuevaFechaEntrega?.format('YYYY-MM-DD'),
          nuevaDistribucionGastos: values.nuevaDistribucionGastos,
          nuevosBienesIncluidos: values.nuevosBienesIncluidos,
          nuevasCondicionesEspeciales: values.nuevasCondicionesEspeciales,
          observaciones: values.observaciones,
        };
        const creado = await otrosiPromesaApi.crear(request);
        setOtrosis((prev) => [creado, ...prev]);
        message.success('Otrosí creado correctamente');
      }
      setModalOtrosiAbierto(false);
      setOtrosiEditando(null);
      formOtrosi.resetFields();
    } catch {
      message.error(otrosiEditando ? 'No se pudo actualizar el otrosí' : 'No se pudo crear el otrosí');
    } finally {
      setGuardando(false);
    }
  };

  const handleGenerarTextoOtrosi = async (id: string) => {
    setGenerandoTextoOtrosiId(id);
    try {
      const actualizado = await otrosiPromesaApi.generarTexto(id, {
        instruccionesAdicionales: instruccionesOtrosi[id]?.trim() || undefined,
      });
      setOtrosis((prev) => prev.map((o) => (o.id === id ? actualizado : o)));
      message.success('Texto del otrosí generado con IA');
    } catch {
      explicarErrorIA();
    } finally {
      setGenerandoTextoOtrosiId(null);
    }
  };

  const handleAplicarOtrosi = async (id: string) => {
    setAplicandoOtrosiId(id);
    try {
      const aplicado = await otrosiPromesaApi.aplicar(id);
      setOtrosis((prev) => prev.map((o) => (o.id === id ? aplicado : o)));
      message.success('Otrosí aplicado. Los datos de la negociación se actualizaron automáticamente.');
      onCambioQuePuedaCerrarNegocio?.();
    } catch {
      message.error('No se pudo aplicar el otrosí');
    } finally {
      setAplicandoOtrosiId(null);
    }
  };

  const abrirModalEditarPromesa = () => {
    if (!promesa) return;
    formEditarPromesa.setFieldsValue({ ciudad: promesa.ciudad, opcionFirma: promesa.opcionFirma });
    setModalEditarPromesaAbierto(true);
  };

  const handleGuardarEditarPromesa = async (values: { ciudad?: string; opcionFirma?: OpcionFirma }) => {
    if (!promesa) return;
    const request: ActualizarPromesaCompraventaRequest = { ciudad: values.ciudad, opcionFirma: values.opcionFirma };
    setGuardando(true);
    try {
      const actualizada = await promesaCompraventaApi.actualizar(promesa.id, request);
      setPromesa(actualizada);
      setModalEditarPromesaAbierto(false);
      message.success('Promesa actualizada correctamente');
    } catch {
      message.error('No se pudo actualizar la promesa');
    } finally {
      setGuardando(false);
    }
  };

  const handleCambiarEstadoPromesa = async (nuevoEstado: EstadoDocumento) => {
    if (!promesa) return;
    setCambiandoEstadoPromesa(true);
    try {
      const actualizada = await promesaCompraventaApi.cambiarEstado(promesa.id, nuevoEstado);
      setPromesa(actualizada);
      message.success('Estado de la promesa actualizado');
    } catch (error) {
      const detalle = (error as { response?: { data?: { mensaje?: string } } })?.response?.data?.mensaje;
      message.error(detalle ?? 'No se pudo cambiar el estado de la promesa');
    } finally {
      setCambiandoEstadoPromesa(false);
    }
  };

  const toggleClausulaPromesa = async (id: string, checked: boolean) => {
    if (!promesa || !checked) return;
    setGuardandoClausulasPromesa(true);
    try {
      const actualizadas = await promesaCompraventaClausulaApi.asignar(promesa.id, [id]);
      setClausulasAsignadasPromesa(new Set(actualizadas.map((c) => c.id)));
    } catch {
      message.error('No se pudo asignar la cláusula');
    } finally {
      setGuardandoClausulasPromesa(false);
    }
  };

  const toggleClausulaOtrosi = async (otrosiId: string, id: string, checked: boolean) => {
    if (!checked) return;
    setGuardandoClausulasOtrosiId(otrosiId);
    try {
      const actualizadas = await otrosiPromesaClausulaApi.asignar(otrosiId, [id]);
      setClausulasAsignadasOtrosi((prev) => ({ ...prev, [otrosiId]: new Set(actualizadas.map((c) => c.id)) }));
    } catch {
      message.error('No se pudo asignar la cláusula');
    } finally {
      setGuardandoClausulasOtrosiId(null);
    }
  };

  const toggleCondicionEntrega = async (id: string, checked: boolean) => {
    if (!acta || !checked) return;
    setGuardandoCondiciones(true);
    try {
      const actualizadas = await actaEntregaCondicionApi.asignar(acta.id, [id]);
      setCondicionesAsignadas(new Set(actualizadas.map((c) => c.id)));
    } catch {
      message.error('No se pudo asignar la condición de entrega');
    } finally {
      setGuardandoCondiciones(false);
    }
  };

  const toggleAccesoEntrega = async (id: string, checked: boolean) => {
    if (!acta || !checked) return;
    setGuardandoAccesos(true);
    try {
      const actualizados = await actaEntregaAccesoApi.asignar(acta.id, [id]);
      setAccesosAsignados(new Set(actualizados.map((c) => c.id)));
    } catch {
      message.error('No se pudo asignar el acceso');
    } finally {
      setGuardandoAccesos(false);
    }
  };

  const togglePazYSalvo = async (id: string, checked: boolean) => {
    if (!acta || !checked) return;
    setGuardandoPazYSalvos(true);
    try {
      const actualizados = await actaEntregaPazYSalvoApi.asignar(acta.id, [id]);
      setPazYSalvosAsignados(new Set(actualizados.map((c) => c.id)));
    } catch {
      message.error('No se pudo asignar el paz y salvo');
    } finally {
      setGuardandoPazYSalvos(false);
    }
  };

  const handleCrearRecibo = async (values: {
    planDePagoId?: string;
    fechaElaboracion?: dayjs.Dayjs;
    conceptoPago: ConceptoPago;
    numeroCuota?: number;
    valorRecibido: number;
    moneda?: string;
    formaPago?: FormaPagoRecibo;
    pagoRealizadoPor?: PagoRealizadoPor;
    pagoRecibidoPor?: PagoRecibidoPor;
    observaciones?: string;
  }) => {
    const request: ReciboPagoRequest = {
      negociacionId,
      planDePagoId: values.planDePagoId,
      fechaElaboracion: values.fechaElaboracion?.format('YYYY-MM-DD'),
      conceptoPago: values.conceptoPago,
      numeroCuota: values.numeroCuota,
      valorRecibido: values.valorRecibido,
      moneda: values.moneda,
      formaPago: values.formaPago,
      pagoRealizadoPor: values.pagoRealizadoPor,
      pagoRecibidoPor: values.pagoRecibidoPor,
      observaciones: values.observaciones,
    };
    setGuardando(true);
    try {
      const creado = await reciboPagoApi.crear(request);
      setRecibos((prev) => [...prev, creado]);
      setModalReciboAbierto(false);
      formRecibo.resetFields();
      message.success('Recibo de pago registrado');
    } catch {
      message.error('No se pudo registrar el recibo de pago');
    } finally {
      setGuardando(false);
    }
  };

  const handleCrearActa = async (values: {
    fechaElaboracion?: dayjs.Dayjs;
    fechaHoraEntrega?: dayjs.Dayjs;
    ciudad?: string;
    lecturaEnergia?: string;
    lecturaAgua?: string;
    lecturaGas?: string;
    danosExistentes?: string;
    reparacionesPendientes?: string;
    acuerdosPosteriores?: string;
    compromisosAsumidos?: string;
    declaracionComprador?: DeclaracionComprador;
    observaciones?: string;
    vendedorEntregaConforme: boolean;
  }) => {
    const request: ActaEntregaRequest = {
      negociacionId,
      fechaElaboracion: values.fechaElaboracion?.format('YYYY-MM-DD'),
      fechaHoraEntrega: values.fechaHoraEntrega?.format('YYYY-MM-DDTHH:mm:ss'),
      ciudad: values.ciudad,
      lecturaEnergia: values.lecturaEnergia,
      lecturaAgua: values.lecturaAgua,
      lecturaGas: values.lecturaGas,
      danosExistentes: values.danosExistentes,
      reparacionesPendientes: values.reparacionesPendientes,
      acuerdosPosteriores: values.acuerdosPosteriores,
      compromisosAsumidos: values.compromisosAsumidos,
      declaracionComprador: values.declaracionComprador,
      observaciones: values.observaciones,
      vendedorEntregaConforme: values.vendedorEntregaConforme,
    };
    setGuardando(true);
    try {
      const creada = await actaEntregaApi.crear(request);
      setActa(creada);
      setModalActaAbierto(false);
      formActa.resetFields();
      message.success('Acta de entrega generada. Esto marca automáticamente el hito "Entrega realizada".');
      onCambioQuePuedaCerrarNegocio?.();
    } catch {
      message.error('No se pudo generar el acta de entrega');
    } finally {
      setGuardando(false);
    }
  };

  const handleRevisarActa = async () => {
    if (!acta) return;
    setRevisandoActa(true);
    try {
      await actaEntregaApi.revisarIA(acta.id);
      message.success('Revisión con IA completada');
    } catch {
      explicarErrorIA();
    } finally {
      setRevisandoActa(false);
    }
  };

  const handleSubirDocPromesa = async (archivo: File, tipo?: string) => {
    if (!promesa) return;
    const nuevo = await documentoPromesaCompraventaApi.subir(
      promesa.id,
      (tipo as TipoDocumentoPromesaCompraventa) ?? 'OTRO',
      archivo,
    );
    setDocumentosPromesa((prev) => [...prev, nuevo]);
  };

  const handleEliminarDocPromesa = async (id: string) => {
    if (!promesa) return;
    await documentoPromesaCompraventaApi.eliminar(promesa.id, id);
    setDocumentosPromesa((prev) => prev.filter((d) => d.id !== id));
  };

  const handleSubirDocActa = async (archivo: File, tipo?: string) => {
    if (!acta) return;
    const nuevo = await documentoActaEntregaApi.subir(acta.id, (tipo as TipoDocumentoActaEntrega) ?? 'OTRO', archivo);
    setDocumentosActa((prev) => [...prev, nuevo]);
  };

  const handleEliminarDocActa = async (id: string) => {
    if (!acta) return;
    await documentoActaEntregaApi.eliminar(acta.id, id);
    setDocumentosActa((prev) => prev.filter((d) => d.id !== id));
  };

  const handleSubirDocOtrosi = async (otrosiId: string, archivo: File) => {
    const nuevo = await documentoOtrosiPromesaApi.subir(otrosiId, undefined, archivo);
    setDocumentosOtrosi((prev) => ({ ...prev, [otrosiId]: [...(prev[otrosiId] ?? []), nuevo] }));
  };

  const handleEliminarDocOtrosi = async (otrosiId: string, id: string) => {
    await documentoOtrosiPromesaApi.eliminar(otrosiId, id);
    setDocumentosOtrosi((prev) => ({ ...prev, [otrosiId]: (prev[otrosiId] ?? []).filter((d) => d.id !== id) }));
  };

  const handleSubirDocRecibo = async (reciboId: string, archivo: File) => {
    const nuevo = await documentoReciboPagoApi.subir(reciboId, archivo);
    setDocumentosRecibo((prev) => ({ ...prev, [reciboId]: [...(prev[reciboId] ?? []), nuevo] }));
  };

  const handleEliminarDocRecibo = async (reciboId: string, id: string) => {
    await documentoReciboPagoApi.eliminar(reciboId, id);
    setDocumentosRecibo((prev) => ({ ...prev, [reciboId]: (prev[reciboId] ?? []).filter((d) => d.id !== id) }));
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <Card
        title="Promesa de compraventa"
        loading={cargando}
        size="small"
        extra={
          promesa && (
            <Space>
              <Select
                size="small"
                value={promesa.estado}
                loading={cambiandoEstadoPromesa}
                style={{ width: 160 }}
                onChange={handleCambiarEstadoPromesa}
                options={ESTADOS_DOCUMENTO.map((e) => ({ value: e, label: ETIQUETA_ESTADO_DOCUMENTO[e] }))}
              />
              <Button size="small" onClick={abrirModalEditarPromesa}>
                Editar
              </Button>
            </Space>
          )
        }
      >
        {promesa ? (
          <Space orientation="vertical">
            <Space>
              <Text strong>{promesa.numeroContrato}</Text>
              <Tag>{promesa.estado}</Tag>
            </Space>
            {promesa.ciudad && <Text type="secondary">Ciudad: {promesa.ciudad}</Text>}
            <Text type="secondary">
              {promesa.textoPromesa
                ? `Texto generado el ${promesa.fechaGeneracionTexto ? new Date(promesa.fechaGeneracionTexto).toLocaleString('es-CO') : ''}`
                : 'Aún no se ha generado el texto del contrato'}
            </Text>
            <Input.TextArea
              placeholder="Instrucciones adicionales para la IA (opcional). Ej: enfatiza la clausula de arras, usa un tono mas sencillo..."
              value={instruccionesPromesa}
              onChange={(e) => setInstruccionesPromesa(e.target.value)}
              rows={2}
              style={{ maxWidth: 480 }}
            />
            <Space wrap>
              <Button icon={<RobotOutlined />} loading={generandoTexto} onClick={handleGenerarTextoPromesa}>
                {promesa.textoPromesa ? 'Regenerar texto con IA' : 'Generar texto con IA'}
              </Button>
              {promesa.textoPromesa && (
                <Button icon={<FileTextOutlined />} onClick={abrirModalTextoPromesa}>
                  Ver / editar texto
                </Button>
              )}
              {promesa.pdfUrl && (
                <Button icon={<FilePdfOutlined />} onClick={handleVerPdfPromesa}>
                  Descargar PDF
                </Button>
              )}
            </Space>
          </Space>
        ) : (
          <Button icon={<FileAddOutlined />} onClick={() => setModalPromesaAbierto(true)}>
            Crear promesa de compraventa
          </Button>
        )}
      </Card>

      {promesa && (
        <Card title="Cláusulas de la promesa" size="small" loading={guardandoClausulasPromesa}>
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {catalogoClausulasPromesa.map((c) => (
              <Checkbox
                key={c.id}
                checked={clausulasAsignadasPromesa.has(c.id)}
                onChange={(e) => toggleClausulaPromesa(c.id, e.target.checked)}
              >
                {c.nombre}
              </Checkbox>
            ))}
          </div>
        </Card>
      )}

      {promesa && (
        <DocumentosAdjuntosCard
          title="Documentos de la promesa"
          documentos={documentosPromesa.map((d) => ({
            id: d.id,
            url: d.url,
            etiqueta: ETIQUETA_TIPO_DOCUMENTO_PROMESA_COMPRAVENTA[d.tipo],
          }))}
          tipoOpciones={TIPOS_DOCUMENTO_PROMESA_COMPRAVENTA.map((t) => ({
            value: t,
            label: ETIQUETA_TIPO_DOCUMENTO_PROMESA_COMPRAVENTA[t],
          }))}
          onSubir={handleSubirDocPromesa}
          onEliminar={handleEliminarDocPromesa}
        />
      )}

      {promesa && (
        <Card
          title="Otrosí a la promesa de compraventa"
          loading={cargando}
          size="small"
          extra={
            <Button size="small" icon={<FileAddOutlined />} onClick={abrirModalCrearOtrosi}>
              Nuevo otrosí
            </Button>
          }
        >
          <ItemList
            size="small"
            items={otrosis}
            rowKey={(otrosi) => otrosi.id}
            emptyDescription="Sin otrosíes registrados"
            renderItem={(otrosi) => (
              <Space orientation="vertical" size={4} style={{ width: '100%' }}>
                <Space wrap>
                  <Text strong>{otrosi.numeroOtrosi}</Text>
                  <Tag>{ETIQUETA_MOTIVO_OTROSI[otrosi.motivo as MotivoOtrosi]}</Tag>
                  <Tag color={otrosi.aplicado ? 'green' : 'default'}>{otrosi.aplicado ? 'Aplicado' : 'Pendiente de aplicar'}</Tag>
                </Space>
                {otrosi.textoOtrosi && (
                  <Text type="secondary" style={{ fontSize: 12 }}>
                    Texto generado el {otrosi.fechaGeneracionTexto ? new Date(otrosi.fechaGeneracionTexto).toLocaleString('es-CO') : ''}
                  </Text>
                )}
                <Input.TextArea
                  placeholder="Instrucciones adicionales para la IA (opcional)"
                  value={instruccionesOtrosi[otrosi.id] ?? ''}
                  onChange={(e) => setInstruccionesOtrosi((prev) => ({ ...prev, [otrosi.id]: e.target.value }))}
                  rows={2}
                  style={{ maxWidth: 480 }}
                />
                <Space wrap>
                  <Button size="small" onClick={() => abrirModalEditarOtrosi(otrosi)} disabled={otrosi.aplicado}>
                    Editar
                  </Button>
                  <Button
                    size="small"
                    icon={<RobotOutlined />}
                    loading={generandoTextoOtrosiId === otrosi.id}
                    onClick={() => handleGenerarTextoOtrosi(otrosi.id)}
                  >
                    {otrosi.textoOtrosi ? 'Regenerar texto con IA' : 'Generar texto con IA'}
                  </Button>
                  {otrosi.textoOtrosi && (
                    <Button size="small" icon={<FileTextOutlined />} onClick={() => abrirModalTextoOtrosi(otrosi)}>
                      Ver / editar texto
                    </Button>
                  )}
                  {otrosi.pdfUrl && (
                    <Button size="small" icon={<FilePdfOutlined />} onClick={() => handleVerPdfOtrosi(otrosi)}>
                      Descargar PDF
                    </Button>
                  )}
                  <Button
                    size="small"
                    type="primary"
                    disabled={otrosi.aplicado}
                    loading={aplicandoOtrosiId === otrosi.id}
                    onClick={() => handleAplicarOtrosi(otrosi.id)}
                  >
                    {otrosi.aplicado ? 'Aplicado' : 'Aplicar a la negociación'}
                  </Button>
                </Space>
                <Card title="Cláusulas del otrosí" size="small" style={{ width: '100%' }} loading={guardandoClausulasOtrosiId === otrosi.id}>
                  <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                    {catalogoClausulasOtrosi.map((c) => (
                      <Checkbox
                        key={c.id}
                        checked={(clausulasAsignadasOtrosi[otrosi.id] ?? new Set()).has(c.id)}
                        onChange={(e) => toggleClausulaOtrosi(otrosi.id, c.id, e.target.checked)}
                      >
                        {c.nombre}
                      </Checkbox>
                    ))}
                  </div>
                </Card>
                <DocumentosAdjuntosCard
                  title="Documentos del otrosí"
                  documentos={(documentosOtrosi[otrosi.id] ?? []).map((d) => ({ id: d.id, url: d.url, etiqueta: d.descripcion }))}
                  onSubir={(archivo) => handleSubirDocOtrosi(otrosi.id, archivo)}
                  onEliminar={(id) => handleEliminarDocOtrosi(otrosi.id, id)}
                />
              </Space>
            )}
          />
        </Card>
      )}

      <Card title="Recibos de pago" loading={cargando} size="small">
        <ItemList
          size="small"
          items={recibos}
          rowKey={(recibo) => recibo.id}
          emptyDescription="Sin pagos registrados"
          renderItem={(recibo) => (
            <Space orientation="vertical" size={0}>
              <Text strong>
                {recibo.numeroRecibo} — {recibo.conceptoPago}
                {recibo.numeroCuota != null ? ` (cuota ${recibo.numeroCuota})` : ''}
              </Text>
              <Text type="secondary">
                {formatearMoneda(recibo.valorRecibido)} {recibo.moneda && recibo.moneda !== 'COP' ? recibo.moneda : ''}
                {recibo.saldoPendiente != null ? ` · Saldo pendiente: ${formatearMoneda(recibo.saldoPendiente)}` : ''}
              </Text>
              {(recibo.pagoRealizadoPor || recibo.pagoRecibidoPor) && (
                <Text type="secondary" style={{ fontSize: 12 }}>
                  {recibo.pagoRealizadoPor ? `Pagó: ${ETIQUETA_PAGO_REALIZADO_POR[recibo.pagoRealizadoPor]}` : ''}
                  {recibo.pagoRealizadoPor && recibo.pagoRecibidoPor ? ' · ' : ''}
                  {recibo.pagoRecibidoPor ? `Recibió: ${ETIQUETA_PAGO_RECIBIDO_POR[recibo.pagoRecibidoPor]}` : ''}
                </Text>
              )}
              <DocumentosAdjuntosCard
                title="Comprobante del pago"
                documentos={(documentosRecibo[recibo.id] ?? []).map((d) => ({ id: d.id, url: d.url }))}
                onSubir={(archivo) => handleSubirDocRecibo(recibo.id, archivo)}
                onEliminar={(id) => handleEliminarDocRecibo(recibo.id, id)}
              />
            </Space>
          )}
        />
        <Button icon={<FileAddOutlined />} onClick={() => setModalReciboAbierto(true)} style={{ marginTop: 8 }}>
          Registrar pago
        </Button>
      </Card>

      <Card title="Acta de entrega" loading={cargando} size="small">
        {acta ? (
          <Space orientation="vertical" style={{ width: '100%' }}>
            <Space>
              <Text strong>{acta.numeroActa}</Text>
              <Tag color={acta.vendedorEntregaConforme ? 'green' : 'orange'}>
                {acta.vendedorEntregaConforme ? 'Entrega conforme' : 'Con observaciones'}
              </Tag>
            </Space>
            {(acta.lecturaEnergia || acta.lecturaAgua || acta.lecturaGas) && (
              <Text type="secondary">
                Lecturas — Energía: {acta.lecturaEnergia ?? '—'} · Agua: {acta.lecturaAgua ?? '—'} · Gas: {acta.lecturaGas ?? '—'}
              </Text>
            )}
            {acta.danosExistentes && <Text type="secondary">Daños existentes: {acta.danosExistentes}</Text>}
            {acta.reparacionesPendientes && <Text type="secondary">Reparaciones pendientes: {acta.reparacionesPendientes}</Text>}
            {acta.acuerdosPosteriores && <Text type="secondary">Acuerdos posteriores: {acta.acuerdosPosteriores}</Text>}
            {acta.compromisosAsumidos && <Text type="secondary">Compromisos asumidos: {acta.compromisosAsumidos}</Text>}
            {acta.declaracionComprador && (
              <Text type="secondary">Declaración del comprador: {ETIQUETA_DECLARACION_COMPRADOR[acta.declaracionComprador]}</Text>
            )}
            {acta.observaciones && <Text type="secondary">Observaciones: {acta.observaciones}</Text>}
            <Button icon={<RobotOutlined />} loading={revisandoActa} onClick={handleRevisarActa}>
              Revisar con IA
            </Button>
          </Space>
        ) : (
          <Button icon={<FileAddOutlined />} onClick={() => setModalActaAbierto(true)}>
            Generar acta de entrega
          </Button>
        )}
      </Card>

      {acta && (
        <Row gutter={16}>
          <Col xs={24} md={8}>
            <Card title="Condiciones de entrega" size="small" loading={guardandoCondiciones}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {catalogoCondicionesEntrega.map((c) => (
                  <Checkbox
                    key={c.id}
                    checked={condicionesAsignadas.has(c.id)}
                    onChange={(e) => toggleCondicionEntrega(c.id, e.target.checked)}
                  >
                    {c.nombre}
                  </Checkbox>
                ))}
              </div>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card title="Accesos entregados" size="small" loading={guardandoAccesos}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {catalogoAccesosEntrega.map((a) => (
                  <Checkbox key={a.id} checked={accesosAsignados.has(a.id)} onChange={(e) => toggleAccesoEntrega(a.id, e.target.checked)}>
                    {a.nombre}
                  </Checkbox>
                ))}
              </div>
            </Card>
          </Col>
          <Col xs={24} md={8}>
            <Card title="Paz y salvos de servicios" size="small" loading={guardandoPazYSalvos}>
              <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
                {catalogoPazYSalvoTipos.map((p) => (
                  <Checkbox key={p.id} checked={pazYSalvosAsignados.has(p.id)} onChange={(e) => togglePazYSalvo(p.id, e.target.checked)}>
                    {p.nombre}
                  </Checkbox>
                ))}
              </div>
            </Card>
          </Col>
        </Row>
      )}

      {acta && (
        <DocumentosAdjuntosCard
          title="Documentos del acta de entrega"
          documentos={documentosActa.map((d) => ({ id: d.id, url: d.url, etiqueta: ETIQUETA_TIPO_DOCUMENTO_ACTA_ENTREGA[d.tipo] }))}
          tipoOpciones={TIPOS_DOCUMENTO_ACTA_ENTREGA.map((t) => ({ value: t, label: ETIQUETA_TIPO_DOCUMENTO_ACTA_ENTREGA[t] }))}
          onSubir={handleSubirDocActa}
          onEliminar={handleEliminarDocActa}
        />
      )}

      <Modal
        title="Crear promesa de compraventa"
        open={modalPromesaAbierto}
        onCancel={() => setModalPromesaAbierto(false)}
        onOk={() => formPromesa.submit()}
        confirmLoading={guardando}
        okText="Crear"
        cancelText="Cancelar"
      >
        <Form form={formPromesa} layout="vertical" onFinish={handleCrearPromesa}>
          <Form.Item name="fechaElaboracion" label="Fecha de elaboración">
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item name="ciudad" label="Ciudad">
            <Input />
          </Form.Item>
          <Form.Item name="opcionFirma" label="Opción de firma">
            <Select options={OPCIONES_FIRMA.map((o) => ({ value: o, label: o }))} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Editar promesa de compraventa"
        open={modalEditarPromesaAbierto}
        onCancel={() => setModalEditarPromesaAbierto(false)}
        onOk={() => formEditarPromesa.submit()}
        confirmLoading={guardando}
        okText="Guardar"
        cancelText="Cancelar"
      >
        <Form form={formEditarPromesa} layout="vertical" onFinish={handleGuardarEditarPromesa}>
          <Form.Item name="ciudad" label="Ciudad">
            <Input />
          </Form.Item>
          <Form.Item name="opcionFirma" label="Opción de firma">
            <Select allowClear options={OPCIONES_FIRMA.map((o) => ({ value: o, label: o }))} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Texto de la promesa de compraventa"
        open={modalTextoPromesaAbierto}
        onCancel={() => setModalTextoPromesaAbierto(false)}
        onOk={handleGuardarTextoPromesa}
        confirmLoading={guardandoTextoPromesa}
        okText="Guardar"
        cancelText="Cancelar"
        width={760}
      >
        <Input.TextArea
          value={textoPromesaEditado}
          onChange={(e) => setTextoPromesaEditado(e.target.value)}
          rows={20}
          style={{ fontFamily: 'monospace', fontSize: 13 }}
        />
      </Modal>

      <Modal
        title="Texto del otrosí"
        open={otrosiTextoEditando != null}
        onCancel={() => setOtrosiTextoEditando(null)}
        onOk={handleGuardarTextoOtrosi}
        confirmLoading={guardandoTextoOtrosi}
        okText="Guardar"
        cancelText="Cancelar"
        width={760}
      >
        <Input.TextArea
          value={textoOtrosiEditado}
          onChange={(e) => setTextoOtrosiEditado(e.target.value)}
          rows={20}
          style={{ fontFamily: 'monospace', fontSize: 13 }}
        />
      </Modal>

      <Modal
        title="Registrar pago"
        open={modalReciboAbierto}
        onCancel={() => setModalReciboAbierto(false)}
        onOk={() => formRecibo.submit()}
        confirmLoading={guardando}
        okText="Registrar"
        cancelText="Cancelar"
      >
        <Form form={formRecibo} layout="vertical" onFinish={handleCrearRecibo} initialValues={{ moneda: 'COP' }}>
          <Form.Item name="fechaElaboracion" label="Fecha">
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item
            name="conceptoPago"
            label="Concepto de pago"
            rules={[{ required: true, message: 'Selecciona el concepto del pago' }]}
          >
            <Select options={CONCEPTOS_PAGO.map((c) => ({ value: c, label: c }))} />
          </Form.Item>
          <Form.Item name="planDePagoId" label="Cuota del plan de pago">
            <Select
              allowClear
              placeholder="Selecciona la cuota que corresponde a este pago"
              options={planDePago.map((p) => ({
                value: p.id,
                label: `Cuota ${p.numeroCuota ?? '—'} — ${formatearMoneda(p.valor)}${p.pagada ? ' (ya marcada como pagada)' : ''}`,
              }))}
            />
          </Form.Item>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Item name="numeroCuota" label="N° de cuota">
                <InputNumber style={{ width: '100%' }} min={1} />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="moneda" label="Moneda">
                <Select options={[{ value: 'COP', label: 'COP' }, { value: 'USD', label: 'USD' }]} />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item
            name="valorRecibido"
            label="Valor recibido"
            rules={[{ required: true, message: 'El valor recibido es obligatorio' }]}
          >
            <InputMoneda />
          </Form.Item>
          <Form.Item name="formaPago" label="Forma de pago">
            <Select options={FORMAS_PAGO_RECIBO.map((f) => ({ value: f, label: f }))} allowClear />
          </Form.Item>
          <Row gutter={8}>
            <Col span={12}>
              <Form.Item name="pagoRealizadoPor" label="Pago realizado por">
                <Select
                  allowClear
                  options={PAGOS_REALIZADO_POR.map((p) => ({ value: p, label: ETIQUETA_PAGO_REALIZADO_POR[p] }))}
                />
              </Form.Item>
            </Col>
            <Col span={12}>
              <Form.Item name="pagoRecibidoPor" label="Pago recibido por">
                <Select
                  allowClear
                  options={PAGOS_RECIBIDO_POR.map((p) => ({ value: p, label: ETIQUETA_PAGO_RECIBIDO_POR[p] }))}
                />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="observaciones" label="Observaciones">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title="Generar acta de entrega"
        open={modalActaAbierto}
        onCancel={() => setModalActaAbierto(false)}
        onOk={() => formActa.submit()}
        confirmLoading={guardando}
        okText="Generar"
        cancelText="Cancelar"
      >
        <Form
          form={formActa}
          layout="vertical"
          onFinish={handleCrearActa}
          initialValues={{ vendedorEntregaConforme: true }}
        >
          <Form.Item name="fechaElaboracion" label="Fecha de elaboración">
            <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
          </Form.Item>
          <Form.Item name="fechaHoraEntrega" label="Fecha y hora de entrega">
            <DatePicker style={{ width: '100%' }} showTime format="YYYY-MM-DD HH:mm" />
          </Form.Item>
          <Form.Item name="ciudad" label="Ciudad">
            <Input />
          </Form.Item>
          <Row gutter={8}>
            <Col span={8}>
              <Form.Item name="lecturaEnergia" label="Lectura de energía">
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="lecturaAgua" label="Lectura de agua">
                <Input />
              </Form.Item>
            </Col>
            <Col span={8}>
              <Form.Item name="lecturaGas" label="Lectura de gas">
                <Input />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="danosExistentes" label="Daños existentes">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="reparacionesPendientes" label="Reparaciones pendientes">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="acuerdosPosteriores" label="Acuerdos posteriores">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="compromisosAsumidos" label="Compromisos asumidos">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="declaracionComprador" label="Declaración del comprador">
            <Select
              allowClear
              options={DECLARACIONES_COMPRADOR.map((d) => ({ value: d, label: ETIQUETA_DECLARACION_COMPRADOR[d] }))}
            />
          </Form.Item>
          <Form.Item name="observaciones" label="Observaciones generales">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="vendedorEntregaConforme" label="El vendedor entrega conforme" valuePropName="checked">
            <Switch />
          </Form.Item>
        </Form>
      </Modal>

      <Modal
        title={otrosiEditando ? 'Editar otrosí' : 'Nuevo otrosí a la promesa'}
        open={modalOtrosiAbierto}
        onCancel={() => {
          setModalOtrosiAbierto(false);
          setOtrosiEditando(null);
        }}
        onOk={() => formOtrosi.submit()}
        confirmLoading={guardando}
        okText={otrosiEditando ? 'Guardar' : 'Crear'}
        cancelText="Cancelar"
        width={640}
      >
        <Form form={formOtrosi} layout="vertical" onFinish={handleGuardarOtrosi}>
          {!otrosiEditando && (
            <Form.Item name="fechaElaboracion" label="Fecha de elaboración">
              <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
            </Form.Item>
          )}
          <Form.Item name="motivo" label="Motivo del otrosí" rules={[{ required: true, message: 'Selecciona el motivo' }]}>
            <Select options={MOTIVOS_OTROSI.map((m) => ({ value: m, label: ETIQUETA_MOTIVO_OTROSI[m] }))} />
          </Form.Item>
          {motivoOtrosiSeleccionado === 'OTRO' && (
            <Form.Item name="otroMotivoTexto" label="Especifica el motivo">
              <Input />
            </Form.Item>
          )}
          <Form.Item name="justificacion" label="Justificación">
            <Select allowClear options={JUSTIFICACIONES_OTROSI.map((j) => ({ value: j, label: ETIQUETA_JUSTIFICACION_OTROSI[j] }))} />
          </Form.Item>

          <Text strong>Nuevas condiciones</Text>
          <Row gutter={8} style={{ marginTop: 8 }}>
            <Col xs={24} sm={8}>
              <Form.Item name="nuevoPrecio" label="Nuevo precio">
                <InputMoneda />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="nuevaFechaEscritura" label="Nueva fecha de escritura">
                <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
            <Col xs={24} sm={8}>
              <Form.Item name="nuevaFechaEntrega" label="Nueva fecha de entrega">
                <DatePicker style={{ width: '100%' }} format="YYYY-MM-DD" />
              </Form.Item>
            </Col>
          </Row>
          <Form.Item name="nuevaDistribucionGastos" label="Nueva distribución de gastos">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="nuevosBienesIncluidos" label="Nuevos bienes incluidos">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="nuevasCondicionesEspeciales" label="Nuevas condiciones especiales">
            <Input.TextArea rows={2} />
          </Form.Item>
          <Form.Item name="observaciones" label="Observaciones">
            <Input.TextArea rows={2} />
          </Form.Item>
        </Form>
      </Modal>
    </Space>
  );
}
