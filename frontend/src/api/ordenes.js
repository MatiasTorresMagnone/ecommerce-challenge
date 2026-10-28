import { api } from './client';

export const obtenerOrdenes = () => api('/ordenes');

export const obtenerDetalleOrden = (id) => api(`/ordenes/${id}`)