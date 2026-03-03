# 🚀 Guía de Uso - API Casilleros Servientrega (Producción)

**Base URL:** `https://casilleroback-production.up.railway.app`

---

## 📋 Índice
1. [Health Check](#1-health-check)
2. [Gestión de Empleados (Couriers)](#2-gestión-de-empleados-couriers)
3. [Autenticación](#3-autenticación)
4. [Paquetes](#4-paquetes)
5. [Depósitos](#5-depósitos)
6. [Retiros](#6-retiros)

---

## 1. Health Check

### Verificar estado del servicio
```bash
curl https://casilleroback-production.up.railway.app/api/health
```

**Respuesta:**
```json
{
  "service": "Servientrega Locker Backend",
  "version": "1.0.0",
  "status": "UP",
  "timestamp": "2026-03-03T14:19:06.439779903"
}
```

---

## 2. Gestión de Empleados (Couriers)

### 2.1 Crear Empleado
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/couriers \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR005",
    "name": "Pedro García",
    "phone": "+573401234567",
    "email": "pedro.garcia@servientrega.com",
    "pin": "7890"
  }'
```

**Respuesta:**
```json
{
  "id": 5,
  "employeeId": "COUR005",
  "name": "Pedro García",
  "phone": "+573401234567",
  "email": "pedro.garcia@servientrega.com",
  "active": true
}
```

### 2.2 Listar Todos los Empleados
```bash
curl https://casilleroback-production.up.railway.app/api/couriers
```

### 2.3 Obtener Empleado por ID
```bash
curl https://casilleroback-production.up.railway.app/api/couriers/COUR005
```

### 2.4 Actualizar Empleado
```bash
# Cambiar PIN
curl -X PUT https://casilleroback-production.up.railway.app/api/couriers/COUR005 \
  -H "Content-Type: application/json" \
  -d '{
    "pin": "1111"
  }'

# Actualizar nombre y teléfono
curl -X PUT https://casilleroback-production.up.railway.app/api/couriers/COUR005 \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Pedro García Actualizado",
    "phone": "+573409999999"
  }'

# Desactivar empleado
curl -X PUT https://casilleroback-production.up.railway.app/api/couriers/COUR005 \
  -H "Content-Type: application/json" \
  -d '{
    "active": false
  }'
```

### 2.5 Eliminar Empleado
```bash
curl -X DELETE https://casilleroback-production.up.railway.app/api/couriers/COUR005
```

---

## 3. Autenticación

### 3.1 Login de Mensajero
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR001",
    "pin": "1234"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "message": "Login successful"
}
```

**Usar el token en requests protegidos:**
```bash
curl https://casilleroback-production.up.railway.app/api/protected-endpoint \
  -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

## 4. Paquetes

### 4.1 Validar Paquete
```bash
curl "https://casilleroback-production.up.railway.app/api/packages/validate?trackingNumber=SRV123456789"
```

**Respuesta:**
```json
{
  "trackingNumber": "SRV123456789",
  "recipientName": "Carlos Rodríguez",
  "recipientPhone": "+573101234567",
  "status": "EN_TRANSITO",
  "dimensions": {
    "width": 30.0,
    "height": 20.0,
    "depth": 15.0,
    "weight": 2.5
  }
}
```

---

## 5. Depósitos

### 5.1 Registrar Depósito
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer YOUR_TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentNumber": 5,
    "courierEmployeeId": "COUR001"
  }'
```

**Respuesta:**
```json
{
  "depositId": 1,
  "trackingNumber": "SRV123456789",
  "compartmentNumber": 5,
  "retrievalCode": "ABC12345",
  "expiresAt": "2026-03-05T14:00:00",
  "message": "Package deposited successfully"
}
```

---

## 6. Retiros

### 6.1 Validar Código de Retiro
```bash
curl "https://casilleroback-production.up.railway.app/api/retrievals/validate?code=ABC12345"
```

**Respuesta:**
```json
{
  "valid": true,
  "trackingNumber": "SRV123456789",
  "compartmentNumber": 5,
  "lockerId": 1,
  "expiresAt": "2026-03-05T14:00:00"
}
```

### 6.2 Registrar Retiro
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/retrievals \
  -H "Content-Type: application/json" \
  -d '{
    "retrievalCode": "ABC12345",
    "secretPin": "1234"
  }'
```

**Respuesta:**
```json
{
  "success": true,
  "trackingNumber": "SRV123456789",
  "compartmentNumber": 5,
  "message": "Package retrieved successfully"
}
```

---

## 📦 Colección Postman

### Importar en Postman

1. Abre Postman
2. Click en **Import**
3. Copia y pega este JSON:

```json
{
  "info": {
    "name": "Casilleros Servientrega - Producción",
    "schema": "https://schema.getpostman.com/json/collection/v2.1.0/collection.json"
  },
  "variable": [
    {
      "key": "baseUrl",
      "value": "https://casilleroback-production.up.railway.app"
    },
    {
      "key": "token",
      "value": ""
    }
  ],
  "item": [
    {
      "name": "Health Check",
      "request": {
        "method": "GET",
        "header": [],
        "url": "{{baseUrl}}/api/health"
      }
    },
    {
      "name": "Crear Empleado",
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"employeeId\": \"COUR005\",\n  \"name\": \"Pedro García\",\n  \"phone\": \"+573401234567\",\n  \"email\": \"pedro.garcia@servientrega.com\",\n  \"pin\": \"7890\"\n}"
        },
        "url": "{{baseUrl}}/api/couriers"
      }
    },
    {
      "name": "Listar Empleados",
      "request": {
        "method": "GET",
        "header": [],
        "url": "{{baseUrl}}/api/couriers"
      }
    },
    {
      "name": "Login Mensajero",
      "event": [
        {
          "listen": "test",
          "script": {
            "exec": [
              "var jsonData = pm.response.json();",
              "pm.collectionVariables.set(\"token\", jsonData.token);"
            ]
          }
        }
      ],
      "request": {
        "method": "POST",
        "header": [{"key": "Content-Type", "value": "application/json"}],
        "body": {
          "mode": "raw",
          "raw": "{\n  \"employeeId\": \"COUR001\",\n  \"pin\": \"1234\"\n}"
        },
        "url": "{{baseUrl}}/api/auth/courier/login"
      }
    },
    {
      "name": "Validar Paquete",
      "request": {
        "method": "GET",
        "header": [],
        "url": {
          "raw": "{{baseUrl}}/api/packages/validate?trackingNumber=SRV123456789",
          "query": [{"key": "trackingNumber", "value": "SRV123456789"}]
        }
      }
    }
  ]
}
```

---

## 🔐 Validaciones de PIN

- **Formato:** Solo dígitos (0-9)
- **Longitud:** 4 a 6 dígitos
- **Ejemplos válidos:** `1234`, `5678`, `123456`
- **Ejemplos inválidos:** `abc`, `12`, `1234567`

---

## 👥 Usuarios de Prueba

| Employee ID | PIN | Nombre |
|-------------|-----|--------|
| COUR001 | 1234 | Juan Pérez |
| COUR002 | 1234 | María González |

**Nota:** Usa el CRUD de empleados para crear más usuarios con PINs personalizados.

---

## 🧪 Flujo Completo de Prueba

### 1. Crear un nuevo empleado
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/couriers \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "TEST001",
    "name": "Usuario Prueba",
    "pin": "9999"
  }'
```

### 2. Hacer login con el nuevo empleado
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "TEST001",
    "pin": "9999"
  }'
```

### 3. Cambiar el PIN
```bash
curl -X PUT https://casilleroback-production.up.railway.app/api/couriers/TEST001 \
  -H "Content-Type: application/json" \
  -d '{
    "pin": "8888"
  }'
```

### 4. Probar login con nuevo PIN
```bash
curl -X POST https://casilleroback-production.up.railway.app/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "TEST001",
    "pin": "8888"
  }'
```

### 5. Eliminar el empleado de prueba
```bash
curl -X DELETE https://casilleroback-production.up.railway.app/api/couriers/TEST001
```

---

## 📝 Notas Importantes

1. **CORS:** Si usas Swagger UI y ves error de CORS, usa Postman o cURL
2. **Tokens JWT:** Expiran en 8 horas (28800000 ms)
3. **PINs:** Se hashean automáticamente con BCrypt
4. **Códigos de retiro:** Expiran en 48 horas por defecto

---

## 🆘 Soporte

- **Swagger UI:** https://casilleroback-production.up.railway.app/swagger-ui.html
- **Health Check:** https://casilleroback-production.up.railway.app/api/health
- **Documentación:** Ver archivo `API_COURIERS.md` en el repositorio
