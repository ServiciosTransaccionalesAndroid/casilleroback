# Guía de Reportes e Históricos - Sistema de Casilleros Servientrega

## 📊 Índice
1. [Reportes Operacionales](#reportes-operacionales)
2. [Históricos y Auditoría](#históricos-y-auditoría)
3. [Exportación de Datos](#exportación-de-datos)
4. [Ejemplos de Uso](#ejemplos-de-uso)

---

## 📈 Reportes Operacionales

### 1. Reporte de Ocupación (OCCUPANCY_RATE)

**Descripción:** Muestra el estado actual de ocupación de un locker específico.

**Endpoint:** `GET /api/reports/occupancy?lockerId={id}`

**Cálculos:**
- **Total Compartimentos:** Cuenta total de compartimentos en el locker
- **Ocupados:** Compartimentos con estado `OCUPADO`
- **Disponibles:** Compartimentos con estado `DISPONIBLE`
- **Tasa de Ocupación (%):** `(Ocupados / Total) * 100`

**Datos Detallados:**
- Desglose por tamaño (SMALL, MEDIUM, LARGE)
- Total, disponibles y ocupados por cada tamaño

**Ejemplo de Respuesta:**
```json
{
  "reportType": "OCCUPANCY_RATE",
  "summary": {
    "totalCompartments": 12,
    "occupied": 2,
    "available": 10,
    "occupancyRate": 16.67
  },
  "data": {
    "bySize": [
      {"size": "SMALL", "total": 4, "available": 4, "occupied": 0},
      {"size": "MEDIUM", "total": 4, "available": 3, "occupied": 1},
      {"size": "LARGE", "total": 4, "available": 3, "occupied": 1}
    ]
  }
}
```

---

### 2. Reporte de Depósitos (DEPOSITS_BY_PERIOD)

**Descripción:** Estadísticas de depósitos realizados en un período de tiempo.

**Endpoint:** `GET /api/reports/deposits?startDate={fecha}&endDate={fecha}&lockerId={id}`

**Cálculos:**
- **Total Depósitos:** Suma de todos los depósitos en el período
- **Promedio por Día:** `Total Depósitos / Días con Datos`
- **Días con Datos:** Cantidad de días únicos con al menos un depósito
- **Día Pico:** Fecha con mayor cantidad de depósitos
- **Depósitos Día Pico:** Cantidad de depósitos en el día pico

**Datos Detallados:**
- Depósitos agrupados por fecha (daily)
- Fecha y cantidad por cada día

**Ejemplo de Respuesta:**
```json
{
  "reportType": "DEPOSITS_BY_PERIOD",
  "summary": {
    "totalDeposits": 45,
    "averagePerDay": 3.75,
    "daysWithData": 12,
    "peakDay": "2026-02-15",
    "peakDayCount": 8
  },
  "data": {
    "daily": [
      {"date": "2026-02-01", "count": 3},
      {"date": "2026-02-02", "count": 5}
    ]
  }
}
```

---

### 3. Reporte de Retiros (RETRIEVALS_BY_PERIOD)

**Descripción:** Estadísticas de retiros realizados en un período.

**Endpoint:** `GET /api/reports/retrievals?startDate={fecha}&endDate={fecha}&lockerId={id}`

**Cálculos:**
- **Total Retiros:** Suma de todos los retiros en el período
- **Promedio por Día:** `Total Retiros / Días con Datos`
- **Días con Datos:** Cantidad de días únicos con al menos un retiro
- **Día Pico:** Fecha con mayor cantidad de retiros

**Datos Detallados:**
- Retiros agrupados por fecha
- Fecha y cantidad por cada día

---

### 4. Reporte de Uso de Compartimentos (COMPARTMENT_USAGE)

**Descripción:** Estadísticas de uso de cada compartimento en un locker.

**Endpoint:** `GET /api/reports/compartment-usage?lockerId={id}`

**Cálculos:**
- **Total Uso:** Suma de veces que se han usado todos los compartimentos
- **Total Compartimentos:** Cantidad de compartimentos en el locker
- **Promedio por Compartimento:** `Total Uso / Total Compartimentos`

**Datos Detallados:**
- Lista de compartimentos con:
  - ID y número de compartimento
  - Tamaño (SMALL, MEDIUM, LARGE)
  - Estado actual (DISPONIBLE, OCUPADO, MANTENIMIENTO)
  - Veces usado (contador de depósitos históricos)

**Ejemplo de Respuesta:**
```json
{
  "reportType": "COMPARTMENT_USAGE",
  "summary": {
    "totalCompartments": 12,
    "totalUsage": 45,
    "averageUsagePerCompartment": 3.75
  },
  "data": {
    "compartments": [
      {
        "compartmentId": 1,
        "compartmentNumber": 1,
        "size": "SMALL",
        "status": "DISPONIBLE",
        "timesUsed": 5
      }
    ]
  }
}
```

---

### 5. Reporte de Paquetes Activos (ACTIVE_PACKAGES)

**Descripción:** Lista de paquetes actualmente almacenados en un locker.

**Endpoint:** `GET /api/reports/active-packages?lockerId={id}`

**Cálculos:**
- **Total Activos:** Cantidad de paquetes con estado `EN_LOCKER`

**Datos Detallados:**
- Tracking number
- Nombre del destinatario
- Código de retiro
- Número de compartimento
- Fecha de depósito

**Ejemplo de Respuesta:**
```json
{
  "reportType": "ACTIVE_PACKAGES",
  "summary": {
    "totalActive": 2
  },
  "data": {
    "packages": [
      {
        "trackingNumber": "SRV123456789",
        "recipientName": "Carlos Rodríguez",
        "retrievalCode": "RCSVBNYV",
        "compartmentNumber": 5,
        "depositedAt": "2026-02-28T10:30:00"
      }
    ]
  }
}
```

---

### 6. Reporte de Códigos Expirados (EXPIRED_CODES)

**Descripción:** Códigos de retiro que expiraron sin ser usados.

**Endpoint:** `GET /api/reports/expired-codes?startDate={fecha}&endDate={fecha}`

**Cálculos:**
- **Total Expirados:** Cantidad de códigos expirados sin usar
- **Tasa de Expiración (%):** `(Expirados / Total Generados) * 100`

**Datos Detallados:**
- Código de retiro
- Tracking number del paquete
- Fecha de generación
- Fecha de expiración
- Locker y compartimento

---

### 7. Reporte de Desempeño de Mensajeros (COURIER_PERFORMANCE)

**Descripción:** Estadísticas de desempeño de mensajeros.

**Endpoint:** `GET /api/reports/courier-performance?courierId={id}&startDate={fecha}&endDate={fecha}`

**Cálculos:**
- **Total Depósitos:** Depósitos realizados por el mensajero
- **Promedio por Día:** Depósitos promedio diarios
- **Locker Más Usado:** Locker donde más depositó
- **Tiempo Promedio por Depósito:** Tiempo promedio de operación

**Datos Detallados:**
- Depósitos por día
- Depósitos por locker
- Distribución por tamaño de compartimento

---

### 8. Resumen Diario (DAILY_SUMMARY)

**Descripción:** Resumen consolidado de todas las operaciones de un día.

**Endpoint:** `GET /api/reports/daily-summary?date={fecha}`

**Cálculos:**
- **Total Depósitos:** Depósitos del día
- **Total Retiros:** Retiros del día
- **Tasa de Retiro (%):** `(Retiros / Depósitos) * 100`
- **Lockers Activos:** Cantidad de lockers con actividad
- **Mensajeros Activos:** Cantidad de mensajeros que trabajaron
- **Tiempo Promedio en Locker:** Promedio de horas entre depósito y retiro

**Datos Detallados:**
- Actividad por hora del día
- Top 5 lockers más usados
- Top 5 mensajeros más activos

---

## 🕐 Históricos y Auditoría

### 1. Histórico de Paquete

**Endpoint:** `GET /api/history/package/{trackingNumber}`

**Descripción:** Todas las operaciones relacionadas con un paquete específico.

**Eventos Registrados:**
- Depósito en locker
- Generación de código de retiro
- Cambios de estado (EN_TRANSITO → EN_LOCKER → ENTREGADO)
- Retiro del paquete
- Intentos de retiro fallidos

**Ejemplo:**
```bash
curl "http://localhost:8090/api/history/package/SRV123456789" \
  -H "Authorization: Bearer $TOKEN"
```

---

### 2. Histórico de Compartimento

**Endpoint:** `GET /api/history/compartment/{compartmentId}`

**Descripción:** Todas las operaciones en un compartimento específico.

**Eventos Registrados:**
- Depósitos realizados
- Retiros realizados
- Cambios de estado del compartimento
- Mantenimientos
- Alertas generadas

---

### 3. Histórico de Mensajero

**Endpoint:** `GET /api/history/courier/{courierId}?startDate={fecha}&endDate={fecha}`

**Descripción:** Todas las operaciones realizadas por un mensajero.

**Eventos Registrados:**
- Depósitos realizados
- Lockers utilizados
- Horarios de operación
- Errores o problemas reportados

---

### 4. Histórico de Locker

**Endpoint:** `GET /api/history/locker/{lockerId}?startDate={fecha}&endDate={fecha}`

**Descripción:** Todas las operaciones en un locker específico.

**Eventos Registrados:**
- Depósitos y retiros
- Cambios de estado de compartimentos
- Alertas generadas
- Mantenimientos realizados

---

### 5. Histórico Paginado

**Endpoint:** `GET /api/history/operations/paged?startDate={fecha}&endDate={fecha}&page={n}&size={n}`

**Descripción:** Todas las operaciones del sistema con paginación.

**Parámetros:**
- `page`: Número de página (inicia en 0)
- `size`: Cantidad de elementos por página (default: 20)
- `startDate`: Fecha inicial
- `endDate`: Fecha final

**Respuesta:**
```json
{
  "content": [...],
  "page": 0,
  "size": 20,
  "totalElements": 156,
  "totalPages": 8,
  "first": true,
  "last": false
}
```

---

## 📤 Exportación de Datos

### Exportar a CSV

**Endpoint:** `GET /api/reports/export/csv?reportType={tipo}&...`

**Tipos Soportados:**
- `OCCUPANCY_RATE` o `OCCUPANCY`
- `DEPOSITS_BY_PERIOD` o `DEPOSITS`
- `RETRIEVALS_BY_PERIOD` o `RETRIEVALS`
- `COMPARTMENT_USAGE`
- `COURIER_PERFORMANCE`
- `DAILY_SUMMARY`

**Formato CSV:**
```csv
"Reporte: OCCUPANCY_RATE","Período: 2026-02-28 a 2026-02-28","Generado: 2026-02-28T15:30:00"

"RESUMEN"
"totalCompartments","12"
"occupied","2"
"available","10"
"occupancyRate","16.67"

"DATOS DETALLADOS"
"BYSIZE"
"size","total","available","occupied"
"SMALL","4","4","0"
"MEDIUM","4","3","1"
"LARGE","4","3","1"
```

**Ejemplo:**
```bash
curl "http://localhost:8090/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  -o reporte.csv
```

---

### Exportar a PDF

**Endpoint:** `GET /api/reports/export/pdf?reportType={tipo}&...`

**Características del PDF:**
- ✅ Branding Servientrega (logo y colores corporativos)
- ✅ Encabezados en rojo corporativo (#ED1C24)
- ✅ Tablas profesionales con bordes y filas alternadas
- ✅ Campos traducidos al español
- ✅ Márgenes y espaciado profesional
- ✅ Footer con información del sistema

**Ejemplo:**
```bash
curl "http://localhost:8090/api/reports/export/pdf?reportType=DEPOSITS_BY_PERIOD&startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN" \
  -o reporte.pdf
```

---

## 💡 Ejemplos de Uso

### Ejemplo 1: Monitoreo de Ocupación

```bash
# Login
TOKEN=$(curl -s http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId":"COUR001","pin":"1234"}' | jq -r '.token')

# Obtener reporte de ocupación
curl "http://localhost:8090/api/reports/occupancy?lockerId=1" \
  -H "Authorization: Bearer $TOKEN" | jq

# Exportar a PDF
curl "http://localhost:8090/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  -o reportes/ocupacion_$(date +%Y%m%d).pdf
```

---

### Ejemplo 2: Análisis de Depósitos Mensuales

```bash
# Reporte de depósitos del mes
curl "http://localhost:8090/api/reports/deposits?startDate=2026-02-01&endDate=2026-02-28" \
  -H "Authorization: Bearer $TOKEN" | jq

# Exportar a CSV para análisis en Excel
curl "http://localhost:8090/api/reports/export/csv?reportType=DEPOSITS_BY_PERIOD&startDate=2026-02-01&endDate=2026-02-28" \
  -H "Authorization: Bearer $TOKEN" \
  -o reportes/depositos_febrero.csv
```

---

### Ejemplo 3: Auditoría de Paquete

```bash
# Ver histórico completo de un paquete
curl "http://localhost:8090/api/history/package/SRV123456789" \
  -H "Authorization: Bearer $TOKEN" | jq

# Resultado muestra:
# - Cuándo se depositó
# - Quién lo depositó
# - Código de retiro generado
# - Cambios de estado
# - Cuándo se retiró (si aplica)
```

---

### Ejemplo 4: Desempeño de Mensajero

```bash
# Reporte de desempeño mensual
curl "http://localhost:8090/api/reports/courier-performance?courierId=1&startDate=2026-02-01&endDate=2026-02-28" \
  -H "Authorization: Bearer $TOKEN" | jq

# Histórico de operaciones del mensajero
curl "http://localhost:8090/api/history/courier/1?startDate=2026-02-01&endDate=2026-02-28" \
  -H "Authorization: Bearer $TOKEN" | jq
```

---

### Ejemplo 5: Reporte Diario Automatizado

```bash
#!/bin/bash
# Script para generar reporte diario automático

DATE=$(date +%Y-%m-%d)
REPORTS_DIR="reportes/diarios/$DATE"
mkdir -p $REPORTS_DIR

# Login
TOKEN=$(curl -s http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId":"COUR001","pin":"1234"}' | jq -r '.token')

# Generar reportes
curl -s "http://localhost:8090/api/reports/export/pdf?reportType=DAILY_SUMMARY&date=$DATE" \
  -H "Authorization: Bearer $TOKEN" \
  -o "$REPORTS_DIR/resumen_diario.pdf"

curl -s "http://localhost:8090/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  -o "$REPORTS_DIR/ocupacion_locker1.pdf"

echo "Reportes generados en: $REPORTS_DIR"
```

---

## 📋 Resumen de Endpoints

| Endpoint | Método | Descripción |
|----------|--------|-------------|
| `/api/reports/occupancy` | GET | Reporte de ocupación |
| `/api/reports/deposits` | GET | Reporte de depósitos |
| `/api/reports/retrievals` | GET | Reporte de retiros |
| `/api/reports/compartment-usage` | GET | Uso de compartimentos |
| `/api/reports/active-packages` | GET | Paquetes activos |
| `/api/reports/expired-codes` | GET | Códigos expirados |
| `/api/reports/courier-performance` | GET | Desempeño mensajeros |
| `/api/reports/daily-summary` | GET | Resumen diario |
| `/api/reports/export/csv` | GET | Exportar a CSV |
| `/api/reports/export/pdf` | GET | Exportar a PDF |
| `/api/history/package/{tracking}` | GET | Histórico de paquete |
| `/api/history/compartment/{id}` | GET | Histórico de compartimento |
| `/api/history/courier/{id}` | GET | Histórico de mensajero |
| `/api/history/locker/{id}` | GET | Histórico de locker |
| `/api/history/operations/paged` | GET | Histórico paginado |

---

## 🔐 Autenticación

Todos los endpoints requieren autenticación JWT:

```bash
# 1. Obtener token
TOKEN=$(curl -s http://localhost:8090/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId":"COUR001","pin":"1234"}' | jq -r '.token')

# 2. Usar token en requests
curl "http://localhost:8090/api/reports/occupancy?lockerId=1" \
  -H "Authorization: Bearer $TOKEN"
```

---

## 📞 Soporte

Para más información, consultar:
- `README.md` - Guía general del proyecto
- `GUIA_OPTIMIZACION.md` - Optimización y performance
- Swagger UI: `http://localhost:8090/swagger-ui.html`
