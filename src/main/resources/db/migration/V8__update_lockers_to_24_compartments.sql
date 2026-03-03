-- V8__update_lockers_to_24_compartments.sql
-- Actualizar lockers para tener 24 compartimientos cada uno

-- Actualizar total_compartments de lockers existentes
UPDATE lockers SET total_compartments = 24 WHERE id = 1;
UPDATE lockers SET total_compartments = 24 WHERE id = 2;

-- Agregar compartimientos faltantes para Locker 1 (de 12 a 24)
INSERT INTO compartments (locker_id, compartment_number, size, status, created_at, updated_at)
VALUES 
(1, 13, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 14, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 15, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 16, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 17, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 18, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 19, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 20, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 21, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(1, 22, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(1, 23, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(1, 24, 'LARGE', 'DISPONIBLE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Agregar compartimientos faltantes para Locker 2 (de 9 a 24)
INSERT INTO compartments (locker_id, compartment_number, size, status, created_at, updated_at)
VALUES 
(2, 10, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 11, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 12, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 13, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 14, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 15, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 16, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 17, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 18, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 19, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 20, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(2, 21, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(2, 22, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(2, 23, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(2, 24, 'LARGE', 'DISPONIBLE', NOW(), NOW())
ON CONFLICT DO NOTHING;

-- Distribución por locker (24 compartimientos):
-- SMALL: 10 compartimientos (1-10)
-- MEDIUM: 8 compartimientos (11-18)
-- LARGE: 6 compartimientos (19-24)
