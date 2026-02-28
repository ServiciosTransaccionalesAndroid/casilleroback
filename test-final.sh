#!/bin/bash
echo "🧪 Prueba de Históricos y Exportación PDF/CSV"
echo "=============================================="
echo ""

BASE_URL="http://localhost:8090"
REPORTS_DIR="reportes"

# Crear carpeta de reportes
mkdir -p $REPORTS_DIR
echo "📁 Carpeta de reportes: $REPORTS_DIR/"
echo ""

echo "1. Login..."
LOGIN_RESPONSE=$(curl -s -X POST $BASE_URL/api/auth/courier/login -H "Content-Type: application/json" -d '{"employeeId":"COUR001","pin":"1234"}')
TOKEN=$(echo $LOGIN_RESPONSE | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "   Token: ${TOKEN:0:40}..."
echo ""

echo "2. Histórico del Paquete SRV123456789"
curl -s "$BASE_URL/api/history/package/SRV123456789" -H "Authorization: Bearer $TOKEN" | head -c 500
echo ""
echo ""

echo "3. Histórico del Compartimento 1"
curl -s "$BASE_URL/api/history/compartment/1" -H "Authorization: Bearer $TOKEN" | head -c 500
echo ""
echo ""

echo "4. Histórico Paginado"
curl -s "$BASE_URL/api/history/operations/paged?startDate=2024-01-01&endDate=2024-12-31&page=0&size=5" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

echo "5. Reporte de Ocupación"
curl -s "$BASE_URL/api/reports/occupancy?lockerId=1" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

echo "6. Reporte de Depósitos"
curl -s "$BASE_URL/api/reports/deposits?startDate=2024-01-01&endDate=2024-12-31" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

echo "7. Exportando a CSV..."
curl -s "$BASE_URL/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_ocupacion.csv
echo "   ✓ $REPORTS_DIR/reporte_ocupacion.csv"

echo "8. Exportando a PDF (Ocupación)..."
curl -s "$BASE_URL/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_ocupacion.pdf
echo "   ✓ $REPORTS_DIR/reporte_ocupacion.pdf"

echo "9. Exportando a PDF (Depósitos)..."
curl -s "$BASE_URL/api/reports/export/pdf?reportType=DEPOSITS_BY_PERIOD&startDate=2024-01-01&endDate=2024-12-31" -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_depositos.pdf
echo "   ✓ $REPORTS_DIR/reporte_depositos.pdf"

echo "10. Exportando a PDF (Compartimentos)..."
curl -s "$BASE_URL/api/reports/export/pdf?reportType=COMPARTMENT_USAGE&lockerId=1" -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_compartimentos.pdf
echo "   ✓ $REPORTS_DIR/reporte_compartimentos.pdf"

echo ""
echo "=============================================="
echo "✅ Archivos Generados:"
echo "=============================================="
ls -lh $REPORTS_DIR/reporte_*.pdf $REPORTS_DIR/reporte_*.csv 2>/dev/null
echo ""
echo "Contenido del CSV:"
head -20 $REPORTS_DIR/reporte_ocupacion.csv
echo ""
echo "Para ver los PDFs:"
echo "  xdg-open $REPORTS_DIR/reporte_ocupacion.pdf  # Linux"
echo "  open $REPORTS_DIR/reporte_ocupacion.pdf      # macOS"
