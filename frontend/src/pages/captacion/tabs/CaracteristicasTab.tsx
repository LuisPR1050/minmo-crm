import { useEffect, useState } from 'react';
import { Button, Card, Checkbox, Col, Input, InputNumber, Row, Select, Skeleton, message } from 'antd';
import { SaveOutlined } from '@ant-design/icons';
import { caracteristicaApi, inmuebleApi, inmuebleCaracteristicaApi } from '../../../api/captacion';
import { CATEGORIAS_DISTRIBUCION, ETIQUETA_CATEGORIA_CARACTERISTICA } from '../../../types/captacion';
import type { Caracteristica, Inmueble } from '../../../types/captacion';

interface Props {
  inmueble: Inmueble;
  onGuardado: () => void;
}

export default function CaracteristicasTab({ inmueble, onGuardado }: Props) {
  const [catalogo, setCatalogo] = useState<Caracteristica[]>([]);
  const [seleccionadas, setSeleccionadas] = useState<Set<string>>(new Set());
  const [numeroHabitaciones, setNumeroHabitaciones] = useState<number | undefined>(inmueble.numeroHabitaciones);
  const [numeroBanos, setNumeroBanos] = useState<number | undefined>(inmueble.numeroBanos);
  const [tipoCocina, setTipoCocina] = useState<string | undefined>(inmueble.tipoCocina);
  const [quienOcupaActualmente, setQuienOcupaActualmente] = useState<string | undefined>(inmueble.quienOcupaActualmente);
  const [otrasCaracteristicas, setOtrasCaracteristicas] = useState(inmueble.otrasCaracteristicasLote ?? '');
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    setLoading(true);
    try {
      const [todas, asignadas] = await Promise.all([
        caracteristicaApi.listar(),
        inmuebleCaracteristicaApi.listar(inmueble.id),
      ]);
      setCatalogo(todas);
      setSeleccionadas(new Set(asignadas.map((c) => c.id)));
    } catch {
      message.error('No se pudieron cargar las características');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    setNumeroHabitaciones(inmueble.numeroHabitaciones);
    setNumeroBanos(inmueble.numeroBanos);
    setTipoCocina(inmueble.tipoCocina);
    setQuienOcupaActualmente(inmueble.quienOcupaActualmente);
    setOtrasCaracteristicas(inmueble.otrasCaracteristicasLote ?? '');
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmueble.id]);

  const toggle = (id: string) => {
    setSeleccionadas((prev) => {
      const copia = new Set(prev);
      if (copia.has(id)) copia.delete(id);
      else copia.add(id);
      return copia;
    });
  };

  const handleGuardar = async () => {
    setGuardando(true);
    try {
      await Promise.all([
        inmuebleCaracteristicaApi.asignar(inmueble.id, Array.from(seleccionadas)),
        inmuebleApi.actualizar(inmueble.id, {
          numeroHabitaciones,
          numeroBanos,
          tipoCocina,
          quienOcupaActualmente,
          otrasCaracteristicasLote: otrasCaracteristicas,
        }),
      ]);
      message.success('Distribución guardada');
      onGuardado();
    } catch {
      message.error('No se pudo guardar la distribución');
    } finally {
      setGuardando(false);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <div>
      <Card title="Distribución interna" size="small" style={{ marginBottom: 16, maxWidth: 560 }}>
        <Row gutter={8}>
          <Col xs={24} sm={8}>
            <div style={{ marginBottom: 4 }}>Habitaciones</div>
            <InputNumber
              style={{ width: '100%' }}
              min={1}
              max={20}
              value={numeroHabitaciones}
              onChange={(v) => setNumeroHabitaciones(v ?? undefined)}
            />
          </Col>
          <Col xs={24} sm={8}>
            <div style={{ marginBottom: 4 }}>Baños</div>
            <InputNumber
              style={{ width: '100%' }}
              min={1}
              max={20}
              value={numeroBanos}
              onChange={(v) => setNumeroBanos(v ?? undefined)}
            />
          </Col>
          <Col xs={24} sm={8}>
            <div style={{ marginBottom: 4 }}>Tipo de cocina</div>
            <Select
              allowClear
              style={{ width: '100%' }}
              value={tipoCocina}
              onChange={(v) => setTipoCocina(v)}
              options={[
                'Básica',
                'Integral',
                'Semi integral',
                'Integral tipo americano',
                'Integral en isla',
                'Integral tipo península',
                'Integral en L',
                'Integral en línea',
                'Integral en U',
              ].map((v) => ({ value: v, label: v }))}
            />
          </Col>
        </Row>
      </Card>

      <Row gutter={16}>
        {CATEGORIAS_DISTRIBUCION.map((categoria) => {
          const items = catalogo.filter((c) => c.categoria === categoria);
          if (items.length === 0) return null;
          return (
            <Col xs={24} md={12} key={categoria} style={{ marginBottom: 16 }}>
              <Card title={ETIQUETA_CATEGORIA_CARACTERISTICA[categoria]} size="small">
                <Row gutter={[8, 8]}>
                  {items.map((item) => (
                    <Col span={12} key={item.id}>
                      <Checkbox checked={seleccionadas.has(item.id)} onChange={() => toggle(item.id)}>
                        {item.nombre}
                      </Checkbox>
                    </Col>
                  ))}
                </Row>
              </Card>
            </Col>
          );
        })}
      </Row>

      <Card title="Ocupantes" size="small" style={{ marginBottom: 16, maxWidth: 560 }}>
        <Select
          allowClear
          style={{ width: '100%' }}
          value={quienOcupaActualmente}
          onChange={(v) => setQuienOcupaActualmente(v)}
          options={['Propietario', 'Arrendatario', 'Familiar del propietario', 'Desocupada', 'Otra persona'].map((v) => ({
            value: v,
            label: v,
          }))}
        />
      </Card>

      <Card title="Otras características" size="small" style={{ marginBottom: 16 }}>
        <Input.TextArea
          rows={3}
          value={otrasCaracteristicas}
          onChange={(e) => setOtrasCaracteristicas(e.target.value)}
          placeholder="Otras características a destacar del inmueble"
        />
      </Card>

      <Button type="primary" icon={<SaveOutlined />} loading={guardando} onClick={handleGuardar}>
        Guardar distribución
      </Button>
    </div>
  );
}
