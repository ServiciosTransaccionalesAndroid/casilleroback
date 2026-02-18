# 🚀 FLUJO CORRECTO DE USO - Backend Casilleros

## ⚠️ IMPORTANTE: Diferencia entre Tracking Number y Código de Retiro

- **Tracking Number** (Número de Guía): SRV123456789
  - Se usa para VALIDAR el paquete
  - Endpoint: GET /api/packages/validate?trackingNumber=SRV123456789

- **Código de Retiro**: A3K7M9P2 (8 caracteres alfanuméricos)
  - Se GENERA automáticamente al hacer un depósito
  - Se usa para RETIRAR el paquete
  - Endpoint: GET /api/retrievals/validate?code=A3K7M9P2

---

## 📋 FLUJO COMPLETO PASO A PASO

### PASO 1: Validar Paquete (Público)
```bash
curl "http://localhost:8090/api/packages/validate?trackingNumber=SRV123456789"
```
✅ Esto verifica que el paquete existe en el sistema

---

### PASO 2: Login (Obtener Token)
```bash
curl -X POST http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "employeeId": "COUR001",
  "name": "Juan Pérez"
}
```

**⚠️ GUARDA EL TOKEN:**
```bash
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### PASO 3: Ver Compartimentos Disponibles
```bash
curl http://localhost:8090/api/lockers/1/compartments \
  -H "Authorization: Bearer $TOKEN"
```

**Elige un compartimento DISPONIBLE** (ejemplo: id=5)

---

### PASO 4: Registrar Depósito (GENERA EL CÓDIGO)
```bash
curl -X POST http://localhost:8090/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1
  }'
```

**Respuesta:**
```json
{
  "depositId": 1,
  "retrievalCode": "A3K7M9P2",  ← ESTE ES EL CÓDIGO DE RETIRO
  "expiresAt": "2024-01-19T10:30:00",
  "message": "Deposit registered successfully. Retrieval code: A3K7M9P2"
}
```

**⚠️ GUARDA EL CÓDIGO DE RETIRO:**
```bash
CODE="A3K7M9P2"
```

---

### PASO 5: Validar Código de Retiro (Público)
```bash
curl "http://localhost:8090/api/retrievals/validate?code=$CODE"
```

**Respuesta:**
```json
{
  "valid": true,
  "compartmentId": 5,
  "trackingNumber": "SRV123456789",
  "expiresAt": "2024-01-19T10:30:00",
  "message": "Code is valid"
}
```

---

### PASO 6: Procesar Retiro (Público)
```bash
curl -X POST http://localhost:8090/api/retrievals \
  -H "Content-Type: application/json" \
  -d "{\"code\": \"$CODE\"}"
```

**Respuesta:**
```json
{
  "retrievalId": 1,
  "timestamp": "2024-01-17T14:20:00",
  "message": "Package retrieved successfully"
}
```

---

## 🎯 RESUMEN DEL FLUJO

```
1. VALIDAR PAQUETE
   ↓ (trackingNumber: SRV123456789)
   
2. LOGIN MENSAJERO
   ↓ (obtener token)
   
3. VER COMPARTIMENTOS
   ↓ (elegir uno disponible)
   
4. DEPOSITAR PAQUETE
   ↓ (GENERA código: A3K7M9P2)
   
5. VALIDAR CÓDIGO
   ↓ (code: A3K7M9P2)
   
6. RETIRAR PAQUETE
   ↓ (code: A3K7M9P2)
   
✅ COMPLETADO
```

---

## ❌ ERROR COMÚN

**INCORRECTO:**
```bash
# ❌ Usar tracking number como código de retiro
curl "http://localhost:8090/api/retrievals/validate?code=SRV123456789"
```

**CORRECTO:**
```bash
# ✅ Primero depositar para obtener código
curl -X POST http://localhost:8090/api/deposits ...
# Respuesta: {"retrievalCode": "A3K7M9P2"}

# ✅ Luego usar el código generado
curl "http://localhost:8090/api/retrievals/validate?code=A3K7M9P2"
```

---

## 🧪 PRUEBA RÁPIDA COMPLETA

```bash
#!/bin/bash

# 1. Validar paquete
echo "1. Validando paquete..."
curl -s "http://localhost:8090/api/packages/validate?trackingNumber=SRV123456789"
echo -e "\n"

# 2. Login
echo "2. Login mensajero..."
LOGIN=$(curl -s -X POST http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}')
TOKEN=$(echo $LOGIN | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token obtenido: ${TOKEN:0:50}..."
echo ""

# 3. Depositar
echo "3. Registrando depósito..."
DEPOSIT=$(curl -s -X POST http://localhost:8090/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV987654321",
    "lockerId": 1,
    "compartmentId": 6,
    "courierId": 1
  }')
echo $DEPOSIT
CODE=$(echo $DEPOSIT | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
echo -e "\nCódigo generado: $CODE\n"

# 4. Validar código
echo "4. Validando código de retiro..."
curl -s "http://localhost:8090/api/retrievals/validate?code=$CODE"
echo -e "\n"

# 5. Procesar retiro
echo "5. Procesando retiro..."
curl -s -X POST http://localhost:8090/api/retrievals \
  -H "Content-Type: application/json" \
  -d "{\"code\": \"$CODE\"}"
echo -e "\n"

echo "✅ Flujo completado!"
```

---

## 📝 NOTAS IMPORTANTES

1. **Tracking Number** ≠ **Código de Retiro**
2. El código de retiro se genera AUTOMÁTICAMENTE al depositar
3. El código expira en 48 horas por defecto
4. Cada código solo se puede usar UNA vez
5. Los códigos son alfanuméricos de 8 caracteres (ej: A3K7M9P2)

---

## 🔗 SWAGGER UI (Más Fácil)

Para probar sin comandos, usa Swagger UI:
http://localhost:8090/swagger-ui.html

1. Expande "auth-controller" → POST /api/auth/courier/login
2. Click "Try it out"
3. Ingresa: {"employeeId": "COUR001", "pin": "1234"}
4. Click "Execute"
5. Copia el token
6. Click "Authorize" (arriba) y pega el token
7. Ahora puedes probar todos los endpoints

---

¿Necesitas ayuda con algún paso específico?
