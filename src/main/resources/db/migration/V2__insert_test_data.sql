INSERT INTO clients (name, email, address, active)
VALUES ('John Doe', 'john@example.com', 'Kyiv, Ukraine', true),
       ('Jane Smith', 'jane@example.com', 'Lviv, Ukraine', true),
       ('Bob Johnson', 'bob@example.com', 'Odesa, Ukraine', false);

INSERT INTO orders (name, supplier_id, consumer_id, price, start_time, end_time)
VALUES ('Order 1', 1, 2, 100.50, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Order 2', 2, 1, 200.75, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
       ('Order 3', 1, 3, 300.25, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);