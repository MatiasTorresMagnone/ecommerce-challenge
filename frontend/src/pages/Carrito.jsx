import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import {
  obtenerCarrito,
  modificarCantidadItem,
  eliminarProductoCarrito,
} from '../api/carrito';
import { imagenUrl } from '../api/client';
import { formatoPrecio } from '../utils/formato';
import { iniciarCheckout } from '../api/checkout';

export default function Carrito() {
  const [carrito, setCarrito] = useState(null);
  const [cargando, setCargando] = useState(true);
  const [actualizandoItem, setActualizandoItem] = useState(null); // ID del item en actualización
  const [error, setError] = useState('');
  const [pagando, setPagando] = useState(false);

  async function cargarCarrito() {
    setCargando(true);
    setError('');
    try {
      setCarrito(await obtenerCarrito());
    } catch (err) {
      setError(err.message || 'Error al obtener el carrito');
    } finally {
      setCargando(false);
    }
  }

  useEffect(() => {
    cargarCarrito();
  }, []);

  async function cambiarCantidad(idItem, nuevaCantidad) {
    if (nuevaCantidad <= 0) {
      await eliminarProducto(idItem);
      return;
    }

    setActualizandoItem(idItem);
    setError('');
    try {
      const carritoActualizado = await modificarCantidadItem(idItem, nuevaCantidad);
      setCarrito(carritoActualizado);
    } catch (err) {
      setError(err.message || 'Error al actualizar cantidad');
    } finally {
      setActualizandoItem(null);
    }
  }

  async function eliminarProducto(idItem) {
    setActualizandoItem(idItem);
    setError('');
    try {
      const carritoActualizado = await eliminarProductoCarrito(idItem);
      setCarrito(carritoActualizado);
    } catch (err) {
      setError(err.message || 'Error al eliminar producto');
    } finally {
      setActualizandoItem(null);
    }
  }

  async function pagar() {
    setPagando(true);
    setError('');

    try {
      const { url } = await iniciarCheckout();
      window.location.href = url;
    } catch (err) {
      setError(err.message || 'Error al iniciar el checkout');
      setPagando(false);
    }
  }

  const items = carrito?.items ?? [];
  const total = carrito?.total ?? 0;

  return (
    <main className="pagina-tienda">
      <div className="encabezado-pagina">
        <div>
          <span className="texto-superior">TU COMPRA</span>
          <h1>Mi carrito</h1>
          <p>Revisá tus productos antes de continuar.</p>
        </div>

        <Link to="/" className="boton-volver">
          ← Seguir comprando
        </Link>
      </div>

      {error && <p className="error" style={{ marginBottom: '1rem' }}>{error}</p>}

      {cargando ? (
        <section className="estado-vacio">
          <p className="vacio">Cargando carrito...</p>
        </section>
      ) : items.length === 0 ? (
        <section className="estado-vacio">
          <div className="vacio-icono">🛒</div>
          <h2>Tu carrito está vacío</h2>
          <p>Agregá productos al carrito para comenzar tu compra.</p>
          <Link to="/" className="boton-principal">
            Explorar catálogo
          </Link>
        </section>
      ) : (
        <div className="carrito-layout">
          <section className="panel panel-carrito">
            <div className="panel-titulo">
              <h2>Productos seleccionados</h2>
              <span className="contador">
                {items.length} {items.length === 1 ? 'producto' : 'productos'}
              </span>
            </div>

            <div className="tabla-contenedor">
              <table className="tabla-tienda tabla-carrito">
                <thead>
                  <tr>
                    <th>Producto</th>
                    <th>Precio</th>
                    <th>Cantidad</th>
                    <th>Subtotal</th>
                    <th></th>
                  </tr>
                </thead>

                <tbody>
                  {items.map((item) => {
                    const prod = item.producto ?? item;
                    const precio = prod.precio ?? prod.precioUnitario ?? 0;
                    const estaProcesando = actualizandoItem === item.idItem;

                    return (
                      <tr key={item.idItem} style={{ opacity: estaProcesando ? 0.5 : 1 }}>
                        <td>
                          <div className="producto-tabla">
                            <div className="producto-miniatura">
                              {prod.imagen ? (
                                <img
                                  src={imagenUrl(prod.imagen)}
                                  alt={prod.nombre}
                                />
                              ) : (
                                '📦'
                              )}
                            </div>
                            <div>
                                <strong>{prod.nombre}</strong>
                            </div>
                          </div>
                        </td>

                        <td>{formatoPrecio(precio)}</td>

                        <td>
                          <div className="control-cantidad">
                            <button
                              type="button"
                              aria-label="Disminuir cantidad"
                              disabled={estaProcesando}
                              onClick={() => cambiarCantidad(item.idItem, item.cantidad - 1)}
                            >
                              −
                            </button>

                            <span>{item.cantidad}</span>

                            <button
                              type="button"
                              aria-label="Aumentar cantidad"
                              disabled={estaProcesando}
                              onClick={() => cambiarCantidad(item.idItem, item.cantidad + 1)}
                            >
                              +
                            </button>
                          </div>
                        </td>

                        <td className="precio-celda">
                          {formatoPrecio(item.subtotal ?? precio * item.cantidad)}
                        </td>

                        <td>
                          <button
                            type="button"
                            className="boton-icono boton-eliminar"
                            aria-label={`Eliminar ${prod.nombre}`}
                            title="Eliminar producto"
                            disabled={estaProcesando}
                            onClick={() => eliminarProducto(item.idItem)}
                          >
                            ×
                          </button>
                        </td>
                      </tr>
                    );
                  })}
                </tbody>
              </table>
            </div>
          </section>

          <aside className="panel resumen-compra">
            <h2>Resumen de compra</h2>

            <div className="resumen-linea">
              <span>Productos</span>
              <span>
                {items.reduce((acc, item) => acc + item.cantidad, 0)}
              </span>
            </div>

            <div className="resumen-linea">
              <span>Subtotal</span>
              <span>{formatoPrecio(total)}</span>
            </div>

            <div className="resumen-total">
              <span>Total</span>
              <strong>{formatoPrecio(total)}</strong>
            </div>

            <button
              type="button"
              className="boton-principal boton-ancho"
              onClick={pagar}
              disabled={pagando || actualizandoItem !== null}
            >
              {pagando ? 'Redirigiendo a Stripe...' : 'Continuar al pago →'}
            </button>

            <p className="texto-seguro">🔒 Compra protegida</p>
          </aside>
        </div>
      )}
    </main>
  );
}