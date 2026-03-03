-- V9__create_admins_table.sql
-- Tabla de administradores para el portal web

CREATE TABLE admins (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

-- Índice para búsqueda por email
CREATE INDEX idx_admins_email ON admins(email);

-- Insertar admin por defecto
-- Email: admin@servientrega.com
-- Password: Admin123! (cambiar en producción)
INSERT INTO admins (email, name, password, role, active, created_at, updated_at)
VALUES (
    'admin@servientrega.com',
    'Administrador Principal',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    true,
    NOW(),
    NOW()
);

-- Nota: El password hasheado corresponde a "Admin123!"
-- En producción, cambiar inmediatamente después del primer login
