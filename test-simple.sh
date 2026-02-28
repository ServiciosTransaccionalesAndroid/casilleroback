#!/bin/bash
TOKEN=$(curl -s -X POST http://localhost:8080/api/auth/courier/login -H "Content-Type: application/json" -d '{"employeeId":"MSG001","password":"password123"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "Token: ${TOKEN:0:30}..."
echo ""
echo "=== Historico Paquete ==="
curl -s "http://localhost:8080/api/history/package/SRV123456789" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""
echo "=== Reporte Ocupacion ==="
curl -s "http://localhost:8080/api/reports/occupancy?lockerId=1" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""
echo "=== Generando PDFs ==="
curl -s "http://localhost:8080/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o reporte_ocupacion.pdf
curl -s "http://localhost:8080/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o reporte_ocupacion.csv
ls -lh reporte_*
echo ""
head -10 reporte_ocupacion.csv
