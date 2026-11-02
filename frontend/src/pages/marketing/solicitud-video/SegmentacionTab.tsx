import { useEffect, useState } from 'react';
import { Card, Checkbox, Space, message } from 'antd';
import {
  canalPublicacionVideoApi,
  destinoVideoApi,
  solicitudVideoCanalApi,
  solicitudVideoDestinoApi,
  solicitudVideoTipoPublicoApi,
  tipoPublicoVideoApi,
} from '../../../api/marketing';
import type { CanalPublicacionVideo, DestinoVideo, TipoPublicoVideo } from '../../../types/marketing';

interface Props {
  solicitudVideoId: string;
}

interface CatalogoSeleccionProps<T extends { id: string; nombre: string }> {
  titulo: string;
  catalogo: T[];
  seleccionados: Set<string>;
  guardando: boolean;
  onToggle: (id: string, checked: boolean) => void;
}

function CatalogoSeleccion<T extends { id: string; nombre: string }>({
  titulo,
  catalogo,
  seleccionados,
  guardando,
  onToggle,
}: CatalogoSeleccionProps<T>) {
  return (
    <Card title={titulo} size="small" loading={guardando}>
      <div style={{ display: 'flex', flexDirection: 'column', gap: 8 }}>
        {catalogo.map((item) => (
          <Checkbox key={item.id} checked={seleccionados.has(item.id)} onChange={(e) => onToggle(item.id, e.target.checked)}>
            {item.nombre}
          </Checkbox>
        ))}
      </div>
    </Card>
  );
}

export default function SegmentacionTab({ solicitudVideoId }: Props) {
  const [catalogoCanales, setCatalogoCanales] = useState<CanalPublicacionVideo[]>([]);
  const [canalesAsignados, setCanalesAsignados] = useState<Set<string>>(new Set());
  const [guardandoCanales, setGuardandoCanales] = useState(false);

  const [catalogoDestinos, setCatalogoDestinos] = useState<DestinoVideo[]>([]);
  const [destinosAsignados, setDestinosAsignados] = useState<Set<string>>(new Set());
  const [guardandoDestinos, setGuardandoDestinos] = useState(false);

  const [catalogoTiposPublico, setCatalogoTiposPublico] = useState<TipoPublicoVideo[]>([]);
  const [tiposPublicoAsignados, setTiposPublicoAsignados] = useState<Set<string>>(new Set());
  const [guardandoTiposPublico, setGuardandoTiposPublico] = useState(false);

  const cargar = async () => {
    try {
      const [
        todosCanales,
        canalesAsignadosData,
        todosDestinos,
        destinosAsignadosData,
        todosTiposPublico,
        tiposPublicoAsignadosData,
      ] = await Promise.all([
        canalPublicacionVideoApi.listarTodos(),
        solicitudVideoCanalApi.listarPorSolicitud(solicitudVideoId),
        destinoVideoApi.listarTodos(),
        solicitudVideoDestinoApi.listarPorSolicitud(solicitudVideoId),
        tipoPublicoVideoApi.listarTodos(),
        solicitudVideoTipoPublicoApi.listarPorSolicitud(solicitudVideoId),
      ]);
      setCatalogoCanales(todosCanales);
      setCanalesAsignados(new Set(canalesAsignadosData.map((c) => c.id)));
      setCatalogoDestinos(todosDestinos);
      setDestinosAsignados(new Set(destinosAsignadosData.map((d) => d.id)));
      setCatalogoTiposPublico(todosTiposPublico);
      setTiposPublicoAsignados(new Set(tiposPublicoAsignadosData.map((t) => t.id)));
    } catch {
      message.error('No se pudo cargar la segmentación del video');
    }
  };

  useEffect(() => {
    cargar();
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [solicitudVideoId]);

  const toggleCanal = async (id: string, checked: boolean) => {
    if (!checked) return;
    setGuardandoCanales(true);
    try {
      const actualizados = await solicitudVideoCanalApi.asignar(solicitudVideoId, [id]);
      setCanalesAsignados(new Set(actualizados.map((c) => c.id)));
    } catch {
      message.error('No se pudo asignar el canal');
    } finally {
      setGuardandoCanales(false);
    }
  };

  const toggleDestino = async (id: string, checked: boolean) => {
    if (!checked) return;
    setGuardandoDestinos(true);
    try {
      const actualizados = await solicitudVideoDestinoApi.asignar(solicitudVideoId, [id]);
      setDestinosAsignados(new Set(actualizados.map((d) => d.id)));
    } catch {
      message.error('No se pudo asignar el destino');
    } finally {
      setGuardandoDestinos(false);
    }
  };

  const toggleTipoPublico = async (id: string, checked: boolean) => {
    if (!checked) return;
    setGuardandoTiposPublico(true);
    try {
      const actualizados = await solicitudVideoTipoPublicoApi.asignar(solicitudVideoId, [id]);
      setTiposPublicoAsignados(new Set(actualizados.map((t) => t.id)));
    } catch {
      message.error('No se pudo asignar el tipo de público');
    } finally {
      setGuardandoTiposPublico(false);
    }
  };

  return (
    <Space orientation="vertical" size={16} style={{ width: '100%' }}>
      <CatalogoSeleccion
        titulo="Canal de publicación"
        catalogo={catalogoCanales}
        seleccionados={canalesAsignados}
        guardando={guardandoCanales}
        onToggle={toggleCanal}
      />
      <CatalogoSeleccion
        titulo="Destino del video"
        catalogo={catalogoDestinos}
        seleccionados={destinosAsignados}
        guardando={guardandoDestinos}
        onToggle={toggleDestino}
      />
      <CatalogoSeleccion
        titulo="Tipo de público"
        catalogo={catalogoTiposPublico}
        seleccionados={tiposPublicoAsignados}
        guardando={guardandoTiposPublico}
        onToggle={toggleTipoPublico}
      />
    </Space>
  );
}
