import { Modal } from 'antd';
import FotosInmuebleGaleria from './FotosInmuebleGaleria';

interface Props {
  inmuebleId: string;
  inmuebleCodigo: string;
  open: boolean;
  onClose: () => void;
}

export default function FotosInmuebleModal({ inmuebleId, inmuebleCodigo, open, onClose }: Props) {
  return (
    <Modal title={`Fotos — ${inmuebleCodigo}`} open={open} onCancel={onClose} footer={null} width={720}>
      <FotosInmuebleGaleria inmuebleId={inmuebleId} activo={open} />
    </Modal>
  );
}
