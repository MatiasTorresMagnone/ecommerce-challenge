import { createContext, useContext, useState } from 'react';
import { api } from '../api/client';

const AuthContext = createContext(null);

function usuarioDesdeToken(token) {
  try {
    const base64 = token.split('.')[1].replace(/-/g, '+').replace(/_/g, '/');
    const p = JSON.parse(atob(base64));
    if (p.exp && p.exp * 1000 < Date.now()) return null; // vencido
    return { email: p.sub, rol: p.rol };
  } catch {
    return null;
  }
}

export function AuthProvider({ children }) {
  const [token, setToken] = useState(() => {
    const guardado = localStorage.getItem('token');
    if (guardado && !usuarioDesdeToken(guardado)) {
      localStorage.removeItem('token');
      return null;
    }
    return guardado;
  });

  const usuario = token ? usuarioDesdeToken(token) : null;

  async function login(mail, password) {
    const { jwtToken } = await api('/auth/login', { method: 'POST', body: { mail, password } });
    localStorage.setItem('token', jwtToken);
    setToken(jwtToken);
  }

  async function registrar(mail, password) {
    await api('/auth/register', { method: 'POST', body: { mail, password } });
    await login(mail, password);
  }

  async function logout() {
        try {
            await api('/auth/logout', { method: 'POST' });
          } catch (err) {
            console.warn('Error al avisar logout al servidor:', err);
          } finally {
            localStorage.removeItem('token');
            setToken(null);
          }
  }

  return (
    <AuthContext.Provider value={{ usuario, esAdmin: usuario?.rol === 'ADMIN', login, registrar, logout }}>
      {children}
    </AuthContext.Provider>
  );
}

export const useAuth = () => useContext(AuthContext);