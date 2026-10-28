import { useState, useEffect } from 'react';
import { Link, useParams } from 'react-router-dom';
import { imagenUrl } from '../api/client';
import { formatoPrecio } from '../utils/formato';
import { obtenerDetalleOrden } from '../api/ordenes';

export default function DetalleOrden() {
  const { id } = useParams();
  const [orden, setOrden] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  useEffect(() => {
    async function cargar() {
      setCargando(true);
      setError('');
      try {
        setOrden(await obtenerDetalleOrden(id));
      } catch (err) {
        setError(err.message);
      } finally {
        setCargando(false);
      }
    }
    cargar();
  }, [id]);

  if (cargando) {
    return (
      <main className="pagina-tienda">
        <p className="vacio">Cargando detalle de la orden...</p>
      </main>
    );
  }

  if (error || !orden) {
    return (
      <main className="pagina-tienda">
        <p className="error">{error || 'Orden no encontrada'}</p>
        <Link to="/ordenes" className="boton-volver">
          ← Volver a mis órdenes
        </Link>
      </main>
    );
  }

  const items = orden.productos ?? [];

  return (
    <main className="pagina-tienda">
      <div className="encabezado-pagina">
        <div>
          <span className="texto-superior">DETALLE DE COMPRA</span>
          <h1>Orden #{orden.idOrden}</h1>
          <p>
            Realizada el{' '}
            {orden.fechaCreacion
              ? new Date(orden.fechaCreacion).toLocaleString()
              : '-'}
          </p>
        </div>

        <Link to="/ordenes" className="boton-volver">
          ← Volver a mis órdenes
        </Link>
      </div>

      <div className="carrito-layout">
        <section className="panel panel-carrito">
          <div className="panel-titulo">
            <h2>Productos comprados</h2>
            <span className={`badge ${String(orden.estado).toUpperCase()}`}>
              {orden.estado}
            </span>
          </div>

          <div className="tabla-contenedor">
            <table className="tabla-tienda">
              <thead>
                <tr>
                  <th>Producto</th>
                  <th>Precio histórico</th>
                  <th style={{ textAlign: 'center' }}>Cantidad</th>
                  <th style={{ textAlign: 'right' }}>Subtotal</th>
                </tr>
              </thead>

              <tbody>
                {items.map((item, index) => (
                  <tr key={item.idProducto ?? index}>
                    <td>
                      <div className="producto-tabla">
                        <div className="producto-miniatura">
                          {item.imagen ? (
                            <img
                              src={imagenUrl(item.imagen)}
                              alt={item.nombreProducto}
                            />
                          ) : (
                            '📦'
                          )}
                        </div>

                        <div>
                          <strong>{item.nombreProducto}</strong>
                        </div>
                      </div>
                    </td>

                    <td>{formatoPrecio(item.precioUnitario)}</td>

                    {/* Agregamos span con la etiqueta 'Cant:' para móviles */}
                    <td style={{ textAlign: 'center' }}>
                      <span className="etiqueta-mobile" style={{ fontSize: '0.85rem', color: '#666', marginRight: '0.25rem' }}>
                        Cant:
                      </span>
                      <strong>{item.cantidad}</strong>
                    </td>

                    <td className="precio-celda" style={{ textAlign: 'right' }}>
                      {formatoPrecio(item.subtotal)}
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>

        <aside className="panel resumen-compra">
          <h2>Resumen de la orden</h2>

          <div className="resumen-linea">
            <span>Nº de Orden</span>
            <span>#{orden.idOrden}</span>
          </div>

          <div className="resumen-linea">
            <span>Estado</span>
            <span className={`badge ${String(orden.estado).toUpperCase()}`}>
              {orden.estado}
            </span>
          </div>

          <div className="resumen-linea">
            <span>Total unidades</span>
            <span>
              {items.reduce((acc, item) => acc + item.cantidad, 0)}
            </span>
          </div>

          <div className="resumen-total">
            <span>Total abonado</span>
            <strong>{formatoPrecio(orden.total)}</strong>
          </div>
        </aside>
      </div>
    </main>
  );
}