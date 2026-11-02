import { useEffect, useState } from 'react';
import { Button, Card, Col, Form, Input, Modal, Row, Select, Skeleton, Switch, message } from 'antd';
import InputMoneda from '../../../components/InputMoneda';
import { PlusOutlined, SaveOutlined } from '@ant-design/icons';
import { aliadoApi, condicionCaptacionApi, propietarioApi } from '../../../api/captacion';
import { useAuth } from '../../../context/AuthContext';
import {
  ETIQUETA_ORIGEN_PROPIEDAD,
  ETIQUETA_TIPO_ALIADO,
  TIPOS_ALIADO,
  TIPOS_CONTACTO_PROPIETARIO,
} from '../../../types/captacion';
import type {
  Aliado,
  AliadoRequest,
  CondicionCaptacionRequest,
  OrigenPropiedad,
  Propietario,
  PropietarioRequest,
  TipoAliado,
} from '../../../types/captacion';

interface Props {
  inmuebleId: string;
  propietarioIdActual?: string;
  onGuardado?: () => void;
}

interface FormValues {
  origenPropiedad?: OrigenPropiedad;
  aliadoId?: string;
  propietarioId?: string;
  whatsappAsesor?: string;
  tipoComision?: string;
  valorComision?: number;
  exclusividad: boolean;
}

interface NuevoAliadoFormValues {
  nombre: string;
  telefono?: string;
  tipo: TipoAliado;
}

interface NuevoPropietarioFormValues {
  nombre: string;
  telefonoPrincipal: string;
  tipoContacto: string;
}

const ORIGENES_PROPIEDAD: OrigenPropiedad[] = ['PROPIETARIO_DIRECTO', 'ALIADO'];

const TIPOS_COMISION = [
  { value: 'PORCENTAJE_VENTA', label: 'Porcentaje de venta' },
  { value: 'MONTO_FIJO', label: 'Monto fijo' },
  { value: 'SOBRE_PRECIO', label: 'Sobre precio' },
];

export default function CondicionesTab({ inmuebleId, propietarioIdActual, onGuardado }: Props) {
  const { usuario } = useAuth();
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);
  const [aliados, setAliados] = useState<Aliado[]>([]);
  const [propietarios, setPropietarios] = useState<Propietario[]>([]);
  const [modalAliadoAbierto, setModalAliadoAbierto] = useState(false);
  const [creandoAliado, setCreandoAliado] = useState(false);
  const [modalPropietarioAbierto, setModalPropietarioAbierto] = useState(false);
  const [creandoPropietario, setCreandoPropietario] = useState(false);
  const [form] = Form.useForm<FormValues>();
  const [formAliado] = Form.useForm<NuevoAliadoFormValues>();
  const [formPropietario] = Form.useForm<NuevoPropietarioFormValues>();

  const cargarAliados = async () => {
    try {
      setAliados(await aliadoApi.listar());
    } catch {
      setAliados([]);
      message.error('No se pudo cargar la lista de aliados');
    }
  };

  const cargarPropietarios = async () => {
    try {
      setPropietarios(await propietarioApi.listar());
    } catch {
      setPropietarios([]);
      message.error('No se pudo cargar la lista de propietarios');
    }
  };

  const cargar = async () => {
    setLoading(true);
    try {
      const condicion = await condicionCaptacionApi.buscarPorInmueble(inmuebleId);
      if (condicion) {
        form.setFieldsValue({
          origenPropiedad: condicion.origenPropiedad,
          aliadoId: condicion.aliadoId,
          propietarioId: propietarioIdActual,
          whatsappAsesor: condicion.whatsappAsesor,
          tipoComision: condicion.tipoComision,
          valorComision: condicion.valorComision,
          exclusividad: condicion.exclusividad,
        });
      } else {
        form.setFieldsValue({ propietarioId: propietarioIdActual });
      }
    } catch {
      // Todavia no existen condiciones de captacion para este inmueble (es nuevo): se deja el formulario en blanco.
      form.setFieldsValue({ propietarioId: propietarioIdActual });
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargarAliados();
    cargarPropietarios();
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmuebleId]);

  const handleGuardar = async (values: FormValues) => {
    setGuardando(true);
    try {
      const request: CondicionCaptacionRequest = {
        inmuebleId,
        asesorId: usuario?.usuarioId,
        origenPropiedad: values.origenPropiedad,
        aliadoId: values.origenPropiedad === 'ALIADO' ? values.aliadoId : undefined,
        propietarioId: values.origenPropiedad === 'PROPIETARIO_DIRECTO' ? values.propietarioId : undefined,
        whatsappAsesor: values.whatsappAsesor,
        tipoComision: values.tipoComision,
        valorComision: values.valorComision,
        exclusividad: values.exclusividad ?? false,
      };
      await condicionCaptacionApi.guardar(request);
      message.success('Condiciones de captación guardadas');
      onGuardado?.();
    } catch {
      message.error('No se pudieron guardar las condiciones');
    } finally {
      setGuardando(false);
    }
  };

  const handleCrearAliado = async (values: NuevoAliadoFormValues) => {
    setCreandoAliado(true);
    try {
      const request: AliadoRequest = { nombre: values.nombre, telefono: values.telefono, tipo: values.tipo };
      const creado = await aliadoApi.crear(request);
      setAliados((prev) => [...prev, creado]);
      form.setFieldValue('aliadoId', creado.id);
      message.success('Aliado creado');
      setModalAliadoAbierto(false);
      formAliado.resetFields();
    } catch {
      message.error('No se pudo crear el aliado');
    } finally {
      setCreandoAliado(false);
    }
  };

  const handleCrearPropietario = async (values: NuevoPropietarioFormValues) => {
    setCreandoPropietario(true);
    try {
      const request: PropietarioRequest = {
        nombre: values.nombre,
        telefonoPrincipal: values.telefonoPrincipal,
        tipoContacto: values.tipoContacto,
      };
      const creado = await propietarioApi.crear(request);
      setPropietarios((prev) => [...prev, creado]);
      form.setFieldValue('propietarioId', creado.id);
      message.success('Propietario creado');
      setModalPropietarioAbierto(false);
      formPropietario.resetFields();
    } catch {
      message.error('No se pudo crear el propietario');
    } finally {
      setCreandoPropietario(false);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <Card size="small" style={{ maxWidth: 560 }}>
      <Form form={form} layout="vertical" onFinish={handleGuardar} initialValues={{ exclusividad: false }}>
        <Form.Item
          name="origenPropiedad"
          label="Origen de la propiedad"
          tooltip="¿Llegó por contacto directo con el propietario o por medio de un aliado?"
        >
          <Select
            allowClear
            options={ORIGENES_PROPIEDAD.map((o) => ({ value: o, label: ETIQUETA_ORIGEN_PROPIEDAD[o] }))}
          />
        </Form.Item>
        <Form.Item noStyle shouldUpdate={(prev, next) => prev.origenPropiedad !== next.origenPropiedad}>
          {({ getFieldValue }) =>
            getFieldValue('origenPropiedad') === 'ALIADO' ? (
              <Form.Item
                name="aliadoId"
                label="Inmobiliaria o asesor aliado"
                rules={[{ required: true, message: 'Selecciona o crea un aliado' }]}
              >
                <Select
                  showSearch
                  placeholder="Selecciona un aliado"
                  optionFilterProp="label"
                  options={aliados.map((a) => ({ value: a.id, label: `${a.nombre} (${ETIQUETA_TIPO_ALIADO[a.tipo]})` }))}
                  popupRender={(menu) => (
                    <>
                      {menu}
                      <div style={{ padding: 8, borderTop: '1px solid #f0f0f0' }}>
                        <Button type="link" icon={<PlusOutlined />} style={{ padding: 0 }} onClick={() => setModalAliadoAbierto(true)}>
                          Crear nuevo aliado
                        </Button>
                      </div>
                    </>
                  )}
                />
              </Form.Item>
            ) : getFieldValue('origenPropiedad') === 'PROPIETARIO_DIRECTO' ? (
              <Form.Item
                name="propietarioId"
                label="Propietario"
                rules={[{ required: true, message: 'Selecciona o crea un propietario' }]}
              >
                <Select
                  showSearch
                  placeholder="Selecciona un propietario"
                  optionFilterProp="label"
                  options={propietarios.map((p) => ({ value: p.id, label: p.nombre }))}
                  popupRender={(menu) => (
                    <>
                      {menu}
                      <div style={{ padding: 8, borderTop: '1px solid #f0f0f0' }}>
                        <Button type="link" icon={<PlusOutlined />} style={{ padding: 0 }} onClick={() => setModalPropietarioAbierto(true)}>
                          Crear nuevo propietario
                        </Button>
                      </div>
                    </>
                  )}
                />
              </Form.Item>
            ) : null
          }
        </Form.Item>

        <Modal
          title="Nuevo aliado"
          open={modalAliadoAbierto}
          onCancel={() => setModalAliadoAbierto(false)}
          footer={null}
          destroyOnHidden
        >
          <Form form={formAliado} layout="vertical" onFinish={handleCrearAliado} initialValues={{ tipo: 'INMOBILIARIA' }}>
            <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'Escribe el nombre' }]}>
              <Input placeholder="Ej: Inmobiliaria del Valle" />
            </Form.Item>
            <Form.Item name="telefono" label="Teléfono">
              <Input placeholder="Ej: 3001234567" />
            </Form.Item>
            <Form.Item name="tipo" label="Tipo">
              <Select options={TIPOS_ALIADO.map((t) => ({ value: t, label: ETIQUETA_TIPO_ALIADO[t] }))} />
            </Form.Item>
            <Button type="primary" htmlType="submit" loading={creandoAliado}>
              Crear aliado
            </Button>
          </Form>
        </Modal>

        <Modal
          title="Nuevo propietario"
          open={modalPropietarioAbierto}
          onCancel={() => setModalPropietarioAbierto(false)}
          footer={null}
          destroyOnHidden
        >
          <Form form={formPropietario} layout="vertical" onFinish={handleCrearPropietario}>
            <Form.Item name="nombre" label="Nombre" rules={[{ required: true, message: 'Escribe el nombre' }]}>
              <Input placeholder="Ej: Carlos Pérez" />
            </Form.Item>
            <Form.Item
              name="telefonoPrincipal"
              label="Teléfono principal"
              rules={[{ required: true, message: 'Escribe el teléfono' }]}
            >
              <Input placeholder="Ej: 3001234567" />
            </Form.Item>
            <Form.Item
              name="tipoContacto"
              label="Tipo de contacto"
              rules={[{ required: true, message: 'Selecciona el tipo de contacto' }]}
            >
              <Select showSearch options={TIPOS_CONTACTO_PROPIETARIO.map((t) => ({ value: t, label: t }))} />
            </Form.Item>
            <Button type="primary" htmlType="submit" loading={creandoPropietario}>
              Crear propietario
            </Button>
          </Form>
        </Modal>
        <Form.Item label="Asesor encargado">
          <Input value={usuario?.nombre} disabled />
        </Form.Item>
        <Form.Item name="whatsappAsesor" label="WhatsApp del asesor">
          <Input placeholder="Ej: 3001234567" />
        </Form.Item>
        <Row gutter={8}>
          <Col xs={24} lg={14}>
            <Form.Item name="tipoComision" label="Tipo de comisión acordada">
              <Select allowClear options={TIPOS_COMISION} />
            </Form.Item>
          </Col>
          <Col xs={24} lg={10}>
            <Form.Item name="valorComision" label="Valor">
              <InputMoneda />
            </Form.Item>
          </Col>
        </Row>
        <Form.Item name="exclusividad" label="Condición de captación pactada" valuePropName="checked">
          <Switch checkedChildren="Con exclusividad" unCheckedChildren="Sin exclusividad" />
        </Form.Item>
        <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={guardando}>
          Guardar condiciones
        </Button>
      </Form>
    </Card>
  );
}
