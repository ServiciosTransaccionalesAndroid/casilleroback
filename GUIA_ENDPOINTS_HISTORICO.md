# Guía de Uso: Endpoints de Histórico

## Endpoints Implementados

### 1. Histórico de Paquete
```
GET /api/history/package/{trackingNumber}
```

**Descripción:** Obtiene el historial completo de operaciones de un paquete.

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/history/package/SRV123456789" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta:**
```json
[
  {
    "id": 1,
    "operationType": "DEPOSIT",
    "entityType": "DEPOSIT",
    "entityId": 1,
    "description": "Paquete SRV123456789 depositado en compartimento 5 del Locker Centro por Juan Pérez",
    "userType": "COURIER",
    "userId": 1,
    "userName": "Juan Pérez",
    "metadata": {
      "depositId": 1,
      "packageId": 1,
      "trackingNumber": "SRV123456789",
      "compartmentId": 5,
      "lockerId": 1
    },
    "timestamp": "2024-02-18T10:30:00"
  },
  {
    "id": 2,
    "operationType": "CODE_GENERATED",
    "entityType": "RETRIEVAL_CODE",
    "entityId": 1,
    "description": "Código de retiro RCSV3K9P generado para paquete SRV123456789 (PIN: 123456)",
    "userType": "SYSTEM",
    "userId": null,
    "userName": null,
    "metadata": {
      "codeId": 1,
      "code": "RCSV3K9P",
      "secretPin": "123456",
      "trackingNumber": "SRV123456789"
    },
    "timestamp": "2024-02-18T10:30:05"
  }
]
```

---

### 2. Histórico de Compartimento
```
GET /api/history/compartment/{compartmentId}
```

**Descripción:** Obtiene el historial de uso de un compartimento específico.

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/history/compartment/5" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 3. Histórico de Mensajero
```
GET /api/history/courier/{courierId}?startDate=2024-01-01&endDate=2024-01-31
```

**Descripción:** Obtiene todas las operaciones realizadas por un mensajero en un rango de fechas.

**Parámetros:**
- `courierId` (path): ID del mensajero
- `startDate` (query): Fecha inicio (formato: YYYY-MM-DD)
- `endDate` (query): Fecha fin (formato: YYYY-MM-DD)

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/history/courier/1?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 4. Histórico de Locker
```
GET /api/history/locker/{lockerId}?startDate=2024-01-01&endDate=2024-01-31
```

**Descripción:** Obtiene todas las operaciones de un locker en un rango de fechas.

**Parámetros:**
- `lockerId` (path): ID del locker
- `startDate` (query): Fecha inicio
- `endDate` (query): Fecha fin

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/history/locker/1?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

---

### 5. Operaciones por Rango de Fechas
```
GET /api/history/operations?startDate=2024-01-01&endDate=2024-01-31&type=DEPOSIT
```

**Descripción:** Obtiene todas las operaciones del sistema en un rango de fechas, con filtro opcional por tipo.

**Parámetros:**
- `startDate` (query, requerido): Fecha inicio
- `endDate` (query, requerido): Fecha fin
- `type` (query, opcional): Tipo de operación

**Tipos de operación disponibles:**
- `DEPOSIT` - Depósitos
- `RETRIEVAL` - Retiros
- `STATUS_CHANGE` - Cambios de estado
- `CODE_GENERATED` - Códigos generados
- `CODE_EXPIRED` - Códigos expirados
- `CODE_VALIDATED` - Códigos validados

**Ejemplo (todas las operaciones):**
```bash
curl -X GET "http://localhost:8090/api/history/operations?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

**Ejemplo (solo depósitos):**
```bash
curl -X GET "http://localhost:8090/api/history/operations?startDate=2024-02-01&endDate=2024-02-18&type=DEPOSIT" \
  -H "Authorization: Bearer {TOKEN}"
```

---

## Casos de Uso

### Auditoría de Paquete
Para rastrear todo el ciclo de vida de un paquete:
```bash
curl -X GET "http://localhost:8090/api/history/package/SRV123456789" \
  -H "Authorization: Bearer {TOKEN}"
```

### Rendimiento de Mensajero
Para ver todas las operaciones de un mensajero en el mes:
```bash
curl -X GET "http://localhost:8090/api/history/courier/1?startDate=2024-02-01&endDate=2024-02-28" \
  -H "Authorization: Bearer {TOKEN}"
```

### Uso de Compartimento
Para ver cuántas veces se ha usado un compartimento:
```bash
curl -X GET "http://localhost:8090/api/history/compartment/5" \
  -H "Authorization: Bearer {TOKEN}"
```

### Reporte Diario
Para obtener todas las operaciones del día:
```bash
curl -X GET "http://localhost:8090/api/history/operations?startDate=2024-02-18&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

---

## Estructura de Respuesta

Todos los endpoints retornan un array de objetos con la siguiente estructura:

```typescript
{
  id: number;                    // ID del log
  operationType: string;         // Tipo de operación
  entityType: string;            // Tipo de entidad
  entityId: number;              // ID de la entidad
  description: string;           // Descripción legible
  userType: string;              // COURIER, SYSTEM, CLIENT
  userId: number | null;         // ID del usuario (si aplica)
  userName: string | null;       // Nombre del usuario (si aplica)
  metadata: object;              // Datos adicionales
  timestamp: string;             // Fecha y hora ISO 8601
}
```

---

## Notas

- Todos los endpoints requieren autenticación JWT
- Las fechas deben estar en formato ISO 8601 (YYYY-MM-DD)
- Los resultados están ordenados por fecha descendente (más reciente primero)
- El campo `metadata` contiene información adicional específica de cada operación
- Los nombres de usuario se resuelven automáticamente para operaciones de tipo COURIER

---

**Documentación Swagger:** http://localhost:8090/swagger-ui.html
