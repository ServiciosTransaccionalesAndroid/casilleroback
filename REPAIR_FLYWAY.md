# 🔧 Fix: Reparar Flyway en Railway

## ❌ Error
```
Detected applied migration not resolved locally: 6
```

## 🎯 Causa
Flyway registró V6 (que falló), pero el archivo ya no existe.

## ✅ Solución

### Ejecuta este SQL en Railway PostgreSQL:

```sql
-- 1. Eliminar registro de V6 fallida
DELETE FROM flyway_schema_history WHERE version = '6';

-- 2. Aplicar cambios manualmente
UPDATE couriers 
SET pin = '$2y$10$8K1p/a0dL1LkDhd95.rvN.LM4f0uIiH5E2jGRjb3OJmrWP3QGQ4Oi',
    updated_at = NOW()
WHERE employee_id = 'COUR002';

INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR003', 'Luis Ramírez', '+573201234567', 'luis.ramirez@servientrega.com', '$2y$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi', true, NOW(), NOW()),
('COUR004', 'Sandra López', '+573301234567', 'sandra.lopez@servientrega.com', '$2y$10$xJ3DJaoFeLB0.eHXfBIz4.VV1pkKh/8Ate3fnNhpauKuClY9QLN4K', true, NOW(), NOW())
ON CONFLICT (employee_id) DO NOTHING;

-- 3. Registrar V7 como aplicada
INSERT INTO flyway_schema_history (installed_rank, version, description, type, script, checksum, installed_by, execution_time, success)
VALUES (
    (SELECT COALESCE(MAX(installed_rank), 0) + 1 FROM flyway_schema_history),
    '7', 'add more couriers', 'SQL', 'V7__add_more_couriers.sql', NULL, 'postgres', 0, true
);
```

### Cómo Ejecutar

1. **Railway Dashboard** → PostgreSQL → **Query**
2. Pega el SQL completo
3. Click **Execute**
4. Ve a **backend** → **Deployments** → **Redeploy**

## 🧪 Verificar

```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR002", "pin": "5678"}'
```

## 📋 PINs Finales

| ID | PIN |
|----|-----|
| COUR001 | 1234 |
| COUR002 | 5678 |
| COUR003 | 9012 |
| COUR004 | 123456 |
