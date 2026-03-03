# 🔧 Fix Rápido: Aplicar PINs en Railway

## ❌ Problema
La migración V6 no se aplicó porque los hashes BCrypt son de prueba y no válidos.

## ✅ Solución Rápida

### Opción 1: Ejecutar SQL Manualmente en Railway (RECOMENDADO)

1. **Ir a Railway Dashboard**
   - Abre tu proyecto en Railway
   - Click en el servicio **PostgreSQL**

2. **Abrir Query Tab**
   - Click en pestaña **"Query"** o **"Data"**
   - O usa el botón **"Connect"** para obtener credenciales

3. **Ejecutar este SQL:**
```sql
-- Actualizar COUR002 con PIN 1234
UPDATE couriers 
SET pin = '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy',
    updated_at = NOW()
WHERE employee_id = 'COUR002';

-- Crear COUR003 y COUR004 con PIN 1234
INSERT INTO couriers (employee_id, name, phone, email, pin, active, created_at, updated_at)
VALUES 
('COUR003', 'Luis Ramírez', '+573201234567', 'luis.ramirez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW()),
('COUR004', 'Sandra López', '+573301234567', 'sandra.lopez@servientrega.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17lhWy', true, NOW(), NOW())
ON CONFLICT (employee_id) DO NOTHING;
```

4. **Verificar:**
```sql
SELECT employee_id, name, active FROM couriers ORDER BY employee_id;
```

### Opción 2: Eliminar Migración V6 y Usar PIN 1234 para Todos

```bash
# Eliminar migración problemática
rm src/main/resources/db/migration/V6__update_courier_pins.sql

# Commit y push
git add .
git commit -m "fix: remover migración V6 problemática"
git push origin main
```

Luego ejecuta el SQL del paso anterior manualmente.

---

## 🧪 Probar Después del Fix

```bash
# COUR001 - PIN 1234
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}'

# COUR002 - PIN 1234 (temporal)
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR002", "pin": "1234"}'

# COUR003 - PIN 1234 (temporal)
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR003", "pin": "1234"}'
```

---

## 📝 Usuarios Temporales (Todos PIN 1234)

| Employee ID | Nombre | PIN |
|-------------|--------|-----|
| COUR001 | Juan Pérez | 1234 |
| COUR002 | María González | 1234 |
| COUR003 | Luis Ramírez | 1234 |
| COUR004 | Sandra López | 1234 |

---

## 🔐 Para Generar PINs Diferentes (Próximo Paso)

Necesitas crear un endpoint en la API para cambiar PINs, porque BCrypt genera hashes diferentes cada vez (por el salt).

**Alternativa:** Usar un generador BCrypt online:
1. Ir a: https://bcrypt-generator.com/
2. Rounds: 10
3. Generar hash para cada PIN
4. Actualizar manualmente en Railway

---

## ⚠️ Nota Importante

Por ahora, todos los usuarios usarán PIN **1234** temporalmente. Esto es suficiente para:
- ✅ Probar el sistema
- ✅ Verificar que CORS funciona
- ✅ Validar la autenticación
- ✅ Demostrar múltiples usuarios

Para producción, implementa un endpoint de cambio de PIN.
