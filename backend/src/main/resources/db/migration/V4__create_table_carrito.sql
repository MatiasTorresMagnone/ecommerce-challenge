CREATE TABLE carrito (
    id_carrito BIGSERIAL PRIMARY KEY,
    usuario_id BIGINT NOT NULL UNIQUE REFERENCES usuario(id_usuario)
);