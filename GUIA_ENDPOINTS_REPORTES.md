# Guía de Uso: Endpoints de Reportes

## Endpoints Implementados

### 1. Reporte de Depósitos
```
GET /api/reports/deposits?startDate=2024-01-01&endDate=2024-01-31&lockerId=1
```

**Descripción:** Obtiene estadísticas de depósitos por día en un período.

**Parámetros:**
- `startDate` (requerido): Fecha inicio (YYYY-MM-DD)
- `endDate` (requerido): Fecha fin (YYYY-MM-DD)
- `lockerId` (opcional): Filtrar por locker específico

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/reports/deposits?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta:**
```json
{
  "reportType": "DEPOSITS_BY_PERIOD",
  "startDate": "2024-02-01",
  "endDate": "2024-02-18",
  "data": {
    "daily": [
      {"date": "2024-02-01", "count": 15},
      {"date": "2024-02-02", "count": 23},
      {"date": "2024-02-03", "count": 18}
    ]
  },
  "summary": {
    "totalDeposits": 215,
    "averagePerDay": 11.94,
    "daysWithData": 18,
    "peakDay": "2024-02-15",
    "peakDayCount": 28
  },
  "generatedAt": "2024-02-18T19:30:00"
}
```

---

### 2. Reporte de Retiros
```
GET /api/reports/retrievals?startDate=2024-01-01&endDate=2024-01-31&lockerId=1
```

**Descripción:** Obtiene estadísticas de retiros por día en un período.

**Parámetros:**
- `startDate` (requerido): Fecha inicio
- `endDate` (requerido): Fecha fin
- `lockerId` (opcional): Filtrar por locker

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/reports/retrievals?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta:**
```json
{
  "reportType": "RETRIEVALS_BY_PERIOD",
  "startDate": "2024-02-01",
  "endDate": "2024-02-18",
  "data": {
    "daily": [
      {"date": "2024-02-01", "count": 12},
      {"date": "2024-02-02", "count": 20},
      {"date": "2024-02-03", "count": 16}
    ]
  },
  "summary": {
    "totalRetrievals": 198,
    "averagePerDay": 11.0,
    "daysWithData": 18,
    "peakDay": "2024-02-16",
    "peakDayCount": 25
  },
  "generatedAt": "2024-02-18T19:30:00"
}
```

---

### 3. Reporte de Ocupación
```
GET /api/reports/occupancy?lockerId=1
```

**Descripción:** Obtiene el estado actual de ocupación de un locker.

**Parámetros:**
- `lockerId` (requerido): ID del locker

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/reports/occupancy?lockerId=1" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta:**
```json
{
  "reportType": "OCCUPANCY_RATE",
  "startDate": "2024-02-18",
  "endDate": "2024-02-18",
  "data": {
    "bySize": [
      {
        "size": "SMALL",
        "total": 4,
        "occupied": 2,
        "available": 2
      },
      {
        "size": "MEDIUM",
        "total": 4,
        "occupied": 3,
        "available": 1
      },
      {
        "size": "LARGE",
        "total": 4,
        "occupied": 1,
        "available": 3
      }
    ]
  },
  "summary": {
    "totalCompartments": 12,
    "occupied": 6,
    "available": 6,
    "occupancyRate": 50.0
  },
  "generatedAt": "2024-02-18T19:30:00"
}
```

---

### 4. Reporte de Códigos Expirados
```
GET /api/reports/expired-codes?startDate=2024-01-01&endDate=2024-01-31
```

**Descripción:** Lista códigos de retiro que expiraron sin ser usados.

**Parámetros:**
- `startDate` (requerido): Fecha inicio
- `endDate` (requerido): Fecha fin

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/reports/expired-codes?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

**Respuesta:**
```json
{
  "reportType": "EXPIRED_CODES",
  "startDate": "2024-02-01",
  "endDate": "2024-02-18",
  "data": {
    "expiredCodes": [
      {
        "code": "RCSV3K9P",
        "expiresAt": "2024-02-15T10:30:00",
        "trackingNumber": "SRV123456789",
        "recipientName": "Carlos Rodríguez"
      },
      {
        "code": "RCSVX7M2",
        "expiresAt": "2024-02-16T14:20:00",
        "trackingNumber": "SRV987654321",
        "recipientName": "Ana Martínez"
      }
    ]
  },
  "summary": {
    "totalExpired": 2
  },
  "generatedAt": "2024-02-18T19:30:00"
}
```

---

## Casos de Uso

### Dashboard Operacional
Obtener métricas del día actual:
```bash
TODAY=$(date +%Y-%m-%d)

# Depósitos del día
curl "http://localhost:8090/api/reports/deposits?startDate=$TODAY&endDate=$TODAY" \
  -H "Authorization: Bearer {TOKEN}"

# Retiros del día
curl "http://localhost:8090/api/reports/retrievals?startDate=$TODAY&endDate=$TODAY" \
  -H "Authorization: Bearer {TOKEN}"

# Ocupación actual
curl "http://localhost:8090/api/reports/occupancy?lockerId=1" \
  -H "Authorization: Bearer {TOKEN}"
```

### Reporte Mensual
```bash
curl "http://localhost:8090/api/reports/deposits?startDate=2024-02-01&endDate=2024-02-29" \
  -H "Authorization: Bearer {TOKEN}"
```

### Análisis de Eficiencia
Comparar depósitos vs retiros:
```bash
# Depósitos
curl "http://localhost:8090/api/reports/deposits?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"

# Retiros
curl "http://localhost:8090/api/reports/retrievals?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

### Monitoreo de Códigos Expirados
```bash
curl "http://localhost:8090/api/reports/expired-codes?startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}"
```

---

## Estructura de Respuesta

Todos los reportes siguen la misma estructura:

```typescript
{
  reportType: string;           // Tipo de reporte
  startDate: string;            // Fecha inicio (YYYY-MM-DD)
  endDate: string;              // Fecha fin (YYYY-MM-DD)
  data: {                       // Datos detallados del reporte
    [key: string]: any;
  };
  summary: {                    // Resumen con métricas clave
    [key: string]: number;
  };
  generatedAt: string;          // Timestamp de generación (ISO 8601)
}
```

---

## Notas

- Todos los endpoints requieren autenticación JWT
- Las fechas deben estar en formato ISO 8601 (YYYY-MM-DD)
- Los reportes se generan en tiempo real
- El campo `summary` contiene métricas calculadas automáticamente
- Los datos diarios están ordenados cronológicamente

---

**Documentación Swagger:** http://localhost:8090/swagger-ui.html
