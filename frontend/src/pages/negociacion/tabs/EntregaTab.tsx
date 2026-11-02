import { useEffect, useState } from 'react';
import { Button, Card, Checkbox, Skeleton, Typography, message } from 'antd';
import { itemEntregaApi } from '../../../api/negociacion';
import type { ItemEntrega } from '../../../types/negociacion';

const { Text } = Typography;

interface Props {
  negociacionId: string;
}

export default function EntregaTab({ negociacionId }: Props) {
  const [catalogo, setCatalogo] = useState<ItemEntrega[]>([]);
  const [incluidos, setIncluidos] = useState<Set<string>>(new Set());
  const [loading, setLoading] = useState(false);
  const [guardando, setGuardando] = useState(false);

  const cargar = async () => {
    setLoading(true);
    try {
      const [todos, seleccionados] = await Promise.all([
        itemEntregaApi.listarTodos(),
        itemEntregaApi.listarIncluidos(negociacionId),
      ]);
      setCatalogo(todos);
      setIncluidos(new Set(seleccionados.map((i) => i.id)));
    } catch {
      message.error('No se pudo cargar la lista de qué incluye la entrega');
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [negociacionId]);

  const toggle = (id: string, checked: boolean) => {
    setIncluidos((prev) => {
      const siguiente = new Set(prev);
      if (checked) siguiente.add(id);
      else siguiente.delete(id);
      return siguiente;
    });
  };

  const handleGuardar = async () => {
    setGuardando(true);
    try {
      await Promise.all(catalogo.map((item) => itemEntregaApi.asignar(negociacionId, item.id, incluidos.has(item.id))));
      message.success('Guardado correctamente');
      cargar();
    } catch {
      message.error('No se pudo guardar');
    } finally {
      setGuardando(false);
    }
  };

  if (loading) return <Skeleton active />;

  return (
    <div style={{ maxWidth: 600 }}>
      <Text type="secondary" style={{ display: 'block', marginBottom: 16 }}>
        Marca lo que queda incluido en la entrega de la propiedad (aires acondicionados, closets, cocina integral, etc.).
      </Text>
      <Card size="small">
        {catalogo.length === 0 ? (
          <Text type="secondary">No hay ítems configurados en el catálogo</Text>
        ) : (
          <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
            {catalogo.map((item) => (
              <Checkbox key={item.id} checked={incluidos.has(item.id)} onChange={(e) => toggle(item.id, e.target.checked)}>
                {item.nombre}
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
