-- EJECUTAR MANUALMENTE EN RAILWAY POSTGRES
-- Solución temporal: Todos usan PIN 1234 hasta generar hashes correctos

-- Verificar usuarios actuales
SELECT employee_id, name, active FROM couriers;

-- Opción 1: Actualizar COUR002 con PIN 1234 (temporal)
UPDATE couriers 
SET pin = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    updated_at = NOW()
WHERE employee_id = 'COUR002';

-- Opción 2: Crear nuevos usuarios con PIN 1234 (temporal)
INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR003', 'Luis Ramírez', '+573201234567', 'luis.ramirez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
('COUR004', 'Sandra López', '+573301234567', 'sandra.lopez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW())
ON CONFLICT (employee_id) DO NOTHING;

-- Verificar cambios
SELECT employee_id, name, phone, active FROM couriers ORDER BY employee_id;

-- NOTA: Todos los usuarios tendrán PIN 1234 temporalmente
-- Para generar PINs diferentes, necesitas usar BCrypt desde la aplicación
