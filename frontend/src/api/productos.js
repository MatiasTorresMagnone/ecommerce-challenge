import { api } from './client';


export const obtenerProductos = () => api('/productos');
export const buscarProductos = (nombre) =>api(`/productos/buscar?nombre=${encodeURIComponent(nombre)}`);
export const obtenerProducto = (id) => api(`/productos/${id}`);


//admin
export const obtenerProductosAdmin = () => api('/admin/productos');
export const crearProducto = (producto) => api('/admin/productos', { method: 'POST', body: producto });
export const actualizarProducto = (id, producto) => api(`/admin/productos/${id}`, { method: 'PUT', body: producto });

export function subirImagenProducto(id, archivo) {
  const datos = new FormData();
  datos.append('archivo', archivo);
  return api(`/admin/productos/${id}/imagen`, { method: 'POST', body: datos });
}

