import { useEffect, useState } from 'react';
import { Select, Typography } from 'antd';
import { inmuebleApi } from '../../api/captacion';
import LandingTab from '../captacion/tabs/LandingTab';
import type { Inmueble } from '../../types/captacion';

const { Paragraph } = Typography;

export default function LandingPromocionTab() {
  const [inmuebles, setInmuebles] = useState<Inmueble[]>([]);
  const [inmuebleSeleccionadoId, setInmuebleSeleccionadoId] = useState<string | null>(null);

  useEffect(() => {
    inmuebleApi
      .listar()
      .then(setInmuebles)
      .catch(() => undefined);
  }, []);

  const handleCambio = (actualizado: Inmueble) => {
    setInmuebles((prev) => prev.map((i) => (i.id === actualizado.id ? actualizado : i)));
  };

  const inmuebleSeleccionado = inmuebles.find((i) => i.id === inmuebleSeleccionadoId) ?? null;

  return (
    <div>
      <Paragraph type="secondary">
        Busca un inmueble para publicar o editar su landing page pública — la página que recibe a los leads desde un
        anuncio o un link compartido.
      </Paragraph>
      <Select
        showSearch
        allowClear
        style={{ width: '100%', maxWidth: 480, marginBottom: 16 }}
        placeholder="Busca por código o propietario..."
        optionFilterProp="label"
        value={inmuebleSeleccionadoId}
        onChange={setInmuebleSeleccionadoId}
        options={inmuebles.map((i) => ({ value: i.id, label: `${i.codigo} — ${i.propietarioNombre}` }))}
      />
      {inmuebleSeleccionado && <LandingTab inmueble={inmuebleSeleccionado} onCambio={handleCambio} />}
    </div>
  );
}
