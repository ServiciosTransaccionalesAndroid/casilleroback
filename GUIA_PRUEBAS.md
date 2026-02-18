# Guía de Prueba - API Casilleros Servientrega

## 🚀 Inicio Rápido

### 1. Iniciar el Sistema
```bash
cd /home/josesilva/casilleroback
docker-compose up -d --build
```

### 2. Verificar que está funcionando
```bash
# Health check
curl http://localhost:8080/api/health

# Ver logs
docker-compose logs -f backend
```

### 3. Acceder a Swagger UI
```
http://localhost:8080/swagger-ui.html
```

---

## 📋 Flujo Completo: Depósito y Retiro de Paquete

### PASO 1: Login del Mensajero

**Endpoint:** `POST /api/auth/courier/login`

```bash
curl -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{
    "employeeId": "COUR001",
    "pin": "1234"
  }'
```

**Respuesta esperada:**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "message": "Login successful"
}
```

**⚠️ IMPORTANTE:** Guarda el token para los siguientes pasos.

```bash
# Guardar token en variable
TOKEN="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

---

### PASO 2: Validar Paquete (Público - No requiere token)

**Endpoint:** `GET /api/packages/validate?trackingNumber={number}`

```bash
curl -X GET "http://localhost:8080/api/packages/validate?trackingNumber=SRV123456789"
```

**Respuesta esperada:**
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

### PASO 3: Consultar Compartimentos Disponibles (Requiere token)

**Endpoint:** `GET /api/lockers/{lockerId}/compartments`

```bash
curl -X GET http://localhost:8080/api/lockers/1/compartments \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "compartmentNumber": 1,
    "size": "SMALL",
    "status": "DISPONIBLE"
  },
  {
    "id": 5,
    "compartmentNumber": 5,
    "size": "MEDIUM",
    "status": "DISPONIBLE"
  }
]
```

**Nota:** Elige un compartimento DISPONIBLE para el depósito.

---

### PASO 4: Registrar Depósito (Requiere token)

**Endpoint:** `POST /api/deposits`

```bash
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

**Respuesta esperada:**
```json
{
  "depositId": 1,
  "retrievalCode": "A3K7M9P2",
  "expiresAt": "2024-01-17T10:30:00",
  "message": "Deposit registered successfully. Retrieval code: A3K7M9P2"
}
```

**⚠️ IMPORTANTE:** Guarda el código de retiro (retrievalCode).

**En los logs verás:**
```
╔════════════════════════════════════════════════════════════════╗
║                      SMS SENT                                  ║
╠════════════════════════════════════════════════════════════════╣
║ To: +573101234567                                              ║
║ Message: Servientrega: Tu paquete está en Locker Centro...    ║
╚════════════════════════════════════════════════════════════════╝
```

---

### PASO 5: Verificar Estado del Locker (Requiere token)

**Endpoint:** `GET /api/lockers/{lockerId}/status`

```bash
curl -X GET http://localhost:8080/api/lockers/1/status \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
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

### PASO 6: Validar Código de Retiro (Público - No requiere token)

**Endpoint:** `GET /api/retrievals/validate?code={code}`

```bash
curl -X GET "http://localhost:8080/api/retrievals/validate?code=A3K7M9P2"
```

**Respuesta esperada (código válido):**
```json
{
  "valid": true,
  "compartmentId": 5,
  "trackingNumber": "SRV123456789",
  "expiresAt": "2024-01-17T10:30:00",
  "message": "Code is valid"
}
```

---

### PASO 7: Procesar Retiro (Público - No requiere token)

**Endpoint:** `POST /api/retrievals`

```bash
curl -X POST http://localhost:8080/api/retrievals \
  -H "Content-Type: application/json" \
  -d '{
    "code": "A3K7M9P2",
    "photoUrl": "https://example.com/photo2.jpg"
  }'
```

**Respuesta esperada:**
```json
{
  "retrievalId": 1,
  "timestamp": "2024-01-16T14:20:00",
  "message": "Package retrieved successfully"
}
```

**En los logs verás:**
```
╔════════════════════════════════════════════════════════════════╗
║                      SMS SENT                                  ║
╠════════════════════════════════════════════════════════════════╣
║ To: +573101234567                                              ║
║ Message: Servientrega: Tu paquete SRV123456789 ha sido...     ║
╚════════════════════════════════════════════════════════════════╝
```

---

## 📊 Flujo de Monitoreo y Métricas

### PASO 8: Consultar Métricas del Locker (Requiere token)

**Endpoint:** `GET /api/metrics/locker/{lockerId}`

```bash
curl -X GET http://localhost:8080/api/metrics/locker/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
{
  "lockerId": 1,
  "totalCompartments": 12,
  "availableCompartments": 12,
  "occupiedCompartments": 0,
  "maintenanceCompartments": 0,
  "occupancyRate": 0.0
}
```

---

### PASO 9: Consultar Métricas Operacionales (Requiere token)

**Endpoint:** `GET /api/metrics/operational`

```bash
curl -X GET http://localhost:8080/api/metrics/operational \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
{
  "totalDeposits": 1,
  "totalRetrievals": 1,
  "pendingRetrievals": 0
}
```

---

### PASO 10: Consultar Utilización por Tamaño (Requiere token)

**Endpoint:** `GET /api/metrics/locker/{lockerId}/utilization`

```bash
curl -X GET http://localhost:8080/api/metrics/locker/1/utilization \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
{
  "SMALL": 0,
  "MEDIUM": 0,
  "LARGE": 0
}
```

---

## 🚨 Flujo de Alertas y Mantenimiento

### PASO 11: Simular Actualización de Estado (Público)

**Endpoint:** `POST /api/lockers/status-update`

```bash
curl -X POST http://localhost:8080/api/lockers/status-update \
  -H "Content-Type: application/json" \
  -d '{
    "lockerId": 1,
    "compartmentId": 5,
    "previousState": "DISPONIBLE",
    "currentState": "MANTENIMIENTO",
    "timestamp": "2024-01-15T10:30:00",
    "sensorReadings": {
      "sensor1": true,
      "sensor2": false,
      "sensor3": false,
      "sensor4": false,
      "infrared": true,
      "error": true
    }
  }'
```

**Respuesta esperada:**
```json
{
  "message": "Status updated successfully",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Esto generará una alerta automática de mantenimiento.**

---

### PASO 12: Consultar Alertas Activas (Requiere token)

**Endpoint:** `GET /api/alerts/locker/{lockerId}`

```bash
curl -X GET http://localhost:8080/api/alerts/locker/1 \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```json
[
  {
    "id": 1,
    "lockerId": 1,
    "compartmentId": 5,
    "alertType": "MAINTENANCE",
    "severity": "CRITICAL",
    "message": "Compartment in maintenance state",
    "createdAt": "2024-01-15T10:30:00"
  }
]
```

---

### PASO 13: Resolver Alerta (Requiere token)

**Endpoint:** `PUT /api/alerts/{alertId}/resolve`

```bash
curl -X PUT http://localhost:8080/api/alerts/1/resolve \
  -H "Authorization: Bearer $TOKEN"
```

**Respuesta esperada:**
```
Alert resolved successfully
```

---

## 🧪 Casos de Prueba Adicionales

### Caso 1: Intentar depósito sin token (debe fallar)

```bash
curl -X POST http://localhost:8080/api/deposits \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SRV987654321",
    "lockerId": 1,
    "compartmentId": 6,
    "courierId": 1
  }'
```

**Respuesta esperada:** HTTP 403 Forbidden

---

### Caso 2: Validar código inválido

```bash
curl -X GET "http://localhost:8080/api/retrievals/validate?code=INVALID"
```

**Respuesta esperada:**
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

### Caso 3: Validar paquete inexistente

```bash
curl -X GET "http://localhost:8080/api/packages/validate?trackingNumber=NOEXISTE"
```

**Respuesta esperada:** HTTP 404 Not Found

---

## 📝 Datos de Prueba Disponibles

### Paquetes:
- `SRV123456789` - Carlos Rodríguez (30x20x15 cm, 2.5 kg)
- `SRV987654321` - Ana Martínez (40x30x25 cm, 5.0 kg)
- `SRV555666777` - Pedro Sánchez (20x15x10 cm, 1.0 kg)

### Mensajeros:
- **COUR001** - Juan Pérez (PIN: 1234)
- **COUR002** - María González (PIN: 1234)

### Lockers:
- **Locker 1** - Centro Comercial Andino (12 compartimentos)
- **Locker 2** - Centro Comercial Unicentro (9 compartimentos)

---

## 🔧 Comandos Útiles

### Ver logs en tiempo real
```bash
docker-compose logs -f backend
```

### Reiniciar el backend
```bash
docker-compose restart backend
```

### Detener todo
```bash
docker-compose down
```

### Limpiar base de datos y reiniciar
```bash
docker-compose down -v
docker-compose up -d --build
```

### Acceder a la base de datos (Adminer)
```
http://localhost:8081
```
- Sistema: PostgreSQL
- Servidor: postgres
- Usuario: locker_user
- Contraseña: locker_pass
- Base de datos: locker_db

---

## 🎯 Script de Prueba Completo

Guarda esto en un archivo `test-api.sh`:

```bash
#!/bin/bash

echo "=== 1. Health Check ==="
curl http://localhost:8080/api/health
echo -e "\n"

echo "=== 2. Login ==="
RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}')
echo $RESPONSE
TOKEN=$(echo $RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token: $TOKEN"
echo -e "\n"

echo "=== 3. Validar Paquete ==="
curl -s "http://localhost:8080/api/packages/validate?trackingNumber=SRV123456789"
echo -e "\n"

echo "=== 4. Ver Compartimentos ==="
curl -s http://localhost:8080/api/lockers/1/compartments \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "=== 5. Registrar Depósito ==="
DEPOSIT=$(curl -s -X POST http://localhost:8080/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1
  }')
echo $DEPOSIT
CODE=$(echo $DEPOSIT | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
echo "Código de retiro: $CODE"
echo -e "\n"

echo "=== 6. Validar Código ==="
curl -s "http://localhost:8080/api/retrievals/validate?code=$CODE"
echo -e "\n"

echo "=== 7. Procesar Retiro ==="
curl -s -X POST http://localhost:8080/api/retrievals \
  -H "Content-Type: application/json" \
  -d "{\"code\": \"$CODE\"}"
echo -e "\n"

echo "=== 8. Métricas ==="
curl -s http://localhost:8080/api/metrics/operational \
  -H "Authorization: Bearer $TOKEN"
echo -e "\n"

echo "=== Prueba completada ==="
```

**Ejecutar:**
```bash
chmod +x test-api.sh
./test-api.sh
```

---

## ✅ Checklist de Pruebas

- [ ] Health check funciona
- [ ] Login genera token JWT válido
- [ ] Validación de paquetes funciona
- [ ] Consulta de compartimentos requiere autenticación
- [ ] Depósito genera código de retiro
- [ ] Notificaciones aparecen en logs
- [ ] Validación de código funciona
- [ ] Retiro libera compartimento
- [ ] Métricas se actualizan correctamente
- [ ] Alertas se generan automáticamente
- [ ] Endpoints sin token retornan 403
- [ ] Swagger UI funciona

---

## 🐛 Troubleshooting

### Error: Connection refused
```bash
# Verificar que los contenedores estén corriendo
docker-compose ps

# Ver logs
docker-compose logs backend
```

### Error: 403 Forbidden
- Verifica que estés usando el token en el header
- Verifica que el token no haya expirado (8 horas)
- Haz login nuevamente

### Error: Package not found
- Usa uno de los tracking numbers de prueba
- Verifica que Flyway haya ejecutado las migraciones

### Base de datos vacía
```bash
# Reiniciar con datos de prueba
docker-compose down -v
docker-compose up -d --build
```

---

¡Listo para probar! 🚀
