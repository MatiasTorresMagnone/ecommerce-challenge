import { api } from './client';

export const obtenerCarrito = () => api('/carrito');
export const agregarAlCarrito = (productoId, cantidad = 1) => api('/carrito/items', { method: 'POST', body: { productoId, cantidad } });
export const modificarCantidadItem = (itemId, cantidad) => api(`/carrito/items/${itemId}`, { method: 'PUT', body: { cantidad }});
export const eliminarProductoCarrito = (itemId) => api(`/carrito/items/${itemId}`, { method: 'DELETE'});

