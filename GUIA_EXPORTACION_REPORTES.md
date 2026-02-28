# Guía de Exportación de Reportes

## Endpoints de Exportación

### 1. Exportar a CSV
```
GET /api/reports/export/csv?reportType={type}&startDate={date}&endDate={date}&lockerId={id}
```

**Descripción:** Exporta cualquier reporte a formato CSV.

**Parámetros:**
- `reportType` (requerido): Tipo de reporte
- `startDate` (opcional): Fecha inicio (YYYY-MM-DD)
- `endDate` (opcional): Fecha fin (YYYY-MM-DD)
- `lockerId` (opcional): ID del locker

**Tipos de reporte disponibles:**
- `DEPOSITS` - Depósitos por período
- `RETRIEVALS` - Retiros por período
- `OCCUPANCY` - Tasa de ocupación
- `EXPIRED_CODES` - Códigos expirados
- `DAILY_SUMMARY` - Resumen diario

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/reports/export/csv?reportType=DEPOSITS&startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}" \
  --output report_deposits.csv
```

**Respuesta:**
- Content-Type: `text/csv`
- Content-Disposition: `attachment; filename="report_DEPOSITS.csv"`
- Archivo CSV descargable

---

### 2. Exportar a PDF
```
GET /api/reports/export/pdf?reportType={type}&startDate={date}&endDate={date}&lockerId={id}
```

**Descripción:** Exporta cualquier reporte a formato PDF.

**Parámetros:** (iguales que CSV)

**Ejemplo:**
```bash
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=DAILY_SUMMARY&startDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}" \
  --output report_summary.pdf
```

**Respuesta:**
- Content-Type: `application/pdf`
- Content-Disposition: `attachment; filename="report_DAILY_SUMMARY.pdf"`
- Archivo PDF descargable

---

## Ejemplos de Uso

### Exportar Depósitos del Mes a CSV
```bash
curl -X GET "http://localhost:8090/api/reports/export/csv?reportType=DEPOSITS&startDate=2024-02-01&endDate=2024-02-29" \
  -H "Authorization: Bearer {TOKEN}" \
  --output depositos_febrero.csv
```

### Exportar Ocupación a PDF
```bash
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=OCCUPANCY&lockerId=1" \
  -H "Authorization: Bearer {TOKEN}" \
  --output ocupacion_locker1.pdf
```

### Exportar Resumen Diario a CSV
```bash
curl -X GET "http://localhost:8090/api/reports/export/csv?reportType=DAILY_SUMMARY&startDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}" \
  --output resumen_diario.csv
```

### Exportar Códigos Expirados a PDF
```bash
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=EXPIRED_CODES&startDate=2024-02-01&endDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}" \
  --output codigos_expirados.pdf
```

---

## Formato de Archivos

### CSV
El archivo CSV contiene:
1. **Header:** Información del reporte (tipo, período, fecha de generación)
2. **Resumen:** Métricas calculadas
3. **Datos Detallados:** Tablas con información completa

**Ejemplo de estructura:**
```csv
Reporte: DEPOSITS_BY_PERIOD,Período: 2024-02-01 a 2024-02-18,Generado: 2024-02-18T19:30:00

RESUMEN
totalDeposits,215
averagePerDay,11.94
daysWithData,18

DATOS DETALLADOS
DAILY
date,count
2024-02-01,15
2024-02-02,23
2024-02-03,18
```

### PDF
El archivo PDF contiene:
1. **Título:** Tipo de reporte centrado
2. **Período:** Rango de fechas
3. **Fecha de generación**
4. **Resumen:** Tabla con métricas clave
5. **Datos Detallados:** Tablas formateadas con todos los datos

---

## Casos de Uso

### Reportes Mensuales
```bash
# Generar reporte mensual de depósitos en PDF
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=DEPOSITS&startDate=2024-02-01&endDate=2024-02-29" \
  -H "Authorization: Bearer {TOKEN}" \
  --output reporte_mensual_febrero.pdf
```

### Análisis de Ocupación
```bash
# Exportar estado de ocupación actual a CSV para análisis
curl -X GET "http://localhost:8090/api/reports/export/csv?reportType=OCCUPANCY&lockerId=1" \
  -H "Authorization: Bearer {TOKEN}" \
  --output analisis_ocupacion.csv
```

### Auditoría de Códigos
```bash
# Generar reporte de códigos expirados para auditoría
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=EXPIRED_CODES&startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer {TOKEN}" \
  --output auditoria_codigos_2024.pdf
```

### Dashboard Ejecutivo
```bash
# Resumen diario para presentación
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=DAILY_SUMMARY&startDate=2024-02-18" \
  -H "Authorization: Bearer {TOKEN}" \
  --output dashboard_ejecutivo.pdf
```

---

## Integración con Scripts

### Script Bash para Reportes Automáticos
```bash
#!/bin/bash

TOKEN="your_jwt_token_here"
DATE=$(date +%Y-%m-%d)
OUTPUT_DIR="./reports"

mkdir -p $OUTPUT_DIR

# Reporte diario en CSV
curl -X GET "http://localhost:8090/api/reports/export/csv?reportType=DAILY_SUMMARY&startDate=$DATE" \
  -H "Authorization: Bearer $TOKEN" \
  --output "$OUTPUT_DIR/daily_$DATE.csv"

# Reporte de ocupación en PDF
curl -X GET "http://localhost:8090/api/reports/export/pdf?reportType=OCCUPANCY&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  --output "$OUTPUT_DIR/occupancy_$DATE.pdf"

echo "Reportes generados en $OUTPUT_DIR"
```

### Cron Job para Reportes Diarios
```bash
# Ejecutar todos los días a las 23:00
0 23 * * * /path/to/generate_reports.sh
```

---

## Notas

- Todos los endpoints requieren autenticación JWT
- Los archivos se descargan automáticamente con nombres descriptivos
- El formato CSV es compatible con Excel y Google Sheets
- Los PDFs están formateados profesionalmente con tablas
- Los reportes se generan en tiempo real (no se cachean)
- Tamaño máximo recomendado: 10,000 registros por reporte

---

## Troubleshooting

### Error: "Unknown report type"
Verifica que el `reportType` sea uno de los valores válidos (DEPOSITS, RETRIEVALS, OCCUPANCY, EXPIRED_CODES, DAILY_SUMMARY).

### Error: "Required parameter missing"
Algunos reportes requieren parámetros específicos:
- DEPOSITS/RETRIEVALS/EXPIRED_CODES: requieren `startDate` y `endDate`
- OCCUPANCY: requiere `lockerId`
- DAILY_SUMMARY: requiere `startDate`

### Archivo vacío o corrupto
Verifica que el token JWT sea válido y que tengas permisos para acceder al reporte.

---

**Documentación Swagger:** http://localhost:8090/swagger-ui.html
