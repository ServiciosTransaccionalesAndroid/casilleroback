#!/bin/bash
echo "🧪 Prueba Completa: Históricos y PDFs"
echo "======================================"
echo ""

BASE_URL="http://localhost:8090"

# Login
echo "1. Login..."
TOKEN=$(curl -s $BASE_URL/api/auth/courier/login -H "Content-Type: application/json" -d '{"employeeId":"COUR001","pin":"1234"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "   ✓ Token obtenido"
echo ""

# Registrar depósito
echo "2. Registrar Depósito..."
DEPOSIT=$(curl -s -X POST $BASE_URL/api/deposits -H "Authorization: Bearer $TOKEN" -H "Content-Type: application/json" -d '{"trackingNumber":"SRV123456789","lockerId":1,"compartmentSize":"MEDIUM"}')
echo "$DEPOSIT" | head -c 200
echo ""
echo ""

# Histórico del paquete
echo "3. Histórico del Paquete SRV123456789"
curl -s "$BASE_URL/api/history/package/SRV123456789" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Histórico del compartimento
echo "4. Histórico del Compartimento 5"
curl -s "$BASE_URL/api/history/compartment/5" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Histórico paginado
echo "5. Histórico Paginado"
curl -s "$BASE_URL/api/history/operations/paged?startDate=2024-01-01&endDate=2026-12-31&page=0&size=10" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Reporte de ocupación
echo "6. Reporte de Ocupación"
curl -s "$BASE_URL/api/reports/occupancy?lockerId=1" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Exportar PDFs
echo "7. Exportando PDFs..."
curl -s "$BASE_URL/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o reporte_ocupacion.pdf
curl -s "$BASE_URL/api/reports/export/pdf?reportType=DEPOSITS_BY_PERIOD&startDate=2024-01-01&endDate=2026-12-31" -H "Authorization: Bearer $TOKEN" -o reporte_depositos.pdf
curl -s "$BASE_URL/api/reports/export/pdf?reportType=COMPARTMENT_USAGE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o reporte_compartimentos.pdf
curl -s "$BASE_URL/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o reporte_ocupacion.csv
echo "   ✓ PDFs y CSV generados"
echo ""

echo "======================================"
echo "✅ Archivos Generados:"
echo "======================================"
ls -lh reporte_*.pdf reporte_*.csv
echo ""

echo "Tipo de archivos:"
file reporte_*.pdf reporte_*.csv
echo ""

echo "Contenido del CSV (primeras 15 líneas):"
head -15 reporte_ocupacion.csv
echo ""

echo "Para ver los PDFs:"
echo "  xdg-open reporte_ocupacion.pdf"
