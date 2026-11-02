import { useEffect, useState } from 'react';
import { isAxiosError } from 'axios';
import { Button, Card, Col, Form, Input, InputNumber, Row, Select, Switch, message } from 'antd';
import { SaveOutlined, ThunderboltOutlined } from '@ant-design/icons';
import { inmuebleApi, puntoReferenciaApi } from '../../../api/captacion';
import InputMoneda from '../../../components/InputMoneda';
import { ENTORNOS, ETIQUETA_TIPO_PROPIEDAD, TIPOS_PROPIEDAD, ZONAS_VALLEDUPAR } from '../../../types/captacion';
import type { Inmueble, InmuebleRequest } from '../../../types/captacion';

interface Props {
  inmueble: Inmueble;
  onGuardado: () => void;
}

type FormValues = InmuebleRequest & { ubicacionGeografica?: string };

function formatearUbicacion(latitud?: number, longitud?: number): string {
  if (latitud == null || longitud == null) return '';
  return `${latitud}, ${longitud}`;
}

function parsearUbicacion(texto?: string): { latitud?: number; longitud?: number } {
  if (!texto) return { latitud: undefined, longitud: undefined };
  const match = texto.match(/(-?\d+(?:\.\d+)?)\s*[,;\s]\s*(-?\d+(?:\.\d+)?)/);
  if (!match) return { latitud: undefined, longitud: undefined };
  return { latitud: parseFloat(match[1]), longitud: parseFloat(match[2]) };
}

export default function InformacionGeneralTab({ inmueble, onGuardado }: Props) {
  const [guardando, setGuardando] = useState(false);
  const [sugiriendoPrecio, setSugiriendoPrecio] = useState(false);
  const [catalogoPuntosReferencia, setCatalogoPuntosReferencia] = useState<string[]>([]);
  const [form] = Form.useForm<FormValues>();

  useEffect(() => {
    puntoReferenciaApi
      .listar()
      .then((puntos) => setCatalogoPuntosReferencia(puntos.map((p) => p.nombre)))
      .catch(() => undefined);
  }, []);

  const handleGuardar = async (values: FormValues) => {
    const { ubicacionGeografica, ...resto } = values;
    const { latitud, longitud } = parsearUbicacion(ubicacionGeografica);
    const payload: InmuebleRequest = { ...resto, latitud, longitud };
    setGuardando(true);
    try {
      await inmuebleApi.actualizar(inmueble.id, payload);
      message.success('Cambios guardados');
      onGuardado();
    } catch {
      message.error('No se pudieron guardar los cambios');
    } finally {
      setGuardando(false);
    }
  };

  const handleSugerirPrecio = async () => {
    setSugiriendoPrecio(true);
    try {
      const resultado = await inmuebleApi.sugerirPrecioConIA(inmueble.id);
      form.setFieldValue('precioSugerido', resultado.precioSugerido);
      message.success({
        content: (
          <div>
            <div>
              Precio sugerido: {resultado.precioSugerido.toLocaleString('es-CO')} COP (rango{' '}
              {resultado.rangoMinimo.toLocaleString('es-CO')} - {resultado.rangoMaximo.toLocaleString('es-CO')})
            </div>
            <div style={{ fontWeight: 400, fontSize: 12, marginTop: 4 }}>{resultado.justificacion}</div>
          </div>
        ),
        duration: 8,
      });
    } catch (error) {
      const detalle = isAxiosError(error) ? (error.response?.data as { mensaje?: string } | undefined)?.mensaje : undefined;
      message.error(detalle ?? 'No se pudo sugerir un precio con IA');
    } finally {
      setSugiriendoPrecio(false);
    }
  };

  return (
    <Form
      form={form}
      layout="vertical"
      onFinish={handleGuardar}
      initialValues={{
        tipoPropiedad: inmueble.tipoPropiedad,
        ciudad: inmueble.ciudad,
        direccionExacta: inmueble.direccionExacta,
        zona: inmueble.zona,
        entorno: inmueble.entorno,
        estratoSocioeconomico: inmueble.estratoSocioeconomico,
        usoSuelo: inmueble.usoSuelo,
        puntosClavesCercanos: inmueble.puntosClavesCercanos,
        ubicacionGeografica: formatearUbicacion(inmueble.latitud, inmueble.longitud),
        areaTotal: inmueble.areaTotal,
        areaConstruida: inmueble.areaConstruida,
        areaPrivada: inmueble.areaPrivada,
        estadoFisico: inmueble.estadoFisico,
        situada: inmueble.situada,
        niveles: inmueble.niveles,
        piso: inmueble.piso,
        vista: inmueble.vista,
        tipoAcceso: inmueble.tipoAcceso,
        medidaFrente: inmueble.medidaFrente,
        medidaFondo: inmueble.medidaFondo,
        puntosReferencia: inmueble.puntosReferencia,
        matriculaInmobiliaria: inmueble.matriculaInmobiliaria,
        referenciaCatastral: inmueble.referenciaCatastral,
        expectativaPrecio: inmueble.expectativaPrecio,
        precioMinimoAceptado: inmueble.precioMinimoAceptado,
        precioSugerido: inmueble.precioSugerido,
        valorAdministracion: inmueble.valorAdministracion,
        valorArriendo: inmueble.valorArriendo,
        valorImpuestos: inmueble.valorImpuestos,
        escuchaOfertas: inmueble.escuchaOfertas,
      }}
    >
      <Row gutter={16}>
        <Col xs={24} lg={12}>
          <Card title="Zona y entorno" size="small" style={{ marginBottom: 16 }}>
            <Form.Item name="tipoPropiedad" label="Tipo de propiedad">
              <Select options={TIPOS_PROPIEDAD.map((t) => ({ value: t, label: ETIQUETA_TIPO_PROPIEDAD[t] }))} />
            </Form.Item>
            <Form.Item name="ciudad" label="Ciudad">
              <Input />
            </Form.Item>
            <Form.Item name="direccionExacta" label="Dirección exacta">
              <Input />
            </Form.Item>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="zona" label="Zona">
                  <Select showSearch allowClear options={ZONAS_VALLEDUPAR.map((z) => ({ value: z, label: z }))} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="entorno" label="Entorno">
                  <Select allowClear options={ENTORNOS.map((e) => ({ value: e, label: e }))} />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="estratoSocioeconomico" label="Estrato">
                  <Select allowClear options={[1, 2, 3, 4, 5, 6, 7].map((n) => ({ value: n, label: String(n) }))} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="usoSuelo" label="Uso de suelo">
                  <Select
                    allowClear
                    options={['Residencial', 'Comercial', 'Mixto', 'Agricola', 'Industrial'].map((v) => ({
                      value: v,
                      label: v,
                    }))}
                  />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item
              name="puntosReferencia"
              label="Puntos de referencia (para búsqueda)"
              tooltip="Lugares cercanos que un cliente podría mencionar al buscar, ej: La Popa, C.C. Mayales"
            >
              <Select
                mode="tags"
                allowClear
                placeholder="Escribe y presiona Enter para agregar"
                options={catalogoPuntosReferencia.map((nombre) => ({ value: nombre, label: nombre }))}
              />
            </Form.Item>
            <Form.Item
              name="ubicacionGeografica"
              label="Ubicación geográfica"
              tooltip="Coordenadas completas, ej: 10.4631, -73.2532"
            >
              <Input placeholder="Ej: 10.4631, -73.2532" />
            </Form.Item>
            <Form.Item name="puntosClavesCercanos" label="Otros detalles importantes">
              <Input.TextArea rows={2} placeholder="Aspectos adicionales que ayuden a referenciar la propiedad" />
            </Form.Item>
          </Card>

          <Card title="Detalles de la propiedad" size="small" style={{ marginBottom: 16 }}>
            <Row gutter={8}>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="areaTotal" label="Área total (m²)">
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="areaConstruida" label="Área construida (m²)">
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="areaPrivada" label="Área privada (m²)">
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="medidaFrente" label="Medida del frente">
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="medidaFondo" label="Medida del fondo">
                  <InputNumber style={{ width: '100%' }} min={0} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item name="estadoFisico" label="Estado físico">
              <Select
                allowClear
                options={['Básico', 'Ampliado', 'Remodelado', 'Ampliado y remodelado'].map((v) => ({
                  value: v,
                  label: v,
                }))}
              />
            </Form.Item>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="situada" label="Posición">
                  <Select allowClear options={['Esquina', 'Medianera'].map((v) => ({ value: v, label: v }))} />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="niveles" label="Niveles">
                  <Select
                    allowClear
                    options={['Una planta', 'Dos plantas', 'Tres plantas'].map((v) => ({ value: v, label: v }))}
                  />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="piso" label="Piso (apartamentos)">
                  <Select
                    allowClear
                    options={Array.from({ length: 30 }, (_, i) => i + 1).map((n) => ({ value: n, label: String(n) }))}
                  />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="vista" label="Vista">
                  <Select allowClear options={['Interior', 'Exterior'].map((v) => ({ value: v, label: v }))} />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item name="tipoAcceso" label="Tipo de acceso (casas, lotes y casas campestres)">
              <Select allowClear options={['Pavimentado', 'Destapado'].map((v) => ({ value: v, label: v }))} />
            </Form.Item>
          </Card>
        </Col>

        <Col xs={24} lg={12}>
          <Card title="Información comercial" size="small" style={{ marginBottom: 16 }}>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="matriculaInmobiliaria" label="No. de matrícula inmobiliaria">
                  <Input />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="referenciaCatastral" label="No. de referencia catastral">
                  <Input />
                </Form.Item>
              </Col>
            </Row>
            <Row gutter={8}>
              <Col xs={24} sm={12}>
                <Form.Item name="expectativaPrecio" label="Expectativa de precio">
                  <InputMoneda />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12}>
                <Form.Item name="precioMinimoAceptado" label="Precio mínimo aceptado">
                  <InputMoneda />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item name="precioSugerido" label="Precio sugerido">
              <InputMoneda
                addonAfter={
                  <Button
                    type="text"
                    size="small"
                    icon={<ThunderboltOutlined />}
                    loading={sugiriendoPrecio}
                    onClick={handleSugerirPrecio}
                  >
                    Sugerir con IA
                  </Button>
                }
              />
            </Form.Item>
            <Row gutter={8}>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="valorAdministracion" label="Administración">
                  <InputMoneda />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="valorArriendo" label="Arriendo">
                  <InputMoneda />
                </Form.Item>
              </Col>
              <Col xs={24} sm={12} md={8}>
                <Form.Item name="valorImpuestos" label="Impuestos">
                  <InputMoneda />
                </Form.Item>
              </Col>
            </Row>
            <Form.Item name="escuchaOfertas" label="¿Escucha ofertas?" valuePropName="checked">
              <Switch checkedChildren="Sí" unCheckedChildren="No" />
            </Form.Item>
          </Card>

          <Button type="primary" icon={<SaveOutlined />} htmlType="submit" loading={guardando} block size="large">
            Guardar cambios
          </Button>
        </Col>
      </Row>
    </Form>
  );
}
