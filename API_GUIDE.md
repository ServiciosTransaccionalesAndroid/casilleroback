# Guía de Implementación - API Casilleros Servientrega

## URL Base
```
http://localhost:8090
```

## Swagger UI
```
http://localhost:8090/swagger-ui.html
```

---

## 1. Health Check

### GET /api/health
Verifica el estado del servicio.

**Autenticación:** No requerida

**Ejemplo:**
```bash
curl http://localhost:8090/api/health
```

**Respuesta:**
```json
{
  "service": "Servientrega Locker Backend",
  "version": "1.0.0",
  "status": "UP",
  "timestamp": "2026-02-18T19:02:56.367522342"
}
```

---

## 2. Autenticación

### POST /api/auth/courier/login
Autentica un mensajero y retorna un token JWT.

**Autenticación:** No requerida

**Body:**
```json
{
  "employeeId": "COUR001",
  "pin": "1234"
}
```

**Ejemplo:**
```bash
curl -X POST "http://localhost:8090/api/auth/courier/login" \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR001",
    "pin": "1234"
  }'
```

**Respuesta:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiJ9...",
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "message": "Login successful"
}
```

**Mensajeros disponibles:**
- `COUR001` - Juan Pérez (PIN: 1234)
- `COUR002` - María González (PIN: 1234)

---

## 3. Validación de Paquetes

### GET /api/packages/validate?trackingNumber={number}
Valida un paquete por número de guía.

**Autenticación:** JWT requerido

**Parámetros:**
- `trackingNumber` (query): Número de guía del paquete

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/packages/validate?trackingNumber=SRV123456789" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta:**
```json
{
  "trackingNumber": "SRV123456789",
  "recipientName": "Carlos Rodríguez",
  "recipientPhone": "+573101234567",
  "recipientEmail": "carlos.rodriguez@email.com",
  "dimensions": {
    "width": 30.0,
    "height": 20.0,
    "depth": 15.0,
    "weight": 2.5
  },
  "status": "EN_TRANSITO"
}
```

**Paquetes disponibles:**
- `SRV123456789` - Carlos Rodríguez
- `SRV987654321` - Ana Martínez
- `SRV555666777` - Pedro Sánchez

---

## 4. Depósito de Paquetes

### POST /api/deposits
Registra el depósito de un paquete en un locker y genera código de retiro + PIN secreto.

**Autenticación:** JWT requerido

**Body:**
```json
{
  "trackingNumber": "SRV123456789",
  "lockerId": 1,
  "compartmentId": 5,
  "courierId": 1,
  "photoUrl": "https://example.com/photo.jpg"
}
```

**Ejemplo:**
```bash
curl -X POST "http://localhost:8090/api/deposits" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
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
  "retrievalCode": "ZHME88F2",
  "secretPin": "123456",
  "expiresAt": "2026-02-20T18:54:33.631460568",
  "message": "Deposit registered successfully. Retrieval code: ZHME88F2 - Secret PIN: 123456"
}
```

**Notas:**
- El código de retiro es alfanumérico de 8 caracteres
- El PIN secreto es numérico de 6 dígitos
- El código expira en 48 horas por defecto
- El compartimento queda en estado OCUPADO
- El paquete cambia a estado EN_LOCKER

**Compartimentos disponibles (Locker 1):**
- 1-4: SMALL
- 5-8: MEDIUM
- 9-12: LARGE

---

## 5. Generación de Código QR

### GET /api/qr/retrieval-code/{code}
Genera un código QR con el código de retiro.

**Autenticación:** No requerida

**Parámetros:**
- `code` (path): Código de retiro
- `width` (query, opcional): Ancho en píxeles (default: 300)
- `height` (query, opcional): Alto en píxeles (default: 300)

**Ejemplo:**
```bash
# Descargar QR
curl "http://localhost:8090/api/qr/retrieval-code/ZHME88F2" --output qr.png

# QR personalizado
curl "http://localhost:8090/api/qr/retrieval-code/ZHME88F2?width=500&height=500" --output qr.png

# Ver en navegador
http://localhost:8090/api/qr/retrieval-code/ZHME88F2
```

**Respuesta:** Imagen PNG

---

## 6. Validación de Código de Retiro

### GET /api/retrievals/validate?code={code}
Valida un código de retiro sin marcarlo como usado.

**Autenticación:** JWT requerido

**Parámetros:**
- `code` (query): Código de retiro

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/retrievals/validate?code=ZHME88F2" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta (válido):**
```json
{
  "valid": true,
  "compartmentId": 5,
  "trackingNumber": "SRV123456789",
  "expiresAt": "2026-02-20T18:54:33.631460",
  "message": "Code is valid"
}
```

**Respuesta (inválido):**
```json
{
  "valid": false,
  "compartmentId": null,
  "trackingNumber": null,
  "expiresAt": null,
  "message": "Invalid, expired, or already used code"
}
```

---

## 7. Retiro de Paquetes

### POST /api/retrievals
Registra el retiro de un paquete usando el código de retiro.

**Autenticación:** JWT requerido

**Body:**
```json
{
  "code": "ZHME88F2"
}
```

**Ejemplo:**
```bash
curl -X POST "http://localhost:8090/api/retrievals" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer {TOKEN}" \
  -d '{
    "code": "ZHME88F2"
  }'
```

**Respuesta:**
```json
{
  "retrievalId": 1,
  "timestamp": "2026-02-18T19:17:11.606238722",
  "message": "Package retrieved successfully"
}
```

**Notas:**
- El código se marca como usado
- El compartimento vuelve a estado DISPONIBLE
- El paquete cambia a estado ENTREGADO
- El código no puede reutilizarse

---

## Flujo Completo de Uso

### 1. Login del Mensajero
```bash
TOKEN=$(curl -s -X POST "http://localhost:8090/api/auth/courier/login" \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}' | jq -r '.token')
```

### 2. Validar Paquete
```bash
curl -X GET "http://localhost:8090/api/packages/validate?trackingNumber=SRV123456789" \
  -H "Authorization: Bearer $TOKEN"
```

### 3. Depositar Paquete
```bash
DEPOSIT=$(curl -s -X POST "http://localhost:8090/api/deposits" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1
  }')

CODE=$(echo $DEPOSIT | jq -r '.retrievalCode')
PIN=$(echo $DEPOSIT | jq -r '.secretPin')
```

### 4. Generar QR
```bash
curl "http://localhost:8090/api/qr/retrieval-code/$CODE" --output qr.png
```

### 5. Cliente Valida Código
```bash
curl -X GET "http://localhost:8090/api/retrievals/validate?code=$CODE" \
  -H "Authorization: Bearer $TOKEN"
```

### 6. Cliente Retira Paquete
```bash
curl -X POST "http://localhost:8090/api/retrievals" \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d "{\"code\": \"$CODE\"}"
```

---

## Códigos de Estado HTTP

- `200 OK` - Operación exitosa
- `400 Bad Request` - Datos inválidos o código ya usado
- `401 Unauthorized` - Token JWT inválido o ausente
- `404 Not Found` - Recurso no encontrado
- `500 Internal Server Error` - Error del servidor

---

## Notas de Seguridad

1. **JWT Token:** Incluir en header `Authorization: Bearer {token}`
2. **Expiración:** Los tokens JWT expiran en 8 horas
3. **Códigos de retiro:** Expiran en 48 horas
4. **PIN Secreto:** Se genera automáticamente (6 dígitos)
5. **Uso único:** Los códigos no pueden reutilizarse

---

## Herramientas de Administración

### Adminer (Base de Datos)
```
http://localhost:8081
```
**Credenciales:**
- Sistema: PostgreSQL
- Servidor: postgres
- Usuario: locker_user
- Contraseña: locker_pass
- Base de datos: locker_db

### Swagger UI (Documentación Interactiva)
```
http://localhost:8090/swagger-ui.html
```

---

## Datos de Prueba

### Mensajeros
| ID | Nombre | PIN |
|---|---|---|
| COUR001 | Juan Pérez | 1234 |
| COUR002 | María González | 1234 |

### Paquetes
| Guía | Destinatario | Estado |
|---|---|---|
| SRV123456789 | Carlos Rodríguez | EN_TRANSITO |
| SRV987654321 | Ana Martínez | EN_TRANSITO |
| SRV555666777 | Pedro Sánchez | EN_TRANSITO |

### Lockers
| ID | Nombre | Compartimentos |
|---|---|---|
| 1 | Locker Centro | 12 (4 SMALL, 4 MEDIUM, 4 LARGE) |
| 2 | Locker Norte | 9 (3 SMALL, 3 MEDIUM, 3 LARGE) |
