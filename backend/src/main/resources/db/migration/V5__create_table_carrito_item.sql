CREATE TABLE carrito_item (
    id_carrito_item BIGSERIAL PRIMARY KEY,
    carrito_id      BIGINT NOT NULL REFERENCES carrito(id_carrito) ON DELETE CASCADE,
    producto_id     BIGINT NOT NULL REFERENCES producto(id_producto),
    cantidad        INT    NOT NULL
);