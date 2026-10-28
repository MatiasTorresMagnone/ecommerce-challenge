import { useState,useEffect } from 'react';
import { imagenUrl } from '../api/client';
import {obtenerProductosAdmin,crearProducto,actualizarProducto,subirImagenProducto} from '../api/productos';
import { formatoPrecio } from '../utils/formato';


const formularioVacio = {
  nombre: '',
  descripcion: '',
  precio: '',
  activo: true,
};

const TIPOS_PERMITIDOS = ['image/jpeg', 'image/png', 'image/webp'];
const MAX_BYTES = 2 * 1024 * 1024;

export default function Admin() {

  const [productos, setProductos] = useState([]);
  const [cargando, setCargando] = useState(true);
  const [busqueda, setBusqueda] = useState('');
  const [productoEditando, setProductoEditando] = useState(null);
  const [formulario, setFormulario] = useState(formularioVacio);
  const [archivo, setArchivo] = useState(null);
  const [claveArchivo, setClaveArchivo] = useState(0);
  const [error, setError] = useState('');
  const [mensaje, setMensaje] = useState('');


  async function cargarProductos() {
    setCargando(true);
    try {
      setProductos(await obtenerProductosAdmin());
    } catch (err) {
      setError(err.message);
    }finally {
      setCargando(false);
    }
  }

   useEffect(() => {
    cargarProductos();
   }, []);

  function limpiarFormulario() {
    setProductoEditando(null);
    setFormulario(formularioVacio);
    setArchivo(null);
    setClaveArchivo((n) => n + 1);
    setError('');
  }

  function irAlFormulario() {
    document.querySelector('.formulario-admin')?.scrollIntoView({ behavior: 'smooth' });
  }

  function abrirNuevo() {
    limpiarFormulario();
    setMensaje('');
    irAlFormulario();
  }

  function editarProducto(producto) {
    limpiarFormulario();
    setMensaje('');

    setProductoEditando(producto.id);
    setFormulario({
       nombre: producto.nombre ?? '',
       descripcion: producto.descripcion ?? '',
       precio: producto.precio ? String(producto.precio) : '',
       activo: producto.activo ?? true,
    });

    irAlFormulario();
  }

  function cambiarCampo(e) {
    const { name, value, type, checked } = e.target;
    setFormulario((actual) => ({
      ...actual,
      [name]: type === 'checkbox' ? checked : value,
    }));
  }

  async function guardarProducto(e) {
   e.preventDefault();
   setError('');
   setMensaje('');

   if (!formulario.nombre.trim()) return setError('Ingresá el nombre del producto.');
   if (!formulario.descripcion.trim()) return setError('Ingresá una descripción.');
   if (!formulario.precio || Number(formulario.precio) <= 0)
       return setError('El precio debe ser mayor que cero.');
   if (archivo && !TIPOS_PERMITIDOS.includes(archivo.type))
       return setError('La imagen debe ser JPG, PNG o WEBP.');
   if (archivo && archivo.size > MAX_BYTES)
       return setError('La imagen no puede pesar más de 2 MB.');

   const payload = {
       nombre: formulario.nombre.trim(),
       descripcion: formulario.descripcion.trim(),
       precio: Number(formulario.precio),
       activo: formulario.activo,
   };

    // Paso 1: guardar el producto
   let guardado;
   try {
       guardado =
       productoEditando !== null
           ? await actualizarProducto(productoEditando, payload)
           : await crearProducto(payload);
   } catch (err) {
       setError(err.message || 'Error al guardar el producto.');
       return;
   }

   // Paso 2: subir la imagen, con el id del producto recién guardado
   if (archivo) {
       try {
            await subirImagenProducto(guardado.id, archivo);
       } catch (err) {
            setProductoEditando(guardado.id); // así reintentar no crea un duplicado
            setError(`El producto se guardó, pero la imagen falló: ${err.message}`);
            await cargarProductos();
            return;
       }
   }

   await cargarProductos();
   limpiarFormulario();
   setMensaje('Producto guardado correctamente.');
  }

  async function alternarActivo(producto) {
    try {
          await actualizarProducto(producto.id, {
            nombre: producto.nombre,
            descripcion: producto.descripcion,
            precio: Number(producto.precio),
            activo: !producto.activo,
          });
          await cargarProductos();
    } catch (err) {
          window.alert(err.message);
    }
  }
  const cancelarEdicion = limpiarFormulario;

  const filtrados = productos.filter((producto) =>
    producto.nombre?.toLowerCase().includes(busqueda.toLowerCase())
  );

  const activos = productos.filter((producto) => producto.activo).length;

  return (
    <main className="pagina-tienda">
      <div className="encabezado-pagina">
        <div>
          <span className="texto-superior">GESTIÓN DE LA TIENDA</span>
          <h1>Administración</h1>
          <p>Gestioná tu catálogo de productos.</p>
        </div>

        <button
          type="button"
          className="boton-principal"
          onClick={abrirNuevo}
        >
          + Nuevo producto
        </button>
      </div>

      <section className="estadisticas-admin">
        <article className="estadistica">
          <span className="estadistica-icono">📦</span>
          <div>
            <span>Total de productos</span>
            <strong>{cargando ? '...' : productos.length}</strong>
          </div>
        </article>

        <article className="estadistica">
          <span className="estadistica-icono">✅</span>
          <div>
            <span>Productos activos</span>
            <strong>{cargando ? '...' : activos}</strong>
          </div>
        </article>

        <article className="estadistica">
          <span className="estadistica-icono">⏸️</span>
          <div>
            <span>Productos inactivos</span>
            <strong>{cargando ? '...' : productos.length - activos}</strong>
          </div>
        </article>
      </section>

      <section className="panel panel-admin">
        <div className="panel-titulo admin-titulo">
          <div>
            <h2>Productos</h2>
            <p>Consultá y modificá los productos de tu tienda.</p>
          </div>

          <input
            className="admin-buscador"
            type="search"
            placeholder="Buscar producto..."
            aria-label="Buscar producto"
            value={busqueda}
            onChange={(e) => setBusqueda(e.target.value)}
          />
        </div>

        <div className="tabla-contenedor">
          <table className="tabla-tienda">
            <thead>
              <tr>
                <th>Producto</th>
                <th>Precio</th>
                <th>Estado</th>
                <th>Acciones</th>
              </tr>
            </thead>

            <tbody>
              {cargando ? (
                <tr>
                  <td colSpan="4">
                    <div className="tabla-vacia">Cargando productos...</div>
                  </td>
                </tr>
              ) : filtrados.length === 0 ? (
                <tr>
                  <td colSpan="4">
                    <div className="tabla-vacia">
                      No se encontraron productos.
                    </div>
                  </td>
                </tr>
              ) : (
                filtrados.map((producto) => (
                  <tr key={producto.id}>
                    <td>
                      <div className="producto-tabla">
                        <div className="producto-miniatura">
                          {producto.imagen ? (
                            <img
                              src={imagenUrl(producto.imagen)}
                              alt={producto.nombre}
                            />
                          ) : (
                            '📦'
                          )}
                        </div>
                        <div>
                          <strong>{producto.nombre}</strong>
                          <span>{producto.descripcion}</span>
                        </div>
                      </div>
                    </td>

                    <td className="precio-celda">
                      {formatoPrecio(producto.precio)}
                    </td>

                    <td>
                      <span
                        className={`estado-producto ${
                          producto.activo ? 'activo' : 'inactivo'
                        }`}
                      >
                        {producto.activo ? 'Activo' : 'Inactivo'}
                      </span>
                    </td>

                    <td>
                      <div className="acciones-tabla">
                        <button
                          type="button"
                          className="boton-tabla boton-editar"
                          onClick={() => editarProducto(producto)}
                        >
                          Editar
                        </button>

                        <button
                          type="button"
                          className="boton-tabla boton-borrar"
                          onClick={() => alternarActivo(producto)}
                        >
                          {producto.activo ? 'Desactivar' : 'Activar'}
                        </button>
                      </div>
                    </td>
                  </tr>
                ))
              )}
            </tbody>
          </table>
        </div>
      </section>

      <section className="panel formulario-admin">
        <div className="panel-titulo">
          <div>
            <h2>
              {productoEditando !== null
                ? 'Editar producto'
                : 'Crear producto'}
            </h2>
            <p>Completá los datos del producto.</p>
          </div>
        </div>

        <form onSubmit={guardarProducto}>
          <div className="formulario-admin-grilla">
            <label>
              Nombre
              <input
                name="nombre"
                value={formulario.nombre}
                onChange={cambiarCampo}
                placeholder="Ej. Teclado mecánico"
                required
              />
            </label>

            <label>
              Precio (USD)
              <input
                name="precio"
                type="number"
                min="0.01"
                step="0.01"
                value={formulario.precio}
                onChange={cambiarCampo}
                placeholder="Ej. 24.90"
                required
              />
            </label>

            <label className="campo-descripcion">
              Descripción
              <textarea
                name="descripcion"
                rows="3"
                value={formulario.descripcion}
                onChange={cambiarCampo}
                placeholder="Describí las características del producto"
                required
              />
            </label>

            <label className="campo-checkbox">
              <input
                name="activo"
                type="checkbox"
                checked={formulario.activo}
                onChange={cambiarCampo}
              />
              Producto activo
            </label>

            <label className="campo-descripcion">
              Imagen (JPG, PNG o WEBP, máximo 2 MB)
              <input
                key={claveArchivo}
                type="file"
                accept="image/jpeg,image/png,image/webp"
                onChange={(e) => setArchivo(e.target.files[0] ?? null)}
              />
            </label>
          </div>
          {mensaje && <p className="exito">{mensaje}</p>}
          {error && <p className="error">{error}</p>}

          <div className="acciones-formulario">
            <button type="submit" className="boton-principal">
              {productoEditando !== null
                ? 'Guardar cambios'
                : 'Crear producto'}
            </button>

            {productoEditando !== null && (
              <button
                type="button"
                className="boton-secundario"
                onClick={cancelarEdicion}
              >
                Cancelar
              </button>
            )}
          </div>
        </form>
      </section>
    </main>
  );
}