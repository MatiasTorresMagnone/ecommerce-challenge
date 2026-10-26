CREATE TABLE orden_detalle (
    id_orden_detalle BIGSERIAL PRIMARY KEY,
    orden_id         BIGINT        NOT NULL REFERENCES orden(id_orden) ON DELETE CASCADE,
    producto_id      BIGINT        NOT NULL REFERENCES producto(id_producto),
    cantidad         INT           NOT NULL,
    precio_unitario  NUMERIC(19,2) NOT NULL,
    subtotal         NUMERIC(19,2) NOT NULL
);