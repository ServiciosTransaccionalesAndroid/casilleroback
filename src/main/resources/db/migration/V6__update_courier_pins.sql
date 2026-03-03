-- V6__update_courier_pins.sql
-- Actualizar PINs de mensajeros con valores diferentes

-- COUR001: PIN 1234 (ya existe, no cambiar)
-- COUR002: PIN 5678
UPDATE couriers 
SET pin = '$2a$10$8K1p/a0dL1LkDhd95.rvN.LM4f0uIiH5E2jGRjb3OJmrWP3QGQ4Oi',
    updated_at = NOW()
WHERE employee_id = 'COUR002';

-- Agregar más mensajeros de prueba con diferentes PINs
INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR003', 'Luis Ramírez', '+573201234567', 'luis.ramirez@servientrega.com', '$2a$10$Vv8YLhHKqNNj5qX5FZ5zXeO8qF5qF5qF5qF5qF5qF5qF5qF5qF5qG', true, NOW(), NOW()),
('COUR004', 'Sandra López', '+573301234567', 'sandra.lopez@servientrega.com', '$2a$10$xJ3DJaoFeLB0.eHXfBIz4.VV1pkKh/8Ate3fnNhpauKuClY9QLN4K', true, NOW(), NOW())
ON CONFLICT (employee_id) DO NOTHING;

-- Comentarios con los PINs en texto plano (solo para desarrollo):
-- COUR001: 1234
-- COUR002: 5678
-- COUR003: 9012
-- COUR004: 123456
