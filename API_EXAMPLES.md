# Ejemplos de Uso - API REST Casilleros Servientrega

## Base URL
```
http://localhost:8080
```

## Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 1. Health Check

### Request
```bash
curl http://localhost:8080/api/health
```

### Response
```json
{
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00",
  "service": "Servientrega Locker Backend",
  "version": "1.0.0"
}
```

---

## 2. Validar Paquete

### Request
```bash
curl -X GET "http://localhost:8080/api/packages/validate?trackingNumber=SRV123456789"
```

### Response
```json
{
  "trackingNumber": "SRV123456789",
  "recipientName": "Carlos Rodríguez",
  "recipientPhone": "+573101234567",
  "recipientEmail": "carlos.rodriguez@email.com",
  "dimensions": {
    "width": 30.00,
    "height": 20.00,
    "depth": 15.00,
    "weight": 2.50
  },
  "status": "EN_TRANSITO"
}
```

---

## 3. Login de Mensajero

### Request
```bash
curl -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR001",
    "pin": "1234"
  }'
```

### Response
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJDT1VSMDAxIiwiaWF0IjoxNzA1MzI2MDAwLCJleHAiOjE3MDUzNTQ4MDB9.signature",
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "message": "Login successful"
}
```

**Nota:** Guarda el token para usarlo en las siguientes peticiones.

---

## 3.1. Uso del Token JWT

Todas las peticiones protegidas requieren el token en el header:

```bash
curl -H "Authorization: Bearer {TOKEN_AQUI}" \
  http://localhost:8080/api/deposits
```

---

## 4. Registrar Depósito (Requiere autenticación)

### Request
```bash
# Primero obtener el token del login
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."

curl -X POST http://localhost:8080/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1,
    "photoUrl": "https://example.com/photo1.jpg"
  }'
```

### Response
```json
{
  "depositId": 1,
  "retrievalCode": "A3K7M9P2",
  "expiresAt": "2024-01-17T10:30:00",
  "message": "Deposit registered successfully. Retrieval code: A3K7M9P2"
}
```

---

## 5. Validar Código de Retiro

### Request
```bash
curl -X GET "http://localhost:8080/api/retrievals/validate?code=A3K7M9P2"
```

### Response (Código válido)
```json
{
  "valid": true,
  "compartmentId": 5,
  "trackingNumber": "SRV123456789",
  "expiresAt": "2024-01-17T10:30:00",
  "message": "Code is valid"
}
```

### Response (Código inválido)
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

## 6. Procesar Retiro

### Request
```bash
curl -X POST http://localhost:8080/api/retrievals \
  -H "Content-Type: application/json" \
  -d '{
    "code": "A3K7M9P2",
    "photoUrl": "https://example.com/photo2.jpg"
  }'
```

### Response
```json
{
  "retrievalId": 1,
  "timestamp": "2024-01-16T14:20:00",
  "message": "Package retrieved successfully"
}
```

---

## 7. Actualizar Estado de Compartimento

### Request
```bash
curl -X POST http://localhost:8080/api/lockers/status-update \
  -H "Content-Type: application/json" \
  -d '{
    "lockerId": 1,
    "compartmentId": 5,
    "previousState": "DISPONIBLE",
    "currentState": "OCUPADO",
    "timestamp": "2024-01-15T10:30:00",
    "sensorReadings": {
      "sensor1": true,
      "sensor2": true,
      "sensor3": true,
      "sensor4": true,
      "infrared": false
    }
  }'
```

### Response
```json
{
  "message": "Status updated successfully",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 8. Consultar Estado del Locker

### Request
```bash
curl -X GET http://localhost:8080/api/lockers/1/status
```

### Response
```json
{
  "lockerId": 1,
  "name": "Locker Centro",
  "location": "Centro Comercial Andino",
  "status": "ACTIVE",
  "totalCompartments": 12,
  "availableCompartments": 11,
  "occupiedCompartments": 1
}
```

---

## 9. Listar Compartimentos

### Request
```bash
curl -X GET http://localhost:8080/api/lockers/1/compartments
```

### Response
```json
[
  {
    "id": 1,
    "compartmentNumber": 1,
    "size": "SMALL",
    "status": "DISPONIBLE"
  },
  {
    "id": 2,
    "compartmentNumber": 2,
    "size": "SMALL",
    "status": "DISPONIBLE"
  },
  {
    "id": 5,
    "compartmentNumber": 5,
    "size": "MEDIUM",
    "status": "OCUPADO"
  }
]
```

---

## Flujo Completo: Depósito y Retiro

### Paso 1: Login del mensajero
```bash
curl -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}'
```

### Paso 2: Validar paquete
```bash
curl -X GET "http://localhost:8080/api/packages/validate?trackingNumber=SRV123456789"
```

### Paso 3: Registrar depósito
```bash
curl -X POST http://localhost:8080/api/deposits \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1
  }'
```
**Respuesta:** Código de retiro `A3K7M9P2`

### Paso 4: Cliente valida código
```bash
curl -X GET "http://localhost:8080/api/retrievals/validate?code=A3K7M9P2"
```

### Paso 5: Cliente retira paquete
```bash
curl -X POST http://localhost:8080/api/retrievals \
  -H "Content-Type: application/json" \
  -d '{"code": "A3K7M9P2"}'
```

---

## Manejo de Errores

### Error de validación
```json
{
  "status": 400,
  "message": "Validation failed",
  "timestamp": "2024-01-15T10:30:00",
  "errors": {
    "trackingNumber": "Tracking number is required",
    "courierId": "Courier ID is required"
  }
}
```

### Error de negocio
```json
{
  "status": 400,
  "message": "Package not found: SRV999999999",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## Datos de Prueba

### Paquetes disponibles:
- `SRV123456789` - Carlos Rodríguez (30x20x15 cm, 2.5 kg)
- `SRV987654321` - Ana Martínez (40x30x25 cm, 5.0 kg)
- `SRV555666777` - Pedro Sánchez (20x15x10 cm, 1.0 kg)

### Mensajeros:
- `COUR001` - Juan Pérez (PIN: 1234)
- `COUR002` - María González (PIN: 1234)

### Lockers:
- Locker 1 - Centro Comercial Andino (12 compartimentos)
- Locker 2 - Centro Comercial Unicentro (9 compartimentos)
