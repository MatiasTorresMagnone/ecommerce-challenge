import { useState } from 'react';
import { NavLink, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Navbar() {
  const { usuario, esAdmin, logout } = useAuth();
  const navigate = useNavigate();
  const [menuAbierto, setMenuAbierto] = useState(false);
  const [texto, setTexto] = useState('');

  function salir() {
    logout();
    setMenuAbierto(false);
    navigate('/');
  }

  function cerrarMenu() {
    setMenuAbierto(false);
  }

  function buscar(e) {
    e.preventDefault();
    const q = texto.trim();
    navigate(q ? `/?q=${encodeURIComponent(q)}` : '/');
    cerrarMenu();
  }

  const linkClass = ({ isActive }) => `nav-link${isActive ? ' active' : ''}`;

  return (
    <header className="navbar">
      <NavLink to="/" end className="marca" onClick={cerrarMenu}>
        <span className="marca-icono">🛒</span>
        <span>CodigoDelSur</span>
      </NavLink>

      <button
        type="button"
        className="nav-menu-boton"
        aria-label={menuAbierto ? 'Cerrar menú' : 'Abrir menú'}
        aria-expanded={menuAbierto}
        onClick={() => setMenuAbierto(!menuAbierto)}
      >
        {menuAbierto ? (
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <path d="m18 6-12 12M6 6l12 12" />
          </svg>
        ) : (
          <svg width="24" height="24" viewBox="0 0 24 24" fill="none"
               stroke="currentColor" strokeWidth="2" strokeLinecap="round">
            <path d="M4 6h16M4 12h16M4 18h16" />
          </svg>
        )}
      </button>

      <nav className={`nav-contenido ${menuAbierto ? 'abierto' : ''}`}>
        {/* Buscador de productos */}
        <form className="nav-buscador" onSubmit={buscar}>
          <input
            type="search"
            placeholder="Buscar productos..."
            aria-label="Buscar productos"
            value={texto}
            onChange={(e) => setTexto(e.target.value)}
          />
        </form>

        <div className="nav-enlaces">
          <NavLink to="/" end className={linkClass} onClick={cerrarMenu}>
            Catálogo
          </NavLink>

          {usuario && !esAdmin && (
            <>
              <NavLink to="/carrito" className={linkClass} onClick={cerrarMenu}>
                Carrito
              </NavLink>
              <NavLink to="/ordenes" className={linkClass} onClick={cerrarMenu}>
                Mis órdenes
              </NavLink>
            </>
          )}

          {esAdmin && (
            <NavLink to="/admin" className={linkClass} onClick={cerrarMenu}>
              Administración
            </NavLink>
          )}
        </div>

        <div className="nav-usuario">
          {usuario ? (
            <>
              <span className="nav-email" title={usuario.email}>
                <span className="nav-avatar">{usuario.email?.charAt(0).toUpperCase()}</span>
                <span className="nav-email-text">{usuario.email}</span>
              </span>
              <button type="button" className="nav-salir" onClick={salir}>
                Salir
              </button>
            </>
          ) : (
            <div className="nav-autenticacion">
              <NavLink to="/login" className="nav-ingresar" onClick={cerrarMenu}>
                Ingresar
              </NavLink>
              <NavLink to="/registro" className="nav-registrarse" onClick={cerrarMenu}>
                Crear cuenta
              </NavLink>
            </div>
          )}
        </div>
      </nav>
    </header>
  );
}