-- V10__create_recipients_table.sql
-- Tabla de clientes/destinatarios

CREATE TABLE recipients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100),
    address VARCHAR(300),
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Índice para búsqueda por teléfono
CREATE INDEX idx_recipients_phone ON recipients(phone);

-- Insertar clientes de prueba
INSERT INTO recipients (name, phone, email, address, created_at, updated_at)
VALUES 
('Carlos Rodríguez', '+573101234567', 'carlos.rodriguez@email.com', 'Calle 123 #45-67, Bogotá', NOW(), NOW()),
('Ana Martínez', '+573109876543', 'ana.martinez@email.com', 'Carrera 45 #12-34, Bogotá', NOW(), NOW()),
('Pedro Sánchez', '+573105556677', 'pedro.sanchez@email.com', 'Avenida 68 #89-01, Bogotá', NOW(), NOW());
