import { useEffect, useState } from 'react';
import { Link, useNavigate, useParams } from 'react-router-dom';
import { obtenerProducto } from '../api/productos';
import { imagenUrl } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { formatoPrecio } from '../utils/formato';
import { agregarAlCarrito } from '../api/carrito';

export default function DetalleProducto() {
  const { id } = useParams();
  const { usuario, esAdmin } = useAuth();
  const navigate = useNavigate();

  const [producto, setProducto] = useState(null);
  const [cantidad, setCantidad] = useState(1);
  const [cargando, setCargando] = useState(true);
  const [agregando, setAgregando] = useState(false);
  const [exito, setExito] = useState(false);
  const [error, setError] = useState('');

  useEffect(() => {
    async function cargar() {
      setCargando(true);
      setError('');
      try {
        setProducto(await obtenerProducto(id));
      } catch (err) {
        setError(err.message);
      } finally {
        setCargando(false);
      }
    }
    cargar();
  }, [id]);

  async function agregar() {
    if (!usuario) {
      navigate('/login');
      return;
    }
    setError('');
    setAgregando(true);
    try {
      await agregarAlCarrito(producto.id, Number(cantidad));
      setExito(true);
      setTimeout(() => setExito(false), 2500);
    } catch (err) {
      setError(err.message);
    } finally {
      setAgregando(false);
    }
  }

  const modificarCantidad = (val) => {
    setCantidad((prev) => Math.max(1, Math.min(99, prev + val)));
  };

  if (cargando) return <main><p className="vacio">Cargando...</p></main>;

  if (!producto) {
    return (
      <main>
        <p className="error">{error || 'Producto no encontrado'}</p>
        <p><Link to="/">← Volver al catálogo</Link></p>
      </main>
    );
  }

  return (
    <main>
      <p><Link to="/" className="boton-volver">← Volver al catálogo</Link></p>

      <div className="detalle-producto">
        <div className="detalle-imagen">
          <img
            src={producto.imagen ? imagenUrl(producto.imagen) : '/imagen_default.png'}
            alt={producto.nombre}
          />
        </div>

        <div className="detalle-info">
          <h2>{producto.nombre}</h2>
          <p className="precio">{formatoPrecio(producto.precio)}</p>
          <p className="descripcion-texto">{producto.descripcion}</p>

          {error && <p className="error">{error}</p>}

          {!esAdmin && (
            <div className="detalle-compra-wrapper">
              <div className="control-cantidad-detalle">
                <button
                  type="button"
                  onClick={() => modificarCantidad(-1)}
                  disabled={cantidad <= 1}
                >
                  −
                </button>
                <span>{cantidad}</span>
                <button
                  type="button"
                  onClick={() => modificarCantidad(1)}
                  disabled={cantidad >= 99}
                >
                  +
                </button>
              </div>

              <button
                type="button"
                className={`boton-agregar-grande ${exito ? 'exito' : ''}`}
                disabled={agregando}
                onClick={agregar}
              >
                {agregando ? (
                  <>
                    <span className="spinner-loader" />
                    Agregando...
                  </>
                ) : exito ? (
                  <>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                      <polyline points="20 6 9 17 4 12"></polyline>
                    </svg>
                    ¡Agregado al carrito!
                  </>
                ) : (
                  <>
                    <svg width="20" height="20" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                      <circle cx="9" cy="21" r="1"></circle>
                      <circle cx="20" cy="21" r="1"></circle>
                      <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                    </svg>
                    Agregar al carrito
                  </>
                )}
              </button>
            </div>
          )}
        </div>
      </div>
    </main>
  );
}