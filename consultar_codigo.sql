-- Consultar código de retiro para un paquete específico

-- Para SRV123456789
SELECT 
    p.tracking_number AS "Número de Guía",
    rc.code AS "Código de Retiro",
    rc.generated_at AS "Generado",
    rc.expires_at AS "Expira",
    rc.used AS "Usado",
    c.compartment_number AS "Compartimento",
    l.name AS "Locker"
FROM packages p
LEFT JOIN deposits d ON p.id = d.package_id
LEFT JOIN retrieval_codes rc ON d.id = rc.deposit_id
LEFT JOIN compartments c ON d.compartment_id = c.id
LEFT JOIN lockers l ON c.locker_id = l.id
WHERE p.tracking_number = 'SRV123456789';

-- Si no hay resultados, significa que el paquete NO ha sido depositado aún
-- Necesitas hacer un depósito primero para generar el código

-- Para ver TODOS los códigos activos:
SELECT 
    p.tracking_number,
    rc.code,
    rc.expires_at,
    rc.used,
    l.name as locker
FROM retrieval_codes rc
JOIN deposits d ON rc.deposit_id = d.id
JOIN packages p ON d.package_id = p.id
JOIN compartments c ON d.compartment_id = c.id
JOIN lockers l ON c.locker_id = l.id
ORDER BY rc.generated_at DESC;
