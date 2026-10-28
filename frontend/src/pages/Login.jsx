import { useState } from 'react';
import { Link, useNavigate } from 'react-router-dom';
import { useAuth } from '../context/AuthContext';

export default function Login() {
  const { login } = useAuth();
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
      await login(mail, password);
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
        <h2>Bienvenido</h2>
        <p className="subtitulo">Ingresá con tu cuenta</p>

        <form onSubmit={enviar}>
          <label>
            Mail
            <input type="email" value={mail} onChange={(e) => setMail(e.target.value)} required />
          </label>
          <label>
            Contraseña
            <input type="password" value={password} onChange={(e) => setPassword(e.target.value)} required />
          </label>

          {error && <p className="error">{error}</p>}

          <button type="submit" disabled={cargando}>
            {cargando ? 'Ingresando...' : 'Ingresar'}
          </button>
        </form>

        <p className="subtitulo" style={{ marginTop: '1.25rem', marginBottom: 0 }}>
          ¿No tenés cuenta? <Link to="/registro">Registrate</Link>
        </p>
      </div>
    </main>
  );
}