import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

interface Props {
  soloAdmin?: boolean;
}

export default function RutaProtegida({ soloAdmin = false }: Props) {
  const { estaAutenticado, usuario } = useAuth();

  if (!estaAutenticado) {
    return <Navigate to="/login" replace />;
  }

  if (soloAdmin && usuario?.rol !== 'ADMIN') {
    return <Navigate to="/" replace />;
  }

  return <Outlet />;
}
