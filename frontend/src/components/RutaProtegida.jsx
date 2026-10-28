import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RutaProtegida({ soloAdmin = false }) {
  const { usuario, esAdmin } = useAuth();
  if (!usuario) return <Navigate to="/login" replace />;
  if (soloAdmin && !esAdmin) return <Navigate to="/" replace />;
  return <Outlet />;
}