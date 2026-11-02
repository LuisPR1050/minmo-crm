import { useEffect, useState } from 'react';
import { Button, Card, Checkbox, Col, Row, Skeleton, message } from 'antd';
import { SaveOutlined } from '@ant-design/icons';
import { caracteristicaApi, inmuebleCaracteristicaApi } from '../../../api/captacion';
import { CATEGORIAS_SERVICIOS_EQUIPAMIENTO, ETIQUETA_CATEGORIA_CARACTERISTICA } from '../../../types/captacion';
import type { Caracteristica } from '../../../types/captacion';

interface Props {
  inmuebleId: string;
}

export default function ServiciosEquipamientoTab({ inmuebleId }: Props) {
  const [catalogo, setCatalogo] = useState<Caracteristica[]>([]);
  const [seleccionadas, setSeleccionadas] = useState<Set<string>>(new Set());
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    setLoading(true);
    try {
      const [todas, asignadas] = await Promise.all([
        caracteristicaApi.listar(),
        inmuebleCaracteristicaApi.listar(inmuebleId),
      ]);
      setCatalogo(todas);
      setSeleccionadas(new Set(asignadas.map((c) => c.id)));
    } catch {
      message.error('No se pudieron cargar los servicios y equipamiento');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [inmuebleId]);

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
      await inmuebleCaracteristicaApi.asignar(inmuebleId, Array.from(seleccionadas));
      message.success('Servicios y equipamiento guardados');
    } catch {
      message.error('No se pudieron guardar los servicios y equipamiento');
    } finally {
      setGuardando(false);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <div>
      <Row gutter={16}>
        {CATEGORIAS_SERVICIOS_EQUIPAMIENTO.map((categoria) => {
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
      <Button type="primary" icon={<SaveOutlined />} loading={guardando} onClick={handleGuardar}>
        Guardar servicios y equipamiento
      </Button>
    </div>
  );
}
