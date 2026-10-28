import { useState, useEffect } from 'react';
import { Link } from 'react-router-dom';
import { formatoPrecio } from '../utils/formato';
import { obtenerOrdenes } from '../api/ordenes';

export default function Ordenes() {
  const [ordenes, setOrdenes] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [error, setError] = useState('');

  const [ordenamiento, setOrdenamiento] = useState('desc');

  useEffect(() => {
    async function cargarOrdenes() {
        setCargando(true)
        setError('');
        try{
            setOrdenes(await obtenerOrdenes());
        }catch(err){
            setError(err.message || 'Error al obtener ordenes');
        }finally{
            setCargando(false);
        }

    }

    cargarOrdenes();
  }, []);

  const toggleOrdenamiento = () => {
    setOrdenamiento((prev) => (prev === 'desc' ? 'asc' : 'desc'));
  };

  const ordenesFiltradas = [...ordenes].sort((a, b) => {
    const idA = a.idOrden ?? a.id ?? 0;
    const idB = b.idOrden ?? b.id ?? 0;
    return ordenamiento === 'asc' ? idA - idB : idB - idA;
  });

  return (
    <main className="pagina-tienda">
      <div className="encabezado-pagina">
        <div>
          <span className="texto-superior">HISTORIAL</span>
          <h1>Mis órdenes</h1>
          <p>Consultá el estado y detalle de tus compras anteriores.</p>
        </div>

        <Link to="/" className="boton-volver">
          ← Volver al catálogo
        </Link>
      </div>

      {error && <p className="error" style={{ marginBottom: '1rem' }}>{error}</p>}

      {cargando ? (
        <section className="estado-vacio">
          <p className="vacio">Cargando órdenes...</p>
        </section>
      ) : ordenes.length === 0 ? (
        <section className="estado-vacio">
          <div className="vacio-icono">📦</div>
          <h2>Aún no tenés órdenes realizadas</h2>
          <p>Realizá tu primera compra desde el carrito.</p>
          <Link to="/" className="boton-principal">
            Explorar catálogo
          </Link>
        </section>
      ) : (
        <section className="panel">
          <div className="panel-titulo" style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
            <div>
              <h2>Órdenes realizadas</h2>
              <span className="contador">
                {ordenes.length} {ordenes.length === 1 ? 'orden' : 'órdenes'}
              </span>
            </div>

            {/* Único botón toggle */}
            <button
              type="button"
              className="boton-tabla boton-editar"
              onClick={toggleOrdenamiento}
            >
              {ordenamiento === 'desc' ? '(↓)' : '(↑)'}
            </button>
          </div>

          <div className="tabla-contenedor">
            <table className="tabla-tienda">
              <thead>
                <tr>
                  <th>Nº de Orden</th>
                  <th>Fecha</th>
                  <th>Estado</th>
                  <th>Total</th>
                  <th>Acciones</th>
                </tr>
              </thead>

              <tbody>
                {ordenesFiltradas.map((orden) => (
                  <tr key={orden.idOrden}>
                    <td>
                      <strong>#{orden.idOrden}</strong>
                    </td>
                    <td>
                      {orden.fechaCreacion
                        ? new Date(orden.fechaCreacion).toLocaleDateString()
                        : '-'}
                    </td>
                    <td>
                      <span className={`badge ${String(orden.estado).toUpperCase()}`}>
                        {orden.estado}
                      </span>
                    </td>
                    <td className="precio-celda">
                      {formatoPrecio(orden.total)}
                    </td>
                    <td>
                      <Link
                        to={`/ordenes/${orden.idOrden ?? orden.id}`}
                        className="boton-tabla boton-editar"
                      >
                        Ver detalle
                      </Link>
                    </td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        </section>
      )}
    </main>
  );
}