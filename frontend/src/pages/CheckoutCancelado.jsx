import { Link } from 'react-router-dom';

export default function CheckoutCancelado() {
  return (
    <main className="pagina-tienda">
      <section className="estado-vacio">
        <div className="vacio-icono">❌</div>
        <h2>Pago cancelado</h2>
        <p>No se realizó ningún cobro. Tu carrito sigue como lo dejaste.</p>
        <Link to="/carrito" className="boton-principal">Volver al carrito</Link>
      </section>
    </main>
  );
}