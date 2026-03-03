-- V7__add_more_couriers.sql
-- Actualizar PINs de mensajeros (todos usan PIN 1234 temporalmente)

-- COUR002: Actualizar con PIN 1234
UPDATE couriers 
SET pin = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    updated_at = NOW()
WHERE employee_id = 'COUR002';

-- Agregar más mensajeros con PIN 1234
INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR003', 'Luis Ramírez', '+573201234567', 'luis.ramirez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
('COUR004', 'Sandra López', '+573301234567', 'sandra.lopez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW())
ON CONFLICT (employee_id) DO NOTHING;

-- Todos los usuarios tienen PIN: 1234 (temporal para desarrollo)
