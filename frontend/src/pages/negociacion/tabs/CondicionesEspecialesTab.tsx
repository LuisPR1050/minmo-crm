import { useEffect, useState } from 'react';
import { Button, Card, Checkbox, Skeleton, Typography, message } from 'antd';
import { condicionEspecialApi } from '../../../api/negociacion';
import type { CondicionEspecial } from '../../../types/negociacion';

const { Text } = Typography;

interface Props {
  negociacionId: string;
}

export default function CondicionesEspecialesTab({ negociacionId }: Props) {
  const [catalogo, setCatalogo] = useState<CondicionEspecial[]>([]);
  const [seleccionadas, setSeleccionadas] = useState<Set<string>>(new Set());
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    setLoading(true);
    try {
      const [todas, asignadas] = await Promise.all([
        condicionEspecialApi.listarTodas(),
        condicionEspecialApi.listarAsignadas(negociacionId),
      ]);
      setCatalogo(todas);
      setSeleccionadas(new Set(asignadas.map((c) => c.id)));
    } catch {
      message.error('No se pudieron cargar las condiciones especiales');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const toggle = (id: string, checked: boolean) => {
    setSeleccionadas((prev) => {
      const siguiente = new Set(prev);
      if (checked) siguiente.add(id);
      else siguiente.delete(id);
      return siguiente;
    });
  };

  const handleGuardar = async () => {
    setGuardando(true);
    try {
      await condicionEspecialApi.asignar(negociacionId, Array.from(seleccionadas));
      message.success('Condiciones especiales guardadas');
      cargar();
    } catch {
      message.error('No se pudieron guardar las condiciones especiales');
    } finally {
      setGuardando(false);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <div style={{ maxWidth: 600 }}>
      <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Condiciones a las que queda sujeto el negocio (aprobación de crédito, venta de otra propiedad, etc.).
      </Text>
      <Card size="small">
        {catalogo.length === 0 ? (
          <Text type="secondary">No hay condiciones especiales configuradas en el catálogo</Text>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {catalogo.map((condicion) => (
              <Checkbox
                key={condicion.id}
                checked={seleccionadas.has(condicion.id)}
                onChange={(e) => toggle(condicion.id, e.target.checked)}
              >
                {condicion.nombre}
              </Checkbox>
            ))}
          </div>
        )}
      </Card>
      <Button type="primary" loading={guardando} onClick={handleGuardar} style={{ marginTop: 16 }}>
        Guardar
      </Button>
    </div>
  );
}
