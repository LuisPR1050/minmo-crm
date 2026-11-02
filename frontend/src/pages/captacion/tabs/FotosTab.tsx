import { Space } from 'antd';
import FotosInmuebleGaleria from '../../../components/FotosInmuebleGaleria';
import VideosInmuebleGaleria from '../../../components/VideosInmuebleGaleria';

interface Props {
  inmuebleId: string;
}

export default function FotosTab({ inmuebleId }: Props) {
  return (
    <Space orientation="vertical" size={24} style={{ width: '100%', maxWidth: 780 }}>
      <FotosInmuebleGaleria inmuebleId={inmuebleId} />
      <VideosInmuebleGaleria inmuebleId={inmuebleId} />
    </Space>
  );
}
