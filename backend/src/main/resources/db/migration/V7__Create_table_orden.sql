CREATE TABLE orden (
    id_orden          BIGSERIAL PRIMARY KEY,
    usuario_id        BIGINT        NOT NULL REFERENCES usuario(id_usuario),
    total             NUMERIC(19,2) NOT NULL,
    estado            VARCHAR(20)   NOT NULL,
    fecha_creacion    TIMESTAMP     NOT NULL,
    stripe_session_id VARCHAR(255)  UNIQUE
);
