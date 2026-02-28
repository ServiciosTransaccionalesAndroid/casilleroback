#!/bin/bash

echo "🧪 Prueba Completa del Sistema de Casilleros"
echo "=============================================="
echo ""

# Colores
GREEN='\033[0;32m'
BLUE='\033[0;34m'
YELLOW='\033[1;33m'
NC='\033[0m' # No Color

# 1. Health Check
echo -e "${BLUE}1. Health Check${NC}"
curl -s http://localhost:8080/api/health | jq
echo ""

# 2. Login
echo -e "${BLUE}2. Login de Mensajero${NC}"
LOGIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/auth/courier/login \
  -H "Content-Type: application/json" \
  -d '{"employeeId":"MSG001","password":"password123"}')

TOKEN=$(echo $LOGIN_RESPONSE | jq -r '.token')
echo "Token obtenido: ${TOKEN:0:30}..."
echo ""

# 3. Validar paquete
echo -e "${BLUE}3. Validar Paquete${NC}"
curl -s "http://localhost:8080/api/packages/validate?trackingNumber=SRV123456789" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 4. Registrar depósito
echo -e "${BLUE}4. Registrar Depósito${NC}"
DEPOSIT_RESPONSE=$(curl -s -X POST http://localhost:8080/api/deposits \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "trackingNumber": "SRV123456789",
    "lockerId": 1,
    "compartmentSize": "MEDIUM"
  }')
echo $DEPOSIT_RESPONSE | jq
echo ""

# Extraer código de retiro
RETRIEVAL_CODE=$(echo $DEPOSIT_RESPONSE | jq -r '.retrievalCode')
SECRET_PIN=$(echo $DEPOSIT_RESPONSE | jq -r '.secretPin')
echo -e "${GREEN}Código de retiro: $RETRIEVAL_CODE${NC}"
echo -e "${GREEN}PIN secreto: $SECRET_PIN${NC}"
echo ""

# 5. Histórico del paquete
echo -e "${BLUE}5. Histórico del Paquete${NC}"
curl -s "http://localhost:8080/api/history/package/SRV123456789" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 6. Histórico del compartimento
echo -e "${BLUE}6. Histórico del Compartimento${NC}"
curl -s "http://localhost:8080/api/history/compartment/1" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 7. Histórico del locker
echo -e "${BLUE}7. Histórico del Locker${NC}"
curl -s "http://localhost:8080/api/history/locker/1?startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 8. Histórico del mensajero
echo -e "${BLUE}8. Histórico del Mensajero${NC}"
curl -s "http://localhost:8080/api/history/courier/1?startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 9. Histórico paginado
echo -e "${BLUE}9. Histórico Paginado (Página 0, 5 elementos)${NC}"
curl -s "http://localhost:8080/api/history/operations/paged?startDate=2024-01-01&endDate=2024-12-31&page=0&size=5" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 10. Reporte de ocupación
echo -e "${BLUE}10. Reporte de Ocupación${NC}"
curl -s "http://localhost:8080/api/reports/occupancy?lockerId=1" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 11. Reporte de depósitos
echo -e "${BLUE}11. Reporte de Depósitos${NC}"
curl -s "http://localhost:8080/api/reports/deposits?startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 12. Reporte de paquetes activos
echo -e "${BLUE}12. Reporte de Paquetes Activos${NC}"
curl -s "http://localhost:8080/api/reports/active-packages?lockerId=1" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 13. Reporte de uso de compartimentos
echo -e "${BLUE}13. Reporte de Uso de Compartimentos${NC}"
curl -s "http://localhost:8080/api/reports/compartment-usage?lockerId=1" \
  -H "Authorization: Bearer $TOKEN" | jq
echo ""

# 14. Exportar reporte a CSV
echo -e "${BLUE}14. Exportar Reporte a CSV${NC}"
curl -s "http://localhost:8080/api/reports/export/csv?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  -o reporte_ocupacion.csv
if [ -f reporte_ocupacion.csv ]; then
  echo -e "${GREEN}✓ CSV generado: reporte_ocupacion.csv${NC}"
  echo "Contenido:"
  cat reporte_ocupacion.csv
else
  echo -e "${YELLOW}⚠ No se pudo generar el CSV${NC}"
fi
echo ""

# 15. Exportar reporte a PDF
echo -e "${BLUE}15. Exportar Reporte a PDF${NC}"
curl -s "http://localhost:8080/api/reports/export/pdf?reportType=OCCUPANCY_RATE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  -o reporte_ocupacion.pdf
if [ -f reporte_ocupacion.pdf ]; then
  SIZE=$(ls -lh reporte_ocupacion.pdf | awk '{print $5}')
  echo -e "${GREEN}✓ PDF generado: reporte_ocupacion.pdf (${SIZE})${NC}"
else
  echo -e "${YELLOW}⚠ No se pudo generar el PDF${NC}"
fi
echo ""

# 16. Exportar reporte de depósitos a PDF
echo -e "${BLUE}16. Exportar Reporte de Depósitos a PDF${NC}"
curl -s "http://localhost:8080/api/reports/export/pdf?reportType=DEPOSITS_BY_PERIOD&startDate=2024-01-01&endDate=2024-12-31" \
  -H "Authorization: Bearer $TOKEN" \
  -o reporte_depositos.pdf
if [ -f reporte_depositos.pdf ]; then
  SIZE=$(ls -lh reporte_depositos.pdf | awk '{print $5}')
  echo -e "${GREEN}✓ PDF generado: reporte_depositos.pdf (${SIZE})${NC}"
else
  echo -e "${YELLOW}⚠ No se pudo generar el PDF${NC}"
fi
echo ""

# 17. Exportar reporte de compartimentos a PDF
echo -e "${BLUE}17. Exportar Reporte de Compartimentos a PDF${NC}"
curl -s "http://localhost:8080/api/reports/export/pdf?reportType=COMPARTMENT_USAGE&lockerId=1" \
  -H "Authorization: Bearer $TOKEN" \
  -o reporte_compartimentos.pdf
if [ -f reporte_compartimentos.pdf ]; then
  SIZE=$(ls -lh reporte_compartimentos.pdf | awk '{print $5}')
  echo -e "${GREEN}✓ PDF generado: reporte_compartimentos.pdf (${SIZE})${NC}"
else
  echo -e "${YELLOW}⚠ No se pudo generar el PDF${NC}"
fi
echo ""

# Resumen
echo -e "${GREEN}=============================================="
echo "✅ Prueba Completa Finalizada"
echo "=============================================="
echo ""
echo "Archivos generados:"
ls -lh reporte_*.pdf reporte_*.csv 2>/dev/null || echo "No se generaron archivos"
echo ""
echo "Para ver los PDFs:"
echo "  xdg-open reporte_ocupacion.pdf      # Linux"
echo "  open reporte_ocupacion.pdf          # macOS"
echo -e "${NC}"
