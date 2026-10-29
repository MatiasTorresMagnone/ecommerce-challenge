import { Routes, Route } from 'react-router-dom';
import Navbar from './components/Navbar';
import RutaProtegida from './components/RutaProtegida';
import RutaPublica from './components/RutaPublica';
import Login from './pages/Login';
import Registro from './pages/Registro';
import Carrito from './pages/Carrito';
import Admin from './pages/Admin';
import Catalogo from './pages/Catalogo';
import DetalleProducto from './pages/DetalleProducto';
import Ordenes from './pages/Ordenes';
import DetalleOrden from './pages/DetalleOrden';
import CheckoutExito from './pages/CheckoutExito';
import CheckoutCancelado from './pages/CheckoutCancelado';

export default function App() {
  return (
    <>
      <Navbar />
      <Routes>
        <Route path="/" element={<Catalogo />} />
        <Route path="/productos/:id" element={<DetalleProducto />} />

        <Route element={<RutaPublica />}>
          <Route path="/login" element={<Login />} />
          <Route path="/registro" element={<Registro />} />
        </Route>
        <Route element={<RutaProtegida />}>
          <Route path="/carrito" element={<Carrito />}/>
          <Route path="/checkout/exito" element={<CheckoutExito  />} />
          <Route path="/checkout/cancelado" element={<CheckoutCancelado />} />
          <Route path="/ordenes" element={<Ordenes />} />
          <Route path="/ordenes/:id" element={<DetalleOrden />} />
        </Route>
         <Route path="*" element={<main className="pagina-tienda"><p className="vacio">Página no encontrada</p></main>} />

        <Route element={<RutaProtegida soloAdmin />}>
          <Route path="/admin" element={<Admin />} />
        </Route>
      </Routes>
    </>
  );
}