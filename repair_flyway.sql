-- EJECUTAR EN RAILWAY POSTGRESQL
-- Fix: Reparar Flyway y aplicar cambios de V7 manualmente

-- 1. Eliminar registro de V6 fallida
DELETE FROM flyway_schema_history WHERE version = '6';

-- 2. Aplicar cambios de V7 manualmente
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

-- 3. Registrar V7 como aplicada
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES (
    (SELECT COALESCE(MAX(installed_rank), 0) + 1 FROM flyway_schema_history),
    '7',
    'add more couriers',
    'SQL',
    'V7__add_more_couriers.sql',
    NULL,
    'postgres',
    0,
    true
);

-- 4. Verificar usuarios
SELECT employee_id, name, active FROM couriers ORDER BY employee_id;

-- 5. Verificar migraciones
SELECT version, description, success FROM flyway_schema_history ORDER BY installed_rank;
