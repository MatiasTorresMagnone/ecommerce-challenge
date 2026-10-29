import { Navigate, Outlet } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function RutaPublica() {
  const { usuario } = useAuth();
  if (usuario) {
    return <Navigate to="/" replace />;
  }
  return <Outlet />;
}