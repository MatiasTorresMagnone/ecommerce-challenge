const API_URL = import.meta.env.VITE_API_URL ?? 'http://localhost:8080';

export class ApiError extends Error {
  constructor(message, status) {
    super(message);
    this.status = status;
  }
}

export async function api(path, { method = 'GET', body } = {}) {
  const headers = {};
  const token = localStorage.getItem('token');
  if (token) headers.Authorization = `Bearer ${token}`;

  let payload;
  if (body instanceof FormData) {
    payload = body;
  } else if (body !== undefined) {
    headers['Content-Type'] = 'application/json';
    payload = JSON.stringify(body);
  }

  let res;
  try {
    res = await fetch(`${API_URL}${path}`, { method, headers, body: payload });
  } catch {
    throw new ApiError('No se pudo conectar con el servidor', 0);
  }

  const texto = await res.text();
  if (!res.ok) {
    if (token && (res.status === 401 || res.status === 403) && !path.startsWith('/auth')) {
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    throw new ApiError(texto || `Error ${res.status}`, res.status);
  }
  if (!texto) return null;
  try {
    return JSON.parse(texto);
  } catch {
    return texto;
  }
}

export const imagenUrl = (nombre) => (nombre ? `${API_URL}/imagenes/${nombre}` : null);