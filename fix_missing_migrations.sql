-- Script para ejecutar migraciones faltantes en Railway
-- Ejecutar este script directamente en la base de datos de Railway

-- V9: Crear tabla admins
CREATE TABLE IF NOT EXISTS admins (
    id BIGSERIAL PRIMARY KEY,
    email VARCHAR(100) NOT NULL UNIQUE,
    name VARCHAR(200) NOT NULL,
    password VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'ADMIN',
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_admins_email ON admins(email);

-- Insertar admin por defecto (password: Admin123!)
INSERT INTO admins (email, name, password, role, active, created_at, updated_at)
VALUES (
    'admin@servientrega.com',
    'Administrador Principal',
    '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    'ADMIN',
    true,
    NOW(),
    NOW()
)
ON CONFLICT (email) DO NOTHING;

-- V10: Crear tabla recipients
CREATE TABLE IF NOT EXISTS recipients (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL UNIQUE,
    email VARCHAR(100),
    address TEXT,
    created_at TIMESTAMP NOT NULL,
    updated_at TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_recipients_phone ON recipients(phone);
CREATE INDEX IF NOT EXISTS idx_recipients_email ON recipients(email);

-- Insertar clientes de prueba
INSERT INTO recipients (name, phone, email, address, created_at, updated_at)
VALUES 
    ('Carlos Rodríguez', '+573101234567', 'carlos.rodriguez@email.com', 'Calle 123 #45-67, Bogotá', NOW(), NOW()),
    ('Ana Martínez', '+573109876543', 'ana.martinez@email.com', 'Carrera 45 #12-34, Bogotá', NOW(), NOW()),
    ('Pedro Sánchez', '+573105556677', 'pedro.sanchez@email.com', 'Avenida 68 #89-01, Bogotá', NOW(), NOW())
ON CONFLICT (phone) DO NOTHING;

-- Registrar migraciones en Flyway
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES 
    (9, '9', 'create admins table', 'SQL', 'V9__create_admins_table.sql', 0, 'locker_user', 0, true),
    (10, '10', 'create recipients table', 'SQL', 'V10__create_recipients_table.sql', 0, 'locker_user', 0, true)
ON CONFLICT (version) DO NOTHING;
