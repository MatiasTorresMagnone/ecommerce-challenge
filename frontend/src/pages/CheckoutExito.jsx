import { useEffect, useState } from 'react';
import { Link, useSearchParams } from 'react-router-dom';
import { obtenerDetalleOrden } from '../api/ordenes';
import { formatoPrecio } from '../utils/formato';

export default function CheckoutExito() {
  const [params] = useSearchParams();
  const idOrden = params.get('orden');   // viene en la URL: /checkout/exito?orden=5

  const [orden, setOrden] = useState(null);
  const [error, setError] = useState('');

  // Consulta la orden; si todavía está PENDIENTE, vuelve a consultar cada 2 segundos
  // (el aviso de pago de Stripe puede tardar unos segundos en llegar)
  useEffect(() => {
    let intentos = 0;
    let cancelado = false;
    let timer;

    async function consultar() {
      try {
        const datos = await obtenerDetalleOrden(idOrden);
        if (cancelado) return;
        setOrden(datos);
        if (datos.estado === 'PENDIENTE' && intentos < 10) {
          intentos++;
          timer = setTimeout(consultar, 2000);
        }
      } catch (err) {
        if (!cancelado) setError(err.message);
      }
    }

    consultar();
    return () => {
      cancelado = true;
      clearTimeout(timer);
    };
  }, [idOrden]);

  if (error) {
    return (
      <main className="pagina-tienda">
        <p className="error">{error}</p>
        <Link to="/ordenes" className="boton-volver">Ir a mis órdenes</Link>
      </main>
    );
  }

  if (!orden) {
    return (
      <main className="pagina-tienda">
        <p className="vacio">Verificando tu pago...</p>
      </main>
    );
  }

  const pagada = orden.estado === 'PAGADA';

  return (
    <main className="pagina-tienda">
      <section className="estado-vacio">
        <div className="vacio-icono">{pagada ? '✅' : '⏳'}</div>
        <h2>{pagada ? '¡Gracias por tu compra!' : 'Estamos confirmando tu pago'}</h2>
        <p>
          {pagada
            ? `Tu orden #${orden.idOrden} por ${formatoPrecio(orden.total)} fue pagada correctamente.`
            : 'Puede demorar unos segundos. Revisá tus órdenes en un momento.'}
        </p>
        <Link to={`/ordenes/${orden.idOrden}`} className="boton-principal">
          Ver detalle de la orden
        </Link>
      </section>
    </main>
  );
}