-- V2__seed_data.sql

-- Insertar locker de prueba
INSERT INTO lockers (name, location, address, latitude, longitude, status, total_compartments, created_at, updated_at)
VALUES 
('Locker Centro', 'Centro Comercial Andino', 'Carrera 11 #82-71, Bogotá', 4.6678, -74.0547, 'ACTIVE', 12, NOW(), NOW()),
('Locker Norte', 'Centro Comercial Unicentro', 'Avenida 15 #123-30, Bogotá', 4.7110, -74.0721, 'ACTIVE', 9, NOW(), NOW());

-- Insertar compartimentos para Locker 1 (12 compartimentos)
INSERT INTO compartments (locker_id, compartment_number, size, status, created_at, updated_at)
VALUES 
(1, 1, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 2, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 3, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 4, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(1, 5, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 6, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 7, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 8, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(1, 9, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(1, 10, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(1, 11, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(1, 12, 'LARGE', 'DISPONIBLE', NOW(), NOW());

-- Insertar compartimentos para Locker 2 (9 compartimentos)
INSERT INTO compartments (locker_id, compartment_number, size, status, created_at, updated_at)
VALUES 
(2, 1, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 2, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 3, 'SMALL', 'DISPONIBLE', NOW(), NOW()),
(2, 4, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 5, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 6, 'MEDIUM', 'DISPONIBLE', NOW(), NOW()),
(2, 7, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(2, 8, 'LARGE', 'DISPONIBLE', NOW(), NOW()),
(2, 9, 'LARGE', 'DISPONIBLE', NOW(), NOW());

-- Insertar mensajeros de prueba (PIN: 1234)
INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR001', 'Juan Pérez', '+573001234567', 'juan.perez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
('COUR002', 'María González', '+573007654321', 'maria.gonzalez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW());

-- Insertar paquetes de prueba
INSERT INTO packages (tracking_number, recipient_name, recipient_phone, recipient_email, width, height, depth, weight, status, created_at, updated_at)
VALUES 
('SRV123456789', 'Carlos Rodríguez', '+573101234567', 'carlos.rodriguez@email.com', 30.00, 20.00, 15.00, 2.50, 'EN_TRANSITO', NOW(), NOW()),
('SRV987654321', 'Ana Martínez', '+573109876543', 'ana.martinez@email.com', 40.00, 30.00, 25.00, 5.00, 'EN_TRANSITO', NOW(), NOW()),
('SRV555666777', 'Pedro Sánchez', '+573105556677', 'pedro.sanchez@email.com', 20.00, 15.00, 10.00, 1.00, 'EN_TRANSITO', NOW(), NOW());
