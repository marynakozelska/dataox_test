CREATE TABLE clients
(
    id             BIGSERIAL PRIMARY KEY,
    name           VARCHAR(255)             NOT NULL,
    email          VARCHAR(255) UNIQUE      NOT NULL,
    address        VARCHAR(255),
    profit         NUMERIC(19, 2) DEFAULT 0 NOT NULL,
    active         BOOLEAN        DEFAULT TRUE,
    deactivated_at TIMESTAMP,
    created_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP,
    updated_at     TIMESTAMP      DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE orders
(
    id          BIGSERIAL PRIMARY KEY,
    name        VARCHAR(255)   NOT NULL,
    supplier_id BIGINT         NOT NULL,
    consumer_id BIGINT         NOT NULL,
    price       NUMERIC(19, 2) NOT NULL CHECK (price > 0),
    start_time  TIMESTAMP,
    end_time    TIMESTAMP,
    created_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (supplier_id) REFERENCES clients (id),
    FOREIGN KEY (consumer_id) REFERENCES clients (id),
    CONSTRAINT unique_business_key UNIQUE (name, supplier_id, consumer_id)
);
