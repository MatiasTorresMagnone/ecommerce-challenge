import { useEffect, useState } from 'react';
import { Link, useNavigate, useSearchParams } from 'react-router-dom';
import { obtenerProductos, buscarProductos } from '../api/productos';
import { agregarAlCarrito, obtenerCarrito } from '../api/carrito';
import { imagenUrl } from '../api/client';
import { useAuth } from '../context/AuthContext';
import { formatoPrecio } from '../utils/formato';

export default function Catalogo() {
  const { usuario, esAdmin } = useAuth();
  const navigate = useNavigate();

  const [params] = useSearchParams();
  const q = params.get('q') ?? '';

  const [productos, setProductos] = useState([]);
  const [idsEnCarrito, setIdsEnCarrito] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');
  const [agregandoId, setAgregandoId] = useState(null);

  useEffect(() => {
    async function cargar() {
      setCargando(true);
      setError('');
      try {
        setProductos(q ? await buscarProductos(q) : await obtenerProductos());
      } catch (err) {
        setError(err.message);
      } finally {
        setCargando(false);
      }
    }
    cargar();
  }, [q]);

  useEffect(() => {
    if (!usuario || esAdmin) {
      setIdsEnCarrito([]);
      return;
    }
    obtenerCarrito()
      .then((res) => setIdsEnCarrito((res?.items ?? []).map((i) => Number(i.productoId))))
      .catch(() => setIdsEnCarrito([]));
  }, [usuario, esAdmin]);

  async function agregar(e, producto) {
    e.preventDefault();
    if (!usuario) {
      navigate('/login');
      return;
    }
    const idProd = producto.idProducto ?? producto.id;
    setError('');
    setAgregandoId(idProd);
    try {
      await agregarAlCarrito(idProd, 1);

      setIdsEnCarrito((prev) => [...prev, Number(idProd)]);
    } catch (err) {
      setError(err.message);
    } finally {
      setAgregandoId(null);
    }
  }

  return (
    <main>
      <h2>{q ? `Resultados para "${q}"` : 'Productos'}</h2>
      <p className="subtitulo">
        {q ? <Link to="/">← Ver todos los productos</Link> : 'Encontrá lo que buscás'}
      </p>

      {error && <p className="error" style={{ marginBottom: '1rem' }}>{error}</p>}

      {cargando ? (
        <p className="vacio">Cargando productos...</p>
      ) : productos.length === 0 ? (
        <p className="vacio">No se encontraron productos.</p>
      ) : (
        <div className="grilla">
          {productos.map((p, index) => {
            const idProd = p.id ?? p.idProducto ?? index;
            const estaAgregando = agregandoId === idProd;
            const estaEnCarrito = idsEnCarrito.includes(Number(idProd));

            return (
              <article className="tarjeta" key={idProd}>
                <Link to={`/productos/${idProd}`} className="enlace-tarjeta">
                  <img
                      src={p.imagen ? imagenUrl(p.imagen) : '/imagen_default.png'}
                      alt={p.nombre}
                  />
                </Link>

                <div className="cuerpo">
                  <Link to={`/productos/${idProd}`} className="enlace-tarjeta">
                    <h3>{p.nombre}</h3>
                  </Link>

                  <div className="precio-contenedor">
                    <p className="precio">{formatoPrecio(p.precio)}</p>
                  </div>

                  {!esAdmin && (
                    estaEnCarrito ? (
                      <Link to="/carrito" className="boton-agregar exito" style={{ textDecoration: 'none' }}>
                        <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2.5" strokeLinecap="round" strokeLinejoin="round">
                          <polyline points="20 6 9 17 4 12"></polyline>
                        </svg>
                        En el carrito ✓
                      </Link>
                    ) : (
                      <button
                        type="button"
                        className="boton-agregar"
                        disabled={estaAgregando}
                        onClick={(e) => agregar(e, p)}
                      >
                        {estaAgregando ? (
                          <>
                            <span className="spinner-loader" />
                            Agregando...
                          </>
                        ) : (
                          <>
                            <svg width="18" height="18" viewBox="0 0 24 24" fill="none" stroke="currentColor" strokeWidth="2" strokeLinecap="round" strokeLinejoin="round">
                              <circle cx="9" cy="21" r="1"></circle>
                              <circle cx="20" cy="21" r="1"></circle>
                              <path d="M1 1h4l2.68 13.39a2 2 0 0 0 2 1.61h9.72a2 2 0 0 0 2-1.61L23 6H6"></path>
                            </svg>
                            Agregar al carrito
                          </>
                        )}
                      </button>
                    )
                  )}
                </div>
              </article>
            );
          })}
        </div>
      )}
    </main>
  );
}