-- V7__add_more_couriers.sql
-- Actualizar PINs de mensajeros con valores únicos

-- COUR002: PIN 5678
UPDATE couriers 
SET pin = '$2y$10$8K1p/a0dL1LkDhd95.rvN.LM4f0uIiH5E2jGRjb3OJmrWP3QGQ4Oi',
    updated_at = NOW()
WHERE employee_id = 'COUR002';

-- COUR003: PIN 9012
-- COUR004: PIN 123456
INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR003', 'Luis Ramírez', '+573201234567', 'luis.ramirez@servientrega.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', true, NOW(), NOW()),
('COUR004', 'Sandra López', '+573301234567', 'sandra.lopez@servientrega.com', '$2y$10$xJ3DJaoFeLB0.eHXfBIz4.VV1pkKh/8Ate3fnNhpauKuClY9QLN4K', true, NOW(), NOW())
ON CONFLICT (employee_id) DO NOTHING;

-- PINs asignados:
-- COUR001: 1234
-- COUR002: 5678
-- COUR003: 9012
-- COUR004: 123456
