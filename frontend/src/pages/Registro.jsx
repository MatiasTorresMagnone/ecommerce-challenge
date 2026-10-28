import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Registro() {
  const { registrar } = useAuth();
  const navigate = useNavigate();

  const [mail, setMail] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [cargando, setCargando] = useState(false);

  async function enviar(e) {
    e.preventDefault();
    setError('');
    setCargando(true);
    try {
      await registrar(mail, password);  // crea la cuenta y deja la sesión iniciada
      navigate('/');
    } catch (err) {
      setError(err.message);
    } finally {
      setCargando(false);
    }
  }

  return (
    <main>
      <div className="form-page">
        <h2>Crear cuenta</h2>
        <p className="subtitulo">Es gratis y toma un minuto</p>

        <form onSubmit={enviar}>
          <label>
            Mail
            <input type="email" value={mail} onChange={(e) => setMail(e.target.value)} required />
          </label>
          <label>
            Contraseña (mínimo 8 caracteres)
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)}
                   minLength={8} required />
          </label>

          {error && <p className="error">{error}</p>}

          <button type="submit" disabled={cargando}>
            {cargando ? 'Creando...' : 'Registrarme'}
          </button>
        </form>

        <p className="subtitulo" style={{ marginTop: '1.25rem', marginBottom: 0 }}>
          ¿Ya tenés cuenta? <Link to="/login">Ingresá</Link>
        </p>
      </div>
    </main>
  );
}