import { api } from './client';

export const iniciarCheckout = () => api('/checkout', { method: 'POST' });