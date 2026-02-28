#!/bin/bash
echo "🧪 Prueba Completa con Datos Reales"
echo "===================================="
echo ""

BASE_URL="http://localhost:8090"
REPORTS_DIR="reportes"

# Crear carpeta de reportes
mkdir -p $REPORTS_DIR
echo "📁 Carpeta de reportes: $REPORTS_DIR/"
echo ""

# Login
echo "1. Login Mensajero..."
TOKEN=$(curl -s $BASE_URL/api/auth/courier/login -H "Content-Type: application/json" -d '{"employeeId":"COUR001","pin":"1234"}' | grep -o '"token":"[^"]*' | cut -d'"' -f4)
echo "   ✓ Token: ${TOKEN:0:30}..."
echo ""

# Validar paquetes
echo "2. Validar Paquetes..."
curl -s "$BASE_URL/api/packages/validate?trackingNumber=SRV123456789" -H "Authorization: Bearer $TOKEN" | head -c 150
echo ""
curl -s "$BASE_URL/api/packages/validate?trackingNumber=SRV987654321" -H "Authorization: Bearer $TOKEN" | head -c 150
echo ""
echo ""

# Depositar paquete 1
echo "3. Depositar Paquete 1 (SRV123456789) en compartimento 5..."
DEPOSIT1=$(curl -s -X POST $BASE_URL/api/deposits \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"trackingNumber":"SRV123456789","lockerId":1,"compartmentId":5,"courierId":1}')
echo "$DEPOSIT1"
CODE1=$(echo $DEPOSIT1 | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
PIN1=$(echo $DEPOSIT1 | grep -o '"secretPin":"[^"]*' | cut -d'"' -f4)
echo "   📦 Código: $CODE1, PIN: $PIN1"
echo ""

# Depositar paquete 2
echo "4. Depositar Paquete 2 (SRV987654321) en compartimento 9..."
DEPOSIT2=$(curl -s -X POST $BASE_URL/api/deposits \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{"trackingNumber":"SRV987654321","lockerId":1,"compartmentId":9,"courierId":1}')
echo "$DEPOSIT2"
CODE2=$(echo $DEPOSIT2 | grep -o '"retrievalCode":"[^"]*' | cut -d'"' -f4)
PIN2=$(echo $DEPOSIT2 | grep -o '"secretPin":"[^"]*' | cut -d'"' -f4)
echo "   📦 Código: $CODE2, PIN: $PIN2"
echo ""

# Histórico del paquete 1
echo "5. Histórico del Paquete SRV123456789..."
curl -s "$BASE_URL/api/history/package/SRV123456789" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Histórico del compartimento
echo "6. Histórico del Compartimento 5..."
curl -s "$BASE_URL/api/history/compartment/5" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Histórico del mensajero
echo "7. Histórico del Mensajero..."
curl -s "$BASE_URL/api/history/courier/1?startDate=2024-01-01&endDate=2026-12-31" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Histórico del locker
echo "8. Histórico del Locker..."
curl -s "$BASE_URL/api/history/locker/1?startDate=2024-01-01&endDate=2026-12-31" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Histórico paginado
echo "9. Histórico Paginado (todas las operaciones)..."
curl -s "$BASE_URL/api/history/operations/paged?startDate=2024-01-01&endDate=2026-12-31&page=0&size=10" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Reporte de ocupación
echo "10. Reporte de Ocupación..."
curl -s "$BASE_URL/api/reports/occupancy?lockerId=1" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Reporte de depósitos
echo "11. Reporte de Depósitos..."
curl -s "$BASE_URL/api/reports/deposits?startDate=2024-01-01&endDate=2026-12-31" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Reporte de paquetes activos
echo "12. Reporte de Paquetes Activos..."
curl -s "$BASE_URL/api/reports/active-packages?lockerId=1" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Reporte de uso de compartimentos
echo "13. Reporte de Uso de Compartimentos..."
curl -s "$BASE_URL/api/reports/compartment-usage?lockerId=1" -H "Authorization: Bearer $TOKEN"
echo ""
echo ""

# Exportar PDFs
echo "14. Exportando Reportes..."
curl -s "$BASE_URL/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_ocupacion.pdf
echo "   ✓ $REPORTS_DIR/reporte_ocupacion.pdf"

curl -s "$BASE_URL/api/reports/export/pdf?reportType=DEPOSITS_BY_PERIOD&startDate=2024-01-01&endDate=2026-12-31" \
  -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_depositos.pdf
echo "   ✓ $REPORTS_DIR/reporte_depositos.pdf"

curl -s "$BASE_URL/api/reports/export/pdf?reportType=COMPARTMENT_USAGE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_compartimentos.pdf
echo "   ✓ $REPORTS_DIR/reporte_compartimentos.pdf"

curl -s "$BASE_URL/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_ocupacion.csv
echo "   ✓ $REPORTS_DIR/reporte_ocupacion.csv"

curl -s "$BASE_URL/api/reports/export/csv?reportType=DEPOSITS_BY_PERIOD&startDate=2024-01-01&endDate=2026-12-31" \
  -H "Authorization: Bearer $TOKEN" -o $REPORTS_DIR/reporte_depositos.csv
echo "   ✓ $REPORTS_DIR/reporte_depositos.csv"

echo ""
echo "===================================="
echo "✅ RESUMEN DE PRUEBAS"
echo "===================================="
echo ""
echo "📦 Paquetes depositados: 2"
echo "   - SRV123456789 → Código: $CODE1, PIN: $PIN1"
echo "   - SRV987654321 → Código: $CODE2, PIN: $PIN2"
echo ""
echo "📄 Archivos generados:"
ls -lh $REPORTS_DIR/reporte_*.pdf $REPORTS_DIR/reporte_*.csv
echo ""
echo "📊 Tipo de archivos:"
file $REPORTS_DIR/reporte_*.pdf $REPORTS_DIR/reporte_*.csv
echo ""
echo "📋 Contenido CSV Ocupación:"
head -20 $REPORTS_DIR/reporte_ocupacion.csv
echo ""
echo "📋 Contenido CSV Depósitos:"
head -20 $REPORTS_DIR/reporte_depositos.csv
echo ""
echo "Para ver los PDFs:"
echo "  xdg-open $REPORTS_DIR/reporte_ocupacion.pdf"
echo "  xdg-open $REPORTS_DIR/reporte_depositos.pdf"
