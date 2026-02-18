# Guía de Implementación - APIs Backend Casilleros Servientrega

## 📋 Índice
1. [Arquitectura General](#arquitectura-general)
2. [Endpoints Implementados](#endpoints-implementados)
3. [Flujos de Integración](#flujos-de-integración)
4. [Autenticación y Seguridad](#autenticación-y-seguridad)
5. [Códigos de Error](#códigos-de-error)
6. [Ejemplos de Integración](#ejemplos-de-integración)

---

## 🏗️ Arquitectura General

### URL Base
```
Desarrollo: http://localhost:8090
Producción: https://api.servientrega.com/lockers
```

### Formato de Respuestas
Todas las respuestas son en formato JSON con estructura estándar:

**Éxito:**
```json
{
  "campo1": "valor1",
  "campo2": "valor2"
}
```

**Error:**
```json
{
  "status": 400,
  "message": "Descripción del error",
  "timestamp": "2024-01-15T10:30:00",
  "errors": {
    "campo": "mensaje de validación"
  }
}
```

---

## 📡 Endpoints Implementados

### 1. Health Check
**Propósito:** Verificar estado del servicio

```http
GET /api/health
```

**Respuesta:**
```json
{
  "service": "Servientrega Locker Backend",
  "version": "1.0.0",
  "status": "UP",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Uso:**
- Monitoreo de disponibilidad
- Health checks de balanceadores
- Verificación de despliegues

---

### 2. Validación de Paquetes
**Propósito:** Validar que un paquete existe y obtener sus datos

```http
GET /api/packages/validate?trackingNumber={number}
```

**Parámetros:**
- `trackingNumber` (string, requerido): Número de guía

**Respuesta Exitosa (200):**
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

**Respuesta Error (404):**
```json
{
  "status": 404,
  "message": "Package not found: SRV999999999",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Casos de Uso:**
- Mensajero escanea paquete antes de depositar
- Validación de dimensiones para asignación de compartimento
- Verificación de destinatario

---

### 3. Login de Mensajero
**Propósito:** Autenticar mensajero y obtener token JWT

```http
POST /api/auth/courier/login
Content-Type: application/json
```

**Body:**
```json
{
  "employeeId": "COUR001",
  "pin": "1234"
}
```

**Respuesta Exitosa (200):**
```json
{
  "token": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "employeeId": "COUR001",
  "name": "Juan Pérez",
  "message": "Login successful"
}
```

**Respuesta Error (400):**
```json
{
  "status": 400,
  "message": "Invalid credentials",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Importante:**
- El token expira en 8 horas
- Guardar token para peticiones subsecuentes
- Incluir en header: `Authorization: Bearer {token}`

---

### 4. Consultar Compartimentos
**Propósito:** Listar compartimentos de un locker y su disponibilidad

```http
GET /api/lockers/{lockerId}/compartments
Authorization: Bearer {token}
```

**Parámetros:**
- `lockerId` (long, path): ID del locker

**Respuesta Exitosa (200):**
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
    "status": "OCUPADO"
  },
  {
    "id": 9,
    "compartmentNumber": 9,
    "size": "LARGE",
    "status": "DISPONIBLE"
  }
]
```

**Estados Posibles:**
- `DISPONIBLE`: Libre para usar
- `OCUPADO`: Contiene un paquete
- `ABIERTO`: Puerta abierta
- `MANTENIMIENTO`: Fuera de servicio

**Tamaños:**
- `SMALL`: Hasta 25cm
- `MEDIUM`: Hasta 40cm
- `LARGE`: Más de 40cm

---

### 5. Registrar Depósito
**Propósito:** Registrar depósito de paquete y generar código de retiro

```http
POST /api/deposits
Authorization: Bearer {token}
Content-Type: application/json
```

**Body:**
```json
{
  "trackingNumber": "SRV123456789",
  "lockerId": 1,
  "compartmentId": 5,
  "courierId": 1,
  "photoUrl": "https://storage.example.com/photo1.jpg"
}
```

**Validaciones:**
- `trackingNumber`: Requerido, debe existir
- `lockerId`: Requerido
- `compartmentId`: Requerido, debe estar DISPONIBLE
- `courierId`: Requerido, debe estar activo
- `photoUrl`: Opcional

**Respuesta Exitosa (200):**
```json
{
  "depositId": 1,
  "retrievalCode": "A3K7M9P2",
  "expiresAt": "2024-01-17T10:30:00",
  "message": "Deposit registered successfully. Retrieval code: A3K7M9P2"
}
```

**Efectos:**
- Compartimento cambia a OCUPADO
- Paquete cambia a EN_LOCKER
- Se genera código de retiro único
- Se envían notificaciones SMS/Email al destinatario
- Se registra en historial

---

### 6. Validar Código de Retiro
**Propósito:** Verificar validez de código antes de abrir compartimento

```http
GET /api/retrievals/validate?code={code}
```

**Parámetros:**
- `code` (string, requerido): Código de retiro

**Respuesta Código Válido (200):**
```json
{
  "valid": true,
  "compartmentId": 5,
  "trackingNumber": "SRV123456789",
  "expiresAt": "2024-01-17T10:30:00",
  "message": "Code is valid"
}
```

**Respuesta Código Inválido (200):**
```json
{
  "valid": false,
  "compartmentId": null,
  "trackingNumber": null,
  "expiresAt": null,
  "message": "Invalid, expired, or already used code"
}
```

**Validaciones:**
- Código existe
- No ha sido usado
- No ha expirado (48 horas por defecto)

---

### 7. Procesar Retiro
**Propósito:** Registrar retiro de paquete

```http
POST /api/retrievals
Content-Type: application/json
```

**Body:**
```json
{
  "code": "A3K7M9P2",
  "photoUrl": "https://storage.example.com/photo2.jpg"
}
```

**Validaciones:**
- `code`: Requerido, debe ser válido
- `photoUrl`: Opcional

**Respuesta Exitosa (200):**
```json
{
  "retrievalId": 1,
  "timestamp": "2024-01-16T14:20:00",
  "message": "Package retrieved successfully"
}
```

**Efectos:**
- Código marcado como usado
- Compartimento cambia a DISPONIBLE
- Paquete cambia a ENTREGADO
- Se envía confirmación SMS/Email al destinatario
- Se registra en historial

---

### 8. Estado del Locker
**Propósito:** Consultar estado general y ocupación

```http
GET /api/lockers/{lockerId}/status
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
{
  "lockerId": 1,
  "name": "Locker Centro",
  "location": "Centro Comercial Andino",
  "status": "ACTIVE",
  "totalCompartments": 12,
  "availableCompartments": 8,
  "occupiedCompartments": 4
}
```

---

### 9. Actualizar Estado de Compartimento
**Propósito:** Recibir actualizaciones del software propietario del locker

```http
POST /api/lockers/status-update
Content-Type: application/json
```

**Body:**
```json
{
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
}
```

**Respuesta (200):**
```json
{
  "message": "Status updated successfully",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Efectos:**
- Se registra en historial
- Si estado = MANTENIMIENTO, se genera alerta automática
- Se notifica a técnicos si hay error de sensores

---

### 10. Métricas Operacionales
**Propósito:** Obtener estadísticas globales

```http
GET /api/metrics/operational
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
{
  "totalDeposits": 150,
  "totalRetrievals": 142,
  "pendingRetrievals": 8
}
```

---

### 11. Métricas del Locker
**Propósito:** Obtener métricas específicas de un locker

```http
GET /api/metrics/locker/{lockerId}
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
{
  "lockerId": 1,
  "totalCompartments": 12,
  "availableCompartments": 8,
  "occupiedCompartments": 3,
  "maintenanceCompartments": 1,
  "occupancyRate": 25.0
}
```

---

### 12. Utilización por Tamaño
**Propósito:** Ver ocupación por tipo de compartimento

```http
GET /api/metrics/locker/{lockerId}/utilization
Authorization: Bearer {token}
```

**Respuesta (200):**
```json
{
  "SMALL": 50,
  "MEDIUM": 25,
  "LARGE": 10
}
```

---

### 13. Consultar Alertas
**Propósito:** Obtener alertas activas de un locker

```http
GET /api/alerts/locker/{lockerId}
Authorization: Bearer {token}
```

**Respuesta (200):**
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

**Tipos de Alerta:**
- `MAINTENANCE`: Mantenimiento requerido
- `SENSOR_ERROR`: Error en sensores
- `DOOR_STUCK`: Puerta atascada
- `TIMEOUT`: Timeout de operación
- `SYSTEM_ERROR`: Error del sistema

**Severidades:**
- `INFO`: Informativa
- `WARNING`: Advertencia
- `CRITICAL`: Crítica

---

### 14. Resolver Alerta
**Propósito:** Marcar alerta como resuelta

```http
PUT /api/alerts/{alertId}/resolve
Authorization: Bearer {token}
```

**Respuesta (200):**
```
Alert resolved successfully
```

---

## 🔄 Flujos de Integración

### Flujo 1: Depósito de Paquete

```
1. Mensajero → Login
   POST /api/auth/courier/login
   → Obtener token

2. Mensajero → Escanear paquete
   GET /api/packages/validate?trackingNumber=XXX
   → Verificar que existe

3. Software Locker → Consultar compartimentos
   GET /api/lockers/1/compartments
   → Elegir compartimento disponible del tamaño adecuado

4. Mensajero → Depositar paquete
   POST /api/deposits
   → Obtener código de retiro

5. Backend → Notificar cliente
   (Automático: SMS + Email con código)

6. Software Locker → Reportar cambio de estado
   POST /api/lockers/status-update
   (previousState: DISPONIBLE, currentState: OCUPADO)
```

### Flujo 2: Retiro de Paquete

```
1. Cliente → Ingresar código en pantalla
   GET /api/retrievals/validate?code=XXX
   → Verificar validez

2. Software Locker → Abrir compartimento
   (Lógica local del locker)

3. Cliente → Retirar paquete
   POST /api/retrievals
   → Registrar retiro

4. Backend → Confirmar entrega
   (Automático: SMS + Email de confirmación)

5. Software Locker → Reportar cambio de estado
   POST /api/lockers/status-update
   (previousState: OCUPADO, currentState: DISPONIBLE)
```

### Flujo 3: Detección de Mantenimiento

```
1. Software Locker → Detectar problema
   (Sensores, puerta atascada, etc.)

2. Software Locker → Reportar estado
   POST /api/lockers/status-update
   (currentState: MANTENIMIENTO, sensorReadings con error)

3. Backend → Generar alerta automática
   (Automático: Alerta CRITICAL)

4. Backend → Notificar técnicos
   (Automático: Email a mantenimiento)

5. Técnico → Resolver problema
   PUT /api/alerts/{id}/resolve
```

---

## 🔐 Autenticación y Seguridad

### Obtener Token

```bash
curl -X POST http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}'
```

### Usar Token

```bash
curl -H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..." \
  http://localhost:8090/api/deposits
```

### Endpoints Públicos (sin token)
- GET /api/health
- GET /api/packages/validate
- GET /api/retrievals/validate
- POST /api/retrievals
- POST /api/lockers/status-update
- POST /api/auth/courier/login

### Endpoints Protegidos (requieren token)
- POST /api/deposits
- GET /api/lockers/{id}/status
- GET /api/lockers/{id}/compartments
- GET /api/metrics/**
- GET /api/alerts/**
- PUT /api/alerts/{id}/resolve

### Roles
- **COURIER**: Depósitos, consultas
- **ADMIN**: Acceso completo (futuro)
- **SYSTEM**: Actualizaciones de estado (futuro)

---

## ⚠️ Códigos de Error

| Código | Descripción | Solución |
|--------|-------------|----------|
| 400 | Bad Request | Verificar formato de datos |
| 401 | Unauthorized | Token inválido o expirado |
| 403 | Forbidden | Sin permisos para el recurso |
| 404 | Not Found | Recurso no existe |
| 500 | Internal Server Error | Error del servidor |

### Ejemplos de Errores

**Validación:**
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

**Autenticación:**
```json
{
  "status": 401,
  "message": "Token expired",
  "timestamp": "2024-01-15T10:30:00"
}
```

**Negocio:**
```json
{
  "status": 400,
  "message": "Package not found: SRV999999999",
  "timestamp": "2024-01-15T10:30:00"
}
```

---

## 💻 Ejemplos de Integración

### JavaScript/Node.js

```javascript
const axios = require('axios');

const BASE_URL = 'http://localhost:8090';

// Login
async function login() {
  const response = await axios.post(`${BASE_URL}/api/auth/courier/login`, {
    employeeId: 'COUR001',
    pin: '1234'
  });
  return response.data.token;
}

// Validar paquete
async function validatePackage(trackingNumber) {
  const response = await axios.get(
    `${BASE_URL}/api/packages/validate?trackingNumber=${trackingNumber}`
  );
  return response.data;
}

// Registrar depósito
async function registerDeposit(token, data) {
  const response = await axios.post(
    `${BASE_URL}/api/deposits`,
    data,
    {
      headers: { 'Authorization': `Bearer ${token}` }
    }
  );
  return response.data;
}

// Uso
(async () => {
  try {
    const token = await login();
    const pkg = await validatePackage('SRV123456789');
    const deposit = await registerDeposit(token, {
      trackingNumber: 'SRV123456789',
      lockerId: 1,
      compartmentId: 5,
      courierId: 1
    });
    console.log('Código de retiro:', deposit.retrievalCode);
  } catch (error) {
    console.error('Error:', error.response.data);
  }
})();
```

### Python

```python
import requests

BASE_URL = 'http://localhost:8090'

# Login
def login():
    response = requests.post(f'{BASE_URL}/api/auth/courier/login', json={
        'employeeId': 'COUR001',
        'pin': '1234'
    })
    return response.json()['token']

# Validar paquete
def validate_package(tracking_number):
    response = requests.get(
        f'{BASE_URL}/api/packages/validate',
        params={'trackingNumber': tracking_number}
    )
    return response.json()

# Registrar depósito
def register_deposit(token, data):
    response = requests.post(
        f'{BASE_URL}/api/deposits',
        json=data,
        headers={'Authorization': f'Bearer {token}'}
    )
    return response.json()

# Uso
if __name__ == '__main__':
    try:
        token = login()
        pkg = validate_package('SRV123456789')
        deposit = register_deposit(token, {
            'trackingNumber': 'SRV123456789',
            'lockerId': 1,
            'compartmentId': 5,
            'courierId': 1
        })
        print(f"Código de retiro: {deposit['retrievalCode']}")
    except Exception as e:
        print(f"Error: {e}")
```

### Java

```java
import java.net.http.*;
import java.net.URI;
import com.google.gson.Gson;

public class LockerClient {
    private static final String BASE_URL = "http://localhost:8090";
    private static final HttpClient client = HttpClient.newHttpClient();
    private static final Gson gson = new Gson();
    
    public static String login() throws Exception {
        String json = "{\"employeeId\":\"COUR001\",\"pin\":\"1234\"}";
        HttpRequest request = HttpRequest.newBuilder()
            .uri(URI.create(BASE_URL + "/api/auth/courier/login"))
            .header("Content-Type", "application/json")
            .POST(HttpRequest.BodyPublishers.ofString(json))
            .build();
        
        HttpResponse<String> response = client.send(request, 
            HttpResponse.BodyHandlers.ofString());
        
        LoginResponse loginResponse = gson.fromJson(
            response.body(), LoginResponse.class);
        return loginResponse.token;
    }
    
    public static void main(String[] args) throws Exception {
        String token = login();
        System.out.println("Token: " + token);
    }
}
```

### cURL (Bash Script)

```bash
#!/bin/bash

BASE_URL="http://localhost:8090"

# Login
TOKEN=$(curl -s -X POST $BASE_URL/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId": "COUR001", "pin": "1234"}' \
  | grep -o '"token":"[^"]*' | cut -d'"' -f4)

echo "Token: $TOKEN"

# Validar paquete
curl -s "$BASE_URL/api/packages/validate?trackingNumber=SRV123456789"

# Registrar depósito
DEPOSIT=$(curl -s -X POST $BASE_URL/api/deposits \
  -H "Content-Type: application/json" \
  -H "Authorization: Bearer $TOKEN" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentId": 5,
    "courierId": 1
  }')

CODE=$(echo $DEPOSIT | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
echo "Código de retiro: $CODE"
```

---

## 📚 Recursos Adicionales

### Documentación Interactiva
- **Swagger UI:** http://localhost:8090/swagger-ui.html
- **OpenAPI JSON:** http://localhost:8090/v3/api-docs

### Base de Datos
- **Adminer:** http://localhost:8081
- **Credenciales:** locker_user / locker_pass

### Datos de Prueba
- **Mensajeros:** COUR001, COUR002 (PIN: 1234)
- **Paquetes:** SRV123456789, SRV987654321, SRV555666777
- **Lockers:** 1 (Centro), 2 (Norte)

### Logs
```bash
docker-compose logs -f backend
```

---

## 🎯 Mejores Prácticas

1. **Siempre validar paquete antes de depositar**
2. **Guardar token y reutilizar (válido 8 horas)**
3. **Manejar errores 401 renovando token**
4. **Validar código antes de abrir compartimento**
5. **Reportar cambios de estado inmediatamente**
6. **Implementar retry logic para fallos de red**
7. **Guardar logs de todas las operaciones**
8. **Usar HTTPS en producción**

---

¿Necesitas más detalles sobre algún endpoint específico?
